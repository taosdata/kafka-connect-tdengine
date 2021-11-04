package com.taosdata.kafka.connect.sink;

import com.taosdata.jdbc.TSDBDriver;
import com.taosdata.jdbc.enums.SchemalessProtocolType;
import com.taosdata.jdbc.enums.SchemalessTimestampType;
import com.taosdata.kafka.connect.util.VersionUtil;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.errors.RetriableException;
import org.apache.kafka.connect.sink.ErrantRecordReporter;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.*;

/**
 * @author huolibo@qq.com
 * @version v1.0.0
 * @JDK: 1.8
 * @description: tdengine sink task
 * @date 2021-11-04 14:12
 */
public class TDengineSinkTask extends SinkTask {
    private static final Logger log = LoggerFactory.getLogger(TDengineSinkTask.class);

    private SinkConfig config;
    private Writer writer;
    ErrantRecordReporter reporter;
    int remainingRetries;

    @Override
    public void start(Map<String, String> map) {
        log.info("Starting TDengine Sink task...");
        config = new SinkConfig(map);
        initTask();
        try {
            reporter = context.errantRecordReporter();
        } catch (NoSuchMethodError | NoClassDefFoundError e) {
            // Will occur in Connect runtimes earlier than 2.6
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
        properties.setProperty(TSDBDriver.PROPERTY_KEY_TIME_ZONE, config.getTimeZone());
        ConnectionProvider provider = new TSDBConnectionProvider(
                config.getConnectionUrl(),
                properties,
                config.getConnectionAttempts(),
                config.getConnectionBackoffMs()
        );
        writer = new CacheWriter<>(provider, config.getConnectionDb());
    }

    @Override
    public void put(Collection<SinkRecord> records) {
        if (records.isEmpty()) {
            return;
        }

        List<SinkRecord> currentGroup = new ArrayList<>();
        int maxBatchSize = config.getBatchSize();

        String previousTopic = "";
        Iterator<SinkRecord> iterator = records.iterator();
        while (iterator.hasNext()) {
            SinkRecord record = iterator.next();
            if (maxBatchSize > 0 && currentGroup.size() == maxBatchSize
                    || !previousTopic.equals(record.topic())) {

                bulkWriteBatch(currentGroup);
                // next batch insert
                currentGroup = new ArrayList<>();
                previousTopic = record.topic();
            }
            currentGroup.add(record);
        }
        bulkWriteBatch(currentGroup);
    }

    private void bulkWriteBatch(final List<SinkRecord> batch) {
        if (batch.isEmpty()) {
            return;
        }

        // do some debug log
        int size = batch.size();
        SinkRecord record = batch.get(0);
        log.debug(
                "Received {} records. First record kafka coordinates:({}-{}-{}). Writing them to the "
                        + "database...",
                size, record.topic(), record.kafkaPartition(), record.kafkaOffset()
        );

        String[] strings = batch.stream().map(ConnectRecord::value).map(String::valueOf).toArray(String[]::new);
        try {
            writer.schemalessInsert(strings, SchemalessProtocolType.parse(config.getSchemalessTypeFormat()),
                    SchemalessTimestampType.NOT_CONFIGURED);
        } catch (SQLException sqle) {
            log.warn(
                    "Write of {} records failed, remainingRetries={}",
                    batch.size(),
                    remainingRetries,
                    sqle
            );
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
                    log.error(
                            "Failing task after exhausting retries; "
                                    + "encountered exceptions on last write attempt. "
                                    + "For complete details on each exception, please enable DEBUG logging.");
                    throw new ConnectException(sqlAllMessagesException);
                }
            }
        }
    }

    private SQLException getAllMessagesException(SQLException sqle) {
        String sqleAllMessages = "Exception chain:" + System.lineSeparator();
        for (Throwable e : sqle) {
            sqleAllMessages += e + System.lineSeparator();
        }
        SQLException sqlAllMessagesException = new SQLException(sqleAllMessages);
        sqlAllMessagesException.setNextException(sqle);
        return sqlAllMessagesException;
    }

    private void unrollAndRetry(Collection<SinkRecord> records) {
        writer.close();
        for (SinkRecord record : records) {
            try {
                writer.schemalessInsert(new String[]{String.valueOf(record.value())},
                        SchemalessProtocolType.parse(config.getSchemalessTypeFormat()), SchemalessTimestampType.NOT_CONFIGURED);
            } catch (SQLException sqle) {
                SQLException sqlAllMessagesException = getAllMessagesException(sqle);
                reporter.report(record, sqlAllMessagesException);
                writer.close();
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
        return VersionUtil.getVersion();
    }
}
