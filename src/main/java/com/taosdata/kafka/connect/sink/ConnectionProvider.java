package com.taosdata.kafka.connect.sink;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author huolibo@qq.com
 * @version v1.0.0
 * @JDK: 1.8
 * @description: each class that implements the interface provides a function to get tdengine connection
 * @date 2021-11-04 14:12
 */
public interface ConnectionProvider {

    /**
     * get a connection to TDengine
     * @return connection
     * @throws SQLException execute sql exception
     */
    Connection getConnection() throws SQLException;
}
