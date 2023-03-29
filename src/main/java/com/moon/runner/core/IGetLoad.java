package com.moon.runner.core;

import com.moon.core.lang.PackageScanner;
import com.moon.core.lang.PackageUtil;
import com.moon.core.lang.ref.ReferenceUtil;
import com.moon.core.lang.reflect.ModifierUtil;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
class IGetLoad {

    private IGetLoad() { noInstanceError(); }

    private final static String[] packages;
    private final static Map<String, Class> NAMED_CACHE = new HashMap<>();

    static {
        Map<String, Object> packagesName = new LinkedHashMap<>();
        PackageScanner scanner = PackageUtil.scanner();
        scanner.scan("com.moon.core");
        packagesName.putIfAbsent("java.util.", null);
        packagesName.putIfAbsent("java.lang.", null);
        for (String className : scanner) {
            Class loaded = loadClass(className);
            if (loaded != null) {
                packagesName.putIfAbsent(loaded.getPackage().getName() + '.', null);
                NAMED_CACHE.putIfAbsent(loaded.getCanonicalName(), loaded);
            }
        }
        Set<String> names = packagesName.keySet();
        packages = names.toArray(new String[names.size()]);
    }

    private final static Class loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (Throwable e) {
            return null;
        }
    }

    private final static Map<String, Class> CACHE = ReferenceUtil.weakMap();

    public final static Class tryLoad(String name) {
        Map<String, Class> cache = NAMED_CACHE;
        Class type = cache.get(name);
        if (type == null && (type = (cache = CACHE).get(name)) == null) {
            for (String packageName : packages) {
                if (ModifierUtil.isPublic(type = loadClass(packageName + name))) {
                    if (cache.get(name) == null) {
                        synchronized (cache) {
                            cache.put(name, type);
                        }
                    }
                    return type;
                }
            }
        }
        return type;
    }

    public final static Class of(String name) {
        Class type = tryLoad(name);
        if (type == null) {
            throw new IllegalArgumentException("can not find class of key: " + name);
        }
        return type;
    }
}
