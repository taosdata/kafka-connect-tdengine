package com.moon.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author moonsky
 */
public final class Base64Decoder extends Base64Coder {

    private final boolean isURL;
    private final boolean isMIME;

    private Base64Decoder(boolean isURL, boolean isMIME) {
        this.isURL = isURL;
        this.isMIME = isMIME;
    }

    static final int[] fromBase64 = new int[256];

    static {
        Arrays.fill(fromBase64, -1);
        for (int i = 0; i < toBase64.length; i++) {
            fromBase64[toBase64[i]] = i;
        }
        fromBase64['='] = -2;
    }

    static final int[] fromBase64URL = new int[256];

    static {
        Arrays.fill(fromBase64URL, -1);
        for (int i = 0; i < toBase64URL.length; i++) {
            fromBase64URL[toBase64URL[i]] = i;
        }
        fromBase64URL['='] = -2;
    }

    static final Base64Decoder RFC4648 = new Base64Decoder(false, false);
    static final Base64Decoder RFC4648_URL_SAFE = new Base64Decoder(true, false);
    static final Base64Decoder RFC2045 = new Base64Decoder(false, true);

    public byte[] decode(byte[] src) {
        byte[] dst = new byte[outLength(src, 0, src.length)];
        int ret = decode0(src, 0, src.length, dst);
        if (ret != dst.length) {
            dst = Arrays.copyOf(dst, ret);
        }
        return dst;
    }

    public byte[] decode(String src) {
        return decode(src.getBytes(StandardCharsets.ISO_8859_1));
    }

    public int decode(byte[] src, byte[] dst) {
        int len = outLength(src, 0, src.length);
        if (dst.length < len) {
            throw new IllegalArgumentException("Output byte array is too small for decoding all input bytes");
        }
        return decode0(src, 0, src.length, dst);
    }

    public ByteBuffer decode(ByteBuffer buffer) {
        int pos0 = buffer.position();
        try {
            byte[] src;
            int sp, sl;
            if (buffer.hasArray()) {
                src = buffer.array();
                sp = buffer.arrayOffset() + buffer.position();
                sl = buffer.arrayOffset() + buffer.limit();
                buffer.position(buffer.limit());
            } else {
                src = new byte[buffer.remaining()];
                buffer.get(src);
                sp = 0;
                sl = src.length;
            }
            byte[] dst = new byte[outLength(src, sp, sl)];
            return ByteBuffer.wrap(dst, 0, decode0(src, sp, sl, dst));
        } catch (IllegalArgumentException iae) {
            buffer.position(pos0);
            throw iae;
        }
    }

    public InputStream wrap(InputStream is) {
        return new DecodeInputStream(Objects.requireNonNull(is), isURL ? fromBase64URL : fromBase64, isMIME);
    }

    private int outLength(byte[] src, int sp, int sl) {
        int[] base64 = isURL ? fromBase64URL : fromBase64;
        int paddings = 0;
        int len = sl - sp;
        if (len == 0) { return 0; }
        if (len < 2) {
            if (isMIME && base64[0] == -1) { return 0; }
            throw new IllegalArgumentException("Input byte[] should at least have 2 bytes for base64 bytes");
        }
        if (isMIME) {
            // scan all bytes to fill out all non-alphabet. a performance
            // trade-off of pre-scan or Arrays.copyOf
            int n = 0;
            while (sp < sl) {
                int b = src[sp++] & 0xff;
                if (b == '=') {
                    len -= (sl - sp + 1);
                    break;
                }
                if ((b = base64[b]) == -1) { n++; }
            }
            len -= n;
        } else {
            if (src[sl - 1] == '=') {
                paddings++;
                if (src[sl - 2] == '=') { paddings++; }
            }
        }
        if (paddings == 0 && (len & 0x3) != 0) { paddings = 4 - (len & 0x3); }
        return 3 * ((len + 3) / 4) - paddings;
    }

    private int decode0(byte[] src, int sp, int sl, byte[] dst) {
        int[] base64 = isURL ? fromBase64URL : fromBase64;
        int dp = 0;
        int bits = 0;
        int shiftto = 18;
        // pos of first byte of 4-byte atom
        while (sp < sl) {
            int b = src[sp++] & 0xff;
            if ((b = base64[b]) < 0) {
                if (b == -2) {
                    // padding byte '='
                    // =     shiftto==18 unnecessary padding
                    // x=    shiftto==12 a dangling single x
                    // x     to be handled together with non-padding case
                    // xx=   shiftto==6&&sp==sl missing last =
                    // xx=y  shiftto==6 last is not =
                    if (shiftto == 6 && (sp == sl || src[sp++] != '=') || shiftto == 18) {
                        throw new IllegalArgumentException("Input byte array has wrong 4-byte ending unit");
                    }
                    break;
                }
                if (isMIME)    // skip if for rfc2045
                { continue; } else {
                    throw new IllegalArgumentException("Illegal base64 character " + Integer.toString(src[sp - 1], 16));
                }
            }
            bits |= (b << shiftto);
            shiftto -= 6;
            if (shiftto < 0) {
                dst[dp++] = (byte) (bits >> 16);
                dst[dp++] = (byte) (bits >> 8);
                dst[dp++] = (byte) (bits);
                shiftto = 18;
                bits = 0;
            }
        }
        // reached end of byte array or hit padding '=' characters.
        if (shiftto == 6) {
            dst[dp++] = (byte) (bits >> 16);
        } else if (shiftto == 0) {
            dst[dp++] = (byte) (bits >> 16);
            dst[dp++] = (byte) (bits >> 8);
        } else if (shiftto == 12) {
            // dangling single "x", incorrectly encoded.
            throw new IllegalArgumentException("Last unit does not have enough valid bits");
        }
        // anything left is invalid, if is not MIME.
        // if MIME, ignore all non-base64 character
        while (sp < sl) {
            if (isMIME && base64[src[sp++]] < 0) { continue; }
            throw new IllegalArgumentException("Input byte array has incorrect ending byte at " + sp);
        }
        return dp;
    }

    /**
     * An input stream for decoding Base64 bytes
     */
    private static class DecodeInputStream extends InputStream {

        private final InputStream is;
        private final boolean isMIME;
        private final int[] base64;      // base64 -> byte mapping
        private int bits = 0;            // 24-bit buffer for decoding
        private int nextin = 18;         // next available "off" in "bits" for input;
        // -> 18, 12, 6, 0
        private int nextout = -8;        // next available "off" in "bits" for output;
        // -> 8, 0, -8 (no byte for output)
        private boolean eof = false;
        private boolean closed = false;

        DecodeInputStream(InputStream is, int[] base64, boolean isMIME) {
            this.is = is;
            this.base64 = base64;
            this.isMIME = isMIME;
        }

        private byte[] sbBuf = new byte[1];

        @Override
        public int read() throws IOException {
            return read(sbBuf, 0, 1) == -1 ? -1 : sbBuf[0] & 0xff;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (closed) { throw new IOException("Stream is closed"); }
            if (eof && nextout < 0)    // eof and no leftover
            { return -1; }
            if (off < 0 || len < 0 || len > b.length - off) { throw new IndexOutOfBoundsException(); }
            int oldOff = off;
            if (nextout >= 0) {       // leftover output byte(s) in bits buf
                do {
                    if (len == 0) { return off - oldOff; }
                    b[off++] = (byte) (bits >> nextout);
                    len--;
                    nextout -= 8;
                } while (nextout >= 0);
                bits = 0;
            }
            while (len > 0) {
                int v = is.read();
                if (v == -1) {
                    eof = true;
                    if (nextin != 18) {
                        if (nextin == 12) { throw new IOException("Base64 stream has one un-decoded dangling byte."); }
                        // treat ending xx/xxx without padding character legal.
                        // same logic as v == '=' below
                        b[off++] = (byte) (bits >> (16));
                        len--;
                        if (nextin == 0) {           // only one padding byte
                            if (len == 0) {          // no enough output space
                                bits >>= 8;          // shift to lowest byte
                                nextout = 0;
                            } else {
                                b[off++] = (byte) (bits >> 8);
                            }
                        }
                    }
                    if (off == oldOff) { return -1; } else { return off - oldOff; }
                }
                if (v == '=') {                  // padding byte(s)
                    // =     shiftto==18 unnecessary padding
                    // x=    shiftto==12 dangling x, invalid unit
                    // xx=   shiftto==6 && missing last '='
                    // xx=y  or last is not '='
                    if (nextin == 18 || nextin == 12 || nextin == 6 && is.read() != '=') {
                        throw new IOException("Illegal base64 ending sequence:" + nextin);
                    }
                    b[off++] = (byte) (bits >> (16));
                    len--;
                    if (nextin == 0) {           // only one padding byte
                        if (len == 0) {          // no enough output space
                            bits >>= 8;          // shift to lowest byte
                            nextout = 0;
                        } else {
                            b[off++] = (byte) (bits >> 8);
                        }
                    }
                    eof = true;
                    break;
                }
                if ((v = base64[v]) == -1) {
                    if (isMIME)                 // skip if for rfc2045
                    { continue; } else { throw new IOException("Illegal base64 character " + Integer.toString(v, 16)); }
                }
                bits |= (v << nextin);
                if (nextin == 0) {
                    nextin = 18;    // clear for next
                    nextout = 16;
                    while (nextout >= 0) {
                        b[off++] = (byte) (bits >> nextout);
                        len--;
                        nextout -= 8;
                        if (len == 0 && nextout >= 0) {  // don't clean "bits"
                            return off - oldOff;
                        }
                    }
                    bits = 0;
                } else {
                    nextin -= 6;
                }
            }
            return off - oldOff;
        }

        @Override
        public int available() throws IOException {
            if (closed) { throw new IOException("Stream is closed"); }
            return is.available();   // TBD:
        }

        @Override
        public void close() throws IOException {
            if (!closed) {
                closed = true;
                is.close();
            }
        }
    }
}
