package com.moon.core.lang.ref;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * 延迟访问器
 *
 * @author moonsky
 */
public final class LazyAccessor<T> implements Supplier<T> {

    /**
     * 值可以为 null 的空对象
     */
    private final static LazyAccessor NULLABLE = new LazyAccessor(true);
    /**
     * 值不能为 null 的空对象
     */
    private final static LazyAccessor REQUIRED = new LazyAccessor(false);
    /**
     * 值访问器
     */
    private final Supplier<? extends T> supplier;
    /**
     * 值是否可为 null
     * <p>
     * 此处与{@link #getNullable()}语义重复
     */
    private final boolean nullable;
    /**
     * 是否已加载过值
     */
    private boolean resolved;
    /**
     * 最终值
     */
    private T value = null;

    public LazyAccessor(Supplier<? extends T> supplier) { this(supplier, false); }

    public LazyAccessor(Supplier<? extends T> supplier, boolean nullable) {
        this.supplier = supplier;
        this.nullable = nullable;
    }

    public static <T> LazyAccessor<T> of(Supplier<? extends T> supplier) { return new LazyAccessor<>(supplier); }

    public static <T> LazyAccessor<T> of(T value) { return new LazyAccessor<>(() -> value); }

    public static <T> LazyAccessor<T> ofNullable(Supplier<? extends T> supplier) {
        return new LazyAccessor<>(supplier, true);
    }

    public static <T> LazyAccessor<T> ofNullable(T value) { return new LazyAccessor<>(() -> value, true); }

    public static <T> LazyAccessor<T> ofNullableEmpty() { return NULLABLE; }

    public static <T> LazyAccessor<T> empty() { return REQUIRED; }

    private LazyAccessor(boolean nullable) {
        this.supplier = null;
        this.nullable = nullable;
        this.resolved = true;
    }

    @Override
    public T get() {
        T value = this.getNullable();
        if (value == null) {
            if (this.nullable) {
                return null;
            } else {
                throw new IllegalStateException("Expected lazy evaluation to yield a non-null value but got null!");
            }
        }
        return value;
    }

    public T getOrDefault(T defaultValue) {
        T value = this.getNullable();
        return value == null ? defaultValue : value;
    }

    public T getOrElse(Supplier<T> defaultSupplier) {
        T value = this.getNullable();
        return value == null ? defaultSupplier.get() : value;
    }

    public Optional<T> getOptional() { return Optional.ofNullable(getNullable()); }

    public T getNullable() {
        T value = this.value;

        if (!this.resolved) {
            value = supplier.get();

            this.value = value;
            this.resolved = true;
        }
        return value;
    }

    public LazyAccessor<T> clear() {
        this.resolved = false;
        this.value = null;
        return this;
    }
}
