package com.taosdata.kafka.connect.source;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.source.SourceRecord;

import java.sql.Timestamp;
import java.util.Map;

/**
 *
 */
public class PendingRecord {
    private final Map<String, String> partition;
    private final Timestamp timestamp;
    private final String topic;
    private final Schema valueSchema;
    private final Object value;

    public PendingRecord(
            Map<String, String> partition,
            Timestamp timestamp,
            String topic,
            Schema valueSchema,
            Object value
    ) {
        this.partition = partition;
        this.timestamp = timestamp;
        this.topic = topic;
        this.valueSchema = valueSchema;
        this.value = value;
    }

    /**
     * @return the timestamp value for the row that generated this record
     */
    public Timestamp timestamp() {
        return timestamp;
    }

    /**
     * @param offset the timestamp to use for the record's offset; may be null
     * @return a {@link SourceRecord} whose source offset contains the provided timestamp
     */
    public SourceRecord record(TimeStampOffset offset) {
        return new SourceRecord(
                partition, offset.toMap(), topic, valueSchema, value
        );
    }

    @Override
    public String toString() {
        StringBuilder partitionStr = new StringBuilder();
        for (Map.Entry<String, String> entry : partition.entrySet()) {
            partitionStr.append(entry.getKey()).append("=").append(entry.getValue()).append(",");
        }
        return "PendingRecord{" + "timestamp=" + timestamp +
                ", partition=" + partitionStr +
                ", topic='" + topic + '\'' +
                ", value=" + value +
                '}';
    }
}
