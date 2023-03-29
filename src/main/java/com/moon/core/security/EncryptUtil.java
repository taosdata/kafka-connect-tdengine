package com.moon.core.security;

import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static com.moon.core.security.EncryptType.*;

/**
 * @author moonsky
 */
public final class EncryptUtil {

    private EncryptUtil() { noInstanceError(); }

    public static String md5(String input) { return MD5.encrypt(input); }

    public static String sha1(String input) { return SHA_1.encrypt(input); }

    public static String sha256(String input) { return SHA_256.encrypt(input); }

    public static String sha384(String input) { return SHA_384.encrypt(input); }

    public static String sha512(String input) { return SHA_512.encrypt(input); }

    public static String encrypt(String input, EncryptType... types) {
        for (EncryptType type : types) { input = type.encrypt(input); }
        return input;
    }

    public static String encrypt(String input, String... types) {
        for (String type : types) { input = EncryptType.encrypt(input, forName(type)); }
        return input;
    }
}
