package com.moon.core.util.support;

import com.moon.core.io.IOUtil;
import com.moon.core.lang.MoonConfig;
import com.moon.core.lang.MoonUtil;
import com.moon.core.lang.ThrowUtil;
import com.moon.core.util.IteratorUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.String.valueOf;

/**
 * @author moonsky
 */
public final class PropertiesSupport {

    private PropertiesSupport() { ThrowUtil.noInstanceError(); }

    private static final MoonConfig config = MoonUtil.getMoonConfig();

    private static final Map<String, HashMap<String, String>> CACHE

        = config.get(PropertiesSupport.class, ConcurrentHashMap::new);

    public static final void refreshAll() {
        CACHE.forEach((key, item) -> CACHE.compute(key, (k, v) -> {
            synchronized (PropertiesSupport.class) {
                return load(k);
            }
        }));
    }

    public static final Map<String, String> getOrNull(String path) {
        try {
            return getOrLoad(path);
        } catch (Throwable t) {
            return null;
        }
    }

    public static final Map<String, String> getOrEmpty(String path) {
        try {
            return getOrLoad(path);
        } catch (Throwable t) {
            return Collections.EMPTY_MAP;
        }
    }

    public static final Map<String, String> getOrLoad(String path) {
        HashMap<String, String> hashMap = CACHE.get(path);
        if (hashMap == null) {
            synchronized (PropertiesSupport.class) {
                if ((hashMap = CACHE.get(path)) == null) {
                    hashMap = load(path);
                }
            }
        }
        return Collections.unmodifiableMap(hashMap);
    }

    private static final HashMap<String, String> load(String path) {
        InputStream stream = IOUtil.getResourceAsStream(path);
        Properties properties = new Properties();
        try {
            properties.load(stream);
            HashMap<String, String> hashMap = toHashMap(properties);
            if (config.isCacheLoadedProperties()) {
                CACHE.put(path, hashMap);
            }
            return hashMap;
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static final HashMap<String, String> toHashMap(Map map) {
        HashMap<String, String> hashMap = new HashMap<>(map == null ? 16 : map.size());
        IteratorUtil.forEach(map, (key, value) -> hashMap.put(valueOf(key), valueOf(value)));
        return hashMap;
    }
}
