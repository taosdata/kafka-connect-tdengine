package com.moon.runner.core;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * @author moonsky
 */
interface IGetter extends BiFunction, Predicate {

    static IGetter reset(Object prevData, Object afterData) {
        IGetter getter = null;
        if (afterData instanceof Number) {
            if (prevData instanceof List) {
                getter = IGetVal.LIST;
            } else if (prevData instanceof Map) {
                getter = IGetVal.MAP;
            } else if (prevData.getClass().isArray()) {
                getter = new IGetArr();
            }
        } else if (afterData instanceof CharSequence) {
            if (prevData instanceof Map) {
                getter = IGetVal.MAP;
            } else {
                Objects.requireNonNull(prevData);
                getter = IGetVal.BEAN;
            }
        } else if (prevData instanceof Map) {
            getter = IGetVal.MAP;
        } else if (prevData instanceof List) {
            getter = IGetVal.LIST;
        } else {
            Objects.requireNonNull(prevData);
            getter = IGetVal.BEAN;
        }
        Objects.requireNonNull(getter);
        return getter;
    }
}
