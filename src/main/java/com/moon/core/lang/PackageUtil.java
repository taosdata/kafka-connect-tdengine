package com.moon.core.lang;

import static com.moon.core.enums.Arrays2.STRINGS;
import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
public final class PackageUtil {

    private PackageUtil() {
        noInstanceError();
    }

    public static PackageScanner scanner() {
        return new PackageScanner();
    }

    public static String[] scan(String packageName) {
        return scanner().scan(packageName).toArray((String[]) STRINGS.empty());
    }
}
