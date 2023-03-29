package com.moon.core.beans;

/**
 * 字段读取/设置执行器
 *
 * @author ZhangDongMin
 */
@FunctionalInterface
public interface FieldExecutor {
    /**
     * 执行
     *
     * @param source     源对象
     * @param value      字段值
     * @param accessAble 访问权限
     * @return 字段值（具体根据实现）
     * @throws Exception 执行异常或访问异常
     */
    Object execute(Object source, Object value, boolean accessAble)
        throws Exception;
}
