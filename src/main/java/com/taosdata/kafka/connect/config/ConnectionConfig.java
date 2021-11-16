package com.taosdata.kafka.connect.config;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * connection configuration
 */
public class ConnectionConfig extends AbstractConfig {
    private static final Logger log = LoggerFactory.getLogger(ConnectionConfig.class);

    private static final String CONNECTION_GROUP = "Connection";

    public static final String CONNECTION_PREFIX = "connection.";

    public static final String CONNECTION_URL_CONFIG = CONNECTION_PREFIX + "url";
    private static final String CONNECTION_URL_DOC =
            "JDBC connection URL.\n" +
                    "For example: jdbc:TAOS://\" + host + \":6030";
    private static final String CONNECTION_URL_DISPLAY = "JDBC URL";

    public static final String CONNECTION_USER = CONNECTION_PREFIX + "user";
    public static final String CONNECTION_USER_DEFAULT = "root";
    private static final String CONNECTION_USER_DOC = "JDBC connection user.";
    private static final String CONNECTION_USER_DISPLAY = "JDBC User";

    public static final String CONNECTION_PASSWORD = CONNECTION_PREFIX + "password";
    public static final String CONNECTION_PASSWORD_DEFAULT = "taosdata";
    private static final String CONNECTION_PASSWORD_DOC = "JDBC connection password.";
    private static final String CONNECTION_PASSWORD_DISPLAY = "JDBC Password";

    public static final String CONNECTION_DB = CONNECTION_PREFIX + "database";
    private static final String CONNECTION_DB_DOC = "JDBC connection database.";
    private static final String CONNECTION_DB_DISPLAY = "JDBC Database";


    public final static String CONNECTION_ATTEMPTS = CONNECTION_PREFIX + "attempts";
    public static final int CONNECTION_ATTEMPTS_DEFAULT = 3;
    public static final String CONNECTION_ATTEMPTS_DOC =
            "Maximum number of attempts to retrieve a valid JDBC connection. " +
                    "Must be a positive integer.";
    public static final String CONNECTION_ATTEMPTS_DISPLAY = "JDBC connection attempts";

    public static final String CONNECTION_BACKOFF = CONNECTION_PREFIX + "backoff.ms";
    public static final long CONNECTION_BACKOFF_DEFAULT = 5000L;
    public static final String CONNECTION_BACKOFF_DOC =
            "Backoff time in milliseconds between connection attempts.";
    public static final String CONNECTION_BACKOFF_DISPLAY =
            "JDBC connection backoff in milliseconds";

    public static ConfigDef config() {
        return new ConfigDef()
                .define(
                        CONNECTION_URL_CONFIG,
                        ConfigDef.Type.STRING,
                        ConfigDef.NO_DEFAULT_VALUE,
                        ConnectionUrlValidator.INSTANCE,
                        ConfigDef.Importance.HIGH,
                        CONNECTION_URL_DOC,
                        CONNECTION_GROUP,
                        1,
                        ConfigDef.Width.LONG,
                        CONNECTION_URL_DISPLAY
                )
                .define(
                        CONNECTION_USER,
                        ConfigDef.Type.STRING,
                        CONNECTION_USER_DEFAULT,
                        ConfigDef.Importance.HIGH,
                        CONNECTION_USER_DOC,
                        CONNECTION_GROUP,
                        2,
                        ConfigDef.Width.MEDIUM,
                        CONNECTION_USER_DISPLAY
                )
                .define(
                        CONNECTION_PASSWORD,
                        ConfigDef.Type.STRING,
                        CONNECTION_PASSWORD_DEFAULT,
                        ConfigDef.Importance.HIGH,
                        CONNECTION_PASSWORD_DOC,
                        CONNECTION_GROUP,
                        3,
                        ConfigDef.Width.MEDIUM,
                        CONNECTION_PASSWORD_DISPLAY
                )
                .define(
                        CONNECTION_ATTEMPTS,
                        ConfigDef.Type.INT,
                        CONNECTION_ATTEMPTS_DEFAULT,
                        ConfigDef.Range.atLeast(1),
                        ConfigDef.Importance.LOW,
                        CONNECTION_ATTEMPTS_DOC,
                        CONNECTION_GROUP,
                        4,
                        ConfigDef.Width.SHORT,
                        CONNECTION_ATTEMPTS_DISPLAY
                )
                .define(
                        CONNECTION_BACKOFF,
                        ConfigDef.Type.LONG,
                        CONNECTION_BACKOFF_DEFAULT,
                        ConfigDef.Importance.LOW,
                        CONNECTION_BACKOFF_DOC,
                        CONNECTION_GROUP,
                        5,
                        ConfigDef.Width.SHORT,
                        CONNECTION_BACKOFF_DISPLAY
                )
                .define(
                        CONNECTION_DB,
                        ConfigDef.Type.STRING,
                        ConfigDef.NO_DEFAULT_VALUE,
                        ConfigDef.Importance.HIGH,
                        CONNECTION_DB_DOC,
                        CONNECTION_GROUP,
                        6,
                        ConfigDef.Width.MEDIUM,
                        CONNECTION_DB_DISPLAY
                );
    }

    private final String connectionUrl;
    private final String connectionUser;
    private final String connectionPassword;
    private final int connectionAttempts;
    private final long connectionBackoffMs;
    private final String connectionDb;

    public ConnectionConfig(ConfigDef def, Map<?, ?> props) {
        super(def, props);
        this.connectionUrl = getString(CONNECTION_URL_CONFIG).trim();
        this.connectionUser = getString(CONNECTION_USER).trim();
        this.connectionPassword = getString(CONNECTION_PASSWORD).trim();
        this.connectionAttempts = getInt(CONNECTION_ATTEMPTS);
        this.connectionBackoffMs = getLong(CONNECTION_BACKOFF);
        this.connectionDb = getString(CONNECTION_DB).trim();
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }

    public String getConnectionUser() {
        return connectionUser;
    }

    public String getConnectionPassword() {
        return connectionPassword;
    }

    public int getConnectionAttempts() {
        return connectionAttempts;
    }

    public long getConnectionBackoffMs() {
        return connectionBackoffMs;
    }

    public String getConnectionDb() {
        return connectionDb;
    }
}
