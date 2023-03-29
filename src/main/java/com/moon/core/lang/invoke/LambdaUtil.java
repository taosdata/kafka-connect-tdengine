package com.moon.core.lang.invoke;

import com.moon.core.lang.StringUtil;
import com.moon.core.lang.ThrowUtil;
import com.moon.core.util.function.SerializableFunction;

/**
 * Lambda 表达式工具类
 *
 * @author moonsky
 */
public abstract class LambdaUtil {

    private LambdaUtil() { ThrowUtil.noInstanceError(); }

    /**
     * 加载/解析 getter、setter 方法引用的 lambda 表达式
     *
     * @param fn  getter、setter
     * @param <T> 对象类型
     *
     * @return lambda 表达式信息
     */
    public static <T> SerializedLambda resolve(SerializableFunction<T, Object> fn) {
        return SerializedLambda.resolve(fn);
    }

    /**
     * 从对象的 getter、setter 方法引用 lambda 表达式中解析属性名，
     * <p>
     * 例：
     * <pre>
     * LambdaUtil.getPropertyName(User::getUsername); // username
     * LambdaUtil.getPropertyName(User::setPassword); // password
     * LambdaUtil.getPropertyName(User::getA); // a
     * LambdaUtil.getPropertyName(User::getAType); // AType
     * LambdaUtil.getPropertyName(User::getbType); // bType
     * </pre>
     *
     * @param getter getter 或 setter 方法引用
     * @param <T>    对象类型
     *
     * @return 属性名
     *
     * @throws IllegalStateException 当无法解析属性名时抛出异常
     * @see StringUtil#decapitalize(String)
     */
    public static <T> String getPropertyName(SerializableFunction<T, Object> getter) {
        SerializedLambda lambda = resolve(getter);
        String propertyName = lambda.getPropertyName();
        if (propertyName == null) {
            String msg = "解析属性名错误 '" + lambda.getImplMethodName() + "', 必须以 'is'、'get'、'set'、'add'、'with' 之一开头";
            throw new IllegalStateException(msg);
        }
        return propertyName;
    }
}
