package com.taosdata.kafka.connect.sink;

import com.taosdata.jdbc.SchemalessStatement;
import com.taosdata.jdbc.enums.SchemalessProtocolType;
import com.taosdata.jdbc.enums.SchemalessTimestampType;
import org.apache.kafka.connect.errors.ConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * @param <T> Connection provider implement
 * @description: cache connection and provide write schemaless function
 *
 * @author huolibo@qq.com
 * @version v1.0.0
 * @JDK: 1.8
 * @date 2021-11-04 14:12
 */
public class CacheWriter<T extends ConnectionProvider> implements Writer {
    private static final Logger log = LoggerFactory.getLogger(CacheWriter.class);

    private final ConnectionProvider provider;

    private Connection connection;

    private final String dbName;

    public CacheWriter(T provider, String dbName) {
        this.provider = provider;
        this.dbName = dbName;
    }

    private synchronized Connection getConnection() throws SQLException {
        try {
            if (this.connection == null) {
                this.connection = provider.getConnection();
                initDB();
            } else if (!isConnectionValid(connection)) {
                log.info("The database connection is invalid. Reconnecting...");
                close();
                this.connection = provider.getConnection();
                initDB();
            }
        } catch (SQLException sqle) {
            throw new ConnectException(sqle);
        }
        return connection;
    }

    private void initDB() {
        try {
            String sql = "create database if not exists " + this.dbName ;
            this.execute(sql);
            sql = "use " + dbName;
            this.execute(sql);
        } catch (SQLException e) {
            log.error("init database error！", e);
        }
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        Statement statement = this.getConnection().createStatement();
        boolean result = statement.execute(sql);
        if (result) {
            ResultSet rs = null;
            try {
                // do nothing with the result set
                rs = statement.getResultSet();
            } finally {
                if (rs != null) {
                    rs.close();
                }
            }
        }
        return result;
    }


    @Override
    public boolean schemalessInsert(String[] records, SchemalessProtocolType protocolType, SchemalessTimestampType timestampType) throws SQLException {
        try (SchemalessStatement statement = new SchemalessStatement(this.getConnection().createStatement())) {
            statement.executeSchemaless(records, protocolType, timestampType);
            return true;
        } catch (SQLException e) {
            log.error("execute batch schemaless insert failure！");
            throw e;
        }
    }

    @Override
    public void close() {
        if (connection != null) {
            try {
                log.info("Try closing connection {}", connection.getMetaData().getURL());
                connection.close();
            } catch (SQLException sqle) {
                log.warn("Ignoring error closing connection", sqle);
            } finally {
                connection = null;
            }
        }
    }

    public boolean isConnectionValid(Connection connection) {
        // test query ...
        String query = checkConnectionQuery();
        try (Statement statement = connection.createStatement()) {
            if (statement.execute(query)) {
                ResultSet rs = null;
                try {
                    // do nothing with the result set
                    rs = statement.getResultSet();
                } finally {
                    if (rs != null) {
                        rs.close();
                    }
                }
            }
        } catch (SQLException sqle) {
            log.debug("Unable to check if the underlying connection is valid", sqle);
            return false;
        }
        return true;
    }

    private String checkConnectionQuery() {
        return "SELECT 1";
    }

}
