package com.taosdata.kafka.connect.source;

import com.alibaba.fastjson.JSON;
import com.taosdata.jdbc.TSDBDriver;
import com.taosdata.jdbc.tmq.*;
import com.taosdata.kafka.connect.db.Processor;
import com.taosdata.kafka.connect.enums.ReadMethodEnum;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.Duration;
import java.util.*;

public class TableExecutor implements Comparable<TableExecutor>, AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(TableExecutor.class);

    private final String tableName;
    private final Map<String, String> partition;

    private TimeStampOffset committedOffset;
    private TimeStampOffset offset;
    // end query time
    private long latestEndTime;

    private ResultSet resultSet;

    private PendingRecord nextRecord;
    private boolean exhaustedResultRecord;

    private final Timestamp start;

    private final TableMapper mapper;
    private long lastUpdate;
    private final long queryInterval;

    private TaosConsumer<Map<String, Object>> consumer;
    private ReadMethodEnum readMethod;
    private String groupId;
    private String autoOffsetReset;
    ConsumerRecords<Map<String, Object>> records;

    public TableExecutor(String tableName,
                         String topic,
                         Map<String, Object> offset,
                         Processor processor,
                         Map<String, String> partition,
                         SourceConfig config,
                         Map<String, String> urls) throws SQLException {
        this.queryInterval = config.getQueryInterval();
        this.tableName = tableName;
        this.committedOffset = this.offset = TimeStampOffset.fromMap(offset);
        log.debug("TableExecutor committed offset is : {}", this.offset.getTimestampOffset());
        this.partition = partition;
        this.start = config.getTimestampInitial();
        this.lastUpdate = 0L;
        this.exhaustedResultRecord = false;
        this.nextRecord = null;
        if (config.getOutFormat().equalsIgnoreCase("line")) {
            mapper = new LineMapper(topic, tableName, config.getFetchMaxRows(), processor);
        } else {
            mapper = new JsonMapper(topic, tableName, config.getFetchMaxRows(), processor);
        }

        this.readMethod = config.getReadMethod();
        this.groupId = config.getSubscriptionGroupId();
        this.autoOffsetReset = config.getSubscriptionAutoOffsetReset();

        if (this.readMethod == ReadMethodEnum.SUBSCRIPTION) {
//            StringBuilder sb = new StringBuilder().append("select _c0,");
//            if (!mapper.tags.isEmpty()) {
//                sb.append("`").append(String.join("`,`", mapper.tags)).append("`");
//                sb.append(", ");
//            }
//            for (int i = 0; i < mapper.columns.size(); i++) {
//                sb.append("`").append(mapper.columns.get(i)).append("`");
//                if (i != mapper.columns.size() - 1) {
//                    sb.append(",");
//                }
//            }
//            sb.append(" from `").append(tableName).append("`");
            processor.execute("create topic if not exists `" + topic + "` as select * from `" + tableName + "`");
            Properties properties = new Properties();
            if ("TAOS".equalsIgnoreCase(urls.get(TSDBDriver.PROPERTY_KEY_PRODUCT_NAME))) {
                properties.setProperty(TMQConstants.CONNECT_TYPE, "jni");
            } else {
                properties.setProperty(TMQConstants.CONNECT_TYPE, "ws");
            }
            properties.setProperty(TMQConstants.CONNECT_IP, urls.get(TSDBDriver.PROPERTY_KEY_HOST));
            properties.setProperty(TMQConstants.CONNECT_PORT, urls.get(TSDBDriver.PROPERTY_KEY_PORT));
            properties.setProperty(TMQConstants.CONNECT_USER, config.getConnectionUser());
            properties.setProperty(TMQConstants.CONNECT_PASS, config.getConnectionPassword());
            properties.setProperty(TMQConstants.ENABLE_AUTO_COMMIT, "false");
            properties.setProperty(TMQConstants.GROUP_ID, this.groupId);
            properties.setProperty(TMQConstants.AUTO_OFFSET_RESET, this.autoOffsetReset);
            properties.setProperty(TMQConstants.VALUE_DESERIALIZER, "com.taosdata.kafka.connect.source.StringDeserializer");
            consumer = new TaosConsumer<>(properties);
            consumer.subscribe(Collections.singleton(topic));
        }
    }

    public long getLastUpdate() {
        return this.lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void startQuery() throws SQLException, ConnectException {
        if (ReadMethodEnum.SUBSCRIPTION == readMethod) {
            mapper.getMetaSchema();
            records = consumer.poll(Duration.ofMillis(10));
            log.info("********** received records: {}", JSON.toJSONString(records));


        } else {
            if (resultSet == null) {
                PreparedStatement stmt = mapper.getOrCreatePreparedStatement();
                Timestamp startTime = null == offset.getTimestampOffset() ? start : offset.getTimestampOffset();
                if (queryInterval == 0) {
                    stmt.setTimestamp(1, startTime);
                    log.debug("query start from: {}", startTime);
                    stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                } else {

                    if (startTime.getTime() == 0) {
                        try (ResultSet rs = stmt.executeQuery("select first(_c0) from " + tableName)) {
                            if (rs.next()) {
                                Timestamp tmp = rs.getTimestamp("_c0");
                                startTime = new Timestamp(tmp.getTime() - 1);
                            }
                        }
                    }

                    log.debug("query start from: {}", startTime);
                    stmt.setTimestamp(1, startTime);
                    long current = System.currentTimeMillis();
                    if (latestEndTime == 0) {
                        latestEndTime = startTime.getTime() + queryInterval;
                    } else {
                        latestEndTime += queryInterval;
                    }

                    if (current < latestEndTime) {
                        latestEndTime = current;
                    }
                    Timestamp endTime = new Timestamp(latestEndTime);

                    log.debug("query end with: {}", endTime);
                    stmt.setTimestamp(2, endTime);
                }
                this.resultSet = stmt.executeQuery();
                exhaustedResultRecord = false;
            }
        }
        this.committedOffset = this.offset;
    }

    public void commitOffset() {
        if (ReadMethodEnum.SUBSCRIPTION == readMethod) {
            consumer.commitAsync();
            records = null;
        }
    }

    public void clearEndQuery() {
        this.latestEndTime = 0;
    }

    public String getTableName() {
        return tableName;
    }

    public void reset(long now, boolean resetOffset) {
        if (resetOffset) {
            this.offset = this.committedOffset;
        }
        closeResultSet();
        mapper.closeStatement();
        this.nextRecord = null;
        this.lastUpdate = now;
    }

    private void closeResultSet() {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException err) {
                // intentionally ignored
                log.warn("closeResultSet error: ", err);
            }
        }
        resultSet = null;
    }

    public boolean next() throws SQLException {
        if (exhaustedResultRecord && nextRecord == null) {
            return false;
        }

        if (nextRecord == null) {
            if (resultSet.next()) {
                nextRecord = mapper.doExtractRecord(resultSet, partition);
                log.debug("doExtractRecord, next: {}", nextRecord);
            } else {
                exhaustedResultRecord = true;
                return false;
            }
        }

        if (!resultSet.next()) {
            exhaustedResultRecord = true;
        }
        return true;
    }

    public SourceRecord extractRecord() {
        if (nextRecord == null) {
            throw new IllegalStateException("No more records are available");
        }
        PendingRecord currentRecord = nextRecord;
        nextRecord = exhaustedResultRecord ? null : mapper.doExtractRecord(resultSet, partition);
        log.debug("doExtractRecord, extractRecord: {}", nextRecord);
        if (nextRecord == null
                || canCommitTimestamp(currentRecord.timestamp(), nextRecord.timestamp())) {
            offset = new TimeStampOffset(currentRecord.timestamp());
        }
        return currentRecord.record(offset);
    }

    public List<SourceRecord> extractRecords() {
        return mapper.process(records, partition, offset);
    }

    private boolean canCommitTimestamp(Timestamp current, Timestamp next) {
        return current == null || next == null || current.before(next);
    }

    @Override
    public int compareTo(TableExecutor other) {
        if (this.lastUpdate < other.lastUpdate) {
            return -1;
        } else {
            return 1;
        }
    }

    @Override
    public void close() throws Exception {
        closeResultSet();
        mapper.closeStatement();
    }
}
