package com.moon.core.util;

import com.moon.core.lang.ThrowUtil;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Collections.emptySet;
import static java.util.Objects.requireNonNull;

/**
 * @author moonsky
 */
public final class EmptyHashMap
    extends PropertiesHashMap
    implements Serializable {

    private static final long serialVersionUID = 6428348081105594320L;

    final static EmptyHashMap INSTANCE = new EmptyHashMap();
    final static EmptyHashMap EMPTY_MAP = INSTANCE;
    final static EmptyHashMap DEFAULT = INSTANCE;

    private EmptyHashMap() {
        if (INSTANCE != null) {
            synchronized (EmptyHashMap.class) {
                if (INSTANCE != null) {
                    ThrowUtil.noInstanceError();
                }
            }
        }
    }

    public final static Map getInstance() { return EMPTY_MAP; }

    @Override
    public int size() { return 0; }

    @Override
    public boolean isEmpty() { return true; }

    @Override
    public boolean containsKey(Object key) { return false; }

    @Override
    public boolean containsValue(Object value) { return false; }

    @Override
    public String get(Object key) { return null; }

    @Override
    public Set<String> keySet() { return emptySet(); }

    @Override
    public Collection<String> values() { return emptySet(); }

    @Override
    public Set<Map.Entry<String, String>> entrySet() { return emptySet(); }

    @Override
    public boolean equals(Object o) { return (o instanceof Map) && ((Map<?, ?>) o).isEmpty(); }

    @Override
    public int hashCode() { return 0; }

    @Override
    @SuppressWarnings("unchecked")
    public String getOrDefault(Object k, String defaultValue) { return defaultValue; }

    @Override
    public void forEach(BiConsumer<? super String, ? super String> action) { requireNonNull(action); }

    @Override
    public void replaceAll(BiFunction<? super String, ? super String, ? extends String> function) { requireNonNull(function); }

    @Override
    public String putIfAbsent(String key, String value) { return null; }

    @Override
    public boolean remove(Object key, Object value) { return false; }

    @Override
    public boolean replace(String key, String old, String now) { return false; }

    @Override
    public String replace(String key, String value) { return null; }

    @Override
    public String computeIfAbsent(String key, Function<? super String, ? extends String> mappingFunction) { return null; }

    @Override
    public String computeIfPresent(String key, BiFunction<? super String, ? super String, ? extends String> remappingFunction) { return null; }

    @Override
    public String compute(String key, BiFunction<? super String, ? super String, ? extends String> remappingFunction) { return null; }

    @Override
    public String merge(String key, String value, BiFunction<? super String, ? super String, ? extends String> remappingFunction) { return null; }
}
