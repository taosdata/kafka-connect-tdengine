package com.moon.core.lang.reflect;

import com.moon.core.lang.ThrowUtil;
import com.moon.core.lang.ref.ReferenceUtil;
import com.moon.core.util.FilterUtil;
import com.moon.core.util.ValidateUtil;

import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;

import static com.moon.core.lang.ClassUtil.toWrapperClass;
import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
final class Reflection {
    private Reflection() {
        noInstanceError();
    }

    final static int NOT_FOUND = -1;

    static final Class[] PRIMITIVE_CLASSES = {
        boolean.class,
        byte.class,
        char.class,
        short.class,
        int.class,
        long.class,
        float.class,
        double.class
    };

    private final static Map<Object, Predicate<Method>> CACHE = ReferenceUtil.manageMap();

    final static Predicate<Method> nameTester(Object name) {
        Predicate<Method> tester = CACHE.get(name);
        if (tester == null) {
            final String finalName = ValidateUtil.requireNotEmpty(name.toString().trim());
            CACHE.put(name, tester = method -> method.getName().equals(finalName));
        }
        return tester;
    }

    final static <T> T throwErr(Class type, String name) {
        return ThrowUtil.runtime("Can not find public method: "
            + type + "." + name + "();");
    }

    final static <T> T throwErr(Class type, String name, Class... parameterTypes) {
        return ThrowUtil.runtime("Can not find public method: "
            + type + "." + name + "(" + Arrays.toString(parameterTypes) + ");");
    }

    final static <E extends Executable> UnmodifiableArrayList unmodify(List<E> li, List<E> l2, Class type) {
        Map<E, Object> map = new LinkedHashMap<>();
        for (E e : li) {
            map.putIfAbsent(e, null);
        }
        for (E e : l2) {
            map.putIfAbsent(e, null);
        }
        return get(type == null ? map.keySet() : getSuper(type, new LinkedHashSet(map.keySet())));
    }

    final static <E extends Executable> UnmodifiableArrayList<E> get() {
        return new UnmodifiableArrayList<>();
    }

    final static <E extends Executable> UnmodifiableArrayList<E> get(Object obj) {
        return UnmodifiableArrayList.ofCollect(obj);
    }

    final static Set<Method> getSuper(Class type, final Set<Method> set) {
        Class superType = type.getSuperclass();
        Package packet = type.getPackage(), superPacket = superType.getPackage();
        Predicate<Executable> tester = packet == superPacket ? Asserts.lowestDefault : Asserts.lowestProtected;
        FilterUtil.filter(superType.getDeclaredMethods(), tester, set);
        for (superType = superType.getSuperclass(); superType != null; superType = superType.getSuperclass()) {
            FilterUtil.filter(superType.getDeclaredMethods(), Asserts.lowestProtected, set);
        }
        return set;
    }

    final static <E extends Executable> UnmodifiableArrayList<? super E> matching(List<E> methods, Class... types) {
        final int length = types.length;
        if (length > 0) {
            int len = (methods = FilterUtil.filter(methods, m -> m.getParameterCount() == length)).size();
            if (len > 0) {
                return doMatching(methods, types, length).flipToUnmodify();
            } else {
                return get().flipToUnmodify();
            }
        } else {
            return FilterUtil.filter(methods, Asserts.noParams, get()).flipToUnmodify();
        }
    }

    /*
     * private
     */

    private final static <E extends Executable> UnmodifiableArrayList<E> doMatching(List<E> methods, Class[] types, int length) {
        UnmodifiableArrayList<E> strong = get();
        UnmodifiableArrayList<E> soft = get();
        UnmodifiableArrayList<E> weak = get();
        Class clazz, type;
        Class[] classes;
        for (E method : methods) {
            classes = method.getParameterTypes();
            int strongCount = 0, softCount = 0, weakCount = 0;
            for (int i = 0; i < length; i++) {
                if ((clazz = classes[i]) == (type = types[i])) {
                    strongCount++;
                } else if (clazz.isPrimitive()) {
                    if (toWrapperClass(clazz) == type) {
                        weakCount++;
                    } else if (isMatchSoft(clazz, type)) {
                        softCount++;
                    }
                } else if (type.isPrimitive()) {
                    if (toWrapperClass(type) == clazz) {
                        weakCount++;
                    } else if (isMatchSoft(clazz, type)) {
                        softCount++;
                    }
                } else if (clazz.isAssignableFrom(type) || type == null) {
                    weakCount++;
                }
            }
            if (weakCount > 0) {
                weak.add(method);
            } else if (softCount > 0) {
                soft.add(method);
            } else if (strongCount > 0) {
                strong.add(method);
            }
        }
        strong.addAll(soft);
        strong.addAll(weak);

        return strong.flipToUnmodify();
    }

    private final static boolean isMatchSoft(Class type1, Class type2) {
        int index = indexOf(type2);
        return index > NOT_FOUND && indexOf(type1) >= index;
    }

    private final static int indexOf(Class type) {
        for (int i = 0; i < PRIMITIVE_CLASSES.length; i++) {
            if (type == PRIMITIVE_CLASSES[i]) {
                return i;
            }
        }
        return NOT_FOUND;
    }
}
