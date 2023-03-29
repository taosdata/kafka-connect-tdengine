package com.moon.core.enums;

import static com.moon.core.lang.ThrowUtil.noInstanceError;
/**
 * @author moonsky
 */
public final class Const {

    private Const() { noInstanceError(); }

    public static final int ZERO = 0;
    public static final int ONE = 1;
    public static final int MINUS_ONE = -1;
    public static final int DEFAULT_SIZE = 16;

    public static final boolean TRUE = true;
    public static final boolean FALSE = false;


    /**
     * ","——英文逗号
     */
    public static final char CHAR_COMMA = Chars.COMMA.value;
    public static final char CHAR_MINUS = Chars.MINUS.value;

    /**
     * string
     */
    public static final String EMPTY = Str.EMPTY;
    public static final String STR_NULL = Str.NULL;
    public static final int WIN_FILE_INVALID_CHAR = 65279;

    public static final class Int {

        public final static int ZERO = 0;
        public final static int ONE = 1;
        public final static int TEN = 10;
    }

    public static final class Str {

        public final static String EMPTY = new String();
        public final static String COMMA = ",";

        public final static String UNDEFINED = "undefined";
        public final static String TRUE = Boolean.TRUE.toString();
        public final static String FALSE = Boolean.FALSE.toString();
        public final static String NULL = "null";

        public final static String YES = "YES";
        public final static String NO = "NO";
        public final static String Y = "Y";
        public final static String N = "N";

        public final static String ZERO = "0";
        public final static String ONE = "1";
        public final static String TEN = "10";
        public final static String NaN = "NaN";
    }
}
