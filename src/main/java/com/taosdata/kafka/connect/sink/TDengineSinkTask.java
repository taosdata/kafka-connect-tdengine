package com.taosdata.kafka.connect.sink;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.taosdata.jdbc.TSDBDriver;
import com.taosdata.kafka.connect.db.CacheProcessor;
import com.taosdata.kafka.connect.db.ConnectionProvider;
import com.taosdata.kafka.connect.db.Processor;
import com.taosdata.kafka.connect.db.TSDBConnectionProvider;
import com.taosdata.kafka.connect.util.VersionUtils;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.errors.RetriableException;
import org.apache.kafka.connect.sink.ErrantRecordReporter;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;

/**
 * TDengine sink task
 */
public class TDengineSinkTask extends SinkTask {
    private static final Logger log = LoggerFactory.getLogger(TDengineSinkTask.class);

    private SinkConfig config;
    private Processor writer;
    ErrantRecordReporter reporter;
    private int remainingRetries;
    ExecutorService executor = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(1000),
            r -> {
                Thread t = new Thread(r);
                t.setName("sink-task-" + t.getId());
                return t;
            }, new ThreadPoolExecutor.CallerRunsPolicy());

    @Override
    public void start(Map<String, String> map) {
        log.info("Starting TDengine Sink task...");
        config = new SinkConfig(map);
        initTask();
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
//        properties.setProperty(TSDBDriver.PROPERTY_KEY_DBNAME, config.getConnectionDb());
//        properties.setProperty(TSDBDriver.PROPERTY_KEY_TIME_ZONE, config.getTimeZone());
        ConnectionProvider provider = new TSDBConnectionProvider(config.getConnectionUrl(), properties, config.getConnectionAttempts(), config.getConnectionBackoffMs());
        writer = new CacheProcessor<>(provider);
    }

    @Override
    public void put(Collection<SinkRecord> records) {
        if (records.isEmpty()) {
            return;
        }
        executor.submit(() -> {
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
        });
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
        // do some debug log
        int size = batch.size();
        SinkRecord sinkRecord = batch.get(0);
        log.debug("Received {} records. First record kafka coordinates:({}-{}-{}). Writing them to the " + "database...", size, sinkRecord.topic(), sinkRecord.kafkaPartition(), sinkRecord.kafkaOffset());
        try {
            String superTable = null;
            String table = null;
            List<JsonSql> values = new ArrayList<>();

            Set<String> colSet = new HashSet<>();
            Set<String> tagSet = new HashSet<>();
            for (SinkRecord record : batch) {
                JsonSql value = new JsonSql();
                String recordString = String.valueOf(record.value());
                JSONObject jsonObject = JSON.parseObject(recordString);
                Object ts = jsonObject.get(SinkConstants.JSON_TIMESTAMP);
                if (ts == null) {
                    log.error("Record must contains " + SinkConstants.JSON_TIMESTAMP + " properties. record: " + recordString);
                    continue;
                }
                value.setTs(checkAndConvertString(ts));

                Object tNameObject = jsonObject.get(SinkConstants.JSON_TABLE_NAME);
                if (tNameObject == null) {
                    log.error("Record must contains " + SinkConstants.JSON_TABLE_NAME + " properties. record: " + recordString);
                    continue;
                }
                String tName = String.valueOf(tNameObject);
                if (!tName.equalsIgnoreCase(table)) {
                    executSql(superTable, table, values);
                    table = tName;
                }

                Object stName = jsonObject.get(SinkConstants.JSON_SUPER_TABLE_NAME);
                if (stName == null && superTable == null) {
                    //
                } else if (stName != null && String.valueOf(stName).equalsIgnoreCase(superTable)) {
                    JSONObject tagObject = jsonObject.getJSONObject(SinkConstants.JSON_TAGS);
                    if (tagObject.size() != tagSet.size()) {
                        executSql(superTable, table, values);
                        tagSet.clear();

                        Map<String, String> tagMap = new HashMap<>();
                        for (Map.Entry<String, Object> col : tagObject.entrySet()) {
                            tagMap.put(col.getKey(), checkAndConvertString(col.getValue()));
                        }
                        tagSet = tagMap.keySet();
                        value.setCols(tagMap);
                    } else {
                        Map<String, String> tagMap = new HashMap<>();
                        for (Map.Entry<String, Object> col : tagObject.entrySet()) {
                            if (!tagSet.contains(col.getKey())) {
                                executSql(superTable, table, values);
                                tagSet.clear();
                            }
                            tagMap.put(col.getKey(), checkAndConvertString(col.getValue()));
                        }
                        if (tagSet.isEmpty()) {
                            tagSet = tagMap.keySet();
                        }
                        value.setCols(tagMap);
                    }
                } else {
                    executSql(superTable, table, values);

                    tagSet.clear();
                    if (stName != null) {
                        JSONObject tagObject = jsonObject.getJSONObject(SinkConstants.JSON_TAGS);
                        Map<String, String> map = new HashMap<>();
                        for (Map.Entry<String, Object> col : tagObject.entrySet()) {
                            map.put(col.getKey(), checkAndConvertString(col.getValue()));
                        }
                        tagSet = map.keySet();
                        value.setTags(map);
                        superTable = String.valueOf(stName);
                    } else {
                        superTable = null;
                    }
                }

                JSONObject propObject = jsonObject.getJSONObject(SinkConstants.JSON_PROPERTIES);
                if (propObject == null) {
                    log.error("Record must contains " + SinkConstants.JSON_PROPERTIES + " properties. record: " + recordString);
                    continue;
                }
                Map<String, String> colMap = new HashMap<>();
                if (propObject.size() != colSet.size()) {
                    executSql(superTable, table, values);
                    colSet.clear();
                    for (Map.Entry<String, Object> col : propObject.entrySet()) {
                        colMap.put(col.getKey(), checkAndConvertString(col.getValue()));
                    }
                    colSet = colMap.keySet();
                } else {
                    for (Map.Entry<String, Object> col : propObject.entrySet()) {
                        if (!colSet.contains(col.getKey())) {
                            executSql(superTable, table, values);
                            tagSet.clear();
                        }
                        colMap.put(col.getKey(), checkAndConvertString(col.getValue()));
                    }
                    if (tagSet.isEmpty()) {
                        tagSet = colMap.keySet();
                    }
                }
                value.setCols(colMap);
                values.add(value);
            }
            executSql(superTable, table, values);
        } catch (SQLException sqle) {
            log.warn("Write of {} records failed, remainingRetries={}", batch.size(), remainingRetries, sqle);
            SQLException sqlAllMessagesException = getAllMessagesException(sqle);
            if (remainingRetries > 0) {
                writer.close();
                remainingRetries--;
                context.timeout(config.getRetryBackoffMs());
                throw new RetriableException(sqlAllMessagesException);
            } else {
                if (reporter != null) {
                    unrollAndRetry(batch);
                } else {
                    log.error("Failing task after exhausting retries; " + "encountered exceptions on last write attempt. " + "For complete details on each exception, please enable DEBUG logging.");
                    throw new ConnectException(sqlAllMessagesException);
                }
            }
        }
    }

    private String checkAndConvertString(Object o) {
        if (o == null) return null;
        if (o instanceof String) return "'" + o + "'";
        return String.valueOf(o);
    }

    private void executSql(String stable, String table, List<JsonSql> values) throws SQLException {
        String sql = convertSql(stable, table, values);
        if (sql != null) {
            writer.execute(sql);
        }
    }

    private String convertSql(String stable, String table, List<JsonSql> jsons) {
        if (table == null || jsons.isEmpty()) {
            return null;
        }

        JsonSql jsonSql = jsons.get(0);
        StringBuilder sb = new StringBuilder("insert into ").append(table);
        if (stable != null) {
            sb.append(" using ").append(stable);

            Map<String, String> tags = jsonSql.getTags();
            sb.append(" (");
            StringBuilder tagSb = new StringBuilder(" tags (");

            int i = 0;
            for (String s : tags.keySet()) {
                sb.append(s);
                tagSb.append(tags.get(s));
                if (i == tags.keySet().size() - 1) {
                    sb.append(") ");
                    tagSb.append(") ");
                } else {
                    sb.append(", ");
                    tagSb.append(", ");
                }
                i++;
            }
            sb.append(tagSb);
        }
        // TODO confirm timestamp col name
        sb.append(" (ts, ");

        Map<String, String> cols = jsonSql.getCols();
        List<String> colList = new ArrayList<>();
        int j = 0;
        for (String s : cols.keySet()) {
            sb.append(s);
            if (j == cols.keySet().size() - 1) {
                sb.append(") ");
            } else {
                sb.append(", ");
            }
            colList.add(s);
            j++;
        }

        sb.append(" values ");
        for (JsonSql json : jsons) {
            sb.append("(").append(json.getTs()).append(", ");
            Map<String, String> col = json.getCols();
            for (int i = 0; i < colList.size(); i++) {
                sb.append(col.get(colList.get(i)));
                if (i == colList.size() - 1) {
                    sb.append(") ");
                } else {
                    sb.append(", ");
                }
            }
        }
        return sb.toString();
    }

    private SQLException getAllMessagesException(SQLException sqle) {
        StringBuilder sqleAllMessages = new StringBuilder("Exception chain:" + System.lineSeparator());
        for (Throwable e : sqle) {
            sqleAllMessages.append(e).append(System.lineSeparator());
        }
        SQLException sqlAllMessagesException = new SQLException(sqleAllMessages.toString());
        sqlAllMessagesException.setNextException(sqle);
        return sqlAllMessagesException;
    }

    private void unrollAndRetry(Collection<SinkRecord> records) {
        for (SinkRecord record : records) {
//            try {
//                String table = "";
//                JsonSql jsonSql = new JsonSql();
//                JSONObject jsonObject = JSON.parseObject(String.valueOf(record.value()));
//                String
//                String stable = String.valueOf(jsonObject.get(SinkConstants.JSON_SUPER_TABLE_NAME));
//                if (stable)
//                executSql(stable,table,Collections.singletonList(jsonSql));
//            } catch (SQLException sqle) {
//                SQLException sqlAllMessagesException = getAllMessagesException(sqle);
//                reporter.report(record, sqlAllMessagesException);
//            }
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
