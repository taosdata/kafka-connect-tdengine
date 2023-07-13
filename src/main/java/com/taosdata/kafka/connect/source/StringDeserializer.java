package com.taosdata.kafka.connect.source;

import com.taosdata.jdbc.tmq.Deserializer;
import com.taosdata.jdbc.tmq.DeserializerException;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

public class StringDeserializer implements Deserializer<Map<String, Object>> {
    String format;

    @Override
    public void configure(Map<?, ?> configs) {
        format = (String) configs.get("format");
    }

    @Override
    public Map<String, Object> deserialize(ResultSet data, String topic, String dbName) throws DeserializerException, SQLException {
        Map<String, Object> map = new HashMap<>();

        ResultSetMetaData metaData = data.getMetaData();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            if (Types.TIMESTAMP == metaData.getColumnType(i)) {
                long result = data.getLong(i);
                if (result > 1_000_000_000_000_000_000L) {
                    result = result / 1_000_000;
                } else if (result > 1_000_000_000_000_000L) {
                    result = result / 1_000;
                }
                map.put(metaData.getColumnLabel(i), result);
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
