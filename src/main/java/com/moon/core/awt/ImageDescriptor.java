package com.moon.core.awt;

/**
 * @author moonsky
 */
interface ImageDescriptor {

    /**
     * 名称
     *
     * @return 图片类型
     */
    String name();

    /**
     * 扩展名
     *
     * @return 图片类型扩展名
     */
    default String extensionName() {
        return name().replace('_', '.').toLowerCase();
    }

    /**
     * 文件后缀
     *
     * @return 图片文件后缀
     */
    default String suffix() {
        return '.' + extensionName();
    }
}
