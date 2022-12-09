package com.taosdata.kafka.connect.sink;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.taosdata.kafka.connect.util.VersionUtils;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.common.utils.ByteBufferOutputStream;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.taosdata.kafka.connect.sink.SinkConfig.SCHEMA_LOCATION;
import static com.taosdata.kafka.connect.sink.SinkConfig.SCHEMA_TYPE;
import static com.taosdata.kafka.connect.sink.SinkConstants.SCHEMA_STRING;
import static com.taosdata.kafka.connect.sink.SinkConstants.SCHEMA_TYPE_LOCAL;

/**
 * tdengine sink connection
 */
public class TDengineSinkConnector extends SinkConnector {
    private static final Logger log = LoggerFactory.getLogger(TDengineSinkConnector.class);

    private SinkConfig config;
    private Map<String, String> configProps;

    @Override
    public void start(Map<String, String> map) {
        log.info("Starting Sink Connector");

        config = new SinkConfig(map);
        String schemaStr = null;
        String schemaLocation = config.getSchemaLocation();
        String schemaType = config.getSchemaType();
        try {
            if (SCHEMA_TYPE_LOCAL.equals(schemaType)) {
                StringBuilder sb = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new FileReader(schemaLocation))) {
                    String str;
                    while ((str = reader.readLine()) != null) {
                        sb.append(str);
                    }
                }
                schemaStr = sb.toString();
            } else {
                InputStream in = null;
                ByteArrayOutputStream outputStream = null;
                try {
                    URL url = new URL(schemaLocation);
                    in = url.openStream();
                    outputStream = new ByteArrayOutputStream();
                    byte[] b = new byte[1024];
                    int n;
                    while ((n = in.read(b)) > 0) {
                        outputStream.write(b, 0, n);
                    }
                    outputStream.flush();
                    outputStream.toByteArray();
                    schemaStr = outputStream.toString();
                } finally {
                    if (outputStream != null)
                        outputStream.close();
                    if (in != null)
                        in.close();
                }
            }
        } catch (IOException e) {
            throw new ConfigException(String.format("JSON schema configuration can not get for path: %s with type %s. error '%s'",
                    schemaLocation, schemaType, e));
        }
        configProps = map;
        configProps.put(SCHEMA_STRING, schemaStr);
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
     * @param settings  setting map
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
