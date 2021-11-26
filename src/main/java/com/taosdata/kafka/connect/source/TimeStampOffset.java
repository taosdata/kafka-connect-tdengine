package com.taosdata.kafka.connect.source;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * timestamp offset format
 */
public class TimeStampOffset {
    private static final Logger log = LoggerFactory.getLogger(TimeStampOffset.class);

    private final Timestamp timestampOffset;

    public TimeStampOffset(Timestamp timestampOffset) {
        this.timestampOffset = timestampOffset;
    }

    public Timestamp getTimestampOffset() {
        return timestampOffset;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        if (null != timestampOffset) {
            map.put(SourceConstants.TIMESTAMP_MILLISECOND, timestampOffset.getTime());
            map.put(SourceConstants.TIMESTAMP_NANOSECOND, (long) timestampOffset.getNanos());
        }
        return map;
    }

    public static TimeStampOffset fromMap(Map<String, ?> map) {
        if (null == map || map.isEmpty()) {
            return new TimeStampOffset(null);
        }

        Timestamp timestamp = Optional.ofNullable(map.get(SourceConstants.TIMESTAMP_MILLISECOND))
                .map(String::valueOf)
                .map(Long::parseLong)
                .map(millis -> {
                    Timestamp ts = new Timestamp(millis);
                    Optional.ofNullable(map.get(SourceConstants.TIMESTAMP_NANOSECOND))
                            .map(String::valueOf)
                            .map(Integer::parseInt)
                            .ifPresent(ts::setNanos);
                    return ts;
                }).orElse(null);

        return new TimeStampOffset(timestamp);
    }
}
