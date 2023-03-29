package com.moon.core.awt;

/**
 * @author moonsky
 */
enum Maths {
    /**
     * 最大值
     */
    MAX {
        @Override
        public int compute(int left, int right) { return Math.max(left, right); }
    },
    /**
     * 最小值
     */
    MIN {
        @Override
        public int compute(int left, int right) { return Math.min(left, right); }
    },
    /**
     * 平均值
     */
    AVG {
        @Override
        public int compute(int left, int right) { return (left + right) / 2; }
    },
    ;

    public abstract int compute(int left, int right);
}
