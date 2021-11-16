package com.taosdata.kafka.connect.db;

import com.taosdata.jdbc.enums.SchemalessProtocolType;
import com.taosdata.jdbc.enums.SchemalessTimestampType;

import java.sql.SQLException;

public interface Processor {

    /**
     * Executes the given SQL statement
     *
     * @param sql sql
     * @return <code>true</code> if the first result is a <code>ResultSet</code>
     * object; <code>false</code> if it is an update count or there are
     * no results
     * @throws SQLException exception
     */
    boolean execute(String sql) throws SQLException;

    /**
     * insert schemaless data
     *
     * @return boolean
     */
    boolean schemalessInsert(String[] records, SchemalessProtocolType protocolType, SchemalessTimestampType timestampType) throws SQLException;


    /**
     * Closes this resource
     */
    void close();
}
