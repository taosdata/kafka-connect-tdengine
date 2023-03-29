package com.moon.core.lang;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author moonsky
 */
public class MoonConfig {

    private final Map<Class, Map> cacheMap = new HashMap<>();

    private boolean cacheLoadedProperties = false;

    public boolean isCacheLoadedProperties() {
        return cacheLoadedProperties;
    }

    public synchronized void clear() {
        cacheMap.forEach((type, map) -> map.clear());
    }

    public void clear(Class type) {
        Map cache = cacheMap.get(type);
        if (cache != null) {
            synchronized (this) {
                cache.clear();
            }
        }
    }

    public Map get(Class type, Supplier<Map> supplier) {
        Map cache = cacheMap.get(type);
        if (cache == null) {
            synchronized (this) {
                cache = cacheMap.get(type);
                if (cache == null) {
                    cacheMap.put(type, cache = supplier.get());
                }
            }
        }
        return cache;
    }

    void override(MoonConfig config) {
        this.cacheLoadedProperties = config.cacheLoadedProperties;
    }
}
