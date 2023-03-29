package com.moon.core.util;

import com.moon.core.lang.Executable;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author moonsky
 */
final class OptionalImpl<T> implements Optional<T> {

    final static Optional EMPTY = Empty.INSTANCE;

    private final T value;

    public OptionalImpl(T value) { this.value = Objects.requireNonNull(value); }

    @Override
    public T getOrNull() { return value; }

    @Override
    public int hashCode() { return Objects.hashCode(value); }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) { return true; }
        if (!(obj instanceof OptionalImpl)) { return false; }
        return Objects.equals(value, ((OptionalImpl) obj).value);
    }

    @Override
    public String toString() { return String.valueOf(value); }

    private enum Empty implements Optional {
        INSTANCE;

        @Override
        public Object getOrNull() { return null; }

        @Override
        public Object get() { throw new NullPointerException("Optional value is null."); }

        @Override
        public Object getOrDefault(Object defaultValue) { return defaultValue; }

        @Override
        public Object getOrElse(Supplier supplier) { return supplier.get(); }

        @Override
        public boolean isPresent() { return false; }

        @Override
        public boolean isAbsent() { return true; }

        @Override
        public Optional filter(Predicate predicate) { return this; }

        @Override
        public Optional elseIfAbsent(Supplier supplier) { return Optional.ofNullable(supplier.get()); }

        @Override
        public Optional defaultIfAbsent(Object defaultValue) { return Optional.ofNullable(defaultValue); }

        @Override
        public Optional ifPresent(Consumer consumer) { return this; }

        @Override
        public Optional ifAbsent(Executable executor) {
            executor.execute();
            return this;
        }

        @Override
        public java.util.Optional toUtil() { return java.util.Optional.empty(); }

        @Override
        public Optional transform(Function computer) {
            return defaultIfAbsent(computer.apply(null));
        }

        @Override
        public Object compute(Function computer) { return computer.apply(null); }

        @Override
        public Object getOrThrow(Supplier supplier) throws Throwable { throw (Throwable) supplier.get(); }


        @Override
        public String toString() { return "null"; }
    }
}
