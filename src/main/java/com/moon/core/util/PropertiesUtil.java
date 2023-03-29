package com.moon.core.util;

import com.moon.core.lang.JoinerUtil;
import com.moon.core.util.support.PropertiesSupport;

import java.util.Map;

import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static com.moon.core.util.TypeUtil.cast;

/**
 * @author moonsky
 */
public final class PropertiesUtil {

    private PropertiesUtil() { noInstanceError(); }

    public static final void refreshAll() { PropertiesSupport.refreshAll(); }

    public static final PropertiesGroup group(Map<? extends String, ?> properties) {
        return PropertiesGroup.of(properties);
    }

    /*
     * -----------------------------------------------------------
     * get parser
     * -----------------------------------------------------------
     */

    /**
     * properties 文件解析器
     *
     * @return 返回解析器
     *
     * @see PropertiesParser
     */
    public static final PropertiesParser parser() { return new PropertiesParser(); }

    /**
     * properties 文件解析器
     *
     * @param namespace
     *
     * @return 返回解析器
     *
     * @see PropertiesParser
     */
    public static final PropertiesParser parser(String namespace) { return new PropertiesParser(namespace); }

    /*
     * -----------------------------------------------------------
     * get properties
     * -----------------------------------------------------------
     */

    /**
     * get all properties
     *
     * @param path resources path or url
     *
     * @return 返回指定资源的配置文件
     */
    public static final Map<String, String> getOrEmpty(String path) { return PropertiesSupport.getOrEmpty(path); }

    public static final Map<String, String> getOrNull(String path) { return PropertiesSupport.getOrNull(path); }

    public static final Map<String, String> get(String path) { return PropertiesSupport.getOrLoad(path); }

    /*
     * -----------------------------------------------------------
     * get value
     * -----------------------------------------------------------
     */

    public static final String getString(String path, String key) { return get(path).get(key); }

    public static final int getIntValue(String path, String key) { return cast().toIntValue(getString(path, key)); }

    public static final long getLongValue(String path, String key) { return cast().toLongValue(getString(path, key)); }

    public static final double getDoubleValue(String path, String key) {
        return cast().toDoubleValue(getString(path,
            key));
    }

    public static final boolean getBooleanValue(String path, String key) {
        return cast().toBooleanValue(getString(path,
            key));
    }

    /*
     * -----------------------------------------------------------
     * get or default
     * -----------------------------------------------------------
     */

    public static final String getOrDefault(String path, String key, String defaultVal) {
        Map<String, String> map = getOrNull(path);
        return (map != null && (key = map.get(key)) != null) ? key : defaultVal;
    }

    public static final int getOrDefault(String path, String key, int defaultVal) {
        Map<String, String> map = getOrNull(path);
        return (map != null && (key = map.get(key)) != null) ? cast().toIntValue(key) : defaultVal;
    }

    public static final long getOrDefault(String path, String key, long defaultVal) {
        Map<String, String> map = getOrNull(path);
        return (map != null && (key = map.get(key)) != null) ? cast().toLongValue(key) : defaultVal;
    }

    public static final double getOrDefault(String path, String key, double defaultVal) {
        Map<String, String> map = getOrNull(path);
        return (map != null && (key = map.get(key)) != null) ? cast().toDoubleValue(key) : defaultVal;
    }

    public static final boolean getOrDefault(String path, String key, boolean defaultVal) {
        Map<String, String> map = getOrNull(path);
        return (map != null && (key = map.get(key)) != null) ? cast().toBooleanValue(key) : defaultVal;
    }

    public static final boolean getOrTrue(String path, String key) { return getOrDefault(path, key, true); }

    public static final boolean getOrFalse(String path, String key) { return getOrDefault(path, key, false); }

    public static final int getOrZero(String path, String key) { return getOrDefault(path, key, 0); }

    public static final int getOrOne(String path, String key) { return getOrDefault(path, key, 1); }

    /*
     * -----------------------------------------------------------
     * get value
     * -----------------------------------------------------------
     */

    public static final String getString(String path, String... vars) {
        return get(path).get(JoinerUtil.join(vars, "."));
    }
}
