package com.moon.core.awt;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;

/**
 * @author moonsky
 */
public enum ImagePainter implements ImageDescriptor {

    JPEG, JPG, GIF, PNG, BMP;

    public boolean gray(String outputPath, String sourcePath) {
        return gray(extensionName(), outputPath, sourcePath);
    }

    public boolean gray(String outputType, String outputPath, String sourcePath) {
        BufferedImage src = OpUtil.read(sourcePath);
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        ColorConvertOp op = new ColorConvertOp(cs, null);
        src = op.filter(src, null);
        return OpUtil.write(src, outputType, outputPath);
    }


}
