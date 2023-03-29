package com.moon.runner.core;

import com.moon.core.lang.ref.IntAccessor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author moonsky
 */
final class Asserts {

    private final static Map<Class, Set<String>> EXCLUDE_METHODS = new HashMap<>();

    static {
        EXCLUDE_METHODS.put(System.class, new HashSet() {{
            add("exit");
            add("gc");
        }});
    }

    /**
     * 禁用
     *
     * @param chars
     * @param indexer
     * @param len
     * @param type
     * @param methodName
     */
    final static void disableIllegalCallers(
        char[] chars, IntAccessor indexer, int len,
        Class type, String methodName
    ) {
        Set<String> strings = EXCLUDE_METHODS.get(type);
        if (strings != null && strings.contains(methodName)) {
            ParseUtil.throwErr(chars, indexer);
        }
    }
}
