package com.moon.core.lang.ref;

import java.lang.ref.Reference;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.moon.core.lang.ObjectUtil.defaultIfNull;

/**
 * @author moonsky
 */
abstract class BaseAccessor<T, A extends BaseAccessor<T, A>> implements Accessor<T, A>, Supplier<T> {

    /**
     * 缓存取值过程
     */
    private final Supplier<T> supplier;
    /**
     * 是否允许 supplier 返回值为 null 值
     * <p>
     * 默认不允许
     */
    private final boolean notAllowNullValue;
    /**
     * 缓存对象
     */
    private volatile Reference<T> reference;

    protected BaseAccessor(Supplier<T> supplier, boolean allowNullValue, boolean initLoaded) {
        this.supplier = Objects.requireNonNull(supplier);
        this.notAllowNullValue = !allowNullValue;
        if (initLoaded) { reload(); }
    }

    /**
     * 子类实现引用对象
     *
     * @param value 引用值
     *
     * @return 目标引用
     */
    protected abstract Reference<T> reference(T value);

    /*
     * ------------------------------------------------------------
     * gets
     * ------------------------------------------------------------
     */

    /**
     * 获取值
     *
     * @return 返回目标值或 null
     */

    public final T getOrDefault(T obj) { return defaultIfNull(get(), obj); }

    public final T getOrElse(Supplier<T> supplier) {
        T curr = get();
        return curr == null ? supplier.get() : curr;
    }

    @Override
    public final T get() {
        T curr = getValue();
        if (curr == null) {
            curr = reload();
        }
        return curr;
    }

    private final T getValue() { return reference == null ? null : reference.get(); }

    /*
     * ------------------------------------------------------------
     * assertions
     * ------------------------------------------------------------
     */

    @Override
    public final boolean isPresent() { return get() != null; }

    @Override
    public final boolean isAbsent() { return get() == null; }

    @Override
    public A clear() {
        this.reference = null;
        return current();
    }

    public final boolean isEquals(Object obj) { return Objects.equals(obj, get()); }

    /*
     * ------------------------------------------------------------
     * consumers
     * ------------------------------------------------------------
     */

    public final A ifPresent(Consumer<T> consumer) {
        T curr = get();
        if (curr != null) {
            consumer.accept(curr);
        }
        return current();
    }

    public final A ifPresentOrThrow(Consumer<T> consumer) { return ifPresentOrThrow(consumer, ""); }

    public final A ifPresentOrThrow(Consumer<T> consumer, String message) {
        T curr = get();
        if (curr != null) {
            consumer.accept(curr);
            return current();
        }
        throw new NullPointerException(message);
    }

    /*
    inner
     */

    private final T reload() {
        T t = supplier.get();
        if (notAllowNullValue) {
            Objects.requireNonNull(t, "不允许 null 值");
        }
        cache(reference(t));
        return t;
    }

    private final synchronized void cache(Reference<T> ref) { reference = ref; }
}
