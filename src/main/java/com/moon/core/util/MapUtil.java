package com.moon.core.util;

import com.moon.core.lang.ArrayUtil;
import com.moon.core.lang.ThrowUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import static com.moon.core.util.TypeUtil.cast;

/**
 * @author moonsky
 */
public final class MapUtil {

    private MapUtil() { ThrowUtil.noInstanceError(); }

    public static Map immutableEmpty() { return EmptyHashMap.EMPTY_MAP; }

    public static <K, V> Map<K, V> immutableIfEmpty(Map<K, V> map) { return isEmpty(map) ? immutableEmpty() : map; }

    public static <K, V> Map<K, V> immutableIfNull(Map<K, V> map) { return map == null ? immutableEmpty() : map; }

    public static <K, V> Map<K, V> newIfNull(Map<K, V> map) { return emptyHashMapIfNull(map); }

    public static <K, V> Map<K, V> newIfEmpty(Map<K, V> map) { return isEmpty(map) ? newHashMap() : map; }

    public static <K, V> Map<K, V> newMap() { return newHashMap(); }

    public static <K, V> Map<K, V> newMap(K key, V value) { return put(null, key, value); }

    public static <K, V> Map<K, V> newMap(K key, V value, K key1, V value1) {
        return put(newMap(key, value), key1, value1);
    }

    public static <K, V, M extends Map<K, V>> M fillAs(M map, V value, Iterable<? extends K> keys) {
        if (keys != null) {
            for (K key : keys) {
                map.put(key, value);
            }
        }
        return map;
    }

    public static <K, V, M extends Map<K, V>> M fillAs(M map, V value, K... keys) {
        return ArrayUtil.isNotEmpty(keys) ? map : fillAs(map, value, Arrays.asList(keys));
    }

    /*
     * ---------------------------------------------------------------------------------
     * of hash map
     * ---------------------------------------------------------------------------------
     */

    public static <K, V> HashMap<K, V> newHashMap() { return new HashMap<>(); }

    public static <K, V> HashMap<K, V> newHashMap(int capacity) { return new HashMap<>(capacity); }

    public static <K, V> HashMap<K, V> newHashMap(Map<K, V> map) { return new HashMap<>(map); }

    public static <K, V> HashMap<K, V> newHashMap(Map<K, V> map, Map<K, V>... maps) {
        return putAll(putAll(newHashMap((maps.length + 1) * 16), map), maps);
    }

    public static <K, V> Map<K, V> emptyHashMapIfNull(Map<K, V> map) { return map == null ? newHashMap() : map; }

    /*
     * ---------------------------------------------------------------------------------
     * of linked hash map
     * ---------------------------------------------------------------------------------
     */

    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap() { return new LinkedHashMap<>(); }

    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(int capacity) { return new LinkedHashMap(capacity); }

    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(Map<K, V> map) { return new LinkedHashMap<>(map); }

    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(Map<K, V> map, Map<K, V>... maps) {
        return putAll(putAll(newLinkedHashMap((maps.length + 1) * 16), map), maps);
    }

    public static <K, V> Map<K, V> emptyLinkedHashMapIfNull(Map<K, V> map) {
        return map == null ? newLinkedHashMap() : map;
    }

    /*
     * ---------------------------------------------------------------------------------
     * of tree hash map
     * ---------------------------------------------------------------------------------
     */

    public static <K, V> TreeMap<K, V> newTreeMap() { return new TreeMap<>(); }

    public static <K, V> TreeMap<K, V> newTreeMap(int capacity) { return newTreeMap(); }

    public static <K, V> TreeMap<K, V> newTreeMap(Map<K, V> map) { return new TreeMap<>(map); }

    public static <K, V> TreeMap<K, V> newTreeMap(Map<K, V> map, Map<K, V>... maps) {
        return putAll(putAll(newTreeMap((maps.length + 1) * 16), map), maps);
    }

    public static <K, V> Map<K, V> emptyTreeMapIfNull(Map<K, V> map) { return map == null ? newTreeMap() : map; }

    /*
     * ---------------------------------------------------------------------------------
     * of concurrent hash map
     * ---------------------------------------------------------------------------------
     */

    public static <K, V> ConcurrentHashMap<K, V> newConcurrentMap() { return new ConcurrentHashMap<>(); }

    public static <K, V> ConcurrentHashMap<K, V> newConcurrentMap(int capacity) {
        return new ConcurrentHashMap(capacity);
    }

    public static <K, V> ConcurrentHashMap<K, V> newConcurrentMap(Map<K, V> map) {
        return new ConcurrentHashMap<>(map);
    }

    public static <K, V> ConcurrentHashMap<K, V> newConcurrentMap(Map<K, V> map, Map<K, V>... maps) {
        return putAll(putAll(newConcurrentMap((maps.length + 1) * 16), map), maps);
    }

    public static <K, V> Map<K, V> emptyConcurrentMapIfNull(Map<K, V> map) {
        return map == null ? newConcurrentMap() : map;
    }

    /*
     * ---------------------------------------------------------------------------------
     * assertions
     * ---------------------------------------------------------------------------------
     */

    public static boolean isEmpty(Map map) { return map == null || map.isEmpty(); }

    public static boolean isNotEmpty(Map map) { return !isEmpty(map); }

    public static int size(Map map) { return map == null ? 0 : map.size(); }

    public static int sizeByObject(Object map) { return map == null ? 0 : ((Map) map).size(); }

    public static int sizeOfAll(Map... maps) {
        int size = 0, i = 0;
        for (; i < maps.length; ) {
            size += size(maps[i++]);
        }
        return size;
    }

    /*
     * ---------------------------------------------------------------------------------
     * operators
     * ---------------------------------------------------------------------------------
     */

    /**
     * 获取 map 的值
     *
     * @param map map 对象
     * @param key 键名
     * @param <T> 键类型
     * @param <E> 值类型
     *
     * @return 对应的值或 null
     */
    public static <T, E> E get(Map<T, E> map, T key) { return map == null ? null : map.get(key); }

    /**
     * 获取 map 的值
     *
     * @param map map 对象
     * @param key 键名
     *
     * @return 对应的值或 null
     */
    public static Object getByObject(Object map, Object key) { return map == null ? null : ((Map) map).get(key); }

    /**
     * 返回非空 map
     *
     * @param map   原始 map
     * @param key   键名
     * @param value 值
     * @param <K>   键数据类型
     * @param <V>   值数据类型
     * @param <M>   Map 类型
     *
     * @return 非 null Map
     */
    public static <K, V, M extends Map<K, V>> M put(M map, K key, V value) {
        map = (M) newIfNull(map);
        map.put(key, value);
        return map;
    }

    /**
     * 忽略类型兼容，数据一定能放进 map 里面；
     * 如果数据类型与 map 不一致，可能会导致处理返回数据的过程中出现异常；
     *
     * @param map   map
     * @param key   key
     * @param value value
     * @param <K>   key 数据类型
     * @param <V>   value 数据类型
     * @param <M>   map 数据类型
     *
     * @return 容器 map
     *
     * @throws NullPointerException 如果 map 为 null
     */
    public static <K, V, M extends Map<K, V>> M putObjectToMap(M map, Object key, Object value) {
        map.put((K) key, (V) value);
        return map;
    }

    /**
     * 忽略类型兼容
     *
     * @param map   map
     * @param key   key
     * @param value value
     * @param <K>   key 数据类型
     * @param <V>   value 数据类型
     *
     * @return 容器 map
     *
     * @throws NullPointerException 如果 map 为 null
     */
    public static <K, V> Map<K, V> putToObjectMap(Object map, Object key, Object value) {
        return putObjectToMap((Map) map, key, value);
    }

    /**
     * 忽略类型兼容，elements 里面的数据一定能放进 map；
     * 如果 elements 数据类型与 map 不一致，可能会导致处理返回数据的过程中出现异常；
     *
     * @param map      结果容器map
     * @param elements 待处理 map 集合
     * @param <K>      键数据类型
     * @param <V>      值数据类型
     * @param <M>      map 集合类型
     *
     * @return 容器 map 集合
     *
     * @throws NullPointerException 如果 map 为 null
     */
    public static <K, V, M extends Map<K, V>> M putAll(M map, Map elements) {
        map.putAll(elements);
        return map;
    }

    /**
     * 合并多个 map
     *
     * @param map  结果容器map
     * @param maps 待处理 map 集合
     * @param <K>  键数据类型
     * @param <V>  值数据类型
     * @param <M>  map 集合类型
     *
     * @return 容器 map 集合
     *
     * @throws NullPointerException 如果 map 为 null
     */
    public static <K, V, M extends Map<K, V>> M putAll(M map, Map<? extends K, ? extends V>... maps) {
        for (Map<? extends K, ? extends V> value : maps) {
            map.putAll(value);
        }
        return map;
    }

    public static Object[] toArray(Map map) { return map == null ? null : map.values().toArray(); }

    /*
     * ---------------------------------------------------------------------------------
     * iterators
     * ---------------------------------------------------------------------------------
     */

    public static <K, V> Iterator<Map.Entry<K, V>> iterator(Map<K, V> map) { return IteratorUtil.of(map); }

    public static <K, V> Set<K> keys(Map<K, V> map) { return map == null ? SetUtil.empty() : map.keySet(); }

    public static <K, V> Collection<V> values(Map<K, V> map) { return map == null ? SetUtil.empty() : map.values(); }

    public static <K, V> Set<Map.Entry<K, V>> entries(Map<K, V> map) {
        return map == null ? SetUtil.empty() : map.entrySet();
    }

    /*
     * ---------------------------------------------------------------------------------
     * get to type or basic value
     * ---------------------------------------------------------------------------------
     */

    /**
     * 要求指定映射必须存在非 null 值
     *
     * @param map map 集合
     * @param key 映射名
     * @param <K> Map 键数据类型
     * @param <V> Map 值数据类型
     *
     * @return 指定键的返回值
     *
     * @throws NullPointerException 当 map == null 或 指定键对应的值不存在或者值为 null 时抛出异常
     */
    public static <K, V> V requireGet(Map<K, V> map, K key) {
        Objects.requireNonNull(map);
        return Objects.requireNonNull(map.get(key));
    }

    /**
     * 按指定类型获取 map 的值
     *
     * @param map
     * @param key
     * @param clazz
     * @param <T>
     * @param <E>
     * @param <C>
     */
    public static <T, E, C> C getAsType(Map<T, E> map, T key, Class<C> clazz) {
        if (map == null) {
            return null;
        }

        E e = map.get(key);

        if (e == null) {
            return null;
        }

        return cast().toType(e, clazz);
    }

    public static <K> String getString(Map<K, ?> map, K key) {
        return map == null ? null : cast().toString(map.get(key));
    }

    public static <K> Boolean getBoolean(Map<K, ?> map, K key) {
        return map == null ? null : cast().toBoolean(map.get(key));
    }

    public static <K> boolean getBooleanValue(Map<K, ?> map, K key) {
        return map != null && cast().toBooleanValue(map.get(key));
    }

    public static <K> Double getDouble(Map<K, ?> map, K key) {
        return map == null ? null : cast().toDouble(map.get(key));
    }

    public static <K> double getDoubleValue(Map<K, ?> map, K key) {
        return map == null ? 0 : cast().toDoubleValue(map.get(key));
    }

    public static <K> Float getFloat(Map<K, ?> map, K key) { return map == null ? null : cast().toFloat(map.get(key)); }

    public static <K> float getFloatValue(Map<K, ?> map, K key) {
        return map == null ? 0 : cast().toFloatValue(map.get(key));
    }

    public static <K> Long getLong(Map<K, ?> map, K key) { return map == null ? null : cast().toLong(map.get(key)); }

    public static <K> long getLongValue(Map<K, ?> map, K key) {
        return map == null ? 0 : cast().toLongValue(map.get(key));
    }

    public static <K> Integer getInteger(Map<K, ?> map, K key) {
        return map == null ? null : cast().toInteger(map.get(key));
    }

    public static <K> int getIntValue(Map<K, ?> map, K key) {
        return map == null ? 0 : cast().toIntValue(map.get(key));
    }

    public static <K> Short getShort(Map<K, ?> map, K key) { return map == null ? null : cast().toShort(map.get(key)); }

    public static <K> short getShortValue(Map<K, ?> map, K key) {
        return map == null ? 0 : cast().toShortValue(map.get(key));
    }

    public static <K> Byte getByte(Map<K, ?> map, K key) { return map == null ? null : cast().toByte(map.get(key)); }

    public static <K> byte getByteValue(Map<K, ?> map, K key) {
        return map == null ? 0 : cast().toByteValue(map.get(key));
    }

    public static <K> Character getCharacter(Map<K, ?> map, K key) {
        return map == null ? null : cast().toCharacter(map.get(key));
    }

    public static <K> char getCharValue(Map<K, ?> map, K key) {
        return map == null ? 0 : cast().toCharValue(map.get(key));
    }

    /*
     * ---------------------------------------------------------------------------------
     * default getSheet invalid
     * ---------------------------------------------------------------------------------
     */

    public static <K> char getOrDefault(Map<K, ?> map, K key, char defaultVal) {
        Object value;
        return map == null ? defaultVal : ((value = map.get(key)) instanceof Character ? (Character) value : defaultVal);
    }

    public static <K> double getOrDefault(Map<K, ?> map, K key, double defaultVal) {
        Object value;
        return map == null ? defaultVal : ((value = map.get(key)) instanceof Number ? ((Number) value).doubleValue() : defaultVal);
    }

    public static <K> float getOrDefault(Map<K, ?> map, K key, float defaultVal) {
        Object value;
        return map == null ? defaultVal : ((value = map.get(key)) instanceof Number ? ((Number) value).floatValue() : defaultVal);
    }

    public static <K> long getOrDefault(Map<K, ?> map, K key, long defaultVal) {
        Object value;
        return map == null ? defaultVal : ((value = map.get(key)) instanceof Number ? ((Number) value).longValue() : defaultVal);
    }

    public static <K> int getOrDefault(Map<K, ?> map, K key, int defaultVal) {
        Object value;
        return map == null ? defaultVal : ((value = map.get(key)) instanceof Number ? ((Number) value).intValue() : defaultVal);
    }

    public static <K> short getOrDefault(Map<K, ?> map, K key, short defaultVal) {
        Object value;
        return map == null ? defaultVal : ((value = map.get(key)) instanceof Number ? ((Number) value).shortValue() : defaultVal);
    }

    public static <K> byte getOrDefault(Map<K, ?> map, K key, byte defaultVal) {
        Object value;
        return map == null ? defaultVal : ((value = map.get(key)) instanceof Number ? ((Number) value).byteValue() : defaultVal);
    }

    public static <K> boolean getOrDefault(Map<K, ?> map, K key, boolean defaultVal) {
        Object value;
        return map == null ? defaultVal : (value = map.get(key)) instanceof Boolean ? (Boolean) value : defaultVal;
    }

    /**
     * Object value = map.getSheet(key);
     * 如果 value 不是一个 boolean 型数据或 map == null，返回 true，否则返回 value
     *
     * @param map
     * @param key
     *
     * @return
     */
    public static <K> boolean getOrTrue(Map<K, ?> map, K key) { return getOrDefault(map, key, true); }

    /**
     * Object value = map.getSheet(key);
     * 如果 value 不是一个 boolean 型数据或 map == null，返回 false，否则返回 value
     *
     * @param map
     * @param key
     *
     * @return
     */
    public static <K> boolean getOrFalse(Map<K, ?> map, K key) { return getOrDefault(map, key, false); }

    public static <K> String getOrDefault(Map<K, ?> map, K key, String defaultVal) {
        Object value;
        return map == null ? defaultVal : (value = map.get(key)) instanceof CharSequence ? value.toString() : defaultVal;
    }

    public static <K, V> V getOrDefault(Map<K, V> map, K key, V defaultVal) {
        return map == null ? defaultVal : map.getOrDefault(key, defaultVal);
    }

    public static <K, V> V getOrElse(Map<K, V> map, K key, Supplier<V> supplier) {
        V value;
        return map == null ? supplier.get() : (value = map.get(key)) == null ? supplier.get() : value;
    }
}
