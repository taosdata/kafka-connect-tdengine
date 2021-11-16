package com.taosdata.kafka.connect.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * each class that implements the interface provides a function to get tdengine connection
 */
public interface ConnectionProvider {

    /**
     * get a connection to TDengine
     * @return connection
     * @throws SQLException execute sql exception
     */
    Connection getConnection() throws SQLException;
}
