package com.moon.core.lang;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
public final class ClassLoaderUtil {

    private ClassLoaderUtil() { noInstanceError(); }

    public static ClassLoader getDefaultClassLoader() {
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl != null) {
                return cl;
            }
        } catch (Throwable t) {
            // ignore
        }
        return ClassLoader.getSystemClassLoader();
    }
}
