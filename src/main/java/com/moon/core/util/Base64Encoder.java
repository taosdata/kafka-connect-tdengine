package com.moon.core.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author moonsky
 */
public final class Base64Encoder extends Base64Coder {

    private static final int MIME_LINE_MAX = 76;
    private static final byte[] CRLF = new byte[]{'\r', '\n'};

    private final byte[] newline;
    private final int lineMax;
    private final boolean isURL;
    private final boolean doPadding;

    Base64Encoder(boolean isURL, byte[] newline, int lineMax, boolean doPadding) {
        this.isURL = isURL;
        this.newline = newline;
        this.lineMax = lineMax;
        this.doPadding = doPadding;
    }

    static final Base64Encoder RFC4648 = new Base64Encoder(false, null, -1, true);
    static final Base64Encoder RFC4648_URL_SAFE = new Base64Encoder(true, null, -1, true);
    static final Base64Encoder RFC2045 = new Base64Encoder(false, CRLF, MIME_LINE_MAX, true);

    private final int outLength(int srcLen) {
        int len = 0;
        if (doPadding) {
            len = 4 * ((srcLen + 2) / 3);
        } else {
            int n = srcLen % 3;
            len = 4 * (srcLen / 3) + (n == 0 ? 0 : n + 1);
        }
        if (lineMax > 0) {
            // line separators
            len += (len - 1) / lineMax * newline.length;
        }
        return len;
    }


    public byte[] encode(byte[] src) {
        // dst array size
        int len = outLength(src.length);
        byte[] dst = new byte[len];
        int ret = encode0(src, 0, src.length, dst);
        if (ret != dst.length) { return Arrays.copyOf(dst, ret); }
        return dst;
    }

    public int encode(byte[] src, byte[] dst) {
        int len = outLength(src.length);         // dst array size
        if (dst.length < len) {
            throw new IllegalArgumentException("Output byte array is too small for encoding all input bytes");
        }
        return encode0(src, 0, src.length, dst);
    }

    @SuppressWarnings("deprecation")
    public String encodeToString(byte[] src) {
        byte[] encoded = encode(src);
        return new String(encoded, 0, 0, encoded.length);
    }

    public ByteBuffer encode(ByteBuffer buffer) {
        int len = outLength(buffer.remaining());
        byte[] dst = new byte[len];
        int ret = 0;
        if (buffer.hasArray()) {
            ret = encode0(buffer.array(), buffer.arrayOffset() + buffer.position(),
                buffer.arrayOffset() + buffer.limit(), dst);
            buffer.position(buffer.limit());
        } else {
            byte[] src = new byte[buffer.remaining()];
            buffer.get(src);
            ret = encode0(src, 0, src.length, dst);
        }
        if (ret != dst.length) { dst = Arrays.copyOf(dst, ret); }
        return ByteBuffer.wrap(dst);
    }


    public OutputStream wrap(OutputStream os) {
        return new EncodeOutputStream(Objects.requireNonNull(os),

            isURL ? toBase64URL : toBase64, newline, lineMax, doPadding);
    }

    public Base64Encoder withoutPadding() {
        return doPadding ? new Base64Encoder(isURL, newline, lineMax, false) : this;
    }

    private int encode0(byte[] src, int off, int end, byte[] dst) {
        char[] base64 = isURL ? toBase64URL : toBase64;
        int sp = off, lineMax = this.lineMax;
        int slen = (end - off) / 3 * 3;
        int sl = off + slen;
        if (lineMax > 0 && slen > lineMax / 4 * 3) { slen = lineMax / 4 * 3; }
        int dp = 0;
        while (sp < sl) {
            int sl0 = Math.min(sp + slen, sl);
            for (int sp0 = sp, dp0 = dp; sp0 < sl0; ) {
                int bits = (src[sp0++] & 0xff) << 16 | (src[sp0++] & 0xff) << 8 | (src[sp0++] & 0xff);
                dst[dp0++] = (byte) base64[(bits >>> 18) & 0x3f];
                dst[dp0++] = (byte) base64[(bits >>> 12) & 0x3f];
                dst[dp0++] = (byte) base64[(bits >>> 6) & 0x3f];
                dst[dp0++] = (byte) base64[bits & 0x3f];
            }
            int dlen = (sl0 - sp) / 3 * 4;
            dp += dlen;
            sp = sl0;
            if (dlen == lineMax && sp < end) {
                for (byte b : newline) {
                    dst[dp++] = b;
                }
            }
        }
        if (sp < end) {               // 1 or 2 leftover bytes
            int b0 = src[sp++] & 0xff;
            dst[dp++] = (byte) base64[b0 >> 2];
            if (sp == end) {
                dst[dp++] = (byte) base64[(b0 << 4) & 0x3f];
                if (doPadding) {
                    dst[dp++] = '=';
                    dst[dp++] = '=';
                }
            } else {
                int b1 = src[sp++] & 0xff;
                dst[dp++] = (byte) base64[(b0 << 4) & 0x3f | (b1 >> 4)];
                dst[dp++] = (byte) base64[(b1 << 2) & 0x3f];
                if (doPadding) {
                    dst[dp++] = '=';
                }
            }
        }
        return dp;
    }

    /**
     * An output stream for encoding bytes into the Base64.
     */
    private static class EncodeOutputStream extends FilterOutputStream {

        private int leftover = 0;
        private int b0, b1, b2;
        private boolean closed = false;

        private final char[] base64;    // byte->base64 mapping
        private final byte[] newline;   // line separator, if needed
        private final int lineMax;
        private final boolean doPadding;// whether or not to pad
        private int linepos = 0;

        EncodeOutputStream(
            OutputStream os, char[] base64, byte[] newline, int lineMax, boolean doPadding
        ) {
            super(os);
            this.base64 = base64;
            this.newline = newline;
            this.lineMax = lineMax;
            this.doPadding = doPadding;
        }

        @Override
        public void write(int b) throws IOException {
            byte[] buf = new byte[1];
            buf[0] = (byte) (b & 0xff);
            write(buf, 0, 1);
        }

        private void checkNewline() throws IOException {
            if (linepos == lineMax) {
                out.write(newline);
                linepos = 0;
            }
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            if (closed) { throw new IOException("Stream is closed"); }
            if (off < 0 || len < 0 || len > b.length - off) { throw new ArrayIndexOutOfBoundsException(); }
            if (len == 0) { return; }
            if (leftover != 0) {
                if (leftover == 1) {
                    b1 = b[off++] & 0xff;
                    len--;
                    if (len == 0) {
                        leftover++;
                        return;
                    }
                }
                b2 = b[off++] & 0xff;
                len--;
                checkNewline();
                out.write(base64[b0 >> 2]);
                out.write(base64[(b0 << 4) & 0x3f | (b1 >> 4)]);
                out.write(base64[(b1 << 2) & 0x3f | (b2 >> 6)]);
                out.write(base64[b2 & 0x3f]);
                linepos += 4;
            }
            int nBits24 = len / 3;
            leftover = len - (nBits24 * 3);
            while (nBits24-- > 0) {
                checkNewline();
                int bits = (b[off++] & 0xff) << 16 | (b[off++] & 0xff) << 8 | (b[off++] & 0xff);
                out.write(base64[(bits >>> 18) & 0x3f]);
                out.write(base64[(bits >>> 12) & 0x3f]);
                out.write(base64[(bits >>> 6) & 0x3f]);
                out.write(base64[bits & 0x3f]);
                linepos += 4;
            }
            if (leftover == 1) {
                b0 = b[off++] & 0xff;
            } else if (leftover == 2) {
                b0 = b[off++] & 0xff;
                b1 = b[off++] & 0xff;
            }
        }

        @Override
        public void close() throws IOException {
            if (!closed) {
                closed = true;
                if (leftover == 1) {
                    checkNewline();
                    out.write(base64[b0 >> 2]);
                    out.write(base64[(b0 << 4) & 0x3f]);
                    if (doPadding) {
                        out.write('=');
                        out.write('=');
                    }
                } else if (leftover == 2) {
                    checkNewline();
                    out.write(base64[b0 >> 2]);
                    out.write(base64[(b0 << 4) & 0x3f | (b1 >> 4)]);
                    out.write(base64[(b1 << 2) & 0x3f]);
                    if (doPadding) {
                        out.write('=');
                    }
                }
                leftover = 0;
                out.close();
            }
        }
    }
}
