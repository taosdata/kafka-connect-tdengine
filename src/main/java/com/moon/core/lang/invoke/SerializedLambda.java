package com.moon.core.lang.invoke;

import com.moon.core.enums.Arrays2;
import com.moon.core.io.IOUtil;
import com.moon.core.lang.ClassUtil;
import com.moon.core.lang.StringUtil;
import com.moon.core.util.function.BiIntFunction;
import com.moon.core.util.function.SerializableFunction;

import java.io.*;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;

/**
 * @author moonsky
 */
@SuppressWarnings("all")
public class SerializedLambda implements Serializable {

    private final static long serialVersionUID = 8025925345765570181L;
    private final static Object[] EMPTY = Arrays2.OBJECTS.empty();

    private final Class<?> capturingClass;
    private final String functionalInterfaceClass;
    private final String functionalInterfaceMethodName;
    private final String functionalInterfaceMethodSignature;
    private final String implClass;
    private final String implMethodName;
    private final String implMethodSignature;
    private final int implMethodKind;
    private final String instantiatedMethodType;
    private final Object[] capturedArgs;

    public SerializedLambda(
        Class<?> capturingClass,
        String functionalInterfaceClass,
        String functionalInterfaceMethodName,
        String functionalInterfaceMethodSignature,
        String implClass,
        String implMethodName,
        String implMethodSignature,
        int implMethodKind,
        String instantiatedMethodType,
        Object[] args
    ) {
        this.capturingClass = capturingClass;
        this.functionalInterfaceClass = functionalInterfaceClass;
        this.functionalInterfaceMethodName = functionalInterfaceMethodName;
        this.functionalInterfaceMethodSignature = functionalInterfaceMethodSignature;
        this.implClass = implClass;
        this.implMethodName = implMethodName;
        this.implMethodSignature = implMethodSignature;
        this.implMethodKind = implMethodKind;
        this.instantiatedMethodType = instantiatedMethodType;
        this.capturedArgs = args == null ? null : (args.length == 0 ? EMPTY : args.clone());
    }

    public static SerializedLambda resolve(SerializableFunction<?, ?> lambda) {
        return serializeLambda(lambda);
    }

    final static SerializedLambda serializeLambda(Serializable serializable) {
        return LambdaCached.getOrPull(serializable, (lambda, hash) -> {
            if (!lambda.getClass().isSynthetic()) {
                throw new IllegalStateException("仅支持传入 lambda 表达式");
            }
            ByteArrayInputStream stream = new ByteArrayInputStream(IOUtil.serialize(lambda));
            try (ObjectInputStream inStream = new LambdaObjectInputStream(stream)) {
                return new LambdaDetail((SerializedLambda) inStream.readObject());
            } catch (ClassNotFoundException | IOException e) {
                throw new IllegalStateException("解析 Lambda 表达式错误");
            }
        });
    }

    /**
     * 声明方法引用的类，如下面示例
     * <pre>
     * package com.example;
     *
     * public class Example {
     *
     *     public void testExample() {
     *         SerializedLambda lambda = SerializedLambda.resolve(User::getUsername);
     *         System.out.println(lambda.getCapturingClass());
     *         // 打印: class com.example.Example
     *     }
     * }
     * </pre>
     * @return
     */
    public Class<?> getCapturingClass() { return capturingClass; }

    /**
     * 函数式接口名称，如{@link Function}的名称的名称就是{@code java.util.function.Function}
     *
     * @return 函数式接口名称
     */
    public String getFunctionalInterfaceClassName() { return useClassName(functionalInterfaceClass); }

    /**
     * 函数式接口类
     *
     * @return 函数式接口类
     */
    public Class<?> getFunctionalInterfaceClass() { return toclass(getFunctionalInterfaceClassName()); }

    /**
     * 函数式接口方法名称，如{@link Function#apply(Object)}的方法名就是{@code apply}
     *
     * @return 函数式接口方法名称
     */
    public String getFunctionalInterfaceMethodName() { return functionalInterfaceMethodName; }

    public String getFunctionalInterfaceMethodSignature() { return functionalInterfaceMethodSignature; }

    /**
     * 方法引用的实际类名，如{@code com.example.User::getUsername}就是{@code com.example.User}
     *
     * @return 方法引用的实际类名
     */
    public String getImplClassName() { return useClassName(implClass); }

    /**
     * 方法引用的实际类，如{@code com.example.User::getUsername}就是{@code com.example.User}
     *
     * @return 方法引用的实际类
     */
    public Class<?> getImplClass() { return toclass(getImplClassName()); }

    /**
     * 方法引用的方法名，如{@code User::getUsername}就是{@code getUsername}
     *
     * @return 方法引用的方法名
     */
    public String getImplMethodName() { return implMethodName; }

    /**
     * 根据方法名判断是否是 getter 方法
     *
     * @return 是否是 getter 方法
     */
    public boolean isGetter() {
        String implMethodName = getImplMethodName();
        int nameLength = implMethodName.length();
        return (implMethodName.startsWith("get") && nameLength > 3)//
            || (implMethodName.startsWith("is") && nameLength > 2);
    }

    /**
     * 根据方法名判断是否是 setter 方法
     *
     * @return 是否是 setter 方法
     */
    public boolean isSetter() {
        String implMethodName = getImplMethodName();
        int nameLength = implMethodName.length();
        return (implMethodName.startsWith("set") && nameLength > 3);
    }

    /**
     * 如果是 getter 或 setter 方法，返回对应的属性名，这里是根据字符串模式解析
     * <p>
     * 所以返回的字段名首字母一定是小写，如果实际定义的是大写，返回就是错误的属性名
     *
     * @return 返回对应的属性名，如果不是 getter 或 setter 方法，返回 null
     */
    public String getPropertyName() { return toFieldName(getImplMethodName()); }

    public String getImplMethodSignature() { return implMethodSignature; }

    public int getImplMethodKind() { return implMethodKind; }

    public String getInstantiatedMethodType() { return instantiatedMethodType; }

    public Object[] getCapturedArgs() { return capturedArgs; }

    @Override
    public String toString() {
        return String.format("%s -> %s::%s",//
            StringUtil.substrAfterLast(getFunctionalInterfaceClassName(), "."),//
            StringUtil.substrAfterLast(getImplClassName(), "."),//
            getImplMethodName());
    }

    private static String useClassName(String classpath) { return classpath.replace('/', '.'); }

    private static Class<?> toclass(String classname) { return ClassUtil.forName(classname); }

    private static String toFieldName(String name) {
        if (name.startsWith("is")) {
            name = name.substring(2);
        } else if (name.startsWith("with")) {
            name = name.substring(4);
        } else {
            if (!name.startsWith("get") && !name.startsWith("set") && !name.startsWith("add")) {
                return null;
            }
            name = name.substring(3);
        }
        return name.length() > 0 ? StringUtil.decapitalize(name) : name;
    }

    private final static class LambdaDetail extends SerializedLambda {

        private final String propertyName;
        private final boolean setter;
        private final boolean getter;

        public LambdaDetail(SerializedLambda lambda) {
            super(lambda.getCapturingClass(),//
                lambda.getFunctionalInterfaceClassName(),//
                lambda.getFunctionalInterfaceMethodName(), //
                lambda.getFunctionalInterfaceMethodSignature(),//
                lambda.getImplClassName(),//
                lambda.getImplMethodName(),//
                lambda.getImplMethodSignature(),//
                lambda.getImplMethodKind(),//
                lambda.getInstantiatedMethodType(),//
                lambda.getCapturedArgs());
            this.propertyName = lambda.getPropertyName();
            this.setter = lambda.isSetter();
            this.getter = lambda.isGetter();
        }

        @Override
        public String getPropertyName() { return propertyName; }

        @Override
        public boolean isGetter() { return getter; }

        @Override
        public boolean isSetter() { return setter; }
    }

    private final static class LambdaCached {

        private final static Map<Object, SerializedLambda> CACHING = new WeakHashMap<>();

        private static <T extends Serializable> SerializedLambda getOrPull(
            T serializable, BiIntFunction<T, SerializedLambda> resolver
        ) {
            // 先读取缓存
            Map<Object, SerializedLambda> caching = LambdaCached.CACHING;
            final int hash = serializable.hashCode();
            SerializedLambda resolved = caching.get(hash);
            if (resolved == null) {
                resolved = resolver.apply(serializable, hash);
                caching.put(hash, resolved);
            }
            return resolved;
        }
    }

    private static class LambdaObjectInputStream extends ObjectInputStream {

        public LambdaObjectInputStream(InputStream in) throws IOException { super(in); }

        @Override
        protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
            Class<?> clazz;
            try {
                clazz = ClassUtil.forName(desc.getName());
            } catch (Exception ex) {
                clazz = super.resolveClass(desc);
            }
            return clazz == java.lang.invoke.SerializedLambda.class ? SerializedLambda.class : clazz;
        }
    }
}
