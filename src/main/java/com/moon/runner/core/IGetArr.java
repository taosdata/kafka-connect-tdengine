package com.moon.runner.core;

import com.moon.core.enums.ArrayOperator;
import com.moon.core.enums.Arrays2;

/**
 * @author moonsky
 */
class IGetArr implements IGetter {
    ArrayOperator getter;

    public boolean sourceTest(Object data) { return data.getClass().isArray(); }

    /**
     * Applies this function to the given arguments.
     *
     * @param o  the first function argument
     * @param o2 the second function argument
     * @return the function result
     */
    @Override
    public Object apply(Object o, Object o2) {
        return getter == null || !test(o)
            ? reset(o).get(o, ((Number) o2).intValue())
            : getter.get(o, ((Number) o2).intValue());
    }

    ArrayOperator reset(Object data) {
        getter = Arrays2.getOrObjects(data.getClass());
        return getter;
    }

    /**
     * Evaluates this predicate on the given argument.
     *
     * @param o the input argument
     * @return {@code true} if the input argument matches the predicate,
     * otherwise {@code false}
     */
    @Override
    public boolean test(Object o) { return getter == null ? sourceTest(o) : getter.test(o) || sourceTest(o); }
}
