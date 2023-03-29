package com.moon.core.lang;

import com.moon.core.util.OSUtil;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.spi.FileSystemProvider;
import java.util.*;

import static com.moon.core.util.FilterUtil.nullableFind;
import static java.nio.file.spi.FileSystemProvider.installedProviders;

/**
 * 包扫描器
 *
 * @author moonsky
 */
public class PackageScanner extends HashSet<String> {

    private final static String DOT_CLASS = ".class";
    private final static Class TYPE = PackageScanner.class;

    PackageScanner() { }

    public PackageScanner scan(String packageName) {
        this.addAll(scanOf(packageName));
        return this;
    }

    public static List<String> scanOf(String packageName) {
        final String currentName = packageName.replaceAll("\\.", "/");
        ClassLoader cl = TYPE.getClassLoader();
        try {
            Enumeration<URL> urls = cl.getResources(currentName);
            ArrayList<String> result = new ArrayList<>();
            while (urls.hasMoreElements()) {
                result.addAll(scanFromUrl(urls.nextElement(), currentName));
            }
            return result;
        } catch (IOException e) {
            return ThrowUtil.runtime(e);
        }
    }

    private static List<String> scanFromUrl(URL currUrl, String packageName) {
        URL tempUrl;
        try {
            tempUrl = new URL(URLDecoder.decode(currUrl.toString(), "UTF-8"));
        } catch (Exception e) {
            tempUrl = ThrowUtil.runtime(e);
        }
        final URL url = tempUrl;
        final String jar = "jar", file = "file", protocol = url.getProtocol();
        FileSystemProvider provider;
        if (protocol.equals(jar)) {
            provider = getZipFSProvider();
            if (provider != null) {
                String target = url.getPath().replaceFirst("file:/", "").replaceFirst("!.*", "");
                try (FileSystem fs = provider.newFileSystem(Paths.get(target), new HashMap<>())) {
                    return walkFileTree(fs.getPath(packageName), null);
                } catch (Exception e) {
                    ThrowUtil.runtime(e);
                }
            }
        } else if (protocol.equals(file)) {
            int end = url.getPath().lastIndexOf(packageName);
            String basePath = url.getPath().substring(1, end);
            try {
                Path path = targetUrlToPath(url);
                return walkFileTree(path, Paths.get(basePath));
            } catch (Exception e) {
                ThrowUtil.runtime(e);
            }
        }
        return Collections.EMPTY_LIST;
    }

    private static Path targetUrlToPath(URL url) {
        final String target = url.getPath();
        return Paths.get(OSUtil.onWindows() ? target.replaceFirst("/", "") : target);
    }

    private static List<String> walkFileTree(Path path, Path basePath) throws Exception {
        final List<String> result = new ArrayList<>();
        Files.walkFileTree(path, new DefaultFileVisitor(result, basePath));
        return result;
    }

    static class DefaultFileVisitor extends SimpleFileVisitor<Path> {

        private final List<String> result;
        private final String packageName;

        DefaultFileVisitor(List<String> result, Path basePath) {
            this.result = result;
            this.packageName = StringUtil.toStringOrEmpty(basePath);
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            if (file.toString().endsWith(DOT_CLASS)) {
                String path = file.toString();
                path = path.replace(packageName, "");
                path = path.substring(1);
                path = path.replace('\\', '/');
                path = path.replace(DOT_CLASS, "");
                path = path.replace('/', '.');
                result.add(path);
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
            return FileVisitResult.CONTINUE;
        }
    }

    private static FileSystemProvider getZipFSProvider() {
        return nullableFind(installedProviders(), provider -> "jar".equals(provider.getScheme()));
    }
}
