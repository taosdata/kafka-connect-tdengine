package com.taosdata.kafka.connect.source;

import com.taosdata.kafka.connect.db.Processor;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Map;

public class TableExecutor implements Comparable<TableExecutor> {
    private static final Logger log = LoggerFactory.getLogger(TableExecutor.class);

    private final String tableName;
    private final Map<String, String> partition;

    private TimeStampOffset committedOffset;
    private TimeStampOffset offset;
    private Timestamp latestQueryTime;

    private ResultSet resultSet;

    private PendingRecord nextRecord;
    private boolean exhaustedResultRecord;
    private final Timestamp start;

    private final TableMapper mapper;
    private long lastUpdate;
    private final long queryInterval;

    public TableExecutor(String tableName,
                         String topic,
                         Map<String, Object> offset,
                         Processor processor,
                         int batchMaxRows,
                         Map<String, String> partition,
                         Timestamp startTime,
                         String format, long queryInterval) throws SQLException {
        this.queryInterval = queryInterval;
        this.tableName = tableName;
        this.committedOffset = this.offset = TimeStampOffset.fromMap(offset);
        log.debug("TableExecutor committed offset is : {}", this.offset.getTimestampOffset());
        this.partition = partition;
        this.start = startTime;
        this.lastUpdate = 0L;
        this.exhaustedResultRecord = false;
        this.nextRecord = null;
        if (format.endsWith("StringConverter")) {
            mapper = new LineMapper(topic, tableName, batchMaxRows, processor);
        } else {
            mapper = new JsonMapper(topic, tableName, batchMaxRows, processor);
        }
    }

    public long getLastUpdate() {
        return this.lastUpdate;
    }

    public void startQuery() throws SQLException, ConnectException {
        if (resultSet == null) {
            PreparedStatement stmt = mapper.getOrCreatePreparedStatement();
            if (queryInterval == 0) {
                Timestamp startTime = null == offset.getTimestampOffset() ? start : offset.getTimestampOffset();
                stmt.setTimestamp(1, startTime);
                log.trace("query start from: {}", startTime);
                stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            } else {
                Timestamp startTime = latestQueryTime;
                if (startTime == null) {
                    startTime = null == offset.getTimestampOffset() ? start : offset.getTimestampOffset();

                    if (startTime.getTime() == 0) {
                        try (ResultSet rs = stmt.executeQuery("select first(_c0) from " + tableName)) {
                            if (rs.next()) {
                                startTime = rs.getTimestamp(1);
                            }
                        }
                    }
                }

                log.debug("query start from: {}", startTime);
                stmt.setTimestamp(1, startTime);
                long current = System.currentTimeMillis();
                long end = startTime.getTime() + queryInterval;
                if (current < end) {
                    end = current;
                }
                Timestamp endTime = new Timestamp(end);

                log.debug("query end with: {}", endTime);
                stmt.setTimestamp(2, endTime);
                latestQueryTime = endTime;
            }
            this.resultSet = stmt.executeQuery();
            exhaustedResultRecord = false;
        }
        this.committedOffset = this.offset;
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

    public void clearLatestQuery() {
        this.latestQueryTime = null;
    }

    public boolean next() throws SQLException {
        if (exhaustedResultRecord && nextRecord == null) {
            return false;
        }

        if (nextRecord == null) {
            if (resultSet.next()) {
                nextRecord = mapper.doExtractRecord(resultSet, partition);
                log.trace("doExtractRecord, next: {}", nextRecord);
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
        log.trace("doExtractRecord, extractRecord: {}", nextRecord);
        if (nextRecord == null
                || canCommitTimestamp(currentRecord.timestamp(), nextRecord.timestamp())) {
            offset = new TimeStampOffset(currentRecord.timestamp());
        }
        return currentRecord.record(offset);
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
}
