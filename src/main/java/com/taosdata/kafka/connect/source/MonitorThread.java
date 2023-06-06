package com.taosdata.kafka.connect.source;

import com.google.common.collect.Sets;
import com.taosdata.jdbc.TSDBDriver;
import com.taosdata.jdbc.utils.StringUtils;
import com.taosdata.kafka.connect.db.ConnectionProvider;
import com.taosdata.kafka.connect.db.TSDBConnectionProvider;
import com.taosdata.kafka.connect.util.SQLUtils;
import org.apache.kafka.connect.connector.ConnectorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * re-balancing occurs，when tables are modified
 */
public class MonitorThread extends Thread {
    private static final Logger log = LoggerFactory.getLogger(MonitorThread.class);

    private final SourceConfig config;
    private final ConnectorContext context;

    private CountDownLatch countDownLatch;
    private Set<String> tables;
    private Connection connection;

    public MonitorThread(Map<String, String> config, ConnectorContext context) throws SQLException {
        this.config = new SourceConfig(config);
        this.context = context;
        init();
    }

    private void init() throws SQLException {
        // timezone charset, etc.
        Properties properties = new Properties();
        properties.setProperty(TSDBDriver.PROPERTY_KEY_USER, config.getConnectionUser());
        properties.setProperty(TSDBDriver.PROPERTY_KEY_PASSWORD, config.getConnectionPassword());
        properties.setProperty(TSDBDriver.PROPERTY_KEY_CHARSET, "UTF-8");
        ConnectionProvider provider = new TSDBConnectionProvider(config.getConnectionUrl(), properties, config.getConnectionAttempts(), config.getConnectionBackoffMs());
        connection = provider.getConnection();
        countDownLatch = new CountDownLatch(1);
    }

    public synchronized Set<String> getTables() {
        if (null != tables && !tables.isEmpty()) {
            return tables;
        }
        log.info("monitor tables is empty");
        long start = System.currentTimeMillis();
        long now = start;
        while ((null == tables || tables.isEmpty()) && (now - start) < config.getPollInterval()) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                log.warn("Error while waiting for tables set to update. InterruptedException " + e);
            }
            now = System.currentTimeMillis();
        }

        if (null == tables || tables.isEmpty()) {
            log.warn("tables could not be get quickly enough.");
        }
        return tables;
    }

    @Override
    public void run() {
        log.info("Monitor process is start");
        while (countDownLatch.getCount() > 0) {
            if (!isTableChange()) {
                try {
                    countDownLatch.await(config.getPollInterval(), TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    log.error("thread is interrupted while await check table change", e);
                }
            }
        }
    }

    private synchronized boolean isTableChange() {
        Set<String> set = new HashSet<>();
        try (Statement statement = connection.createStatement()) {
            String dbName = config.getConnectionDb();
            ResultSet resultSet = statement.executeQuery(SQLUtils.showSTableSql(dbName));
            while (resultSet.next()) {
                set.add(resultSet.getString(1));
            }
            resultSet = statement.executeQuery(SQLUtils.showTableSql(dbName));
            while (resultSet.next()) {
                set.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
            log.error("error occur while show Tables in db {}", config.getConnectionDb(), e);
        }

        if (null == tables) {
            tables = set;
            this.notifyAll();
            return false;
        }

        Set<String> diffTables = Sets.symmetricDifference(set, tables);
        if (!diffTables.isEmpty()) {
            log.info("tables is change, request task reconfiguration");
            tables = set;
            context.requestTaskReconfiguration();
            this.notifyAll();
        }
        return false;
    }

    public void shutdown() {
        log.info("Monitor Thread shutdown");
        if (null != connection) {
            try {
                connection.close();
            } catch (SQLException e) {
                log.error("Monitor thread close connection exception", e);
            }

        }
        countDownLatch.countDown();
    }
}
