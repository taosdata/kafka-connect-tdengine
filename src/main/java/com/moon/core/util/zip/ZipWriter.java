package com.moon.core.util.zip;

import com.moon.core.exception.DefaultException;
import com.moon.core.io.FileUtil;
import com.moon.core.io.IOUtil;
import com.moon.core.lang.StringUtil;
import com.moon.core.util.function.ThrowingConsumer;
import com.moon.core.util.function.ThrowingRunnable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.moon.core.io.FileUtil.formatFilepath;

/**
 * @author moonsky
 */
public class ZipWriter {

    private final static char SEP = '/';

    private final ZipOutputStream zipStream;
    private final ThrowingRunnable entryCloser;

    public ZipWriter(OutputStream out) {
        this.zipStream = out instanceof ZipOutputStream ? (ZipOutputStream) out : new ZipOutputStream(out);
        entryCloser = () -> zipStream.closeEntry();
    }

    public ZipWriter addEntry(String fullFileName, ThrowingConsumer<ZipOutputStream> out) {
        try {
            out.accept(nextEntry(fullFileName));
        } catch (Throwable e) {
            DefaultException.with(e);
        } finally {
            closeEntry();
        }
        return this;
    }

    public ZipWriter addEntry(String fullFileName, InputStream in) {
        try {
            IOUtil.copy(in, nextEntry(fullFileName));
        } catch (IOException e) {
            DefaultException.with(e);
        } finally {
            closeEntry();
        }
        return this;
    }

    public ZipWriter addFileEntry(File file) {
        return addFileEntry(null, file);
    }

    public ZipWriter addFileEntry(String name, File file) {
        if (file == null || !file.exists()) { return this; }
        name = name == null ? file.getName() : name;
        if (file.isFile()) {
            return addThenClosed(name, FileUtil.getInputStream(file));
        } else if (file.isDirectory()) {
            return addDirectoryEntries(name, file);
        }
        throw DefaultException.with(file);
    }

    public ZipWriter addDirectoryEntries(File directory) {
        return addDirectoryEntries(directory.getName(), directory);
    }

    public ZipWriter addDirectoryEntries(String entryRoot, File directory) {
        final String path = formatAndEnsureEnds(directory.getAbsolutePath());
        String rootName = StringUtil.emptyIfNull(entryRoot);
        final String root = formatAndEnsureEnds(rootName);
        FileUtil.traverse(directory).forEach(file -> {
            String currName = formatAndEnsureEnds(file.getAbsolutePath());
            String entryName = currName.replaceFirst(path, root);

            addThenClosed(entryName, FileUtil.getInputStream(file));
        });
        return this;
    }

    public void closeWriter() { IOUtil.close(zipStream); }

    private ZipOutputStream nextEntry(String name) throws IOException {
        zipStream.putNextEntry(new ZipEntry(clearEnds(name)));
        return zipStream;
    }

    private ZipWriter addThenClosed(String name, InputStream in) {
        try {
            return addEntry(name, in);
        } finally {
            IOUtil.close(in);
        }
    }

    private String formatAndEnsureEnds(String path) { return ensureEndsSep(formatFilepath(path)); }

    private void closeEntry() {
        try {
            entryCloser.run();
        } catch (Throwable t) {
            // ignore
        }
    }

    private String ensureEndsSep(String path) { return isEnds(path) ? path : path + SEP; }

    private String clearEnds(String name) { return isEnds(name) ? name.substring(0, name.length() - 1) : name; }

    private boolean isEnds(String src) { return src.charAt(src.length() - 1) == SEP; }
}
