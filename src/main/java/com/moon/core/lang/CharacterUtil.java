package com.moon.core.lang;

import static com.moon.core.lang.CharUtil.toCharValue;
import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
public final class CharacterUtil {
    private CharacterUtil() {
        noInstanceError();
    }

    /**
     * @param value 带转换的值
     * @return 字符
     * @see IntUtil#toIntValue(Object)
     */
    public static final Character toCharacter(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Character) {
            return (Character) value;
        }
        if (value instanceof Number) {
            return (char) ((Number) value).intValue();
        }
        if (value instanceof CharSequence) {
            return toCharValue(value.toString());
        }
        if (value instanceof Boolean) {
            boolean bool = (boolean) value;
            return (char) (bool ? 1 : 0);
        }
        try {
            return toCharValue(ParseSupportUtil.unboxing(value));
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Can not cast to Character of: %s", value), e);
        }
    }
}
