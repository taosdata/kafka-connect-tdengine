package com.taosdata.kafka.connect.source;

import com.google.common.collect.Lists;
import com.taosdata.kafka.connect.util.VersionUtils;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceConnector;
import org.apache.kafka.connect.util.ConnectorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.*;

/**
 * start monitor thread and create config for soruceTask while connector start
 */
public class TDengineSourceConnector extends SourceConnector {
    private static final Logger log = LoggerFactory.getLogger(TDengineSourceConnector.class);

    private Map<String, String> map;
    private MonitorThread monitorThread;

    @Override
    public void start(Map<String, String> props) {
        log.info("Starting TDengine Source Connector");
        this.map = props;
        try {
            monitorThread = new MonitorThread(map, context);
        } catch (SQLException e) {
            throw new ConnectException(e);
        }
        monitorThread.start();
        log.info("Started TDengine Source Connector");
    }

    @Override
    public Class<? extends Task> taskClass() {
        return TDengineSourceTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        List<Map<String, String>> result;
        Set<String> currentTables = monitorThread.getTables();
        if (currentTables.isEmpty()) {
            result = Collections.emptyList();

            log.warn("No tasks will be run because no tables were found");
        } else {
            int numGroups = Math.min(currentTables.size(), maxTasks);
            numGroups = Math.max(numGroups, 1);
            // 也许这里需要开发指定table分组
            List<List<String>> tablesGroup =
                    ConnectorUtils.groupPartitions(Lists.newArrayList(currentTables), numGroups);
            result = new ArrayList<>(tablesGroup.size());
            for (List<String> taskTables : tablesGroup) {
                Map<String, String> taskProps = new HashMap<>(map);
                String tables = String.join(",", taskTables);
                taskProps.put(SourceConstants.CONFIG_TABLES, tables);
                result.add(taskProps);
            }
            log.info(
                    "Producing task configs with no custom query for tables: {}",
                    Arrays.toString(currentTables.toArray())
            );
        }
        return result;
    }

    @Override
    public void stop() {
        log.info("TDengine Source Connector Stop!");

        monitorThread.shutdown();
    }

    @Override
    public ConfigDef config() {
        return SourceConfig.config();
    }

    @Override
    public String version() {
        return VersionUtils.getVersion();
    }
}
