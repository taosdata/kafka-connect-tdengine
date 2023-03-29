package com.moon.core.io;

import com.moon.core.lang.ThrowUtil;
import com.moon.core.lang.ref.LongAccessor;
import com.moon.core.util.IteratorUtil;
import com.moon.core.util.ResourceUtil;
import com.moon.core.util.function.ThrowingConsumer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.channels.Selector;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Objects;
import java.util.stream.Stream;

import static com.moon.core.enums.Charsets.UTF_8;
import static com.moon.core.io.FileUtil.getInputStream;
import static com.moon.core.io.FileUtil.getOutputStream;
import static com.moon.core.lang.ThrowUtil.runtime;
import static com.moon.core.util.IteratorUtil.forEachLines;

/**
 * @author moonsky
 */
public final class IOUtil {

    private IOUtil() { ThrowUtil.noInstanceError(); }

    /*
     * -----------------------------------------------------------------------
     * get stream
     * -----------------------------------------------------------------------
     */

    public static InputStream getResourceAsStream(String path) {
        return ResourceUtil.getResourceAsInputStream(path);
    }

    public static BufferedOutputStream getBufferedOutputStream(File file) {
        return getBufferedOutputStream(getOutputStream(file));
    }

    public static BufferedOutputStream getBufferedOutputStream(OutputStream os) {
        return os instanceof BufferedOutputStream ? (BufferedOutputStream) os : new BufferedOutputStream(os);
    }

    public static BufferedInputStream getBufferedInputStream(File file) {
        return getBufferedInputStream(getInputStream(file));
    }

    public static BufferedInputStream getBufferedInputStream(InputStream is) {
        return is instanceof BufferedInputStream ? (BufferedInputStream) is : new BufferedInputStream(is);
    }

    /*
     * -----------------------------------------------------------------------
     * get buffered writer
     * -----------------------------------------------------------------------
     */

    public static BufferedWriter getBufferedWriter(OutputStream os, Charset charset) {
        return new BufferedWriter(getWriter(os, charset));
    }

    public static BufferedWriter getBufferedWriter(OutputStream os, String charset) {
        return getBufferedWriter(os, Charset.forName(charset));
    }

    public static BufferedWriter getBufferedWriter(OutputStream os) {
        return getBufferedWriter(os, UTF_8.charset());
    }

    public static BufferedWriter getBufferedWriter(Writer writer) {
        if (writer instanceof BufferedWriter) {
            return (BufferedWriter) writer;
        } else {
            return new BufferedWriter(writer);
        }
    }

    public static BufferedWriter getBufferedWriter(File file) { return getBufferedWriter(getWriter(file)); }

    /*
     * -----------------------------------------------------------------------
     * get writer
     * -----------------------------------------------------------------------
     */

    public static Writer getWriter(OutputStream os, Charset charset) {
        return getBufferedWriter(new OutputStreamWriter(os, charset));
    }

    public static Writer getWriter(OutputStream os, String charset) {
        return getWriter(os, Charset.forName(charset));
    }

    public static Writer getWriter(OutputStream os) { return getWriter(os, UTF_8.charset()); }

    public static Writer getWriter(File file) {
        try {
            return new FileWriter(file);
        } catch (IOException e) {
            return runtime(e);
        }
    }

    /*
     * -----------------------------------------------------------------------
     * get buffered reader
     * -----------------------------------------------------------------------
     */

    public static BufferedReader getBufferedReader(InputStream is, Charset charset) {
        return new BufferedReader(getReader(is, charset));
    }

    public static BufferedReader getBufferedReader(InputStream is, String charset) {
        return getBufferedReader(is, Charset.forName(charset));
    }

    public static BufferedReader getBufferedReader(InputStream is) {
        return getBufferedReader(is, UTF_8.charset());
    }

    public static BufferedReader getBufferedReader(Reader reader) {
        if (reader instanceof BufferedReader) {
            return (BufferedReader) reader;
        } else {
            return new BufferedReader(reader);
        }
    }

    public static BufferedReader getBufferedReader(File file) { return getBufferedReader(getReader(file)); }

    /*
     * -----------------------------------------------------------------------
     * get reader
     * -----------------------------------------------------------------------
     */

    public static Reader getReader(InputStream is, Charset charset) { return new InputStreamReader(is, charset); }

    public static Reader getReader(InputStream is, String charset) {
        return getReader(is, Charset.forName(charset));
    }

    public static Reader getReader(InputStream is) { return getReader(is, UTF_8.charset()); }

    public static Reader getReader(File file) {
        try {
            return new FileReader(file);
        } catch (FileNotFoundException e) {
            return runtime(e);
        }
    }

    public static BufferedReader getResourceAsReader(String path, Charset charset) {
        return getBufferedReader(getResourceAsStream(path), charset);
    }

    public static Reader getResourceAsReader(String path, String charset) {
        return getResourceAsReader(path, Charset.forName(charset));
    }

    public static Reader getResourceAsReader(String path) { return getResourceAsReader(path, UTF_8.charset()); }

    /*
     * -----------------------------------------------------------------------
     * to string
     * -----------------------------------------------------------------------
     */

    public static String toString(File file) { return toString(getBufferedReader(file)); }

    public static String toString(Reader reader) {
        try (StringWriter writer = new StringWriter()) {
            copy(reader, writer);
            return writer.toString();
        } catch (IOException e) {
            return runtime(e);
        }
    }

    public static String toString(InputStream in, Charset charset) {
        return toString(getBufferedReader(in, charset));
    }

    public static String toString(InputStream in, String charset) {
        return toString(getBufferedReader(in, charset));
    }

    public static String toString(InputStream in) { return toString(in, UTF_8.charset()); }

    public static String toString(URL url) { return toString(url, UTF_8.charset()); }

    public static String toString(URL url, String charset) { return toString(url, Charset.forName(charset)); }

    public static String toString(URL url, Charset charset) {
        try (InputStream is = url.openStream()) {
            return toString(is, charset);
        } catch (IOException e) {
            return runtime(e);
        }
    }

    /*
     * -----------------------------------------------------------------------
     * copies
     * -----------------------------------------------------------------------
     */

    public static long copy(InputStream is, OutputStream stream) {
        LongAccessor accessor = LongAccessor.of();
        byte[] buffer = new byte[10240];
        IteratorUtil.forEach(is, buffer, limit -> {
            write(stream, buffer, 0, limit);
            accessor.increment(limit);
        });
        flushCloseable(stream);
        return accessor.get();
    }

    public static long copy(Reader reader, Writer writer) {
        LongAccessor accessor = LongAccessor.of();
        char[] buffer = new char[5120];
        IteratorUtil.forEach(reader, buffer, limit -> {
            write(writer, buffer, 0, limit);
            accessor.increment(limit);
        });
        flush(writer);
        return accessor.get();
    }

    public static long copy(InputStream is, Writer writer) { return copy(getBufferedReader(is), writer); }

    public static long copy(InputStream is, Writer writer, String charset) {
        return copy(getBufferedReader(is, charset), writer);
    }

    public static long copy(InputStream is, Writer writer, Charset charset) {
        return copy(getBufferedReader(is, charset), writer);
    }

    public static long copy(Reader reader, OutputStream os) { return copy(reader, getBufferedWriter(os)); }

    public static long copy(Reader reader, OutputStream os, String charset) {
        return copy(reader, getBufferedWriter(os, charset));
    }

    public static long copy(Reader reader, OutputStream os, Charset charset) {
        return copy(reader, getBufferedWriter(os, charset));
    }

    public static long copy(InputStream is, StringBuilder sb, Charset charset) {
        return copy(getBufferedReader(is, charset), sb);
    }

    public static long copy(InputStream is, StringBuffer sb, Charset charset) {
        return copy(getBufferedReader(is, charset), sb);
    }

    public static long copy(InputStream is, StringBuilder sb, String charset) {
        return copy(getBufferedReader(is, charset), sb);
    }

    public static long copy(InputStream is, StringBuffer sb, String charset) {
        return copy(getBufferedReader(is, charset), sb);
    }

    public static long copy(InputStream is, StringBuilder sb) { return copy(getBufferedReader(is), sb); }

    public static long copy(InputStream is, StringBuffer sb) { return copy(getBufferedReader(is), sb); }

    public static int copy(Reader reader, StringBuilder sb) {
        int len = sb.length();
        forEachLines(reader, sb::append);
        return sb.length() - len;
    }

    public static int copy(Reader reader, StringBuffer sb) {
        int len = sb.length();
        forEachLines(reader, sb::append);
        return sb.length() - len;
    }

    /**
     * 将字符串写入 Writer
     *
     * @param cs     字符串
     * @param writer writer
     *
     * @return 写入字节数
     */
    public static int copy(CharSequence cs, Writer writer) {
        int len = cs == null ? 0 : cs.length();
        if (len > 0) {
            Objects.requireNonNull(writer);
            char[] chars = new char[len];
            cs.toString().getChars(0, len, chars, 0);
            write(writer, chars, 0, len);
        }
        return len;
    }

    /**
     * 将字符串写入输出流
     *
     * @param cs 待写字符串
     * @param os output stream
     *
     * @return 写出字符数
     */
    public static int copy(CharSequence cs, OutputStream os) {
        int len = cs == null ? 0 : cs.length();
        if (len > 0) {
            write(os, cs.toString().getBytes());
        }
        return len;
    }

    /**
     * 将字符串写入输出流
     *
     * @param cs      待写出字符串
     * @param os      Output Stream
     * @param charset 字符集
     *
     * @return 写出字节数
     */
    public static int copy(CharSequence cs, OutputStream os, String charset) {
        int len = cs == null ? 0 : cs.length();
        if (len > 0) {
            try {
                write(os, cs.toString().getBytes(charset));
            } catch (UnsupportedEncodingException e) {
                runtime(e);
            }
        }
        return len;
    }

    /**
     * 将字符串写入输出流
     *
     * @param cs      待写出字符串
     * @param os      Output Stream
     * @param charset 字符集
     *
     * @return 写出字节数
     */
    public static int copy(CharSequence cs, OutputStream os, Charset charset) {
        int len = cs == null ? 0 : cs.length();
        if (len > 0) {
            write(os, cs.toString().getBytes(charset));
        }
        return len;
    }

    /*
     * -----------------------------------------------------------------------
     * read
     * -----------------------------------------------------------------------
     */

    public static int read(InputStream is, byte[] bytes, int start, int max) {
        try {
            return is.read(bytes, start, max);
        } catch (IOException e) {
            return runtime(e);
        }
    }

    public static int read(InputStream is, byte[] bytes) { return read(is, bytes, 0, bytes.length); }

    public static int read(Reader reader, char[] chars, int start, int max) {
        try {
            return reader.read(chars, start, max);
        } catch (IOException e) {
            return runtime(e);
        }
    }

    public static int read(Reader reader, char[] chars) { return read(reader, chars, 0, chars.length); }

    /*
     * -----------------------------------------------------------------------
     * write
     * -----------------------------------------------------------------------
     */

    public static void write(OutputStream os, byte[] bytes, int start, int limit) {
        try {
            os.write(bytes, start, limit);
        } catch (IOException e) {
            runtime(e);
        }
    }

    public static void write(OutputStream os, byte[] bytes) { write(os, bytes, 0, bytes.length); }

    public static void write(Writer writer, char[] chars, int start, int limit) {
        try {
            writer.write(chars, start, limit);
        } catch (IOException e) {
            runtime(e);
        }
    }

    /**
     * write a char array in to Writer of all char
     *
     * @param writer writer
     * @param chars  字符
     */
    public static void write(Writer writer, char[] chars) { write(writer, chars, 0, chars.length); }

    /*
     * -----------------------------------------------------------------------
     * auto close consumer
     * -----------------------------------------------------------------------
     */

    public static void autoClose(Reader reader, ThrowingConsumer<? super Reader> consumer) {
        autoCloseHandle(reader, consumer);
    }

    public static void autoClose(Writer writer, ThrowingConsumer<? super Writer> consumer) {
        autoCloseHandle(writer, consumer);
    }

    public static void autoClose(InputStream is, ThrowingConsumer<? super InputStream> consumer) {
        autoCloseHandle(is, consumer);
    }

    public static void autoClose(OutputStream os, ThrowingConsumer<? super OutputStream> consumer) {
        autoCloseHandle(os, consumer);
    }

    public static void autoCloseAcceptAny(AutoCloseable closeable, ThrowingConsumer<? super AutoCloseable> consumer) {
        autoCloseHandle(closeable, consumer);
    }

    private static void autoCloseHandle(AutoCloseable closeable, ThrowingConsumer consumer) {
        try {
            consumer.accept(closeable);
        } catch (Throwable throwable) {
            ThrowUtil.runtime(throwable);
        } finally {
            close(closeable);
        }
    }

    /*
     * -----------------------------------------------------------------------
     * close
     * -----------------------------------------------------------------------
     */

    public static Closer close() { return Closer.VALUE; }

    public static Closer close(Stream stream) { return closeCloseable(stream); }

    public static Closer close(Reader reader) { return closeCloseable(reader); }

    public static Closer close(Writer writer) { return closeCloseable(writer); }

    public static Closer close(InputStream is) { return closeCloseable(is); }

    public static Closer close(OutputStream os) { return closeCloseable(os); }

    public static Closer close(Socket socket) { return closeCloseable(socket); }

    public static Closer close(ServerSocket socket) { return closeCloseable(socket); }

    public static Closer close(Selector selector) { return closeCloseable(selector); }

    public static Closer close(Closeable close) { return closeCloseable(close); }

    public static Closer close(AutoCloseable closeable) { return closeCloseable(closeable); }

    /*
     * -----------------------------------------------------------------------
     * close multi
     * -----------------------------------------------------------------------
     */

    public static Closer close(Writer writer, Reader reader) { return close(writer).close(reader); }

    public static Closer close(OutputStream os, InputStream is) { return close(os).close(is); }

    public static Closer close(ResultSet set, Statement stmt, Connection connect) {
        return close(set).close(stmt).close(connect);
    }

    public static void close(Closeable... closes) { IteratorUtil.forEach(closes, IOUtil::closeCloseable); }

    public static void closeAll(AutoCloseable... closes) { IteratorUtil.forEach(closes, IOUtil::closeCloseable); }

    /*
     * -----------------------------------------------------------------------
     * flusher
     * -----------------------------------------------------------------------
     */

    public static Flusher flush() { return Flusher.VALUE; }

    public static Flusher flush(Flushable flushable) {
        try {
            flushable.flush();
        } catch (NullPointerException | IOException e) {
            // ignore
        }
        return flush();
    }

    public static Flusher flushCloseable(AutoCloseable closeable) {
        return (closeable instanceof Flushable) ? flush((Flushable) closeable) : flush();
    }

    public enum Flusher {
        VALUE;

        public Flusher flush(Flushable flushable) { return IOUtil.flush(flushable); }

        public Flusher flushCloseable(AutoCloseable close) { return IOUtil.flushCloseable(close); }
    }

    /*
     * -----------------------------------------------------------------------
     * closer
     * -----------------------------------------------------------------------
     */

    public static Closer closeCloseable(AutoCloseable close) {
        try {
            flushCloseable(close);
            close.close();
        } catch (Throwable e) {
            // ignore
        }
        return Closer.VALUE;
    }

    public enum Closer {
        /**
         * 关闭
         */
        VALUE;

        public Closer close(AutoCloseable close) { return closeCloseable(close); }
    }

    public static byte[] serialize(Object object) {
        if (object == null) {
            return null;
        }
        ByteArrayOutputStream arrOut = new ByteArrayOutputStream(1024);
        try (ObjectOutputStream oos = new ObjectOutputStream(arrOut)) {
            oos.writeObject(object);
            oos.flush();
        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to serialize object of type: " + object.getClass(), ex);
        }
        return arrOut.toByteArray();
    }

    public static Object deserialize(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return ois.readObject();
        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to deserialize object", ex);
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException("Failed to deserialize object type", ex);
        }
    }

    public static <T> T deserializeAs(byte[] bytes, Class<T> type) {
        Object deserialize = deserialize(bytes);
        return deserialize == null ? null : type.cast(deserialize);
    }
}
