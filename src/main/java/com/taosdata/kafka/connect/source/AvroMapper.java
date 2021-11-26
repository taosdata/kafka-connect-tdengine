package com.taosdata.kafka.connect.source;

import com.google.common.collect.Maps;
import com.taosdata.kafka.connect.db.Processor;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;

/**
 *
 */
public class AvroMapper extends TableMapper{
    private static final Logger log = LoggerFactory.getLogger(AvroMapper.class);

    private final Schema schema ;

    public AvroMapper(String topic, String tableName, int batchMaxRows, Processor processor) throws SQLException {
        super(topic, tableName, batchMaxRows, processor);
        this.schema = getSchema();
    }

    private Schema getSchema() {
//        if (null != schema) {
//            return schema;
//        }
        Schema TAG_SCHEMA = SchemaBuilder.map(Schema.STRING_SCHEMA, Schema.STRING_SCHEMA).build();
        SchemaBuilder schema = SchemaBuilder.struct().name(tableName);
        schema.field("metric", Schema.STRING_SCHEMA);
        schema.field("tags", TAG_SCHEMA);
        schema.field("timestamp", org.apache.kafka.connect.data.Timestamp.builder().build());

        for (String column : columns) {
            String value = columnType.get(column);
            switch (value) {
                case "TIMESTAMP":
                    Schema timestampSchema = org.apache.kafka.connect.data.Timestamp.builder().build();
                    schema.field(column, timestampSchema);
                    break;
                case "NCHAR":
                    schema.field(column, Schema.STRING_SCHEMA);
                    break;
                case "INT":
                    schema.field(column, Schema.INT32_SCHEMA);
                    break;
                case "BIGINT":
                    schema.field(column, Schema.INT64_SCHEMA);
                    break;
                case "FLOAT":
                    schema.field(column, Schema.FLOAT32_SCHEMA);
                    break;
                case "DOUBLE":
                    schema.field(column, Schema.FLOAT64_SCHEMA);
                    break;
                case "BINARY":
                    schema.field(column, Schema.BYTES_SCHEMA);
                    break;
                case "SMALLINT":
                    schema.field(column, Schema.INT16_SCHEMA);
                    break;
                case "TINYINT":
                    schema.field(column, Schema.INT8_SCHEMA);
                    break;
                case "BOOL":
                    schema.field(column, Schema.BOOLEAN_SCHEMA);
                    break;
                default:
                    throw new IllegalArgumentException("Found invalid datatype in table - column " + value
                            + " with type " + value.getClass());
            }
        }
        return schema.build();
    }

    @Override
    public PendingRecord doExtractRecord(ResultSet resultSet, Map<String, String> partition) {
        Struct struct = new Struct(schema);
        try {
            struct.put("metric", tableName);
            struct.put("timestamp", resultSet.getTimestamp(1));
            for (String column : columns) {
                String value = columnType.get(column);
                switch (value) {
                    case "TIMESTAMP":
                        struct.put(column,resultSet.getTimestamp(column));
                        break;
                    case "NCHAR":
                        struct.put(column, resultSet.getString(column));
                        break;
                    case "INT":
                    case "TINYINT":
                    case "SMALLINT":
                        struct.put(column, resultSet.getInt(column));
                        break;
                    case "BIGINT":
                        struct.put(column, resultSet.getLong(column));
                        break;
                    case "FLOAT":
                        struct.put(column, resultSet.getFloat(column));
                        break;
                    case "DOUBLE":
                        struct.put(column, resultSet.getDouble(column));
                        break;
                    case "BINARY":
                        struct.put(column, resultSet.getObject(column));
                        break;
                    case "BOOL":
                        struct.put(column, resultSet.getBoolean(column));
                        break;
                    default:
                        throw new IllegalArgumentException("Found invalid datatype in table - column " + value
                                + " with type " + value.getClass());
                }
            }
            Map<String, Object> map = Maps.newHashMap();
            if (tags.size() > 0) {
                for (String tag : tags) {
                    String value = columnType.get(tag);
                    switch (value) {
                        case "TIMESTAMP":
                        case "NCHAR":
                            map.put(tag, resultSet.getString(tag));
                            break;
                        case "INT":
                        case "TINYINT":
                        case "SMALLINT":
                            map.put(tag, resultSet.getInt(tag));
                            break;
                        case "BIGINT":
                            map.put(tag, resultSet.getLong(tag));
                            break;
                        case "FLOAT":
                            map.put(tag, resultSet.getFloat(tag));
                            break;
                        case "DOUBLE":
                            map.put(tag, resultSet.getDouble(tag));
                            break;
                        case "BINARY":
                            map.put(tag, resultSet.getObject(tag));
                            break;
                        case "BOOL":
                            map.put(tag, resultSet.getBoolean(tag));
                            break;
                        default:
                            throw new IllegalArgumentException("Found invalid datatype in table - column " + value
                                    + " with type " + value.getClass());
                    }
                }
            }
            if(!map.isEmpty()){
                struct.put("tags", map);
            }
        } catch (SQLException e) {
            log.error("resultSet get value error", e);
        }
        Timestamp ts = (Timestamp) struct.get("timestamp");

        return new PendingRecord(partition, ts, topic, struct.schema(), struct);
    }
}
