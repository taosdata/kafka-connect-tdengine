package com.moon.core.lang.ref;

import java.lang.ref.Reference;
import java.util.function.Supplier;

/**
 * 缓存一个访问过程
 * <p>
 * 此类的作用是将一个对象的返回值缓存起来，
 * <p>
 * 缓存的对象不保证一直存在，在适当的时候可能会被回收
 * <p>
 * 但当再一次访问的时候，会去执行这个过程，再一次获得这个值并缓存
 * <p>
 * 频繁访问的时候有助于提高速度。
 * <p>
 * 传统的单例模式主要分为饿汉模式和懒汉模式，以及利用方法锁、对象锁、类加载机制或枚举及其衍生方法，
 * <p>
 * 但是这些模式在处理时间和空间的关系上还有一点缺憾，对于小对象，简单值等
 * <p>
 * 直接创建一个静态实例，无论什么模式，所占用的空间是可以接受的，同样对于单纯的大对象，
 * <p>
 * 无论是类加载时加载还是使用时加载，都会一直存在，时间和空间的协调上有不合适的地方。
 * <p>
 * 此类主要针对在一定时期内频繁访问，而其他时候并不重要的对象，这时候我们可以将这个过程
 * <p>
 * 缓存，使用完毕之后内存空间仍然可能会被释放和再次利用。
 *
 * @author moonsky
 */
public class PhantomAccessor<T> extends BaseAccessor<T, PhantomAccessor<T>> {

    public PhantomAccessor(Supplier<T> supplier) { this(supplier, false); }

    public PhantomAccessor(Supplier<T> supplier, boolean allowNullValue) { this(supplier, allowNullValue, false); }

    public PhantomAccessor(Supplier<T> supplier, boolean allowNullValue, boolean initValue) {
        super(supplier, allowNullValue, initValue);
    }

    @Override
    protected Reference<T> reference(T value) { return ReferenceUtil.weak(value); }

    public static <T> PhantomAccessor<T> of(Supplier<T> supplier) { return new PhantomAccessor<>(supplier); }

    public static <T> PhantomAccessor<T> ofNullable(Supplier<T> supplier) {
        return new PhantomAccessor<>(supplier, true);
    }
}
