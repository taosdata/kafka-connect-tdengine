package com.taosdata.kafka.connect.source;

import com.alibaba.fastjson.JSONArray;
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
public class JsonMapper extends TableMapper {
    private static final Logger log = LoggerFactory.getLogger(JsonMapper.class);

    public JsonMapper(String topic, String tableName, int batchMaxRows, Processor processor) throws SQLException {
        super(topic, tableName, batchMaxRows, processor);
    }

    @Override
    public PendingRecord doExtractRecord(ResultSet resultSet, Map<String, String> partition) {
        JSONArray array = new JSONArray();
        Timestamp ts = null;
        try {
            ts = resultSet.getTimestamp(1);
            for (String column : columns) {
                JSONObject tagObject = new JSONObject();
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
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("timestamp", ts);
                jsonObject.put("metric", tableName + "_" + column);
                if (!tagObject.isEmpty()) {
                    jsonObject.put("tags", tagObject);
                }
                String value = columnType.get(column);
                switch (value) {
                    case "TIMESTAMP":
                        jsonObject.put("value", resultSet.getTimestamp(column));
                        break;
                    case "NCHAR":
                        jsonObject.put("value", resultSet.getString(column));
                        break;
                    case "INT":
                    case "TINYINT":
                    case "SMALLINT":
                        jsonObject.put("value", resultSet.getInt(column));
                        break;
                    case "BIGINT":
                        jsonObject.put("value", resultSet.getLong(column));
                        break;
                    case "FLOAT":
                        jsonObject.put("value", resultSet.getFloat(column));
                        break;
                    case "DOUBLE":
                        jsonObject.put("value", resultSet.getDouble(column));
                        break;
                    case "BINARY":
                        jsonObject.put("value", resultSet.getObject(column));
                        break;
                    case "BOOL":
                        jsonObject.put("value", resultSet.getBoolean(column));
                        break;
                    default:
                        throw new IllegalArgumentException("Found invalid datatype in table - column " + value
                                + " with type " + value.getClass());
                }
                array.add(jsonObject);
            }
        } catch (SQLException e) {
            log.error("resultSet get value error", e);
        }
        return new PendingRecord(partition, ts, topic, null, array.toJSONString());
    }
}
