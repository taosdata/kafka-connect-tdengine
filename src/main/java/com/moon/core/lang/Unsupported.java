package com.moon.core.lang;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 仅做标记用，代表注解位置的功能目前不支持
 * <p>
 * 如果调用可能不会报错，但一定不会有效
 * <p>
 * {@link UnsupportedOperationException}往往只能在执行到具体位置时才报错
 * 这时可能已经调用相关代码、花费相应时间，这个注解可标记在某个功能的入口
 * <p>
 * 将来可能会支持，也可能会删除
 *
 * @author moonsky
 */
@Target({
    ElementType.ANNOTATION_TYPE,
    ElementType.CONSTRUCTOR,
    ElementType.FIELD,
    ElementType.LOCAL_VARIABLE,
    ElementType.METHOD,
    ElementType.PACKAGE,
    ElementType.PARAMETER,
    ElementType.TYPE,
    ElementType.TYPE_PARAMETER,
    ElementType.TYPE_USE
})
@Retention(RetentionPolicy.SOURCE)
public @interface Unsupported {

    /**
     * 提示文字
     *
     * @return
     */
    String value() default "";
}
