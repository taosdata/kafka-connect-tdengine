package com.moon.core.util;

import com.moon.core.enums.Arrays2;
import com.moon.core.lang.StringUtil;

/**
 * @author moonsky
 */
public class RequireValidateException extends IllegalArgumentException {

    private final static Object[] EMPTY = Arrays2.OBJECTS.empty();

    private static Object[] notEmpty(Object... values) {
        return values == null ? EMPTY : values;
    }

    public RequireValidateException(String message) { super(message); }

    public RequireValidateException(String messageTemplate, Object... params) {
        super(StringUtil.format(messageTemplate, notEmpty(params)));
    }
}
