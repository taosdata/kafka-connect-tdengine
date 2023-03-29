package com.moon.core.util.zip;

import com.moon.core.lang.ThrowUtil;

import java.io.OutputStream;

/**
 * @author moonsky
 */
public final class ZipUtil {
    private ZipUtil() { ThrowUtil.noInstanceError(); }

    public final ZipWriter writer(OutputStream outputStream) { return new ZipWriter(outputStream); }
}
