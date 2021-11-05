package com.taosdata.kafka.connect.sink;

import com.taosdata.jdbc.enums.SchemalessProtocolType;
import com.taosdata.jdbc.enums.SchemalessTimestampType;

import java.sql.SQLException;

/**
 * @author huolibo@qq.com
 * @version v1.0.0
 * @JDK: 1.8
 * @description: use connection pool to execute
 * @date 2021-11-04 14:12
 */
public class PoolWriter implements Writer {

    @Override
    public boolean execute(String sql) throws SQLException {
        return false;
    }

    @Override
    public boolean schemalessInsert(String[] records, SchemalessProtocolType protocolType, SchemalessTimestampType timestampType) throws SQLException {
        return false;
    }


    public void close() {

    }

    public void shutdown() throws Exception {

    }
}
