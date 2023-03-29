package com.moon.core.lang.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Objects;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
public final class AnnotationUtil {
    private AnnotationUtil() { noInstanceError(); }

    public static <T extends Annotation> boolean
    isAnnotationPresent(AnnotatedElement ae, Class<T> annotationType) { return ae.isAnnotationPresent(annotationType); }

    public static <T extends Annotation> T
    get(AnnotatedElement ae, Class<T> annotationType) { return ae.getAnnotation(annotationType); }

    public static <A extends Annotation> boolean equals(A a1, A a2) { return Objects.equals(a1, a2); }
}
