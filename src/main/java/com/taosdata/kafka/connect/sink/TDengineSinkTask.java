package com.taosdata.kafka.connect.sink;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.taosdata.jdbc.TSDBDriver;
import com.taosdata.kafka.connect.db.CacheProcessor;
import com.taosdata.kafka.connect.db.ConnectionProvider;
import com.taosdata.kafka.connect.db.Processor;
import com.taosdata.kafka.connect.db.TSDBConnectionProvider;
import com.taosdata.kafka.connect.exception.RecordException;
import com.taosdata.kafka.connect.exception.SchemaException;
import com.taosdata.kafka.connect.util.VersionUtils;
import jdk.internal.joptsimple.internal.Strings;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.errors.RetriableException;
import org.apache.kafka.connect.sink.ErrantRecordReporter;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static com.taosdata.kafka.connect.sink.JSONUtils.*;
import static com.taosdata.kafka.connect.sink.SinkConstants.*;

/**
 * TDengine sink task
 */
public class TDengineSinkTask extends SinkTask {
    private static final Logger log = LoggerFactory.getLogger(TDengineSinkTask.class);

    private SinkConfig config;
    private Processor writer;
    ErrantRecordReporter reporter;
    private int remainingRetries;
    Map<String, Schema> topics = Maps.newHashMap();

    @Override
    public void start(Map<String, String> map) {
        log.info("Starting TDengine Sink task...");
        config = new SinkConfig(map);
        initTask();
        String schemaPath = config.getSchemaLocation();
        StringBuilder schemaStr = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(schemaPath))) {
            String str;
            while ((str = reader.readLine()) != null) {
                schemaStr.append(str);
            }
        } catch (IOException e) {
            throw new ConfigException(String.format("JSON schema configuration can not get. error '%s'", e));
        }

        JSONObject jsonObject = JSON.parseObject(schemaStr.toString());

        Map<String, Schema> schemas = Maps.newHashMap();
        for (Object o : jsonObject.getJSONArray(SCHEMAS)) {
            Schema schema = new Schema();
            Map<String, Index> indexMap = Maps.newHashMap();
            String stableName = null;

            JSONObject schemaObject = (JSONObject) JSON.toJSON(o);
            schemaObjectValidator(schemaObject);
            for (Map.Entry<String, Object> entry : schemaObject.entrySet()) {
                switch (entry.getKey()) {
                    case SCHEMA_NAME: {
                        if (entry.getValue() == null) {
                            throw new SchemaException(String.format("schema: %s. %s's value is null",
                                    schemaStr, SCHEMA_NAME));
                        }
                        schema.setName(String.valueOf(entry.getValue()));
                        break;
                    }
                    case SCHEMA_DATABASE: {
                        schema.setDatabase(getString(entry.getValue()));
                        break;
                    }
                    case SCHEMA_STABLE_NAME_SPECIFY: {
                        schema.setStableNameSpecify(getString(entry.getValue()));
                        break;
                    }
                    case SCHEMA_STABLE_NAME: {
                        stableName = getString(entry.getValue());
                        break;
                    }
                    case SCHEMA_TABLE_NAME: {
                        if (entry.getValue() == null) {
                            throw new SchemaException(String.format("schema: %s. %s's value is null",
                                    schemaStr, SCHEMA_TABLE_NAME));
                        }
                        JSONArray objects = JSON.parseArray(String.valueOf(entry.getValue()));
                        schema.setTableName(objects.toArray(new String[0]));
                        break;
                    }
                    case SCHEMA_DELIMITER: {
                        schema.setDelimiter(getString(entry.getValue()));
                        break;
                    }
                    default: {
                        String key = entry.getKey();
                        String[] split = key.split(Schema.SEPARATOR);
                        Map<String, Index> tmp = indexMap;
                        for (int i = 0; i < split.length - 1; i++) {
                            String name = split[i];
                            if (tmp.containsKey(name)) {
                                Index index = tmp.get(name);
                                tmp = index.getIndexMap();
                                continue;
                            }
                            Index index = new Index(name);
                            tmp.put(name, index);
                            tmp = index.getIndexMap();
                        }
                        String name = split[split.length - 1];
                        Index index = new Index(name);
                        JSONObject value = (JSONObject) JSON.toJSON(entry.getValue());
                        index.setColumn(json2Col(value));
                        tmp.put(name, index);
                    }
                }
            }
            if (null != schema.getStableNameSpecify()) {
                schema.setStableName(null);
            } else {
                if (null == stableName) {
                    throw new SchemaException(String.format("schema: %s. %s and %s cannot be both empty ",
                            schemaStr, SCHEMA_STABLE_NAME, SCHEMA_STABLE_NAME_SPECIFY));
                }
                schema.setStableName(stableName);
            }
            schema.setIndexMap(indexMap);
            schemas.put(schema.getName(), schema);
        }

        JSONObject topicObject = jsonObject.getJSONObject(TOPIC);
        for (Map.Entry<String, Object> entry : topicObject.entrySet()) {
            String v = String.valueOf(entry.getValue());
            if (!schemas.containsKey(v)) {
                throw new SchemaException(String.format("topic cannot find the corresponding schema, topic:%s, schema:%s"
                        , entry.getKey(), entry.getValue()));
            }
            topics.put(entry.getKey(), schemas.get(v));
        }

        try {
            reporter = context.errantRecordReporter();
        } catch (NoSuchMethodError | NoClassDefFoundError e) {
            reporter = null;
        }
        // There will be a retry at the end
        remainingRetries = config.getMaxRetries() - 1;
        log.debug("Started TDengine sink task");
    }

    private void initTask() {
        Properties properties = new Properties();
        properties.setProperty(TSDBDriver.PROPERTY_KEY_USER, config.getConnectionUser());
        properties.setProperty(TSDBDriver.PROPERTY_KEY_PASSWORD, config.getConnectionPassword());
        properties.setProperty(TSDBDriver.PROPERTY_KEY_CHARSET, config.getCharset());
        ConnectionProvider provider = new TSDBConnectionProvider(config.getConnectionUrl(), properties, config.getConnectionAttempts(), config.getConnectionBackoffMs());
        writer = new CacheProcessor<>(provider);
    }

    @Override
    public void put(Collection<SinkRecord> records) {
        if (records.isEmpty()) {
            return;
        }
        // do some debug log
        int size = records.size();
        records.stream().findFirst().ifPresent(sinkRecord -> {
            log.debug("Received {} records. First record kafka coordinates:({}-{}-{}). Writing them to the "
                    + "database...", size, sinkRecord.topic(), sinkRecord.kafkaPartition(), sinkRecord.kafkaOffset());
        });

        List<SinkRecord> currentGroup = new ArrayList<>();
        int maxBatchSize = config.getBatchSize();

        String previousTopic = "";
        for (SinkRecord record : records) {
            if (record == null) continue;
            if (maxBatchSize > 0 && currentGroup.size() == maxBatchSize || !previousTopic.equals(record.topic())) {

                bulkWriteBatch(currentGroup, previousTopic);
                // next batch insert
                currentGroup = new ArrayList<>();
                previousTopic = record.topic();
            }
            currentGroup.add(record);
        }
        bulkWriteBatch(currentGroup, previousTopic);
    }

    private void bulkWriteBatch(final List<SinkRecord> batch, String topic) {
        if (batch.isEmpty()) {
            return;
        }

        if (config.isSingleDatabase()) {
            writer.setDbName(config.getConnectionDb());
        } else {
            writer.setDbName(config.getConnectionDatabasePrefix() + topic);
        }

        try {
            List<JsonSql> values = new ArrayList<>();
            for (SinkRecord record : batch) {
                JsonSql value = null;
                String recordString = String.valueOf(record.value());
                log.trace("raw record String: {}", recordString);
                JSONObject jsonObject = JSON.parseObject(recordString);
                Schema schema = topics.get(topic);
                try {
                    value = schemaHandler(schema, jsonObject);
                } catch (RecordException e) {
                    reporter.report(record, e);
                }
                values.add(value);
            }
            executSql(values);
        } catch (Exception e) {
            log.warn("Write of {} records failed, remainingRetries={} ", batch.size(), remainingRetries, e);
            if (remainingRetries > 0) {
                if (e instanceof SQLException) {
                    writer.close();
                }
                remainingRetries--;
                context.timeout(config.getRetryBackoffMs());
                throw new RetriableException(e);
            } else {
                if (reporter != null) {
                    unrollAndRetry(batch);
                } else {
                    log.error("Failing task after exhausting retries; " + "encountered exceptions on last write attempt. " + "For complete details on each exception, please enable DEBUG logging.");
                    throw new ConnectException(e);
                }
            }
        }
    }

    private JsonSql schemaHandler(Schema schema, JSONObject jsonObject) {
        JsonSql sql = new JsonSql();

        for (Map.Entry<String, Index> entry : schema.getIndexMap().entrySet()) {
            convert(entry.getValue(), jsonObject.get(entry.getKey()), sql);
        }

        if (null != schema.getStableNameSpecify()) {
            sql.setStName(schema.getStableNameSpecify());
        } else {
            if (sql.getAll().containsKey(schema.getStableName())) {
                sql.setStName(sql.getAll().get(schema.getStableName()));
            } else {
                throw new RecordException(String.format("schema: %s cannot find stable value, %s config is %s"
                        , schema.getName(), SCHEMA_STABLE_NAME, schema.getStableName()));
            }
        }

        if (schema.getDelimiter() != null) {
            sql.settName(Arrays.stream(schema.getTableName()).map(c -> sql.getAll().get(c))
                    .collect(Collectors.joining(schema.getDelimiter())));
        }else {
            sql.settName(sql.getAll().get(schema.getTableName()[0]));
        }
        return sql;
    }

    private void convert(Index index, Object object, JsonSql sql) {
        if (index.getIndexMap().isEmpty()) {
            Column column = index.getColumn();
            Map<String, String> tmp;
            Map<String, String> all = sql.getAll();
            if (column.getOther() != null) {
                all.put(column.getOther(), String.valueOf(object));
            } else {
                if (column.isTag()) {
                    tmp = sql.getTag();
                } else {
                    tmp = sql.getCols();
                }
                if (null == object) {
                    if (null != column.getDefaultValue()) {
                        tmp.put(column.getTargetColumn(), checkAndConvertString(column.getDefaultValue()));
                        all.put(column.getTargetColumn(), String.valueOf(column.getDefaultValue()));
                    } else {
                        if (!column.isOptional()) {
                            throw new RecordException("The " + index.getName() + " field is blank");
                        }
                    }
                } else {
                    if (column.isTypeEquals()) {
                        tmp.put(column.getTargetColumn(), checkAndConvertString(object));
                        all.put(column.getTargetColumn(), String.valueOf(object));
                    } else {
                        if ("string".equalsIgnoreCase(column.getTargetType())) {
                            tmp.put(column.getTargetColumn(), "'" + object + "'");
                            all.put(column.getTargetColumn(), String.valueOf(object));
                        }
                    }
                }
            }
        } else {

            for (Map.Entry<String, Index> entry : index.getIndexMap().entrySet()) {
                convert(entry.getValue(), ((JSONObject) object).get(entry.getKey()), sql);
            }
        }
    }

    private String checkAndConvertString(Object o) {
        if (o == null) return null;
        if (o instanceof String) return "'" + o + "'";
        return String.valueOf(o);
    }

    private void executSql(List<JsonSql> values) throws SQLException {
        if (values.isEmpty())
            return;

        String sql = convertSql(values);
        writer.execute(sql);
    }

    private String convertSql(List<JsonSql> jsons) {

        StringBuilder sb = new StringBuilder("insert into ");
        for (JsonSql json : jsons) {
            sb.append("`").append(json.gettName()).append("`");
            sb.append(" using ").append(json.getStName()).append(" (");
            StringBuilder tv = new StringBuilder(" tags (");
            int i = 0;
            for (Map.Entry<String, String> entry : json.getTag().entrySet()) {
                sb.append(entry.getKey());
                tv.append(entry.getValue());
                if (i == json.getTag().size() - 1) {
                    sb.append(") ");
                    tv.append(") ");
                } else {
                    sb.append(", ");
                    tv.append(", ");
                }
                i++;
            }
            sb.append(tv).append(" (");

            StringBuilder cv = new StringBuilder(" values (");
            i = 0;
            for (Map.Entry<String, String> entry : json.getCols().entrySet()) {
                sb.append(entry.getKey());
                cv.append(entry.getValue());
                if (i == json.getCols().size() - 1) {
                    sb.append(") ");
                    cv.append(") ");
                } else {
                    sb.append(", ");
                    cv.append(", ");
                }
                i++;
            }
            sb.append(cv);
        }
        return sb.toString();
    }

    private void unrollAndRetry(Collection<SinkRecord> records) {
        for (SinkRecord record : records) {
            try {
                JSONObject jsonObject = JSON.parseObject(String.valueOf(record.value()));

                JsonSql value = schemaHandler(topics.get(record.topic()), jsonObject);

                executSql(Collections.singletonList(value));
            } catch (Exception e) {
                reporter.report(record, e);
            }
        }
    }

    @Override
    public void stop() {
        log.info("Stopping TDengine sink task");
        try {
            writer.close();
        } catch (Exception e) {
            log.warn("Ignoring error closing connection", e);
        } finally {
            writer = null;
        }
    }

    @Override
    public String version() {
        return VersionUtils.getVersion();
    }
}
