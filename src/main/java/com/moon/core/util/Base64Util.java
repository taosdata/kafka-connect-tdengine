package com.moon.core.util;

import com.moon.core.lang.ThrowUtil;

import java.util.Objects;

import static com.moon.core.util.Base64Decoder.*;

/**
 * Copied by jdk1.8{@link java.util.Base64}
 *
 * @author moonsky
 */
public class Base64Util {

    private Base64Util() { ThrowUtil.noInstanceError(); }

    public static Base64Encoder getEncoder() { return Base64Encoder.RFC4648; }

    public static Base64Encoder getUrlEncoder() { return Base64Encoder.RFC4648_URL_SAFE; }

    public static Base64Encoder getMimeEncoder() { return Base64Encoder.RFC2045; }

    public static Base64Encoder getMimeEncoder(int lineLength, byte[] lineSeparator) {
        Objects.requireNonNull(lineSeparator);
        int[] base64 = Base64Decoder.fromBase64;
        for (byte b : lineSeparator) {
            if (base64[b & 0xff] != -1) {
                throw new IllegalArgumentException(
                    "Illegal base64 line separator character 0x" + Integer.toString(b, 16));
            }
        }
        if (lineLength <= 0) {
            return Base64Encoder.RFC4648;
        }
        return new Base64Encoder(false, lineSeparator, lineLength >> 2 << 2, true);
    }

    public static Base64Decoder getDecoder() { return RFC4648; }

    public static Base64Decoder getUrlDecoder() { return RFC4648_URL_SAFE; }

    public static Base64Decoder getMimeDecoder() { return RFC2045; }
}
