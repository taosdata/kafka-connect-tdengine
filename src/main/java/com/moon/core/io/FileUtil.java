package com.moon.core.io;

import com.moon.core.lang.LangUtil;
import com.moon.core.lang.StringUtil;
import com.moon.core.lang.ThrowUtil;
import com.moon.core.util.IteratorUtil;
import com.moon.core.util.function.ThrowingConsumer;

import java.io.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static com.moon.core.lang.LangUtil.applyBi;
import static com.moon.core.lang.ThrowUtil.runtime;

/**
 * @author moonsky
 */
public final class FileUtil {

    private FileUtil() { ThrowUtil.noInstanceError(); }

    /*
     * -----------------------------------------------------------------------
     * copies
     * -----------------------------------------------------------------------
     */

    /**
     * 将 sourceFilepath 绝对路径所指向的文件或目录复制到 targetDir
     *
     * @param sourceFilepath 源路径
     * @param targetDir      目标目录
     */
    public static void copyToDirectory(String sourceFilepath, String targetDir) {
        copyToDirectory(new File(sourceFilepath), new File(targetDir));
    }

    /**
     * 将 sourceFilepath 绝对路径所指向的文件复制到 targetDir，并将文件命名为 newFileName
     * 新文件名设置只针对复制文件有效，如果是复制文件夹，将使用原文件名
     *
     * @param sourceFilepath 源路径
     * @param targetDir      目标目录
     */
    public static void copyToDirectory(String sourceFilepath, String targetDir, String newFileName) {
        copyToFile(new File(sourceFilepath), new File(targetDir, newFileName));
    }

    public static void copyToDirectory(File sourceFile, final File targetDir) {
        if (sourceFile == null || targetDir == null) {
            return;
        } else if (sourceFile.isFile()) {
            copyToFile(sourceFile, new File(targetDir, sourceFile.getName()));
        } else if (sourceFile.isDirectory()) {
            String root = sourceFile.getParent();
            int len = root.length();
            IteratorUtil.forEach(traverseDirectory(sourceFile),
                file -> copyToFile(file, new File(targetDir, file.getAbsolutePath().substring(len))));
        }
    }

    public static void copyToFile(String sourceFilepath, String targetFilePath) {
        copyToFile(new File(sourceFilepath), new File(targetFilePath));
    }

    public static void copyToFile(File sourceFile, File targetFile) {
        if (exists(sourceFile)) {
            if (sourceFile.isDirectory()) {
                copyToDirectory(sourceFile, targetFile.getParentFile());
            } else if (sourceFile.isFile()) {
                try (FileOutputStream output = getOutputStream(targetFile);
                    FileInputStream input = getInputStream(sourceFile)) {
                    IOUtil.copy(input, output);
                } catch (IOException e) {
                    runtime(e);
                }
            }
        }
    }

    /*
     * -----------------------------------------------------------------------
     * io
     * -----------------------------------------------------------------------
     */

    /**
     * 获取文件的输出流，如果文件不存在，会创建文件以及目录结构，创建失败返回空
     *
     * @param file 目标文件
     */
    public static FileOutputStream getOutputStream(File file) { return getOutputStream(file, false); }

    public static FileOutputStream getOutputStream(String filePath) { return getOutputStream(filePath, false); }

    public static FileOutputStream getOutputStream(File file, boolean append) {
        return createNewFile(file) ? applyBi(file,
            append,
            FileOutputStream::new) : ThrowUtil.runtime("File not exist: " + file);
    }

    public static FileOutputStream getOutputStream(String filePath, boolean append) {
        return getOutputStream(new File(StringUtil.trimToEmpty(filePath)), append);
    }

    public static FileInputStream getInputStream(String filePath) {
        return getInputStream(new File(StringUtil.trimToEmpty(filePath)));
    }

    /**
     * 从已知文件获取输入流，如不存在返回空
     *
     * @param file 目标文件
     */
    public static FileInputStream getInputStream(File file) { return LangUtil.apply(file, FileInputStream::new); }

    public static File getFile(String... paths) {
        int length = paths == null ? 0 : paths.length;
        if (length < 1) {
            return null;
        }
        if (length == 1) {
            return new File(paths[0]);
        }
        File file = new File(paths[0], paths[1]);
        for (int i = 2; i < length; i++) {
            file = new File(file, paths[i]);
        }
        return file;
    }

    /*
     * -----------------------------------------------------------------------
     * creates
     * -----------------------------------------------------------------------
     */

    public static boolean mkdirs(String... pathOptions) {
        File dir = getFile(pathOptions);
        return dir != null && mkdirs(dir);
    }

    public static boolean mkdirs(String path) { return mkdirs(new File(path)); }

    public static boolean mkdirs(File dir) {
        if (dir.exists()) {
            if (dir.isFile()) {
                return ThrowUtil.runtime("The target exist and is a file: " + dir);
            } else {
                return true;
            }
        } else {
            return dir.mkdirs();
        }
    }

    /**
     * 创建文件以其目录结构，返回创建成功与否的状态
     *
     * @param file 目标文件
     */
    public static boolean createNewFile(File file) {
        if (!exists(file)) {
            File parent = file.getParentFile();
            if (parent.exists() || parent.mkdirs()) {
                try {
                    return file.createNewFile();
                } catch (IOException e) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean createNewFile(String filepath) { return createNewFile(new File(filepath)); }

    /*
     * -----------------------------------------------------------------------
     * travellers
     * -----------------------------------------------------------------------
     */

    /**
     * 文件列表遍历器
     *
     * @return 文件列表遍历器
     */
    public static FileTraveller traveller() { return new FileTraveller(); }

    /**
     * 遍历指定目录的文件列表，如遇不可访问的安全保护会打印相应错误信息，但不会影响程序执行
     *
     * @param dirPath 目标文件路径
     */
    public static List<File> traverseDirectory(String dirPath) { return traveller().traverse(dirPath); }

    public static List<File> traverseDirectory(File dirPath) { return traveller().traverse(dirPath); }

    public static List<File> traverse(String dirPath) { return traveller().traverse(dirPath); }

    public static List<File> traverse(File dir) { return traveller().traverse(dir); }

    public static List<File> traverseAll(File... dirs) {
        FileTraveller traveller = traveller();
        for (int i = 0; i < dirs.length; i++) {
            traveller.traverse(dirs[i]);
        }
        return traveller;
    }

    public static List<File> traverseAll(String... dirs) {
        Traveller<File> traveller = traveller();
        for (int i = 0; i < dirs.length; i++) {
            traveller.traverse(dirs[i]);
        }
        return traveller;
    }

    public static List<File> traverseAll(List<File> dirs) {
        FileTraveller traveller = traveller();
        for (int i = 0; i < dirs.size(); i++) {
            traveller.traverse(dirs.get(i));
        }
        return traveller;
    }

    /*
     * -----------------------------------------------------------------------
     * write or append lines
     * append：追加字符串行至文件末尾
     * write: 可手动控制是追加还是覆盖
     * -----------------------------------------------------------------------
     */

    private static ThrowingConsumer<Object> newLineWriter(Writer writer) {
        BufferedWriter bw = IOUtil.getBufferedWriter(writer);
        return line -> {
            bw.newLine();
            bw.write(String.valueOf(line));
        };
    }

    public static void writeLinesToWriter(Writer writer, Iterator<? extends Object> lines) {
        ThrowingConsumer<Object> consumer = newLineWriter(writer);
        IteratorUtil.forEach(lines, line -> LangUtil.accept(line, consumer));
    }

    public static void writeLinesToWriter(Writer writer, Collection<? extends Object> lines) {
        ThrowingConsumer<Object> consumer = newLineWriter(writer);
        IteratorUtil.forEach(lines, line -> LangUtil.accept(line, consumer));
    }

    public static void writeLinesToWriter(Writer writer, CharSequence... lines) {
        ThrowingConsumer<Object> consumer = newLineWriter(writer);
        IteratorUtil.forEach(lines, line -> LangUtil.accept(line, consumer));
    }

    public static void writeLinesToOutput(OutputStream os, Iterator<? extends CharSequence> lines) {
        IOUtil.autoClose(IOUtil.getWriter(os), w -> writeLinesToWriter(w, lines));
    }

    public static void writeLinesToOutput(OutputStream os, Collection<? extends CharSequence> lines) {
        IOUtil.autoClose(IOUtil.getWriter(os), w -> writeLinesToWriter(w, lines));
    }

    public static void writeLinesToOutput(OutputStream os, CharSequence... lines) {
        IOUtil.autoClose(IOUtil.getWriter(os), w -> writeLinesToWriter(w, lines));
    }

    public static void appendLinesToFile(File existFile, Iterator<? extends CharSequence> lines) {
        writeLinesToOutput(getOutputStream(existFile, true), lines);
    }

    public static void appendLinesToFile(File existFile, Collection<? extends CharSequence> lines) {
        writeLinesToOutput(getOutputStream(existFile, true), lines);
    }

    public static void appendLinesToFile(File existFile, CharSequence... lines) {
        writeLinesToOutput(getOutputStream(existFile, true), lines);
    }

    public static void appendLinesToFile(String existFilePath, Iterator<? extends CharSequence> lines) {
        appendLinesToFile(new File(existFilePath), lines);
    }

    public static void appendLinesToFile(String existFilePath, Collection<? extends CharSequence> lines) {
        appendLinesToFile(new File(existFilePath), lines);
    }

    public static void appendLinesToFile(String existFilePath, CharSequence... lines) {
        appendLinesToFile(new File(existFilePath), lines);
    }

    /*
     * -----------------------------------------------------------------------
     * length
     * -----------------------------------------------------------------------
     */

    /**
     * 返回文件大小(单位 Bit)
     *
     * @param file 目标文件
     *
     * @return 返回文件大小(单位 Bit)
     */
    public static long lengthToBit(File file) { return lengthToB(file) << 3; }

    /**
     * 返回文件大小(单位 B)
     *
     * @param filepath 目标文件绝对路径
     *
     * @return 返回文件大小(单位 B)
     */
    public static long length(String filepath) { return lengthToB(new File(filepath)); }

    /**
     * 返回文件大小(单位 B)
     *
     * @param file 目标文件
     *
     * @return 返回文件大小(单位 B)
     */
    public static long lengthToB(File file) { return file.length(); }

    /**
     * 返回文件大小(单位 KB)
     *
     * @param file 目标文件
     *
     * @return 返回文件大小(单位 KB)
     */
    public static long lengthToKB(File file) { return lengthToB(file) >> 10; }

    /**
     * 返回文件大小(单位 MB)
     *
     * @param file 目标文件
     *
     * @return 文件大小(单位 MB)
     */
    public static long lengthToMB(File file) { return lengthToKB(file) >> 10; }

    /**
     * 返回文件大小(单位 GB)
     *
     * @param file 目标文件
     *
     * @return 文件大小(单位 GB)
     */
    public static long lengthToGB(File file) { return lengthToMB(file) >> 10; }

    /**
     * 返回文件大小(单位 TB)
     *
     * @param file 目标文件
     *
     * @return 文件大小(单位 TB)
     */
    public static long lengthToTB(File file) { return lengthToGB(file) >> 10; }

    /**
     * 返回文件大小(单位 PB)
     *
     * @param file 目标文件
     *
     * @return 文件大小(单位 PB)
     */
    public static long lengthToPB(File file) { return lengthToTB(file) >> 10; }

    /**
     * 返回文件大小(单位 EB)
     *
     * @param file 目标文件
     *
     * @return 文件大小(单位 EB)
     */
    public static long lengthToEB(File file) { return lengthToPB(file) >> 10; }

    /**
     * 返回文件大小(单位 ZB)
     *
     * @param file 目标文件
     *
     * @return 文件大小(单位 ZB)
     */
    public static long lengthToZB(File file) { return lengthToEB(file) >> 10; }

    /*
     * -----------------------------------------------------------------------
     * delete
     * -----------------------------------------------------------------------
     */

    /**
     * 深度删除所有文件
     *
     * @param dir 目标目录
     *
     * @return 是否还存在目标目录
     */
    public static boolean deleteAllFiles(String dir) { return deleteAllFiles(new File(dir)); }

    /**
     * 深度删除所有文件
     *
     * @param dir 目标目录
     *
     * @return 是否还存在目标目录
     */
    public static boolean deleteAllFiles(File dir) {
        return deleteAllFiles(dir, false);
    }

    public static boolean deleteAllFiles(File dir, boolean deleteDirectory) {
        if (dir == null) {
            return true;
        } else if (dir.isDirectory()) {
            boolean deleted = true;
            for (File file : traverseDirectory(dir)) {
                deleted = delete(file, deleteDirectory) && deleted;
            }
            return deleted;
        } else if (dir.isFile()) {
            return delete(dir);
        }
        return !dir.exists();
    }

    /**
     * 删除文件，返回 file 所指向的文件是否还存在
     *
     * @param file 待删除文件
     *
     * @return 是否还存在目标文件
     */
    public static boolean delete(File file) { return delete(file, true); }

    public static boolean delete(File file, boolean deleteDirectory) {
        if (file == null) {
            return true;
        }
        try {
            if (file.isDirectory()) {
                if (deleteDirectory) {
                    return file.delete();
                }
                return false;
            }
            return file.delete();
        } catch (SecurityException e) {
            return !file.exists();
        }
    }

    /*
     * -----------------------------------------------------------------------
     * others
     * -----------------------------------------------------------------------
     */

    /**
     * \
     */
    public static final char WIN_FileSeparator_Char = (char) 92;

    /**
     * /
     */
    public static final char APP_FileSeparator_Char = (char) 47;

    /**
     * 格式化文件名，确保文件名是指定扩展名（扩展名忽略大小写）
     *
     * @param filename  文件名
     * @param extension 扩展名
     *
     * @return 一定包含指定扩展名后缀的文件名
     */
    public final static String formatFilename(String filename, String extension) {
        char dot = '.';
        String dotExtension = extension.charAt(0) == dot ? extension : dot + extension;
        int dotIndex = filename.lastIndexOf(dot);
        return (dotIndex > 0 && filename.substring(dotIndex)
            .equalsIgnoreCase(dotExtension)) ? filename : (filename + dotExtension);
    }

    /**
     * 格式化文件路径，确保文件路径分隔符符合所有系统
     * <p>
     * 主要是路径分割符反斜线“\”只在 windows 系统有效，将其替换为正斜线“/”
     *
     * @param filepath 文件原路径
     *
     * @return 符合所有系统的格式化路径
     */
    public final static String formatFilepath(String filepath) {
        return StringUtil.replace(filepath, WIN_FileSeparator_Char, APP_FileSeparator_Char);
    }

    public static boolean exists(File file) { return file != null && file.exists(); }

    public static boolean exists(String filePath) { return filePath != null && new File(filePath).exists(); }
}
