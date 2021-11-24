package com.taosdata.kafka.connect.sink;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.taosdata.kafka.connect.util.VersionUtils;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * tdengine sink connection
 */
public class TDengineSinkConnector extends SinkConnector {
    private static final Logger log = LoggerFactory.getLogger(TDengineSinkConnector.class);

    private Map<String, String> configProps;

    @Override
    public void start(Map<String, String> map) {
        log.info("Starting Sink Connector");
        configProps = map;
    }

    @Override
    public Class<? extends Task> taskClass() {
        return TDengineSinkTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        log.info("Setting task configurations for {} workers.", maxTasks);
        return this.multiple(this.configProps, maxTasks);
    }

    @Override
    public void stop() {
        log.info("Sink Connector Stop!");
    }

    @Override
    public ConfigDef config() {
        return SinkConfig.config();
    }

    @Override
    public String version() {
        return VersionUtils.getVersion();
    }

    /**
     * Method is used to generate a list of taskConfigs based on the supplied settings.
     *
     * @param settings setting map
     * @param taskCount number of task
     * @return list
     */
    private List<Map<String, String>> multiple(Map<String, String> settings, final int taskCount) {
        Preconditions.checkNotNull(settings, "settings cannot be null.");
        Preconditions.checkState(taskCount > 0, "taskCount must be greater than 0.");
        final List<Map<String, String>> result = new ArrayList<>(taskCount);
        for (int i = 0; i < taskCount; i++) {
            result.add(settings);
        }
        return ImmutableList.copyOf(result);
    }
}
