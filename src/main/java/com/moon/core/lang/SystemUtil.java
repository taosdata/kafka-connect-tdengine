package com.moon.core.lang;

import com.moon.core.util.ResourceUtil;

import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
public final class SystemUtil {

    private SystemUtil() { noInstanceError(); }

    public static boolean resourceExists(String path) { return ResourceUtil.resourceExists(path); }

    public static InputStream getResourceAsInputStream(String path) {
        return ResourceUtil.getResourceAsInputStream(path);
    }

    public static long now() { return System.currentTimeMillis(); }

    public static Map<String, String> getAll() {
        return new HashMap(System.getProperties());
    }

    public static String get(String name) { return get(name, null); }

    public static String get(String name, String defaultValue) {
        Objects.requireNonNull(name);

        String value = null;
        try {
            if (System.getSecurityManager() == null) {
                value = System.getProperty(name);
            } else {
                value = AccessController.doPrivileged((PrivilegedAction<String>) () -> System.getProperty(name));
            }
        } catch (Exception ignore) {
        }

        return value == null ? defaultValue : value;
    }

    public static boolean getBooleanValue(String name, boolean defaultValue) {
        return BooleanUtil.defaultIfInvalid(get(name), defaultValue);
    }

    public static int getIntValue(String name, int defaultValue) {
        return IntUtil.defaultIfInvalid(get(name), defaultValue);
    }

    public static String getAppFileSeparator() { return "/"; }

    public static String getAppLineSeparator() { return "\n"; }

    public static String getFileSeparator() { return get("file.separator"); }

    public static String getLineSeparator() { return get("line.separator"); }

    /**
     * 缓存目录
     */
    public static String getTempDir() { return get("java.io.tmpdir"); }

    /**
     * 用户目录
     */
    public static String getUserDir() { return get("user.dir"); }

    public static String getUserHome() { return get("user.home"); }

    /**
     * 工作目录
     */
    public static String getWorkingDir() { return get("user.dir"); }

    public static String getJvmName() { return get("java.vm.name"); }

    public static String getJvmVersion() { return get("java.vm.version"); }

    public static String getJvmInfo() { return get("java.vm.info"); }

    public static String getJavaVersion() { return get("java.version"); }

    /**
     * java 版本
     *
     * @return 1.6 => 6; 1.7 => 7; 1.8 => 8; 9 => 9
     */
    public static int getJavaVersionAsInt() {
        String version = getJavaVersion();
        if (StringUtil.isEmpty(version)) {
            return -1;
        }
        final int lastDashNdx = version.lastIndexOf('-');
        if (lastDashNdx >= 0) {
            version = version.substring(0, lastDashNdx);
        }
        if (version.startsWith("1.")) {
            // up to java 8
            final int index = version.indexOf('.', 2);
            return IntUtil.toIntValue(version.substring(2, index));
        } else {
            final int index = version.indexOf('.');
            return Integer.parseInt(index < 0 ? version : version.substring(0, index));
        }
    }
}
