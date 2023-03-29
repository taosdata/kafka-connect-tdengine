package com.moon.core.util;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author moonsky
 */
public final class NothingMap<K, V> extends AbstractMap<K, V> implements Map<K, V> {

    public static final NothingMap DEFAULT = new NothingMap();

    public NothingMap() { }

    public NothingMap(Object value) { }

    public NothingMap(Object value, Object... values) { }

    public static final <K, V> NothingMap<K, V> getInstance() { return DEFAULT; }

    @Override
    public int size() { return 0; }

    @Override
    public boolean isEmpty() { return true; }

    @Override
    public boolean containsValue(Object value) { return false; }

    @Override
    public boolean containsKey(Object key) { return false; }

    @Override
    public V get(Object key) { return null; }

    @Override
    public V put(K key, V value) { return null; }

    @Override
    public V remove(Object key) { return null; }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) { }

    @Override
    public void clear() { }

    @Override
    public Set<K> keySet() { return new HashSet<>(); }

    @Override
    public Collection<V> values() { return new ArrayList<>(); }

    @Override
    public boolean equals(Object o) { return o == this; }

    @Override
    public int hashCode() { return System.identityHashCode(this); }

    @Override
    public String toString() { return getClass().getSimpleName() + "{}"; }

    @Override
    protected Object clone() { return new NothingMap<>(); }

    @Override
    public V getOrDefault(Object key, V defaultValue) { return defaultValue; }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) { }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) { }

    @Override
    public V putIfAbsent(K key, V value) { return null; }

    @Override
    public boolean remove(Object key, Object value) { return false; }

    @Override
    public boolean replace(K key, V oldValue, V newValue) { return false; }

    @Override
    public V replace(K key, V value) { return null; }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) { return null; }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) { return null; }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) { return null; }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) { return null; }

    @Override
    public Set<Entry<K, V>> entrySet() { return new HashSet<>(); }
}
