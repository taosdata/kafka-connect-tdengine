package com.moon.core.util.condition;

import java.util.function.BooleanSupplier;

/**
 * @author benshaoye
 */
public final class DynamicCondition implements Conditional {

    private final BooleanSupplier dynamicCondition;

    public DynamicCondition(boolean matched) {
        this(matched ? BooleanStrategy.TRUE : BooleanStrategy.FALSE);
    }

    public DynamicCondition(BooleanSupplier dynamicCondition) {
        this.dynamicCondition = dynamicCondition;
    }

    public static DynamicCondition of(boolean condition) { return new DynamicCondition(condition); }

    public static DynamicCondition of(BooleanSupplier dynamicCondition) { return new DynamicCondition(dynamicCondition); }

    /**
     * 返回是否符合条件
     *
     * @return true: 符合条件
     */
    @Override
    public boolean isTrue() {
        return dynamicCondition.getAsBoolean();
    }

    @SuppressWarnings("all")
    enum BooleanStrategy implements BooleanSupplier {
        TRUE(Boolean.TRUE),
        FALSE(Boolean.FALSE);

        private final Boolean value;

        BooleanStrategy(Boolean value) { this.value = value; }

        @Override
        public boolean getAsBoolean() { return value; }
    }
}
