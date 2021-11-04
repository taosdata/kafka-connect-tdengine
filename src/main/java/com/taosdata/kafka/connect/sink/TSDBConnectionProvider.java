package com.taosdata.kafka.connect.sink;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author huolibo@qq.com
 * @version v1.0.0
 * @JDK: 1.8
 * @description: "jdbc:TAOS" connection
 * @date 2021-11-04 14:12
 */
public class TSDBConnectionProvider implements ConnectionProvider {
    private static final Logger log = LoggerFactory.getLogger(TSDBConnectionProvider.class);

    private final String url;
    private final Properties properties;
    private final int maxConnectionAttempts;
    private final long connectionRetryBackoffMs;

    public TSDBConnectionProvider(String url, Properties properties, int maxConnectionAttempts, long connectionRetryBackoffMs) {
        this.url = url;
        this.properties = properties;
        this.maxConnectionAttempts = maxConnectionAttempts;
        this.connectionRetryBackoffMs = connectionRetryBackoffMs;
    }

    @Override
    public Connection getConnection() throws SQLException {
        int attempts = 0;
        while (attempts < maxConnectionAttempts) {
            try {
                log.info("create TDengine Connection, Attempt {} of {}", attempts, maxConnectionAttempts);
                return DriverManager.getConnection(url, properties);
            } catch (SQLException exception) {
                attempts++;
                if (attempts < maxConnectionAttempts) {
                    log.info("Unable to connect to database on attempt {}/{}. Will retry in {} ms.", attempts,
                            maxConnectionAttempts, connectionRetryBackoffMs, exception
                    );
                    try {
                        Thread.sleep(connectionRetryBackoffMs);
                    } catch (InterruptedException e) {
                        // this is ok because just woke up early
                    }
                } else {
                    log.error("Exception thrown connecting to TDengine. Reached the maximum config attempt count.");
                    throw exception;
                }
            }
        }
        throw new RuntimeException("Unable to open Connection: TSDBConnectionProvider");
    }
}
