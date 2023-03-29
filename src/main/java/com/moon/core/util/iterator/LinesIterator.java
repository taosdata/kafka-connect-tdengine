package com.moon.core.util.iterator;

import com.moon.core.io.FileUtil;
import com.moon.core.io.IOUtil;
import com.moon.core.lang.StringUtil;
import com.moon.core.util.ResourceUtil;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * @author moonsky
 */
public class LinesIterator implements Iterator<String>, Closeable {

    private BufferedReader reader;
    private final boolean initSuccess;
    private final boolean autoClose;

    private String currentString;

    /*
     * -------------------------------------------------------------
     * from io
     * -------------------------------------------------------------
     */

    private LinesIterator(Reader reader, boolean autoClose) {
        if (reader != null) {
            this.reader = IOUtil.getBufferedReader(reader);
            if (this.reader != null) {
                this.readLine();
                this.initSuccess = currentString != null;
            } else {
                this.initSuccess = false;
            }
        } else {
            this.initSuccess = false;
        }
        this.autoClose = autoClose;
    }

    public LinesIterator(Reader reader) {
        this(reader, false);
    }

    public LinesIterator(InputStream inputStream) {
        this(inputStream, false);
    }

    private LinesIterator(InputStream inputStream, boolean autoClose) {
        this(IOUtil.getReader(inputStream), autoClose);
    }

    public LinesIterator(InputStream inputStream, String charset) {
        this(inputStream, charset, false);
    }

    private LinesIterator(InputStream inputStream, String charset, boolean autoClose) {
        this(inputStream, Charset.forName(charset), autoClose);
    }

    public LinesIterator(InputStream inputStream, Charset charset) {
        this(inputStream, charset, false);
    }

    private LinesIterator(InputStream inputStream, Charset charset, boolean autoClose) {
        this(IOUtil.getReader(inputStream, charset), autoClose);
    }

    /*
     * -------------------------------------------------------------
     * from file
     * -------------------------------------------------------------
     */

    public LinesIterator(File file) {
        this(FileUtil.getInputStream(file), true);
    }

    public LinesIterator(File file, String charset) {
        this(FileUtil.getInputStream(file), charset, true);
    }

    public LinesIterator(File file, Charset charset) {
        this(FileUtil.getInputStream(file), charset, true);
    }

    public LinesIterator(CharSequence filePath) {
        this(ResourceUtil.getResourceAsInputStream(StringUtil.trimToNull(filePath)), true);
    }

    public LinesIterator(CharSequence filePath, String charset) {
        this(ResourceUtil.getResourceAsInputStream(StringUtil.trimToNull(filePath)), charset, true);
    }

    public LinesIterator(CharSequence filePath, Charset charset) {
        this(ResourceUtil.getResourceAsInputStream(StringUtil.trimToNull(filePath)), charset, true);
    }

    /*
     * -------------------------------------------------------------
     * methods
     * -------------------------------------------------------------
     */

    @Override
    public boolean hasNext() {
        return this.initSuccess && this.currentString != null;
    }

    @Override
    public String next() {
        String tempString = this.currentString;

        this.readLine();
        if (this.currentString == null) {
            this.clear();
        }
        return tempString;
    }

    /*
     * -------------------------------------------------------------
     * tools
     * -------------------------------------------------------------
     */

    @Override
    public void close() {
        this.clear();
    }

    public void clear() {
        if (this.autoClose) {
            IOUtil.close(this.reader);
        }
        this.reader = null;
    }

    private void readLine() {
        try {
            this.currentString = this.reader.readLine();
        } catch (IOException e) {
            this.currentString = null;
            e.printStackTrace();
        }
    }

    @Override
    protected void finalize() {
        this.clear();
    }
}
