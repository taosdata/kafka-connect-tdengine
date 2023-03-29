package com.moon.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 通用建造者，使用方法：
 * <p>
 * 摘自：https://mp.weixin.qq.com/s/VzkdhRUUAGgKpTWspeypkA
 * <pre>
 * package com.example;
 *
 * public class Employee {
 *
 *     private String username;
 *     private String address;
 *     private int age;
 *
 *     public void setUsername(String username) {
 *         this.username = username;
 *     }
 *
 *     public void setAge(int age) {
 *         this.age = age;
 *     }
 *
 *     // 省略其他 setter 和 getter 方法
 *
 *     public static void main(String[] args) {
 *         Employee employee = Builder.of(Employee::new)
 *             .with(Employee::setUsername, "username")
 *             .with(Employee::setAge, 24)
 *             // 其他 setter
 *             .build();
 *     }
 * }
 * </pre>
 *
 * @author benshaoye
 */
public final class Builder<T> {

    private final Supplier<T> creator;
    private List<Consumer<T>> setters;

    public Builder(Supplier<T> creator) { this.creator = creator; }

    private List<Consumer<T>> ensureSetters() {
        return setters == null ? (setters = new ArrayList<>()) : setters;
    }

    public static <T> Builder<T> of(Supplier<T> creator) {
        return new Builder<>(creator);
    }

    public <P> Builder<T> with(Setter<T, P> setter, P param) {
        ensureSetters().add(it -> setter.set(it, param));
        return this;
    }

    public <P1, P2> Builder<T> with(BiSetter<T, P1, P2> setter, P1 p1, P2 p2) {
        ensureSetters().add(it -> setter.set(it, p1, p2));
        return this;
    }

    public <P1, P2, P3> Builder<T> with(MultiSetter<T, P1, P2, P3> setter, P1 p1, P2 p2, P3 p3) {
        ensureSetters().add(it -> setter.set(it, p1, p2, p3));
        return this;
    }

    public T build() {
        if (creator == null) {
            return null;
        }
        T instance = creator.get();
        List<Consumer<T>> setters = this.setters;
        if (setters != null) {
            for (Consumer<T> setter : setters) {
                setter.accept(instance);
            }
        }
        return instance;
    }

    public interface Setter<T, P> {

        /**
         * 设置值
         *
         * @param it    对象
         * @param param 值
         */
        void set(T it, P param);
    }

    public interface BiSetter<T, P1, P2> {

        /**
         * 设置值
         *
         * @param it 对象
         * @param p1 值
         * @param p2 值
         */
        void set(T it, P1 p1, P2 p2);
    }

    public interface MultiSetter<T, P1, P2, P3> {

        /**
         * 设置值
         *
         * @param it 对象
         * @param p1 值
         * @param p2 值
         * @param p3 值
         */
        void set(T it, P1 p1, P2 p2, P3 p3);
    }
}
