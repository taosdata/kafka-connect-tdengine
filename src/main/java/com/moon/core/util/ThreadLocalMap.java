package com.moon.core.util;

import com.moon.core.enums.Maps;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 本地线程可见的 Map
 * <p>
 * 依赖{@link ThreadLocal}实现的当前线程 Map
 *
 * @author moonsky
 */
public class ThreadLocalMap<K, V> extends ThreadLocal<Map<K, V>> implements Map<K, V> {

    private final static Supplier CONSTRUCTOR = Maps.HashMaps;

    private final Supplier<Map<K, V>> constructor;

    private Supplier<Map<K, V>> constructor() {
        return constructor == null ? CONSTRUCTOR : constructor;
    }

    private Map<K, V> getMap() {
        Map<K, V> m = get();
        if (m == null) {
            set(m = constructor().get());
        }
        return m;
    }

    public ThreadLocalMap() { this(CONSTRUCTOR); }

    public ThreadLocalMap(Supplier<Map<K, V>> constructor) {
        this.constructor = (constructor == null) ? CONSTRUCTOR : constructor;
    }

    public ThreadLocalMap(Map<K, V> map) { this(() -> new HashMap<>(map)); }

    @Override
    public int size() { return getMap().size(); }

    @Override
    public boolean isEmpty() { return getMap().isEmpty(); }

    @Override
    public boolean containsKey(Object key) { return getMap().containsKey(key); }

    @Override
    public boolean containsValue(Object value) { return getMap().containsValue(value); }

    @Override
    public V get(Object key) { return getMap().get(key); }

    @Override
    public V put(K key, V value) { return getMap().put(key, value); }

    @Override
    public V remove(Object key) { return getMap().remove(key); }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) { getMap().putAll(m); }

    @Override
    public void clear() { getMap().clear(); }

    @Override
    public Set<K> keySet() { return getMap().keySet(); }

    @Override
    public Collection<V> values() { return getMap().values(); }

    @Override
    public Set<Entry<K, V>> entrySet() { return getMap().entrySet(); }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) { this.getMap().forEach(action); }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return getMap().merge(key, value, remappingFunction);
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return getMap().compute(key, remappingFunction);
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return getMap().computeIfAbsent(key, mappingFunction);
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return getMap().computeIfPresent(key, remappingFunction);
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) { return getMap().getOrDefault(key, defaultValue); }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) { getMap().replaceAll(function); }

    @Override
    public V putIfAbsent(K key, V value) {
        return getMap().putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return getMap().remove(key, value);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return getMap().replace(key, oldValue, newValue);
    }

    @Override
    public V replace(K key, V value) {
        return getMap().replace(key, value);
    }

    @Override
    public Map<K, V> get() {
        Object cached = super.get();
        if (cached instanceof Map) {
            return (Map<K, V>) cached;
        }
        if (cached == null) {
            return null;
        }
        throw new IllegalArgumentException();
    }

    @Override
    public void remove() { super.remove(); }
}
