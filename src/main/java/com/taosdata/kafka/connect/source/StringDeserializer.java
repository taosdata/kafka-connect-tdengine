package com.taosdata.kafka.connect.source;

import com.taosdata.jdbc.tmq.Deserializer;
import com.taosdata.jdbc.tmq.DeserializerException;
import com.taosdata.kafka.connect.enums.DataPrecision;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class StringDeserializer implements Deserializer<Map<String, Object>> {
    String format;
    String timestampType;

    @Override
    public void configure(Map<?, ?> configs) {
        format = (String) configs.get("format");
        timestampType = (String) configs.get("precision");
    }

    @Override
    public Map<String, Object> deserialize(ResultSet data, String topic, String dbName) throws DeserializerException, SQLException {
        Map<String, Object> map = new HashMap<>();

        ResultSetMetaData metaData = data.getMetaData();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            if (Types.TIMESTAMP == metaData.getColumnType(i)) {
                Timestamp result = data.getTimestamp(i);
                if (timestampType.equalsIgnoreCase(DataPrecision.NS.name())){
                    long milliseconds = result.getTime();
                    long nanoseconds = milliseconds * 1000000 + result.getNanos();
                    map.put(metaData.getColumnLabel(i), nanoseconds);
                } else if (timestampType.equalsIgnoreCase(DataPrecision.US.name())){
                    long milliseconds = result.getTime();
                    long microseconds = milliseconds * 1000 + result.getNanos() / 1000;
                    map.put(metaData.getColumnLabel(i), microseconds);
                } else {
                    long milliseconds = result.getTime();
                     map.put(metaData.getColumnLabel(i), milliseconds);
                }
            } else if (Types.BINARY == metaData.getColumnType(i)) {
                byte[] bytes = data.getBytes(i);
                if (bytes != null) {
                    map.put(metaData.getColumnLabel(i), new String(bytes));
                }
            } else {
                map.put(metaData.getColumnLabel(i), data.getObject(i));
            }
        }
        return map;
    }

}
