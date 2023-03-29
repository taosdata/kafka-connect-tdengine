package com.moon.runner.core;

import com.moon.core.lang.BooleanUtil;
import com.moon.core.lang.ref.ReferenceUtil;

import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author moonsky
 */
abstract class DataConst<T> implements AsConst {

    final static AsConst NULL = DataNull.NULL;
    final static AsConst TRUE = DataBool.TRUE;
    final static AsConst FALSE = DataBool.FALSE;

    private final static Map<Object, AsConst> CACHE = ReferenceUtil.manageMap();
    private final static ReentrantLock LOCK = new ReentrantLock();

    protected final static AsConst getValue(Object key) { return CACHE.get(key); }

    protected final static AsConst putValue(Object key, AsConst value) {
        try {
            LOCK.lock();
            CACHE.put(key, value);
            return value;
        } finally {
            LOCK.unlock();
        }
    }

    final T value;

    protected DataConst(T value) { this.value = value; }

    @Override
    public Object run(Object data) { return value; }

    public T getValue() { return value; }

    @Override
    public boolean isNumber() { return value instanceof Number; }

    @Override
    public boolean isString() { return value instanceof CharSequence; }

    @Override
    public final String toString() { return String.valueOf(value); }

    public static final AsConst get(Object data) {
        if (data == null) {
            return DataConst.NULL;
        }
        if (data instanceof CharSequence) {
            return DataStr.valueOf(data.toString());
        }
        if (data instanceof Number) {
            return DataNum.valueOf((Number) data);
        }
        if (Boolean.TRUE.equals(data)) {
            return DataConst.TRUE;
        }
        if (Boolean.FALSE.equals(data)) {
            return DataConst.FALSE;
        }
        if (data instanceof AsConst) {
            return (AsConst) data;
        }
        return DataObj.valueOf(data);
    }

    public static final AsConst temp(Object data){
        if (data == null) {
            return DataConst.NULL;
        }
        if (data instanceof CharSequence) {
            return DataStr.tempStr(data.toString());
        }
        if (data instanceof Number) {
            return DataNum.tempNum((Number) data);
        }
        if (Boolean.TRUE.equals(data)) {
            return DataConst.TRUE;
        }
        if (Boolean.FALSE.equals(data)) {
            return DataConst.FALSE;
        }
        if (data instanceof AsConst) {
            return (AsConst) data;
        }
        return DataObj.tempObj(data);
    }

    public static final AsConst getOpposite(DataConst data) {
        BooleanUtil.requireTrue(data instanceof DataNum);
        Number num = (Number) data.run(), value;
        if (num instanceof Double || num instanceof Float) {
            value = -num.doubleValue();
        } else if (num instanceof Long) {
            value = -num.longValue();
        } else {
            value = -num.intValue();
        }
        return DataNum.valueOf(value);
    }
}
