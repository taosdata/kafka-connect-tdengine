package com.taosdata.kafka.connect.source;

import com.google.common.collect.Maps;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.Map;

/**
 *
 */
class TimeStampOffsetTest {

    @Test
    void fromMap() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Map<String, Object> map = Maps.newHashMap();
        map.put(SourceConstants.TIMESTAMP_MILLISECOND, timestamp.getTime());
        map.put(SourceConstants.TIMESTAMP_NANOSECOND,  timestamp.getNanos());
        TimeStampOffset offset = TimeStampOffset.fromMap(map);
        Assertions.assertEquals(timestamp.getTime(), offset.getTimestampOffset().getTime());
        Assertions.assertEquals(timestamp.getNanos(), offset.getTimestampOffset().getNanos());
    }
}