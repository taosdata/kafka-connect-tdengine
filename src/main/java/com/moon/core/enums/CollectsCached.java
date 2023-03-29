package com.moon.core.enums;

import com.moon.core.util.CollectUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author moonsky
 */
final class CollectsCached {

    private final static Map<Class, Collects> CACHE = new HashMap<>();

    static void put(Class type, Collects collects) {
        CACHE.put(type, collects);
    }

    static Collects[] toValuesArr() {
        return CollectUtil.toArray(CACHE.values(), Collects[]::new);
    }

    static Collects get(Class type) {
        return CACHE.get(type);
    }

    static Collects getOrDefault(Class type, Collects defaultVal) {
        return CACHE.getOrDefault(type, defaultVal);
    }
}
