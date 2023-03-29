package com.moon.core.util;

/**
 * @author benshaoye
 */
public interface Storage<K, V> {

    /**
     * 缓存一个值
     *
     * @param key   键
     * @param value 值
     */
    void set(K key, V value);

    /**
     * 获取缓存的值
     *
     * @param key 键
     *
     * @return 对应的值或 null
     */
    V get(K key);

    /**
     * 是否存在缓存过的值
     * @param key 键
     * @return 是否存在缓存过的值
     */
    boolean hasKey(K key);

    /**
     * 删除一个缓存过的值
     *
     * @param key 将要删除的键
     */
    void remove(K key);
}
