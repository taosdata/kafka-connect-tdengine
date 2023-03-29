package com.moon.core.lang.reflect;

import com.moon.core.lang.ThrowUtil;
import com.moon.core.lang.ref.FinalAccessor;
import com.moon.core.lang.ref.WeakLocation;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import static com.moon.core.lang.ClassUtil.getClasses;
import static com.moon.core.lang.ThrowUtil.runtime;
import static com.moon.core.lang.reflect.ReflectionSupport.castAsPossibly;
import static com.moon.core.lang.reflect.ReflectionSupport.findByParameterTypes;
import static com.moon.core.lang.reflect.UnmodifiableArrayList.unmodifiable;
import static com.moon.core.util.OptionalUtil.computeOrNull;

/**
 * @author ZhangDongMin
 */
public final class ConstructorUtil {

    private ConstructorUtil() {
        ThrowUtil.noInstanceError();
    }

    final static int ONE = 1;
    final static int ZERO = 0;
    final static WeakLocation<Class, Integer, Object> WEAK = WeakLocation.ofManaged();
    final static WeakLocation<Class, Integer, Supplier<Constructor>> WEAK_ITEM = WeakLocation.ofManaged();

    /**
     * 从字符串中解析构造实体，要求构造器必须是 public 修饰的
     * {@code constructorCaller}类似{@code com.name.ObjectClass()}
     * <pre>
     * newInstance("com.name.ObjectClass")              ====> ObjectClass 对象
     * newInstance("ObjectClass()", "com.name")         ====> ObjectClass 对象
     * newInstance("Object()", "com.name", "Class")     ====> ObjectClass 对象
     * newInstance("Object(1, 2)", "com.name", "Class") ====> 有参构造器构造的 ObjectClass 对象
     * </pre>
     *
     * @param constructorCaller 构造器，必须不为 null
     * @param packageName       包名，可为 null
     * @param classnameSuffix   类名后缀，可为 null
     * @param <T>               返回对象数据类型
     *
     * @return
     */
    static <T> T newInstance(String constructorCaller, String packageName, String classnameSuffix) {

        throw new UnsupportedOperationException();
    }

    /**
     * 获取指定了类的所有公共构造器
     *
     * @param <T>
     * @param type
     *
     * @return
     */
    public static <T> List getConstructors(Class<T> type) {
        return (List) WEAK.getOrWithElse(type, ONE, () -> unmodifiable(type.getConstructors()));
    }

    /**
     * 获取指定了类的所有构造器
     *
     * @param <T>
     * @param type
     *
     * @return
     */
    public static <T> List getDeclaredConstructors(Class<T> type) {
        return (List) WEAK.getOrWithElse(type, ZERO, () -> unmodifiable(type.getDeclaredConstructors()));
    }

    /**
     * 获取指定类的空参构造器
     *
     * @param <T>
     * @param type
     *
     * @return
     */
    public static <T> Constructor getEmptyConstructor(Class type) {
        return WEAK_ITEM.getOrWithElse(type, ONE, () -> {
            Exception exception = null;
            FinalAccessor<Constructor<T>> accessor = new FinalAccessor<>();
            try {
                accessor.set(type.getConstructor());
            } catch (NoSuchMethodException e) {
                exception = e;
            }
            String message = computeOrNull(exception, ex -> ex.getMessage());
            return () -> accessor.requireGet(message);
        }).get();
    }

    /**
     * 获取指定类的空参构造器
     *
     * @param type
     * @param <T>
     *
     * @return
     */
    public static <T> Constructor<T> getEmptyDeclaredConstructor(Class type) {
        return WEAK_ITEM.getOrWithCompute(type, ONE, (clazz, val) -> {
            FinalAccessor<Constructor<T>> accessor = FinalAccessor.of();
            Exception exception = null;
            try {
                accessor.set(clazz.getDeclaredConstructor());
            } catch (NoSuchMethodException e) {
                exception = e;
            }
            String message = computeOrNull(exception, ex -> ex.getMessage());
            return () -> accessor.requireGet(message);
        }).get();
    }

    /**
     * 获取指定类指定参数类型完全匹配的构造器
     *
     * @param type
     * @param parameterTypes
     * @param <T>
     *
     * @return
     */
    public static <T> Constructor<T> getConstructor(Class type, Class... parameterTypes) {
        try {
            if (parameterTypes.length > 0) {
                return type.getConstructor(parameterTypes);
            } else {
                return getEmptyConstructor(type);
            }
        } catch (Exception e) {
            return runtime(e);
        }
    }

    /**
     * 获取指定类能执行指定参数类型的所有构造器，并且按匹配程度先后排序
     *
     * @param type
     * @param parameterTypes
     * @param <T>
     *
     * @return
     */
    public static <T> List<Constructor<T>> getMatchConstructors(Class type, Class... parameterTypes) {
        return (List) WEAK.getOrWithElse(type, Arrays.hashCode(parameterTypes), () -> {
            List<Constructor<T>> list = getDeclaredConstructors(type);
            return findByParameterTypes(list, list.size(), parameterTypes);
        });
    }

    /**
     * 获取匹配指定类指定参数类型的“最佳构造器”
     *
     * @param type
     * @param parameterTypes
     * @param <T>
     *
     * @return
     */
    public static <T> Constructor<T> getMatchConstructor(Class<T> type, Class... parameterTypes) {
        return WEAK_ITEM.getOrWithElse(type, Arrays.hashCode(parameterTypes), () -> {
            String message = null;
            FinalAccessor<Constructor<T>> accessor = new FinalAccessor<>();
            List<Constructor<T>> list = getMatchConstructors(type, parameterTypes);
            if (!list.isEmpty()) {
                accessor.set(list.get(0));
            } else {
                message = "Can not find constructor of type: " + type + " with parameterTypes: " + Arrays.toString(
                    parameterTypes);
            }
            final String finalMessage = message;
            return () -> accessor.requireGet(finalMessage);
        }).get();
    }

    /**
     * 用空构造器创建一个 type 类实例
     *
     * @param type
     * @param <T>
     *
     * @return
     */
    public static <T> T newInstance(Class<T> type) {
        return newInstance(type, false);
    }

    /**
     * 用空构造器创建一个 type 类实例
     *
     * @param type
     * @param <T>
     *
     * @return
     */
    public static <T> T newInstance(Class<T> type, boolean accessible) {
        return accessible ? newInstance(getEmptyDeclaredConstructor(type),
            accessible) : newInstance(getConstructor(type));
    }

    /**
     * 用字段参数创建一个 type 类实例
     *
     * @param type
     * @param arguments
     * @param <T>
     *
     * @return
     */
    public static <T> T newInstance(Class<T> type, Object... arguments) {
        return newInstance(type, false, arguments);
    }

    /**
     * 用字段参数创建一个 type 类实例
     *
     * @param accessible
     * @param type
     * @param arguments
     * @param <T>
     *
     * @return
     */
    public static <T> T newInstance(Class<T> type, boolean accessible, Object... arguments) {
        try {
            if (arguments.length > 0) {
                Class[] classes = getClasses(arguments);
                Constructor<T> constructor = getMatchConstructor(type, classes);
                return newInstance(constructor, accessible, arguments);
            } else {
                return newInstance(type, accessible);
            }
        } catch (Exception e) {
            return runtime(e);
        }
    }

    /**
     * 用空构造器创建一个实例
     *
     * @param constructor 空参数构造器
     * @param <T>
     *
     * @return
     */
    public static <T> T newInstance(Constructor<T> constructor) {
        return newInstance(constructor, false);
    }

    /**
     * 用空构造器创建一个实例
     *
     * @param accessible
     * @param constructor 用空构造器创建一个实例
     * @param <T>
     *
     * @return
     */
    public static <T> T newInstance(Constructor<T> constructor, boolean accessible) {
        return createInstance(accessible, constructor, () -> constructor.newInstance());
    }

    /**
     * 用指定构造器字段参数创建一个实例
     *
     * @param constructor
     * @param arguments
     * @param <T>
     *
     * @return
     */
    public static <T> T newInstance(Constructor<T> constructor, Object... arguments) {
        return newInstance(constructor, false, arguments);
    }

    /**
     * 用指定构造器字段参数创建一个实例
     *
     * @param accessible
     * @param constructor
     * @param arguments
     * @param <T>
     *
     * @return
     */
    public static <T> T newInstance(Constructor<T> constructor, boolean accessible, Object... arguments) {
        return createInstance(accessible, constructor, () -> constructor.newInstance(arguments));
    }

    /**
     * 尽可能的执行构造函数并返回实例；
     * 首先采用常规方式执行构造函数，如捕获参数异常，
     * 就会尝试把参数尽可能转换成构造器能够接受的参数类型，然后再次尝试执行构造函数
     *
     * @param asPossibly  是否强制执行
     * @param accessible
     * @param constructor
     * @param arguments
     * @param <T>
     *
     * @return
     */
    public static <T> T newInstance(
        boolean asPossibly, Constructor<T> constructor, boolean accessible, Object... arguments
    ) {
        try {
            return newInstance(constructor, accessible, arguments);
        } catch (IllegalArgumentException e) {
            if (asPossibly) {
                Class[] parameterTypes = constructor.getParameterTypes();
                arguments = castAsPossibly(parameterTypes, arguments);
                return newInstance(constructor, accessible, arguments);
            }
            return runtime(e);
        }
    }

    private static <T> T createInstance(boolean accessible, Constructor c, Callable<T> callable) {
        try {
            T result;
            if (accessible && !c.isAccessible()) {
                c.setAccessible(true);
                result = callable.call();
                c.setAccessible(false);
            } else {
                result = callable.call();
            }
            return result;
        } catch (Exception e) {
            return runtime(e);
        }
    }
}
