package com.moon.core.lang;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 仅做标记用，代表注解位置的功能不推荐使用，但暂时还保留着
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
public @interface Warning {

    /**
     * 提示文字
     *
     * @return
     */
    String value() default "";
}
