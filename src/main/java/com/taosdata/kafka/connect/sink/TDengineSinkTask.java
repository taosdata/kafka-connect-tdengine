package com.taosdata.kafka.connect.sink;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.taosdata.jdbc.TSDBDriver;
import com.taosdata.kafka.connect.db.CacheProcessor;
import com.taosdata.kafka.connect.db.ConnectionProvider;
import com.taosdata.kafka.connect.db.Processor;
import com.taosdata.kafka.connect.db.TSDBConnectionProvider;
import com.taosdata.kafka.connect.exception.RecordException;
import com.taosdata.kafka.connect.exception.SchemaException;
import com.taosdata.kafka.connect.util.VersionUtils;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.errors.RetriableException;
import org.apache.kafka.connect.sink.ErrantRecordReporter;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
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

        String schemaStr1 = null;
        String schemaLocation = config.getSchemaLocation();
        String schemaType = config.getSchemaType();
        try {
            if (SCHEMA_TYPE_LOCAL.equals(schemaType)) {
                StringBuilder sb = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new FileReader(schemaLocation))) {
                    String str;
                    while ((str = reader.readLine()) != null) {
                        sb.append(str);
                    }
                }
                schemaStr1 = sb.toString();
            } else {
                InputStream in = null;
                ByteArrayOutputStream outputStream = null;
                try {
                    URL url = new URL(schemaLocation);
                    in = url.openStream();
                    outputStream = new ByteArrayOutputStream();
                    byte[] b = new byte[1024];
                    int n;
                    while ((n = in.read(b)) > 0) {
                        outputStream.write(b, 0, n);
                    }
                    outputStream.flush();
                    outputStream.toByteArray();
                    schemaStr1 = outputStream.toString();
                } finally {
                    if (outputStream != null)
                        outputStream.close();
                    if (in != null)
                        in.close();
                }
            }
        } catch (IOException e) {
            throw new ConfigException(String.format("JSON schema configuration can not get for path: %s with type %s. error '%s'",
                    schemaLocation, schemaType, e));
        }
        log.error("schema type: {}, task schema content: {}.", schemaType, schemaStr1);


        String schemaStr = map.get(SCHEMA_STRING);
        JSONObject jsonObject = JSON.parseObject(schemaStr);

        Map<String, Schema> schemas = Maps.newHashMap();
        JSONArray jsonArray = jsonObject.getJSONArray(SCHEMAS);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject schemaObject = jsonArray.getJSONObject(i);
            schemaObjectValidator(schemaObject);

            Schema schema = new Schema();
            Map<String, StableSchema> stableSchemaMap = Maps.newHashMap();
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
                    case SCHEMA_STABLE_NAME: {
                        schema.setStableName(getString(entry.getValue()));
                        break;
                    }
                    case SCHEMA_STABLE_NAME_DEFAULT: {
                        schema.setDefaultStable(getString(entry.getValue()));
                        break;
                    }
                    case SCHEMA_STABLES: {
                        JSONObject stableObject = (JSONObject) JSON.toJSON(entry.getValue());
                        for (String stName : stableObject.keySet()) {
                            StableSchema stableSchema = new StableSchema();
                            stableSchemaMap.put(stName, stableSchema);
                            JSONObject tableObject = stableObject.getJSONObject(stName);
                            Map<String, Index> indexMap = Maps.newHashMap();
                            stableSchema.setIndexMap(indexMap);
                            for (Map.Entry<String, Object> tableEntry : tableObject.entrySet()) {
                                switch (tableEntry.getKey()) {
                                    case SCHEMA_TABLE_NAME: {
                                        if (tableEntry.getValue() == null) {
                                            throw new SchemaException(String.format("schema: %s. %s's value is null",
                                                    schemaStr, SCHEMA_TABLE_NAME));
                                        }
                                        JSONArray objects = JSON.parseArray(String.valueOf(tableEntry.getValue()));
                                        stableSchema.setTableName(objects.toArray(new String[0]));
                                        break;
                                    }
                                    case SCHEMA_DELIMITER: {
                                        stableSchema.setDelimiter(getString(tableEntry.getValue()));
                                        break;
                                    }
                                    default: {
                                        String key = tableEntry.getKey();
                                        String[] split = key.split(Schema.SEPARATOR);
                                        Map<String, Index> tmp = indexMap;
                                        for (int j = 0; j < split.length - 1; j++) {
                                            String name = split[j];
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
                                        JSONObject value = (JSONObject) JSON.toJSON(tableEntry.getValue());
                                        index.setColumn(json2Col(value));
                                        tmp.put(name, index);
                                    }
                                }
                            }
                        }
                        break;
                    }
                    default: {
                        // other undefined schema
                    }
                }
            }
            if (null == schema.getStableName() && null == schema.getDefaultStable()) {
                throw new SchemaException(String.format("schema: %s. %s and %s cannot be both empty ",
                        JSON.toJSONString(schema), SCHEMA_STABLE_NAME, SCHEMA_STABLE_NAME_DEFAULT));
            }
            if (!Strings.isNullOrEmpty(schema.getDefaultStable())
                    && !stableSchemaMap.containsKey(schema.getDefaultStable())) {
                throw new SchemaException(String.format("default stableName: %s cannot found in schema: %s.",
                        schema.getDefaultStable(), JSON.toJSONString(schema)));
            }
            schema.setStableSchemaMap(stableSchemaMap);
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
        Schema schema = topics.get(topic);
        if (null == schema) {
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

                List<JsonSql> value = null;
                String recordString = getString(record.value());
                log.trace("raw record String: {}", recordString);
                try {
                    value = schemaHandler(schema, recordString);
                } catch (RecordException | JSONException e) {
                    log.error(String.valueOf(e));
                    reporter.report(record, e);
                    continue;
                }
                values.addAll(value);
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

    private List<JsonSql> schemaHandler(Schema schema, String recordString) {
        if (null == recordString) {
            throw new RecordException("record is null");
        }
        Object obj = JSON.parse(recordString);
        if (obj instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) obj;
            return Collections.singletonList(convertJSONObject(schema, jsonObject));
        } else if (obj instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) obj;
            List<JsonSql> jsonSqlList = new ArrayList<>();
            jsonArray.forEach(o -> {
                JSONObject jsonObject = (JSONObject) JSON.toJSON(o);
                JsonSql jsonSql = convertJSONObject(schema, jsonObject);
                if (null != jsonSql) {
                    jsonSqlList.add(jsonSql);
                }
            });
            return jsonSqlList;
        }
        return Collections.EMPTY_LIST;
    }

    private JsonSql convertJSONObject(Schema schema, JSONObject jsonObject) {
        JsonSql sql = new JsonSql();

        String stableName = schema.getStableName();
        String defaultStable = schema.getDefaultStable();
        String name;
        if (!Strings.isNullOrEmpty(stableName)) {
            String[] split = stableName.split(Schema.SEPARATOR);
            String s = findValue(split, jsonObject);
            if (s != null) {
                name = s;
            } else {
                if (Strings.isNullOrEmpty(defaultStable)) {
                    String msg = String.format("record : %s could not be found a suitable configuration in schema: %s."
                            , JSON.toJSONString(jsonObject), schema.getName());
                    throw new RecordException(msg);
                }
                name = defaultStable;
            }
        } else {
            if (Strings.isNullOrEmpty(defaultStable)) {
                String msg = String.format("record : %s could not be found a suitable configuration in schema: %s."
                        , JSON.toJSONString(jsonObject), schema.getName());
                throw new RecordException(msg);
            }
            name = defaultStable;
        }
        sql.setStName(name);

        StableSchema stableScheme = schema.getStableSchemaMap().get(name);
        if (null == stableScheme) {
            String msg = String.format("record : %s could not be found a suitable configuration in schema: %s."
                    , JSON.toJSONString(jsonObject), schema.getName());
            throw new RecordException(msg);
        }
        for (Map.Entry<String, Index> entry : stableScheme.getIndexMap().entrySet()) {
            if (null == jsonObject.get(entry.getKey())) {
                return null;
            }
            convert(entry.getValue(), jsonObject.get(entry.getKey()), sql);
        }

        if (stableScheme.getDelimiter() != null) {
            sql.settName(Arrays.stream(stableScheme.getTableName()).map(c -> sql.getAll().get(c))
                    .collect(Collectors.joining(stableScheme.getDelimiter())));
        } else {
            sql.settName(sql.getAll().get(stableScheme.getTableName()[0]));
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

    private String findValue(String[] strings, JSONObject jsonObject) {
        try {
            if (strings.length == 1) {
                return jsonObject.getString(strings[0]);
            }
            String key = strings[0];
            if (jsonObject.containsKey(key)) {
                JSONObject json = jsonObject.getJSONObject(key);
                strings = Arrays.copyOfRange(strings, 1, strings.length);
                findValue(strings, json);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
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
                List<JsonSql> value = schemaHandler(topics.get(record.topic()), getString(record.value()));

                executSql(value);
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
