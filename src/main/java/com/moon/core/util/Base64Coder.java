package com.moon.core.util;

/**
 * @author moonsky
 */
abstract class Base64Coder {

    @SuppressWarnings("all")
    protected static final String based64Codes = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
        "abcdefghijklmnopqrstuvwxyz" +
        "0123456789";

    protected static final char[] toBase64 = (based64Codes + "+/").toCharArray();
    protected static final char[] toBase64URL = (based64Codes + "-_").toCharArray();

    protected Base64Coder() {}
}
