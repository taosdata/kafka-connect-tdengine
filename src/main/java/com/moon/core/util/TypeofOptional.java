package com.moon.core.util;

import com.moon.core.util.function.NullableFunction;

import java.util.*;

/**
 * @author moonsky
 */
public enum TypeofOptional implements NullableFunction {
    /**
     * MoonOptional
     */
    MoonOptional(Optional.class, OptionalImpl.class, OptionalImpl.EMPTY.getClass()) {
        @Override
        public Object nullable(Object data) { return ((Optional) data).getOrNull(); }
    },
    UtilOptional(java.util.Optional.class) {
        @Override
        public Object nullable(Object data) { return ((java.util.Optional) data).orElse(null); }
    },
    UtilOptionalInt(OptionalInt.class) {
        @Override
        public Object nullable(Object data) {
            OptionalInt optional = (OptionalInt) data;
            return optional.isPresent() ? optional.getAsInt() : null;
        }
    },
    UtilOptionalLong(OptionalLong.class) {
        @Override
        public Object nullable(Object data) {
            OptionalLong optional = (OptionalLong) data;
            return optional.isPresent() ? optional.getAsLong() : null;
        }
    },
    UtilOptionalDouble(OptionalDouble.class) {
        @Override
        public Object nullable(Object data) {
            OptionalDouble optional = (OptionalDouble) data;
            return optional.isPresent() ? optional.getAsDouble() : null;
        }
    };

    TypeofOptional(Class... classes) {
        for (Class optionalClass : classes) {
            registerIfAbsent(optionalClass, this);
        }
    }

    public static void registerIfAbsent(Class optionalClass, NullableFunction nullable) {
        Cached.optionalTypeCached.putIfAbsent(optionalClass, nullable);
    }

    public static NullableFunction resolveByClass(Class type) {
        return Cached.optionalTypeCached.get(type);
    }

    public static NullableFunction resolveByObject(Object object) {
        return object == null ? null : resolveByClass(object.getClass());
    }

    public static Object resolveOrNull(Object object) {
        if (object == null) {
            return null;
        }
        NullableFunction fn = resolveByClass(object.getClass());
        return fn == null ? object : resolveOrNull(fn.nullable(object));
    }

    final static class Cached {

        final static Map<Class, NullableFunction> optionalTypeCached = new HashMap<>();
    }
}
