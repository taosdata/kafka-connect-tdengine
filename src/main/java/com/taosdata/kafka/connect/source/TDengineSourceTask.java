package com.taosdata.kafka.connect.source;

import com.alibaba.fastjson.JSON;
import com.taosdata.jdbc.TSDBDriver;
import com.taosdata.kafka.connect.db.CacheProcessor;
import com.taosdata.kafka.connect.db.ConnectionProvider;
import com.taosdata.kafka.connect.db.Processor;
import com.taosdata.kafka.connect.db.TSDBConnectionProvider;
import com.taosdata.kafka.connect.enums.ReadMethodEnum;
import com.taosdata.kafka.connect.util.VersionUtils;
import org.apache.kafka.common.utils.SystemTime;
import org.apache.kafka.common.utils.Time;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.apache.kafka.connect.storage.OffsetStorageReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Task for get data from db
 */
public class TDengineSourceTask extends SourceTask {
    private static final Logger log = LoggerFactory.getLogger(TDengineSourceTask.class);
    private static long totol = 0;
    private SourceConfig config;
    private Processor processor;
    private ReadMethodEnum readMethod;

    private final Queue<TableExecutor> executors = new PriorityQueue<>();
    private Map<TableExecutor, Integer> consecutiveEmptyResults;
    private final Time time;

    public TDengineSourceTask() {
        this.time = new SystemTime();
    }

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

        Map<String, String> urls = null;
        if (ReadMethodEnum.SUBSCRIPTION == config.getReadMethod()) {
            urls = UrlParser.parse(config.getConnectionUrl());
            if (null == urls || urls.isEmpty()) {
                throw new ConnectException("url is empty");
            }
            readMethod = ReadMethodEnum.SUBSCRIPTION;
        } else {
            readMethod = ReadMethodEnum.QUERY;
        }

        List<String> tables = config.getTables();
        if (null != tables && !tables.isEmpty()) {
            for (String table : tables) {
                Map<String, String> partition = Collections.singletonMap(SourceConstants.TABLE_NAME_KEY, table);
                OffsetStorageReader offsetStorageReader = context.offsetStorageReader();
                Map<String, Object> offset = offsetStorageReader.offset(partition);
                TableExecutor executor;
                try {
                    String topicName;
                    String dbName = config.getConnectionDb();
                    String topicDelimiter = config.getTopicDelimiter();
                    if (config.isTopicPerSuperTable()) {
                        if (config.isTopicNameIgnoreDb()) {
                            topicName = config.getTopicPrefix() + topicDelimiter + table;
                        } else {
                            topicName = config.getTopicPrefix() + topicDelimiter + dbName + topicDelimiter + table;
                        }
                    } else {
                        topicName = config.getTopicPrefix() + topicDelimiter + dbName;
                    }
                    log.debug("start poll data from db {} table: {}, to topic: {}", dbName, table, topicName);
                    executor = new TableExecutor(table, topicName, offset, processor, partition, config, urls);
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

        TableExecutor executor = executors.peek();
        assert executor != null;

        // If not in the middle of an update, wait for next update time
        long nextUpdate = executor.getLastUpdate() + config.getPollInterval();
        long now = this.time.milliseconds();
        long sleepMs = nextUpdate - now;
        if (sleepMs > 0) {
            log.debug("Waiting {} ms to poll {} next", sleepMs, executor.getTableName());
            this.time.sleep(sleepMs);
        } else if (consecutiveEmptyResults.get(executor) > 0) {
            // rejoin the queue to avoid hammering the DB
            executor.setLastUpdate(this.time.milliseconds());
            executors.add(executor);
            consecutiveEmptyResults.put(executor, 0);
            return Collections.emptyList();
        }

        log.info("start poll new data from table: {}", executor.getTableName());
        List<SourceRecord> results = new ArrayList<>();
        try {
            executor.startQuery();
            if (readMethod == ReadMethodEnum.SUBSCRIPTION) {
                results = executor.extractRecords();
                resetAndRequeueHead(executor, false);
                executor.commitOffset();
                if (!results.isEmpty()) {
                    for (SourceRecord record : results) {
                        log.info("********** received poll results: {}", record.toString());
                    }
                    totol += results.size();
                    log.info("********** received results poll len: {}, totol:{}", results.size(), totol);
                }
                return results;
            } else {
                int batchMaxRows = config.getFetchMaxRows();
                boolean hadNext = true;
                while (results.size() < batchMaxRows && (hadNext = executor.next())) {
                    executor.clearEndQuery();
                    SourceRecord record = executor.extractRecord();
                    if (record.value() instanceof List) {
                        for (Struct struct : (List<Struct>) record.value()) {
                            results.add(new SourceRecord(record.sourcePartition(), record.sourceOffset(), record.topic(), struct.schema(), struct));
                        }
                    } else {
                        results.add(record);
                    }
                }
                if (!hadNext) {
                    resetAndRequeueHead(executor, false);
                }

                if (results.isEmpty()) {
                    consecutiveEmptyResults.compute(executor, (k, v) -> v + 1);
                    log.debug("No updates for {}", executor.getTableName());
                    return Collections.emptyList();
                } else {
                    consecutiveEmptyResults.put(executor, 0);
                    log.debug("Returning {} records for {}. last record time: {}",
                            results.size(), executor.getTableName(), results.get(results.size() - 1).timestamp());
                    return results;
                }
            }
        } catch (SQLException e) {
            resetAndRequeueHead(executor, true);
            log.error("SQL exception while running query for table: {}", executor.getTableName(), e);
        }
        return Collections.emptyList();
    }

    private void resetAndRequeueHead(TableExecutor executor, boolean resetOffset) {
        TableExecutor e = executors.poll();
        assert e == executor;
        executor.reset(this.time.milliseconds(), resetOffset);
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
