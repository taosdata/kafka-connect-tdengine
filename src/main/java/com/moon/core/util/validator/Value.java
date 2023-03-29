package com.moon.core.util.validator;

import com.moon.core.enums.Const;
import com.moon.core.model.supplier.ValueSupplier;

import static java.util.Objects.requireNonNull;

/**
 * @author moonsky
 */
abstract class Value<T> implements ValueSupplier<T> {

    final static String NONE = Const.EMPTY;

    final boolean nullable;

    final T value;

    Value(T value, boolean nullable) { this.value = (this.nullable = nullable) ? value : requireNonNull(value); }

    @Override
    public final T getValue() { return value; }

    final boolean isNullable() { return nullable; }
}
