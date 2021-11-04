package com.taosdata.kafka.connect.source;

import com.taosdata.kafka.connect.util.VersionUtil;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;

import java.util.List;
import java.util.Map;

/**
 * @author huolibo@qq.com
 * @version v1.0.0
 * @description: source task
 * @JDK: 1.8
 * @date 2021-11-04 15:25
 */
public class TDengineSourceTask extends SourceTask {
    @Override
    public String version() {
        return VersionUtil.getVersion();
    }

    @Override
    public void start(Map<String, String> props) {

    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        return null;
    }

    @Override
    public void stop() {

    }
}
