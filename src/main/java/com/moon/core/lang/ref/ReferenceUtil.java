package com.moon.core.lang.ref;

import com.moon.core.lang.ThrowUtil;

import java.lang.ref.*;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Supplier;

/**
 * @author moonsky
 */
public final class ReferenceUtil {

    private ReferenceUtil() { ThrowUtil.noInstanceError(); }

    /**
     * 返回引用的值，如果值为 null，则执行 supplier，并返回 supplier 的返回值
     * 当 supplier 的返回值本来就是 null 时，这儿用引用值为 null 判断并不严谨
     * 实际上用 isEnqueued 判断更为妥当，但 isEnqueued 只有在有队列的情况下，
     * 被回收之后才会为 true，否则无论是否被回收均为 false
     *
     * @param reference
     * @param supplier
     * @param <T>
     * @return
     */
    public static <T> T getOrDefault(Reference<T> reference, Supplier<T> supplier) {
        if (reference != null) {
            T ret = reference.get();
            if (ret != null) {
                return ret;
            }
        }
        return supplier.get();
    }

    public static <T> T getOrDefault(Reference<T> reference, T defaultValue) {
        if (reference != null) {
            T ret = reference.get();
            if (ret != null) {
                return ret;
            }
        }
        return defaultValue;
    }

    public static <T> Reference<T> ref(T data) { return weak(data); }

    public static <T> Reference<T> ref(T data, ReferenceQueue<? extends T> queue) { return weak(data, queue); }

    public static <T> Reference<T> weak(T obj) { return new WeakReference<>(obj); }

    public static <T> Reference<T> weak(T obj, ReferenceQueue<? extends T> queue) {
        return new WeakReference(obj, queue);
    }

    public static <T> Reference<T> soft(T obj) { return new SoftReference<>(obj); }

    public static <T> Reference<T> soft(T obj, ReferenceQueue<? extends T> queue) {
        return new SoftReference(obj, queue);
    }

    public static <T> PhantomReference<T> phantom(T data, ReferenceQueue<? extends T> queue) {
        return new PhantomReference(data, queue);
    }

    public static <K, V> WeakHashMap<K, V> weakMap() { return new WeakHashMap<>(); }

    public static <K, V> WeakHashMap<K, V> weakMap(int initCapacity) { return new WeakHashMap<>(initCapacity); }

    public static <K, V> WeakHashMap<K, V> weakMap(Map<? extends K, ? extends V> m) { return new WeakHashMap<>(m); }

    public static <K, V> WeakHashMap<K, V> manageMap() { return WeakMapManager.manage(weakMap()); }

    public static <K, V> WeakHashMap<K, V> manageMap(int initCapacity) {
        return WeakMapManager.manage(weakMap(initCapacity));
    }

    public static <K, V> WeakHashMap<K, V> manageMap(Map<? extends K, ? extends V> m) {
        return WeakMapManager.manage(weakMap(m));
    }
}
