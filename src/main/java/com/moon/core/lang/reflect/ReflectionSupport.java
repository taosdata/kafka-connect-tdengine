package com.moon.core.lang.reflect;

import com.moon.core.util.TypeUtil;

import java.lang.reflect.Executable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author ZhangDongMin
 */
final class ReflectionSupport {

    private ReflectionSupport() {
        noInstanceError();
    }

    static final Class[] PRIMITIVE_CLASSES = {
        boolean.class, byte.class, char.class, short.class, int.class, long.class, float.class, double.class
    };

    static final Class[] WRAPPER_CLASSES = {
        Boolean.class, Byte.class, Character.class, Short.class, Integer.class, Long.class, Float.class, Double.class
    };

    static Object[] castAsPossibly(Class[] types, Object[] arguments) {
        int len1 = types.length;
        int len2 = arguments.length;
        Class type;
        Object[] result = new Object[len1];
        for (int i = 0; i < len1; i++) {
            type = types[i];
            if (type == null) {
                continue;
            } else if (i < len2) {
                result[i] = TypeUtil.cast().toType(arguments[i], type);
            } else {
                result[i] = TypeUtil.cast().toType(null, type);
            }
        }
        return result;
    }

    static <T extends Executable> List<T> findByParameterTypes(
        List<T> matchOnlyTypes, int size, Class... parameterTypes
    ) {
        UnmodifiableArrayList<T> strong = new UnmodifiableArrayList<>();
        UnmodifiableArrayList<T> soft = new UnmodifiableArrayList<>();
        UnmodifiableArrayList<T> weak = new UnmodifiableArrayList<>();
        for (int i = 0; i < size; i++) {
            T item = matchOnlyTypes.get(i);
            MatchLevel level = checkMatchLevel(item.getParameterTypes(), parameterTypes);
            switch (level) {
                case FINAL:
                    strong.add(item);
                    break;
                case SOFT:
                    soft.add(item);
                    break;
                case WEAK:
                    weak.add(item);
                    break;
            }
        }
        strong.addAll(soft);
        strong.addAll(weak);
        return strong.flipToUnmodify();
    }

    static MatchLevel checkMatchLevel(Class[] types1, Class[] types2) {
        int length1 = types1.length;
        MatchLevel matchLevel = MatchLevel.FINAL;
        int level = matchLevel.ordinal();
        if (length1 == types2.length) {
            MatchLevel tempLevel;
            for (int i = 0; i < length1; i++) {
                Class type1 = types1[i];
                Class type2 = types2[i];
                tempLevel = checkLevel(type1, type2);
                if (tempLevel == MatchLevel.NONE) {
                    return MatchLevel.NONE;
                } else if (tempLevel.ordinal() > level) {
                    matchLevel = tempLevel;
                    level = tempLevel.ordinal();
                }
            }
            return matchLevel;
        }
        return MatchLevel.NONE;
    }

    /**
     * 返回匹配级别，
     * 完全相同 == 强匹配
     * type1 是基本类型， type2 是相应包装类似
     *
     * @param type1
     * @param type2
     *
     * @return
     *
     * @see MatchLevel
     */
    static MatchLevel checkLevel(Class type1, Class type2) {
        if (type1 == type2) {
            return MatchLevel.FINAL;
        } else if (type2.isPrimitive()) {
            int index2 = indexOf(PRIMITIVE_CLASSES, type2);
            if (type1 == WRAPPER_CLASSES[index2]) {
                return MatchLevel.WEAK;
            }
            int index1 = indexOf(PRIMITIVE_CLASSES, type1);
            if (index1 > index2 && index1 > 2 && index2 > 0) {
                return MatchLevel.SOFT;
            }
        } else if (type1.isPrimitive()) {
            if (type2 == null) {
                return MatchLevel.NONE;
            }
            int index1 = indexOf(PRIMITIVE_CLASSES, type1);
            if (type2 == WRAPPER_CLASSES[index1]) {
                return MatchLevel.WEAK;
            }
            int index2 = indexOf(PRIMITIVE_CLASSES, type2);
            if (index1 > index2 && index1 > 2 && index2 > 0) {
                return MatchLevel.SOFT;
            }
        } else if (type1.isAssignableFrom(type2)) {
            return MatchLevel.WEAK;
        }
        return MatchLevel.NONE;
    }

    static <P, E extends P, C extends Collection<E>> C filter(
        C collect, C resultContainer, Predicate<P> predicate
    ) {
        if (collect != null) {
            for (E e : collect) {
                if (predicate.test(e)) {
                    resultContainer.add(e);
                }
            }
        }
        return resultContainer;
    }

    static <T> int indexOf(T[] arr, T obj) {
        if (arr == null) {
            return -1;
        }
        T item;
        for (int i = 0; i < arr.length; i++) {
            item = arr[i];
            if (Objects.equals(obj, item)) {
                return i;
            }
        }
        return -1;
    }
}
