package com.moon.core.util;

import com.moon.core.lang.ThrowUtil;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机数工具，线程安全
 *
 * @author moonsky
 */
public final class RandomUtil {

    private RandomUtil() {
        ThrowUtil.noInstanceError();
    }

    private volatile static ThreadLocalRandom random = current();

    public static ThreadLocalRandom get() {
        ThreadLocalRandom r = random;
        return r == null ? next() : r;
    }

    public static synchronized ThreadLocalRandom next() {
        return random = current();
    }

    public static ThreadLocalRandom current() {
        return ThreadLocalRandom.current();
    }

    public static int nextInt() {
        return get().nextInt();
    }

    public static int nextInt(int bound) {
        return get().nextInt(bound);
    }

    public static int nextInt(int min, int max) {
        return get().nextInt(min, max);
    }

    public static long nextLong() {
        return get().nextLong();
    }

    public static long nextLong(long bound) {
        return get().nextLong(bound);
    }

    public static long nextLong(long min, long max) {
        return get().nextLong(min, max);
    }

    public static double nextDouble() {
        return get().nextDouble();
    }

    public static double nextDouble(double bound) {
        return get().nextDouble(bound);
    }

    public static double nextDouble(double min, double max) {
        return get().nextDouble(min, max);
    }

    public static boolean nextBoolean() {
        return get().nextBoolean();
    }

    /**
     * 数组乱序
     *
     * @param arr 数组
     * @param <T> 数组元素类型
     *
     * @return 乱序后的数组
     */
    public static <T> T[] randomOrder(T[] arr) {
        Arrays.sort(arr, (o1, o2) -> nextInt());
        return arr;
    }
}
