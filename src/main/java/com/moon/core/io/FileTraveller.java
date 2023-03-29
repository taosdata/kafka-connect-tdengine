package com.moon.core.io;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 此方法支持遍历多个文件夹，然后一次性返回所有文件列表
 *
 * @author moonsky
 */
public class FileTraveller extends ArrayList<File> implements Traveller<File>, List<File> {

    private final boolean ignoreSecurity;

    public FileTraveller() {
        this(true);
    }

    public FileTraveller(boolean ignoreSecurity) {
        this.ignoreSecurity = ignoreSecurity;
    }

    @Override
    public FileTraveller traverse(String dirPath) {
        traverse(new File(dirPath));
        return this;
    }

    @Override
    public FileTraveller traverse(File dir) {
        if (dir.exists()) {
            try {
                if (dir.isDirectory()) {
                    String[] paths = dir.list();
                    int len = paths == null ? 0 : paths.length;
                    for (int i = 0; i < len; i++) {
                        traverse(new File(dir, paths[i]));
                    }
                    super.add(dir);
                } else if (dir.isFile()) {
                    super.add(dir);
                }
            } catch (SecurityException e) {
                if (!ignoreSecurity) {
                    throw new IllegalArgumentException(dir.getAbsolutePath(), e);
                }
            }
        }
        return this;
    }

    @Override
    public boolean add(File file) {
        traverse(file);
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends File> c) {
        if (c != null) {
            for (File file : c) {
                traverse(file);
            }
        }
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends File> c) {
        super.remove(index);
        return this.addAll(c);
    }

    @Override
    public File set(int index, File element) {
        super.remove(index);
        this.add(index, element);
        return this.get(index);
    }

    @Override
    public void add(int index, File element) {
        this.traverse(element);
    }
}
