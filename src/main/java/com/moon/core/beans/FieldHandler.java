package com.moon.core.beans;

/**
 * @author ZhangDongMin
 */
@FunctionalInterface
public interface FieldHandler {
    /**
     * 方法执行
     *
     * @param source 源对象
     * @param value  字段值
     * @return 返回字段值，或 setter
     * @throws Exception 执行异常或访问异常
     */
    Object handle(Object source, Object value) throws Exception;
}
