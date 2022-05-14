package com.taosdata.kafka.connect.source;

import com.taosdata.jdbc.TSDBDriver;
import com.taosdata.kafka.connect.db.CacheProcessor;
import com.taosdata.kafka.connect.db.ConnectionProvider;
import com.taosdata.kafka.connect.db.Processor;
import com.taosdata.kafka.connect.db.TSDBConnectionProvider;
import com.taosdata.kafka.connect.util.VersionUtils;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.apache.kafka.connect.storage.OffsetStorageReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Task for get data from db
 */
public class TDengineSourceTask extends SourceTask {
    private static final Logger log = LoggerFactory.getLogger(TDengineSourceTask.class);

    private SourceConfig config;
    private Processor processor;

    private final Queue<TableExecutor> executors = new LinkedList<>();
    private Map<TableExecutor, Integer> consecutiveEmptyResults;

    @Override
    public void start(Map<String, String> props) {
        log.info("Starting TDengine source task");
        this.config = new SourceConfig(props);
        Properties properties = new Properties();
        properties.setProperty(TSDBDriver.PROPERTY_KEY_USER, config.getConnectionUser());
        properties.setProperty(TSDBDriver.PROPERTY_KEY_PASSWORD, config.getConnectionPassword());
        properties.setProperty(TSDBDriver.PROPERTY_KEY_CHARSET, "UTF-8");
        ConnectionProvider provider = new TSDBConnectionProvider(config.getConnectionUrl(), properties,
                config.getConnectionAttempts(), config.getConnectionBackoffMs());
        processor = new CacheProcessor<>(provider);
        processor.setDbName(config.getConnectionDb());

        List<String> tables = config.getTables();
        if (null != tables && tables.size() > 0) {
            for (String table : tables) {
                Map<String, String> partition = Collections.singletonMap(SourceConstants.TABLE_NAME_KEY, table);
                OffsetStorageReader offsetStorageReader = context.offsetStorageReader();
                Map<String, Object> offset = offsetStorageReader.offset(partition);
                TableExecutor executor;
                try {
                    executor = new TableExecutor(table, config.getTopicPrefix() + config.getConnectionDb(),
                            offset, processor, config.getFetchMaxRows(), partition, config.getTimestampInitial(),
                            config.getOutFormat());
                } catch (SQLException e) {
                    log.error("error occur", e);
                    throw new ConnectException(e);
                }
                executors.add(executor);
            }
        }
        consecutiveEmptyResults = executors.stream().collect(
                Collectors.toMap(Function.identity(), (q) -> 0, (x, y) -> x));
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {

        TableExecutor executor = executors.poll();
        assert executor != null;

        // If not in the middle of an update, wait for next update time
        if (null != executor.getLastCommittedOffset()) {
            long nextUpdate = executor.getLastCommittedOffset().getTime()
                    + config.getPollInterval();
            long now = System.currentTimeMillis();
            long sleepMs = Math.min(nextUpdate - now, 1000);
            if (sleepMs > 0) {
                log.trace("Waiting {} ms to poll {} next", nextUpdate - now, executor.getTableName());
                TimeUnit.MILLISECONDS.sleep(sleepMs);
            } else if (consecutiveEmptyResults.get(executor) > 0) {
                TimeUnit.MILLISECONDS.sleep(config.getPollInterval());
            }
        }
        log.info("start poll new data from table:" + executor.getTableName());
        List<SourceRecord> results = new ArrayList<>();
        try {
            executor.startQuery();

            int batchMaxRows = config.getFetchMaxRows();
            boolean hadNext = true;
            while (results.size() < batchMaxRows && (hadNext = executor.next())) {
                results.add(executor.extractRecord());
            }
            if (!hadNext) {
                resetAndRequeueHead(executor, false);
            }

            if (results.isEmpty()) {
                consecutiveEmptyResults.compute(executor, (k, v) -> v + 1);
                log.trace("No updates for {}", executor.getTableName());
                return null;
            } else {
                consecutiveEmptyResults.put(executor, 0);
                return results;
            }

        } catch (SQLException e) {
            resetAndRequeueHead(executor, true);
            log.error("SQL exception while running query for table: {}", executor.getTableName(), e);
        }
        return null;
    }

    private void resetAndRequeueHead(TableExecutor executor, boolean resetOffset) {
        executor.reset(resetOffset);
        executors.add(executor);
    }

    @Override
    public void stop() {
        log.info("Stop TDengine Source Task");
        processor.close();
    }

    @Override
    public String version() {
        return VersionUtils.getVersion();
    }
}
