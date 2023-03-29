package com.moon.core.util;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.stream.Stream;

import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static java.util.Optional.ofNullable;

/**
 * @author moonsky
 */
public abstract class UnsafeUtil {

    private final static java.util.Optional<Unsafe> UNSAFE;
    private final static Optional<Unsafe> UNSAFE0;

    static {
        Class type = Unsafe.class;
        Field[] fs = type.getDeclaredFields();
        Stream<Field> stream = Stream.of(fs).filter(f -> f.getType() == type);
        java.util.Optional<Field> optional = stream.findFirst();
        Unsafe unsafe = null;
        if (optional.isPresent()) {
            Field tf = optional.get();
            try {
                tf.setAccessible(true);
                unsafe = (Unsafe) tf.get(null);
            } catch (Exception e) {
                // ignore
            } finally {
                tf.setAccessible(false);
            }
        }
        UNSAFE = ofNullable(unsafe);
        UNSAFE0 = Optional.ofNullable(unsafe);
    }

    private UnsafeUtil() { noInstanceError(); }

    public static java.util.Optional<Unsafe> getAsUtilOptional() { return UNSAFE; }

    public static Optional<Unsafe> get() { return UNSAFE0; }

    public static Unsafe getUnsafe() { return get().get(); }

    public static <T> T nullableNewInstance(Class<T> type) {
        Optional<Unsafe> optional = get();
        if (optional.isPresent()) {
            try {
                return (T) optional.get().allocateInstance(type);
            } catch (InstantiationException e) {
                throw new IllegalStateException("Can not new instance of: " + type, e);
            }
        }
        return null;
    }

    public static <T> T requiredNewInstance(Class<T> type) {
        try {
            return (T) getUnsafe().allocateInstance(type);
        } catch (InstantiationException e) {
            throw new IllegalStateException("Can not new instance of: " + type, e);
        }
    }
}
