package com.taosdata.kafka.connect.source;

import com.taosdata.kafka.connect.db.Processor;
import com.taosdata.kafka.connect.enums.OutputFormatEnum;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonMapper extends TableMapper {
    private static final Logger log = LoggerFactory.getLogger(JsonMapper.class);

    public JsonMapper(String topic, String tableName, int batchMaxRows, Processor processor) throws SQLException {
        super(topic, tableName, batchMaxRows, processor, OutputFormatEnum.JSON);
    }

    @Override
    public PendingRecord doExtractRecord(ResultSet resultSet, Map<String, String> partition) {
        List<Struct> structs = new ArrayList<>();
        Struct tagStruct = new Struct(tagBuilder.build());

        Timestamp ts = null;
        try {
            ts = resultSet.getTimestamp(1);
            long result = resultSet.getLong(1);
            for (String tag : tags) {
                tagStruct.put(tag, getValue(resultSet, tag, columnType.get(tag)));
            }
            for (String column : columns) {
                Schema value = valueBuilder.get(column);
                Struct valueStruct = new Struct(value)
                        .put("metric", column)
                        .put("timestamp", result)
                        .put("value", getValue(resultSet, column, columnType.get(column)))
                        .put("tags", tagStruct);
                structs.add(valueStruct);
            }
        } catch (SQLException e) {
            log.error("resultSet get value error", e);
        }
        return new PendingRecord(partition, ts, topic, null, structs);
    }

    private Object getValue(ResultSet resultSet, String name, String type) throws SQLException {
        switch (type) {
            case "TINYINT":
                return resultSet.getByte(name);
            case "SMALLINT":
                return resultSet.getShort(name);
            case "INT":
                return resultSet.getInt(name);
            case "TIMESTAMP":
            case "BIGINT":
                return resultSet.getLong(name);
            case "FLOAT":
                return resultSet.getFloat(name);
            case "DOUBLE":
                return resultSet.getDouble(name);
            case "BOOL":
                return resultSet.getBoolean(name);
            case "NCHAR":
            case "JSON":
                return resultSet.getString(name);
            case "BINARY":
                return resultSet.getBytes(name);
            default:
                return null;
        }
    }
}
