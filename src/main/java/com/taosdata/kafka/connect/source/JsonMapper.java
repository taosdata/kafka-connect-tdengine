package com.taosdata.kafka.connect.source;

import com.alibaba.fastjson.JSONObject;
import com.taosdata.kafka.connect.db.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;

/**
 *
 */
public class JsonMapper extends TableMapper{
    private static final Logger log = LoggerFactory.getLogger(JsonMapper.class);

    public JsonMapper(String topic, String tableName, int batchMaxRows, Processor processor) throws SQLException {
        super(topic, tableName, batchMaxRows, processor);
    }

    @Override
    public PendingRecord doExtractRecord(ResultSet resultSet, Map<String, String> partition) {
        JSONObject jsonObject = new JSONObject();
        JSONObject tagObject = new JSONObject();
        try {
            jsonObject.put("metric", tableName);
            jsonObject.put("timestamp", resultSet.getTimestamp(1));
            for (String column : columns) {
                String value = columnType.get(column);
                switch (value) {
                    case "TIMESTAMP":
                        jsonObject.put(column,resultSet.getTimestamp(column));
                        break;
                    case "NCHAR":
                        jsonObject.put(column, resultSet.getString(column));
                        break;
                    case "INT":
                    case "TINYINT":
                    case "SMALLINT":
                        jsonObject.put(column, resultSet.getInt(column));
                        break;
                    case "BIGINT":
                        jsonObject.put(column, resultSet.getLong(column));
                        break;
                    case "FLOAT":
                        jsonObject.put(column, resultSet.getFloat(column));
                        break;
                    case "DOUBLE":
                        jsonObject.put(column, resultSet.getDouble(column));
                        break;
                    case "BINARY":
                        jsonObject.put(column, resultSet.getObject(column));
                        break;
                    case "BOOL":
                        jsonObject.put(column, resultSet.getBoolean(column));
                        break;
                    default:
                        throw new IllegalArgumentException("Found invalid datatype in table - column " + value
                                + " with type " + value.getClass());
                }
            }
            if (tags.size() > 0) {
                for (String tag : tags) {
                    String value = columnType.get(tag);
                    switch (value) {
                        case "TIMESTAMP":
                        case "NCHAR":
                            tagObject.put(tag, resultSet.getString(tag));
                            break;
                        case "INT":
                        case "TINYINT":
                        case "SMALLINT":
                            tagObject.put(tag, resultSet.getInt(tag));
                            break;
                        case "BIGINT":
                            tagObject.put(tag, resultSet.getLong(tag));
                            break;
                        case "FLOAT":
                            tagObject.put(tag, resultSet.getFloat(tag));
                            break;
                        case "DOUBLE":
                            tagObject.put(tag, resultSet.getDouble(tag));
                            break;
                        case "BINARY":
                            tagObject.put(tag, resultSet.getObject(tag));
                            break;
                        case "BOOL":
                            tagObject.put(tag, resultSet.getBoolean(tag));
                            break;
                        default:
                            throw new IllegalArgumentException("Found invalid datatype in table - column " + value
                                    + " with type " + value.getClass());
                    }
                }
            }
            jsonObject.put("tags", tagObject);
        } catch (SQLException e) {
            log.error("resultSet get value error", e);
        }
        Timestamp ts = (Timestamp) jsonObject.get("timestamp");

        return new PendingRecord(partition, ts, topic, null, jsonObject.toJSONString());
    }
}
