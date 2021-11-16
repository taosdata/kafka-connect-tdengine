package com.taosdata.kafka.connect.db;

import com.taosdata.jdbc.enums.SchemalessProtocolType;
import com.taosdata.jdbc.enums.SchemalessTimestampType;

import java.sql.SQLException;

/**
 * use connection pool to execute
 */
public class PoolProcessor implements Processor {

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
