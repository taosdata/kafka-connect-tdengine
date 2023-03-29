package com.moon.core.util.iterator;

import com.moon.core.io.FileUtil;
import com.moon.core.io.IOUtil;
import com.moon.core.util.ResourceUtil;

import java.io.File;
import java.io.InputStream;
import java.util.Iterator;

/**
 * @author moonsky
 */
public class FileStreamIterator implements Iterator {

    private InputStream in;
    private byte[] buffer;
    private int length = 0;
    private int len = -1;
    private boolean autoClose = true;

    public FileStreamIterator(String fileAbsPath, byte[] readBuffer) {
        this.initParameters(ResourceUtil.getResourceAsInputStream(fileAbsPath), readBuffer, true);
    }

    public FileStreamIterator(File file, byte[] readBuffer) {
        this.initParameters(FileUtil.getInputStream(file), readBuffer, true);
    }

    public FileStreamIterator(InputStream inputStream, byte[] readBuffer) {
        this.initParameters(inputStream, readBuffer, false);
    }

    private void initParameters(InputStream in, byte[] buffer, boolean autoClose) {
        if (in != null) {
            this.in = in;
            this.buffer = buffer;
            this.autoClose = autoClose;
            this.length = buffer.length;
        }
    }


    @Override
    public boolean hasNext() {
        try {
            this.len = this.in.read(buffer, 0, this.length);
            if (this.len < 0) {
                this.close();
                return false;
            } else {
                return true;
            }
        } catch (Throwable throwable) {
            return false;
        }
    }


    @Override
    public Integer next() {
        return this.len;
    }


    @Override
    protected void finalize() {
        this.close();
    }

    private void close() {
        if (this.autoClose && this.in != null) {
            IOUtil.close(this.in);
            this.in = null;
        }
    }
}
