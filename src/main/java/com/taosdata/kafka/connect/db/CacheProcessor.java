package com.taosdata.kafka.connect.db;

import com.taosdata.jdbc.SchemalessWriter;
import com.taosdata.jdbc.enums.SchemalessProtocolType;
import com.taosdata.jdbc.enums.SchemalessTimestampType;
import org.apache.kafka.connect.errors.ConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * cache connection and provide write schemaless function
 *
 * @param <T> Connection provider implement
 */
public class CacheProcessor<T extends ConnectionProvider> implements Processor {
    private static final Logger log = LoggerFactory.getLogger(CacheProcessor.class);

    private final ConnectionProvider provider;

    private Connection connection;

    private String dbName;

    public CacheProcessor(T provider) {
        this.provider = provider;
    }

    @Override
    public void setDbName(String dbName) {
        this.dbName = dbName;
        this.initDB();
    }

    @Override
    public Connection getConnection() throws SQLException {
        try {
            if (this.connection == null) {
                this.connection = provider.getConnection();
            } else if (!isConnectionValid(connection)) {
                log.info("The database connection is invalid. Reconnecting...");
                close();
                this.connection = provider.getConnection();
            }
        } catch (SQLException sqle) {
            throw new ConnectException(sqle);
        }
        return connection;
    }

    private void initDB() {
        try {
            String sql = "create database if not exists " + this.dbName + " precision 'ns'";
            this.execute(sql);
            sql = "use " + dbName;
            this.execute(sql);
        } catch (SQLException e) {
            log.error("init database error！", e);
            throw new ConnectException(e);
        }
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        Statement statement = this.getConnection().createStatement();
        log.info("Processor execute SQL : {}", sql);
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
        SchemalessWriter writer = new SchemalessWriter(this.getConnection());
        writer.write(records, protocolType, timestampType);
        return true;
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

    public boolean isConnectionValid(java.sql.Connection connection) {
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
