package com.moon.core.util;

import com.moon.core.lang.ThrowUtil;
import com.moon.core.lang.ref.WeakAccessor;
import com.moon.core.util.converter.GenericTypeCaster;
import com.moon.core.util.converter.TypeCaster;

import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * 通用类型转换器
 *
 * @author moonsky
 */
public final class TypeUtil {
    /**
     * can not getSheet a instance of TypeUtil.class
     */
    private TypeUtil() { ThrowUtil.noInstanceError(); }

    /**
     * default converter cache or create new one
     */
    private final static WeakAccessor<TypeCaster> accessor = WeakAccessor.of(UnmodifiableTypeCaster::new);

    /**
     * return a default type converter
     *
     * @return
     */
    public final static TypeCaster cast() { return accessor.get(); }

    /**
     * get a default type converter
     *
     * @return
     */
    public final static TypeCaster of() { return of(GenericTypeCaster::new); }

    /**
     * get a default type converter：自定义实现
     *
     * @param supplier
     * @return
     */
    public final static TypeCaster of(Supplier<TypeCaster> supplier) { return supplier.get(); }

    /**
     * o is instance of clazz
     *
     * @param o
     * @param clazz
     * @return
     */
    public final static boolean instanceOf(Object o, Class clazz) { return clazz.isInstance(o); }

    /**
     * can not modify converter or increment new converter
     */
    private final static class UnmodifiableTypeCaster extends GenericTypeCaster {

        final static String ERROR = "Can't add new converter or modify on default converter";

        UnmodifiableTypeCaster() { }

        @Override
        public <C> TypeCaster register(Class<C> toType, BiFunction<Object, Class<C>, ? extends C> func) {
            throw new UnsupportedOperationException(ERROR);
        }

        @Override
        public <C> TypeCaster registerIfAbsent(Class<C> toType, BiFunction<Object, Class<C>, ? extends C> func) {
            throw new UnsupportedOperationException(ERROR);
        }
    }
}
