package com.moon.core.lang.reflect;

/**
 * @author moonsky
 */
public enum MatchLevel {
    /**
     * 强匹配，比如用“==”判断的或者用 a.equals(b) 判断；
     */
    FINAL,
    /**
     * 软匹配，比如自动类型转换，int to long、char to int 等；
     */
    SOFT,
    /**
     * 弱匹配，比如用 instanceof 、基本类型的包装类型等符合继承规则的判断；
     */
    WEAK,
    /**
     * 不匹配，不属于以上任一种
     */
    NONE
}
