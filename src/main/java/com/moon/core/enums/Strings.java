package com.moon.core.enums;

import com.moon.core.lang.StringUtil;

/**
 * @author moonsky
 */
public enum Strings implements EnumDescriptor {
    /**
     * 空字符串
     */
    EMPTY(""),
    /**
     * 点：,
     */
    DOT,
    /**
     * 空格
     */
    SPACE,
    /**
     * 百分号：%
     */
    PERCENT,
    /**
     * 下划线：_
     */
    UNDERLINE,
    /**
     * 乘号：*
     */
    MULTIPLY,
    /**
     * 加号：+
     */
    PLUS,
    /**
     * 逗号：,
     */
    COMMA,
    /**
     * 减号：-
     */
    MINUS,
    /**
     * 除号：/
     */
    DIVISION,
    /**
     * 冒号：:
     */
    COLON,
    /**
     * 竖线：|
     */
    VERTICAL_LINE,
    /**
     * 常用符号
     */
    SYMBOLS("~`!@#$%^&*()_+-={}|[]\\:\";'<>?,./"),
    /**
     * 常用中文符号
     */
    CHINESE_SYMBOLS("~·！@#￥%…&*（）—+-={}|【】、：”；’《》？，。、"),
    /**
     * 数字
     */
    NUMBERS("0123456789"),
    /**
     * 大写字母
     */
    UPPERS("ABCDEFGHIJKLMNOPQRSTUVWXYZ"),
    /**
     * 小写字母
     */
    LOWERS("abcdefghijklmnopqrstuvwxyz"),
    /**
     * 字母
     */
    LETTERS("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"),
    /**
     * YES
     */
    YES("YES"),
    /**
     * YES
     */
    NO("NO"),
    ;

    public final String value;

    Strings() { this(null); }

    Strings(String value) {
        this.value = value == null ? Chars.valueOf(name()).toString() : StringUtil.distinctChars(value);
    }

    public String getValue() { return value; }

    @Override
    public String getText() { return value; }

    public static char[] toCharArray(Strings...items){
        int length = items == null ? 0 : items.length;
        if (length == 0) {
            return Arrays2.CHARS.empty();
        }
        String[] strings = new String[length];
        for (int i = 0; i < length; i++) {
            strings[i] = items[i].value;
        }
        return StringUtil.concat(strings).toCharArray();
    }
}
