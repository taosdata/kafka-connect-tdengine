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
        // do some debug log
        int size = records.size();
        records.stream().findFirst().ifPresent(sinkRecord -> {
            log.debug("Received {} records. First record kafka coordinates:({}-{}-{}). Writing them to the "
                    + "database...", size, sinkRecord.topic(), sinkRecord.kafkaPartition(), sinkRecord.kafkaOffset());
        });

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

        try {
            List<JsonSql> values = new ArrayList<>();

            String superTable = null;
            String table = null;
            Set<String> colSet = new HashSet<>();
            for (SinkRecord record : batch) {
                JsonSql value = new JsonSql();
                String recordString = String.valueOf(record.value());
                JSONObject jsonObject = JSON.parseObject(recordString);
                Object tsObject = jsonObject.get(SinkConstants.JSON_TIMESTAMP);
                if (tsObject == null) {
                    log.error("Record must contains {} properties. record: {}", SinkConstants.JSON_TIMESTAMP, recordString);
                    continue;
                }
                value.setTs(checkAndConvertString(tsObject));

                Object tNameObject = jsonObject.get(SinkConstants.JSON_TABLE_NAME);
                if (tNameObject == null) {
                    log.error("Record must contains {} properties. record: {}", SinkConstants.JSON_TABLE_NAME, recordString);
                    continue;
                }
                String tName = String.valueOf(tNameObject).toLowerCase();
                if (!tName.equals(table)) {
                    executSql(values);
                    values = new ArrayList<>();
                    table = tName;
                }
                value.settName(tName);

                Object stNameObject = jsonObject.get(SinkConstants.JSON_SUPER_TABLE_NAME);
                if (stNameObject != null) {
                    String stName = String.valueOf(stNameObject).toLowerCase();
                    if (!stName.equals(superTable)) {
                        executSql(values);
                        values = new ArrayList<>();
                        superTable = stName;
                    }
                    value.setStName(stName);
                    Object tagObject = jsonObject.get(SinkConstants.JSON_TAG);
                    if (tagObject != null) {
                        String tag = checkAndConvertString(tagObject);
                        value.setTag(tag);
                    }
                } else {
                    if (superTable != null) {
                        executSql(values);
                        values = new ArrayList<>();
                        superTable = null;
                    }
                }

                JSONObject propObject = jsonObject.getJSONObject(SinkConstants.JSON_PROPERTIES);
                if (propObject == null) {
                    log.error("Record must contains {} properties. record: {},", SinkConstants.JSON_PROPERTIES, recordString);
                    continue;
                }
                Map<String, String> colMap = new HashMap<>();
                if (propObject.size() != colSet.size()) {
                    executSql(values);
                    colSet.clear();
                    for (Map.Entry<String, Object> col : propObject.entrySet()) {
                        colMap.put(col.getKey(), checkAndConvertString(col.getValue()));
                    }
                    colSet = colMap.keySet();
                } else {
                    for (Map.Entry<String, Object> col : propObject.entrySet()) {
                        if (!colSet.isEmpty() && !colSet.contains(col.getKey())) {
                            colSet.clear();
                        }
                        colMap.put(col.getKey(), checkAndConvertString(col.getValue()));
                    }
                    if (colSet.isEmpty()) {
                        executSql(values);
                        values = new ArrayList<>();
                        colSet = colMap.keySet();
                    }
                }
                value.setCols(colMap);
                values.add(value);
            }
            executSql(values);
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

    private void executSql(List<JsonSql> values) throws SQLException {
        if (values.isEmpty())
            return;

        String sql = convertSql(values);
        if (sql != null) {
            writer.execute(sql);
        }
    }

    private String convertSql(List<JsonSql> jsons) {
        JsonSql jsonSql = jsons.get(jsons.size() - 1);
        String table = jsonSql.gettName();
        String stable = jsonSql.getStName();
        StringBuilder sb = new StringBuilder("insert into ").append(table);
        if (stable != null) {
            sb.append(" using ").append(stable).append(" (").append(SinkConstants.JSON_TABLE_NAME);

            StringBuilder tagSb = new StringBuilder(" tags ('").append(table).append("'");
            String tag = jsonSql.getTag();
            if (tag != null) {
                sb.append(", ").append(SinkConstants.JSON_TAG).append(")");
                tagSb.append(", ").append(tag).append(")");
            }
            sb.append(tagSb);
        }

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
            try {
                JsonSql value = new JsonSql();
                String recordString = String.valueOf(record.value());
                JSONObject jsonObject = JSON.parseObject(recordString);

                Object tsObject = jsonObject.get(SinkConstants.JSON_TIMESTAMP);
                if (tsObject == null) {
                    throw new SQLException("Record must contains " + SinkConstants.JSON_TIMESTAMP + " properties. record: " + recordString);
                }
                value.setTs(String.valueOf(tsObject));

                Object tNameObject = jsonObject.get(SinkConstants.JSON_TABLE_NAME);
                if (tNameObject == null) {
                    throw new SQLException("Record must contains " + SinkConstants.JSON_TABLE_NAME + " properties. record: " + recordString);
                }
                String tName = String.valueOf(tNameObject).toLowerCase();
                value.settName(tName);

                Object stNameObject = jsonObject.get(SinkConstants.JSON_SUPER_TABLE_NAME);
                if (stNameObject != null) {
                    String stName = String.valueOf(stNameObject).toLowerCase();
                    value.setStName(stName);
                    Object tagObject = jsonObject.get(SinkConstants.JSON_TAG);
                    if (tagObject != null) {
                        String tag = checkAndConvertString(tagObject);
                        value.setTag(tag);
                    }
                }

                JSONObject propObject = jsonObject.getJSONObject(SinkConstants.JSON_PROPERTIES);
                if (propObject == null) {
                    throw new SQLException("Record must contains " + SinkConstants.JSON_PROPERTIES + " properties. record: " + recordString);
                }
                Map<String, String> colMap = new HashMap<>();
                for (Map.Entry<String, Object> col : propObject.entrySet()) {
                    colMap.put(col.getKey(), checkAndConvertString(col.getValue()));
                }
                value.setCols(colMap);

                executSql(Collections.singletonList(value));
            } catch (SQLException sqle) {
                SQLException sqlAllMessagesException = getAllMessagesException(sqle);
                reporter.report(record, sqlAllMessagesException);
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
