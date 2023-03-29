package com.moon.runner;

import com.moon.core.beans.BeanInfoUtil;
import com.moon.core.beans.FieldDescriptor;
import com.moon.core.enums.ArrayOperator;
import com.moon.core.enums.Arrays2;
import com.moon.core.util.IteratorUtil;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * 大多数实现并没有意义，只是为了保证正常运行是不会报错
 * 传入的数据默认是不可变数据
 * 【注意】:本类的使用场景是有针对性的，故在数据的严谨性上做了一定妥协，比如：
 * 如果迭代每一个键值对，此对象会忠诚的返回每一对映射关系，
 * 但是如果计算 size 或者判断 isEmpty 等，如果存在后值键覆盖前值键的情况
 * size 将小于能迭代的项
 * 传入的参数是不可变的，故不能进行 reset 、increment 、remove 等操作
 *
 * @author moonsky
 */
public class RunnerDataMap extends HashMap {
    private final Object[] dataArr;
    private final Map[] valuers;
    private final int lastIndex;
    private final int length;

    public RunnerDataMap() { this(Arrays2.OBJECTS.empty()); }

    public RunnerDataMap(Object... dataArr) {
        dataArr = Arrays2.OBJECTS.emptyIfNull(dataArr);
        int length = this.length = dataArr.length;
        this.dataArr = dataArr;
        this.lastIndex = length - 1;
        this.valuers = new Map[length];
    }

    @Override
    public Object get(Object key) {
        Object data = super.get(key);
        if (data != null) {
            return data;
        }
        if (super.containsKey(key)) {
            return null;
        } else {
            for (int i = lastIndex; i > -1; i--) {
                data = getValGetter(i).get(key);
                if (data != null) {
                    return data;
                }
            }
        }
        return null;
    }

    private HashSet otherKeys;

    public HashSet getOtherKeys() {
        if (otherKeys == null) {
            otherKeys = new HashSet();
            for (int i = 0; i < length; i++) {
                otherKeys.addAll(getValGetter(i).keySet());
            }
        }
        return otherKeys;
    }

    @Override
    public Set keySet() {
        Set keys = new HashSet(getOtherKeys());
        keys.addAll(super.keySet());
        return keys;
    }

    private ArrayList otherValues;

    public ArrayList getOtherValues() {
        if (otherValues == null) {
            otherValues = new ArrayList();
            for (int i = 0; i < length; i++) {
                otherValues.addAll(getValGetter(i).values());
            }
        }
        return otherValues;
    }

    @Override
    public Collection values() {
        Set values = new HashSet(getOtherValues());
        values.addAll(super.values());
        return values;
    }

    private HashSet<Entry> otherEntries;

    public HashSet<Entry> getOtherEntries() {
        if (otherEntries == null) {
            otherEntries = new HashSet();
            for (int i = 0; i < length; i++) {
                otherEntries.addAll(getValGetter(i).entrySet());
            }
        }
        return otherEntries;
    }

    @Override
    public Set<Entry> entrySet() {
        Set entries = new HashSet(getOtherEntries());
        entries.addAll(super.entrySet());
        return entries;
    }

    @Override
    public boolean containsKey(Object key) { return super.containsKey(key) || getOtherKeys().contains(key); }

    @Override
    public boolean containsValue(Object value) {
        return super.containsValue(value) || getOtherValues().contains(value);
    }

    @Override
    public int size() { return super.size() + getOtherEntries().size(); }

    @Override
    public boolean isEmpty() { return size() == 0; }

    @Override
    public void forEach(BiConsumer action) {
        super.forEach(action);
        getOtherEntries().forEach(entry -> action.accept(entry.getKey(), entry.getValue()));
    }

    /*
     * -----------------------------------------------------------
     * declaration
     * -----------------------------------------------------------
     */

    private Map getValGetter(int index) {
        Map getter = valuers[index];
        if (getter == null) {
            getter = valuers[index] = initGetter(dataArr[index]);
        }
        return getter;
    }

    private static Map initGetter(Object data) {
        if (data instanceof Map) {
            return (Map) data;
        }
        if (data instanceof List) {
            return new ListGetter(data);
        }
        if (data == null) {
            return NULL;
        }
        if (data.getClass().isArray()) {
            return new ArrayGetter(data);
        }
        return new FieldGetter(data);
    }

    interface DefaultMap<KEY, VALUE> extends Map<KEY, VALUE> {
        /**
         * 容器
         *
         * @return
         */
        Map<KEY, VALUE> getRes();

        /**
         * 大小
         *
         * @return
         */
        @Override
        default int size() { return getRes().size(); }

        /**
         * 是否为空
         *
         * @return
         */
        @Override
        default boolean isEmpty() { return getRes().isEmpty(); }

        /**
         * 检测是否包含键
         *
         * @param key
         * @return
         */
        @Override
        default boolean containsKey(Object key) { return getRes().containsKey(key); }

        /**
         * 检测是否包含值
         *
         * @param value
         * @return
         */
        @Override
        default boolean containsValue(Object value) { return getRes().containsValue(value); }

        /**
         * 获取
         *
         * @param key
         * @return
         */
        @Override
        default VALUE get(Object key) { return getRes().get(key); }

        /**
         * 放进
         *
         * @param key
         * @param value
         * @return
         */
        @Override
        default VALUE put(KEY key, VALUE value) { return getRes().put(key, value); }

        /**
         * 删除
         *
         * @param key
         * @return
         */
        @Override
        default VALUE remove(Object key) { return getRes().remove(key); }

        /**
         * 设置
         *
         * @param m
         */
        @Override
        default void putAll(Map m) { getRes().putAll(m); }

        /**
         * 清空
         */
        @Override
        default void clear() { getRes().clear(); }

        /**
         * 键结果集
         *
         * @return
         */
        @Override
        default Set keySet() { return getRes().keySet(); }

        /**
         * 值结果集
         *
         * @return
         */
        @Override
        default Collection values() { return getRes().values(); }

        /**
         * 映射节点集合
         *
         * @return
         */
        @Override
        default Set<Entry<KEY, VALUE>> entrySet() { return getRes().entrySet(); }
    }

    private final static DefaultMap NULL = () -> new HashMap<>(1);

    protected static class ListGetter implements DefaultMap {
        final List data;
        Map<Integer, Object> res;

        @Override
        public Map<Integer, Object> getRes() {
            if (res == null) {
                res = new HashMap<>();
                IteratorUtil.forEach(data, (item, index) -> res.put(index, item));
            }
            return res;
        }

        public ListGetter(Object data) { this.data = (List) data; }
    }

    protected static class ArrayGetter implements DefaultMap {
        final Object data;
        final int length;
        final ArrayOperator getter;

        public ArrayGetter(Object arr) {
            this.data = arr;
            getter = Arrays2.getOrObjects(arr);
            length = getter.length(arr);
        }

        Map map;

        @Override
        public Map getRes() {
            if (map == null) {
                Map map = this.map = new HashMap(length);
                getter.forEach(data, (item, index) -> map.put(index, item));
            }
            return map;
        }
    }

    protected static class FieldGetter implements DefaultMap {
        final Object data;
        final Class type;

        Map res;

        @Override
        public Map getRes() {
            if (res == null) {
                Map<String, FieldDescriptor> descriptorMap = BeanInfoUtil.getFieldDescriptorsMap(type);
                res = new HashMap(descriptorMap.size());
                descriptorMap.forEach((name, desc) ->
                    res.put(name, desc.getValueIfPresent(data, true)));
            }
            return res;
        }

        public FieldGetter(Object data) {
            this.data = data;
            this.type = data.getClass();
        }
    }
}
