package com.moon.core.enums;

import com.moon.core.lang.CharUtil;

import java.util.function.IntPredicate;
import java.util.function.Predicate;

/**
 * @author moonsky
 */
public enum IntTesters implements IntPredicate {
    /**
     * true
     */
    TRUE {
        @Override
        public boolean test(int value) { return true; }
    },
    /**
     * false
     */
    FALSE {
        @Override
        public boolean test(int value) { return false; }
    },
    /**
     * 数字
     */
    DIGIT {
        @Override
        public boolean test(int value) { return CharUtil.isDigit(value); }
    },
    /**
     * 数字
     */
    NUMERIC {
        @Override
        public boolean test(int value) { return CharUtil.isDigit(value); }
    },
    /**
     * 大写或小写字母
     */
    LETTER {
        @Override
        public boolean test(int value) { return CharUtil.isLetter(value); }
    },
    /**
     * 大写字母
     */
    UPPER {
        @Override
        public boolean test(int value) { return CharUtil.isUpperCase(value); }
    },
    /**
     * 小写字母
     */
    LOWER {
        @Override
        public boolean test(int value) { return CharUtil.isLowerCase(value); }
    },
    /**
     * 空白字符
     */
    BLANK {
        @Override
        public boolean test(int value) { return Character.isWhitespace(value); }
    },
    /**
     * 0
     */
    ZERO {
        @Override
        public boolean test(int value) { return value == 0; }
    },
    /**
     * 1
     */
    ONE {
        @Override
        public boolean test(int value) { return value == 1; }
    },
    /**
     * 负整数
     */
    NEGATIVE {
        @Override
        public boolean test(int value) { return value < 0; }
    },
    /**
     * 负整数和〇
     */
    NEGATIVE_OR_ZERO {
        @Override
        public boolean test(int value) { return value <= 0; }
    },
    /**
     * 正整数
     */
    POSITIVE {
        @Override
        public boolean test(int value) { return value > 0; }
    },
    /**
     * 自然数：正整数和〇
     */
    POSITIVE_OR_ZERO {
        @Override
        public boolean test(int value) { return value >= 0; }
    },
    /**
     * 奇数
     */
    ODD {
        @Override
        public boolean test(int value) { return (value & 1) == 1; }
    },
    /**
     * 偶数
     */
    EVEN {
        @Override
        public boolean test(int value) { return (value & 1) == 0; }
    },
    /**
     * 汉字
     */
    CHINESE_WORD {
        @Override
        public boolean test(int value) { return value > 19967 && value < 40880; }
    };

    public final Predicate<Number> notPredicate;
    public final Predicate<Number> predicate;
    public final IntPredicate not;

    IntTesters() {
        predicate = n -> n != null && test(n.intValue());
        notPredicate = predicate.negate();
        not = value -> !test(value);
    }

    public Predicate<Number> asPredicate() { return predicate; }

    /**
     * 返回断言函数，用来检查一个值是否小于 value
     *
     * @param value 期望值
     *
     * @return 断言函数
     */
    public static Predicate lt(double value) { return o -> isNum(o) && ((Number) o).doubleValue() < value; }

    /**
     * 返回断言函数，用来检查一个值是否小于或等于 value
     *
     * @param value 期望值
     *
     * @return 断言函数
     */
    public static Predicate le(double value) { return o -> isNum(o) && ((Number) o).doubleValue() <= value; }

    /**
     * 返回断言函数，用来检查一个值是否大于 value
     *
     * @param value 期望值
     *
     * @return 断言函数
     */
    public static Predicate gt(double value) { return o -> isNum(o) && ((Number) o).doubleValue() > value; }

    /**
     * 返回断言函数，用来检查一个值是否大于或等于 value
     *
     * @param value 期望值
     *
     * @return 断言函数
     */
    public static Predicate ge(double value) { return o -> isNum(o) && ((Number) o).doubleValue() >= value; }

    /**
     * 返回断言函数，用来检查一个值是否等于 value
     *
     * @param value 期望值
     *
     * @return 断言函数
     */
    public static Predicate eq(double value) { return o -> isNum(o) && ((Number) o).doubleValue() == value; }

    /**
     * 返回断言函数，用来检查一个值是否不等于 value
     *
     * @param value 期望值
     *
     * @return 断言函数
     */
    public static Predicate not(double value) { return o -> isNum(o) && ((Number) o).doubleValue() != value; }

    /**
     * 判断对象是否是{@link Number}
     *
     * @param o 待测对象
     *
     * @return true: 是数字；否则返回 false
     */
    private static boolean isNum(Object o) { return o instanceof Number; }
}
