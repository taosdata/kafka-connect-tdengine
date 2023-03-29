package com.moon.core.lang;

import com.moon.core.lang.support.NumberSupport;

import java.math.BigDecimal;
import java.util.Objects;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
public class NumberUtil {

    protected NumberUtil() { noInstanceError(); }

    /**
     * 是否是广义数字类
     *
     * @param type 待测类
     *
     * @return true | false
     */
    public final static boolean isGeneralNumberClass(Class type) {
        return isWrapperNumberClass(type) || isPrimitiveNumberClass(type) || Number.class.isAssignableFrom(type);
    }

    /**
     * 是否是数字基本数据类型类
     *
     * @param type 待测类
     *
     * @return 当 type 是 byte、short、int、long、float、double 其中之一时返回 true，否则返回 false
     */
    public final static boolean isPrimitiveNumberClass(Class type) {
        return NumberSupport.isNumberPrimitiveClass(type);
    }

    /**
     * 是否是数字基本包装类型类
     *
     * @param type 待测类
     *
     * @return 当 type 是 Byte、Short、Integer、Long、Float、Double 其中之一时返回 true，否则返回 false
     */
    public final static boolean isWrapperNumberClass(Class type) {
        return NumberSupport.isNumberWrapperClass(type);
    }

    /**
     * 用{@link Comparable#compareTo(Object)}比较两个数字大小；
     * <p>
     * {@link BigDecimal}在比较时，{@code equals}会比较精度，{@code compareTo}不会比较精度
     * 【阿里巴巴Java开发手册（嵩山版）】
     *
     * @param num1 数字
     * @param num2 数字
     *
     * @return 如果两个数字相等，返回 true
     */
    public final static boolean compareEquals(Number num1, Number num2) {
        try {
            Comparable value1 = (Comparable) num1;
            return (value1 != null && value1.compareTo(num2) == 0) || Objects.equals(num2, num1);
        } catch (ClassCastException e) {
            // http://www.cocoachina.com/articles/52532
            return Objects.equals(num2, num1);
        }
    }
}
