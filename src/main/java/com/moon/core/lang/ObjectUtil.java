package com.moon.core.lang;

import com.moon.core.lang.invoke.LambdaUtil;
import com.moon.core.util.function.SerializableFunction;

import java.util.Objects;
import java.util.function.Function;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
public final class ObjectUtil {

    private final static String NULL = null;

    private ObjectUtil() { noInstanceError(); }

    public static <T> T defaultIfNull(T obj, T defaultValue) { return obj == null ? defaultValue : obj; }

    public static boolean equals(Object o1, Object o2) { return Objects.equals(o1, o2); }

    public static <T> boolean equalsProperties(T o1, T o2, Function<? super T, Object>... propertiesGetter) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null || o2 == null) {
            return false;
        }
        int length = propertiesGetter == null ? 0 : propertiesGetter.length;
        if (length > 0) {
            for (Function<? super T, Object> getter : propertiesGetter) {
                if (!Objects.equals(getter.apply(o1), getter.apply(o2))) {
                    return false;
                }
            }
            return true;
        }
        throw new IllegalStateException("没有任何可比较的属性");
    }

    public static <T> String toString(T object, SerializableFunction<T, Object>... getters) {
        if (object == null) {
            return "null";
        }
        StringBuilder builder = new StringBuilder(getters.length * 16);
        builder.append(object.getClass().getSimpleName()).append("{");
        for (SerializableFunction<T, Object> getter : getters) {
            Object value = getter.apply(object);
            builder.append(LambdaUtil.getPropertyName(getter)).append("=");
            if (value instanceof CharSequence) {
                builder.append("'").append(value).append('\'');
            } else if (value instanceof Number || value instanceof Boolean) {
                builder.append(value);
            } else if (value == null) {
                builder.append(NULL);
            } else {
                builder.append("{").append(value).append('}');
            }
            builder.append(", ");
        }
        int length = builder.length();
        return builder.delete(length - 2, length).append("}").toString();
    }

    @SafeVarargs
    public static <T> String toStringAsJson(T object, SerializableFunction<T, Object>... getters) {
        if (object == null) {
            return "null";
        }
        StringBuilder builder = new StringBuilder(getters.length * 16);
        builder.append("{\"@type\":\"").append(object.getClass().getName()).append("\",");
        for (SerializableFunction<T, Object> getter : getters) {
            Object value = getter.apply(object);
            builder.append('"').append(LambdaUtil.getPropertyName(getter)).append("\":");
            if (value instanceof CharSequence) {
                builder.append('"').append(value).append('"');
            } else if (value instanceof Number || value instanceof Boolean) {
                builder.append(value);
            } else if (value == null) {
                builder.append(NULL);
            } else if (value instanceof Iterable) {
                Iterable iterable = (Iterable) value;
                for (Object o : iterable) {
                    builder.append(o).append(',');
                }
                builder.deleteCharAt(builder.length() - 1);
            } else {
                builder.append(value);
            }
            builder.append(",");
        }
        int length = builder.length();
        return builder.deleteCharAt(length - 1).append("}}").toString();
    }

    public static boolean contentEquals(ContentEquals a, ContentEquals b) {
        return (a == b) || (a != null && a.contentEquals(b));
    }

    public static <T> T getInitializeValue(Class<T> type) {
        if (type == null) { return null; }
        if (type.isPrimitive()) {
            if (type == int.class) { return (T) Integer.valueOf(0); }
            if (type == long.class) { return (T) Long.valueOf(0); }
            if (type == double.class) { return (T) Double.valueOf(0); }
            if (type == boolean.class) { return (T) Boolean.FALSE; }
            if (type == byte.class) { return (T) Byte.valueOf((byte) 0); }
            if (type == short.class) { return (T) Short.valueOf((short) 0); }
            if (type == float.class) { return (T) Float.valueOf(0); }
            if (type == char.class) { return (T) Character.valueOf(' '); }
        }
        return null;
    }
}
