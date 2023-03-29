package com.moon.core.util;

import com.moon.core.lang.Executable;
import com.moon.core.lang.ThrowUtil;

import java.util.function.Supplier;

/**
 * 系统工具
 *
 * @author moonsky
 */
public final class OSUtil {

    private OSUtil() { ThrowUtil.noInstanceError(); }

    private static final boolean osIsLinux = nameHas("linux");
    private static final boolean osIsMacOsX = nameHas("mac name x");
    private static final boolean osIsWindows = nameHas("windows");

    public static <T> T get(
        Supplier<T> ifLinux, Supplier<T> ifWindows, Supplier<T> ifMac, Supplier<T> other
    ) {
        return (onLinux() ? ifLinux : (onWindows() ? ifWindows : (onMacOS() ? ifMac : other))).get();
    }

    public static boolean onLinux() { return osIsLinux; }

    public static void ifOnLinux(Executable executor) {
        if (onLinux()) {
            executor.execute();
        }
    }

    public static boolean onMacOS() { return osIsMacOsX; }

    public static void ifOnMacOS(Executable executor) {
        if (onMacOS()) {
            executor.execute();
        }
    }

    public static boolean onWindows() { return osIsWindows; }

    public static void ifOnWindows(Executable executor) {
        if (onWindows()) {
            executor.execute();
        }
    }

    public static boolean onWindowsXP() { return nameHas("windows xp"); }

    public static boolean onWindows2003() { return nameHas("windows 2003"); }

    public static boolean onWindowsVista() { return nameHas("windows vista"); }

    public static boolean onWindows7() { return nameHas("windows 7"); }

    public static boolean onWindows8() { return nameHas("windows 8"); }

    public static boolean onWindows10() { return nameHas("windows 10"); }

    private static boolean nameHas(String search) {
        String name = System.getProperty("os.name");
        return (name == null ? "" : name.toLowerCase()).contains(search);
    }
}
