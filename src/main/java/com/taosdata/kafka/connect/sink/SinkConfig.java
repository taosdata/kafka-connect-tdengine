package com.taosdata.kafka.connect.sink;

import com.taosdata.kafka.connect.config.*;
import org.apache.kafka.common.config.ConfigDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * all sink task need config
 */
public class SinkConfig extends ConnectionConfig {
    private static final Logger log = LoggerFactory.getLogger(SinkConfig.class);

    private static final String WRITES_GROUP = "Writes";

    public static final String MAX_RETRIES = "max.retries";
    public static final int MAX_RETRIES_DEFAULT = 3;
    private static final String MAX_RETRIES_DOC =
            "The maximum number of times to retry on errors before failing the task.";
    private static final String MAX_RETRIES_DISPLAY = "Maximum Retries";

    public static final String RETRY_BACKOFF_MS = "retry.backoff.ms";
    public static final int RETRY_BACKOFF_MS_DEFAULT = 3000;
    private static final String RETRY_BACKOFF_MS_DOC =
            "The time in milliseconds to wait following an error before a retry attempt is made.";
    private static final String RETRY_BACKOFF_MS_DISPLAY = "Retry Backoff (millis)";

    public static final String BATCH_SIZE = "batch.size";
    public static final int BATCH_SIZE_DEFAULT = 3000;
    private static final String BATCH_SIZE_DOC =
            "Specifies how many records to attempt to batch together for insertion into the destination"
                    + " table, when possible.";
    private static final String BATCH_SIZE_DISPLAY = "Batch Size";

    public final static String CHARSET_CONF = "db.charset";
    public final static String CHARSET_DOC = "The character set to use for String key and values.";

    private static final String DB_TIMEUNIT_CONFIG = "db.timeunit";
    public static final String DB_TIMEUNIT_DEFAULT = "milliseconds";
    private static final String DB_TIMEUNIT_CONFIG_DOC = "timeunit for writing data to TDengine";
    private static final String DB_TIMEUNIT_CONFIG_DISPLAY = "DB Time Unit";

    private static final String DB_SCHEMALESS_CONFIG = "db.schemaless";
    private static final String DB_SCHEMALESS_CONFIG_DOC = "schemaless format for writing data to TDengine";
    private static final String DB_SCHEMALESS_CONFIG_DISPLAY = "DB Schemaless Format";

    private final int maxRetries;
    private final long retryBackoffMs;
    private final int batchSize;
    private final String charset;
    private final String timeunit;
    private final String schemalessTypeFormat;

    public SinkConfig(Map<?, ?> originals) {
        super(config(), originals);
        this.maxRetries = getInt(MAX_RETRIES);
        this.retryBackoffMs = getInt(RETRY_BACKOFF_MS);
        this.batchSize = getInt(BATCH_SIZE);
        this.charset = getString(CHARSET_CONF);
        this.timeunit = getString(DB_TIMEUNIT_CONFIG);
        this.schemalessTypeFormat = getString(DB_SCHEMALESS_CONFIG);
    }

    public static ConfigDef config() {
        return ConnectionConfig.config()
                .define(
                        BATCH_SIZE,
                        ConfigDef.Type.INT,
                        BATCH_SIZE_DEFAULT,
                        ConfigDef.Range.atLeast(0),
                        ConfigDef.Importance.MEDIUM,
                        BATCH_SIZE_DOC,
                        WRITES_GROUP,
                        1,
                        ConfigDef.Width.SHORT,
                        BATCH_SIZE_DISPLAY
                )
                .define(
                        MAX_RETRIES,
                        ConfigDef.Type.INT,
                        MAX_RETRIES_DEFAULT,
                        ConfigDef.Range.atLeast(0),
                        ConfigDef.Importance.MEDIUM,
                        MAX_RETRIES_DOC,
                        WRITES_GROUP,
                        2,
                        ConfigDef.Width.SHORT,
                        MAX_RETRIES_DISPLAY
                )
                .define(
                        RETRY_BACKOFF_MS,
                        ConfigDef.Type.INT,
                        RETRY_BACKOFF_MS_DEFAULT,
                        ConfigDef.Range.atLeast(0),
                        ConfigDef.Importance.MEDIUM,
                        RETRY_BACKOFF_MS_DOC,
                        WRITES_GROUP,
                        3,
                        ConfigDef.Width.SHORT,
                        RETRY_BACKOFF_MS_DISPLAY
                )
                .define(
                        DB_TIMEUNIT_CONFIG,
                        ConfigDef.Type.STRING,
                        DB_TIMEUNIT_DEFAULT,
                        TimeUnitValidator.INSTANCE,
                        ConfigDef.Importance.MEDIUM,
                        DB_TIMEUNIT_CONFIG_DOC,
                        WRITES_GROUP,
                        5,
                        ConfigDef.Width.SHORT,
                        DB_TIMEUNIT_CONFIG_DISPLAY
                )
                .define(
                        DB_SCHEMALESS_CONFIG,
                        ConfigDef.Type.STRING,
                        ConfigDef.NO_DEFAULT_VALUE,
                        SchemalessValidator.INSTANCE,
                        ConfigDef.Importance.HIGH,
                        DB_SCHEMALESS_CONFIG_DOC,
                        WRITES_GROUP,
                        6,
                        ConfigDef.Width.SHORT,
                        DB_SCHEMALESS_CONFIG_DISPLAY

                )
                .define(
                        CHARSET_CONF,
                        ConfigDef.Type.STRING,
                        "UTF-8",
                        CharsetValidator.INSTANCE,
                        ConfigDef.Importance.LOW,
                        CHARSET_DOC
                )

                ;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public long getRetryBackoffMs() {
        return retryBackoffMs;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public String getCharset() {
        return charset;
    }

    public String getTimeunit() {
        return timeunit;
    }

    public String getSchemalessTypeFormat() {
        return schemalessTypeFormat;
    }
}
