package com.moon.core.awt;

import com.moon.core.lang.IntUtil;
import com.moon.core.lang.ref.IntAccessor;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static com.moon.core.enums.Const.ZERO;

/**
 * @author moonsky
 */
public enum ImageJoiner implements ImageDescriptor {

    JPG,
    JPEG,
    GIF,
    PNG,
    BMP;

    /*
     * ----------------------------------------------------------------------------
     * join
     * ----------------------------------------------------------------------------
     */

    public final boolean verticalJoin(String outputPath, String... sourcePaths) {
        return join(Direction.VERTICAL, outputPath, sourcePaths);
    }

    public final boolean horizontalJoin(String outputPath, String... sourcePaths) {
        return join(Direction.HORIZONTAL, outputPath, sourcePaths);
    }

    public final boolean join(Direction direction, String outputPath, String... sourceImages) {
        return join(direction, extensionName(), outputPath, sourceImages);
    }

    public final boolean join(Direction direction, String outputType, String outputPath, String... sourceImages) {
        BufferedImage resultImage = join(direction, sourceImages);
        return OpUtil.write(resultImage, outputType, outputPath);
    }

    /*
     * ----------------------------------------------------------------------------
     * zoom join
     * ----------------------------------------------------------------------------
     */

    public boolean joinWithSameHeight(String outputPath, String... sourceImages) {
        return joinWithSameHeight(extensionName(), outputPath, sourceImages);
    }

    public boolean joinWithSameHeight(String outputType, String outputPath, String... sourceImages) {
        BufferedImage resultImage = joinWithSameHeight(sourceImages);
        return OpUtil.write(resultImage, outputType, outputPath);
    }

    public boolean joinWithSameWidth(String outputPath, String... sourceImages) {
        return joinWithSameWidth(extensionName(), outputPath, sourceImages);
    }

    public boolean joinWithSameWidth(String outputType, String outputPath, String... sourceImages) {
        BufferedImage resultImage = joinWithSameWidth(sourceImages);
        return OpUtil.write(resultImage, outputType, outputPath);
    }

    /*
     * ----------------------------------------------------------------------------
     * inner
     * ----------------------------------------------------------------------------
     */

    private static BufferedImage joinWithSameSize(Operator operator, String... sourceImages) {
        final int length = sourceImages.length;
        return length < 1 ? OpUtil.empty() : operator
            .accept(sourceImages, IntAccessor.of(Integer.MAX_VALUE), new int[length], new int[length],
                new ArrayList(length));
    }

    private interface Operator {

        /**
         * inner
         *
         * @param sources  源
         * @param accessor 索引
         * @param heights  高
         * @param widths   宽
         * @param images   图片文件列表
         *
         * @return 操作后的图片
         */
        BufferedImage accept(
            String[] sources,
            IntAccessor accessor,
            int[] heights,
            int[] widths,
            List<BufferedImage> images
        );
    }

    private static BufferedImage joinWithSameWidth(String... sourceImages) {
        return joinWithSameSize((sources, accessor, heights, widths, images) -> {
            final int length = sources.length;
            for (int i = 0; i < length; i++) {
                BufferedImage image = OpUtil.read(sources[i]);

                int width = image.getWidth();
                accessor.set(IntUtil.min(width, accessor.get()));

                widths[i] = width;
                heights[i] = image.getHeight();
                images.add(image);
            }

            int width = accessor.get();
            int wholeHeight = Direction.V.computeWhole(length, width, widths, heights);

            BufferedImage resultImage = OpUtil.createBuffered(width, wholeHeight);
            for (int i = 0, index = 0; i < images.size(); i++) {
                BufferedImage image = images.get(i);
                int height = heights[i];

                BufferedImage newImage = OpUtil.createBuffered(width, height);
                int[] imageRgbPoints = new int[width * height];

                newImage.getGraphics().drawImage(image, ZERO, ZERO, width, height, null);
                imageRgbPoints = newImage.getRGB(ZERO, ZERO, width, height, imageRgbPoints, ZERO, width);

                resultImage.setRGB(ZERO, index, width, height, imageRgbPoints, ZERO, width);
                index += height;
            }
            return resultImage;
        }, sourceImages);
    }

    private static BufferedImage joinWithSameHeight(String... sourceImages) {
        return joinWithSameSize((sources, accessor, heights, widths, images) -> {
            final int length = sources.length;
            for (int i = 0; i < length; i++) {
                BufferedImage image = OpUtil.read(sources[i]);

                int height = image.getHeight();
                accessor.set(IntUtil.min(height, accessor.get()));

                heights[i] = height;
                widths[i] = image.getWidth();
                images.add(image);
            }

            int height = accessor.get();
            int wholeWidth = Direction.H.computeWhole(length, height, widths, heights);

            BufferedImage resultImage = OpUtil.createBuffered(wholeWidth, height);
            for (int i = 0, index = 0; i < images.size(); i++) {
                BufferedImage image = images.get(i);
                int width = widths[i];

                BufferedImage newImage = OpUtil.createBuffered(width, height);

                newImage.getGraphics().drawImage(image, ZERO, ZERO, width, height, null);
                int[] points = newImage.getRGB(ZERO, ZERO, width, height, new int[width * height], ZERO, width);

                resultImage.setRGB(index, ZERO, width, height, points, ZERO, width);
                index += width;
            }
            return resultImage;
        }, sourceImages);
    }

    private static BufferedImage join(Direction direction, String... sourceImages) {
        int len = sourceImages.length;
        if (len < 1) {
            return OpUtil.empty();
        }

        BufferedImage[] images = new BufferedImage[len];
        int[][] rgbArrays = new int[len][];
        for (int i = 0; i < len; i++) {
            BufferedImage current = OpUtil.read(sourceImages[i]);

            int width = current.getWidth();
            int height = current.getHeight();
            // 从图片中读取RGB: 这句可以不用
            int[] rgbArray = new int[width * height];
            images[i] = current;
            rgbArrays[i] = current.getRGB(ZERO, ZERO, width, height, rgbArray, ZERO, width);
        }
        return Join.newSize(images, rgbArrays, direction);
    }

    private static final class Join {

        static BufferedImage newSize(
            BufferedImage[] images, int[][] rgbArrays, Direction direction
        ) {
            int width = 0;
            int height = 0;
            for (int i = 0; i < images.length; i++) {
                height = direction.computeHeight(Maths.MAX, height, images[i].getHeight());
                width = direction.computeWidth(Maths.MAX, width, images[i].getWidth());
            }

            return height < 1 || width < 1 ? OpUtil.empty() : Join.out(images, rgbArrays, width, height, direction);
        }

        static BufferedImage out(
            BufferedImage[] images, int[][] rgbArrays, int outWidth, int outHeight, Direction direction
        ) {
            BufferedImage outImage = OpUtil.createBuffered(outWidth, outHeight);
            int index = 0;
            for (int i = 0; i < images.length; i++) {
                BufferedImage image = images[i];
                int width = image.getWidth();
                int height = image.getHeight();
                direction.setRGB(outImage, index, width, height, rgbArrays[i], ZERO, width);
                index = direction.nextIndex(index, width, height);
            }
            return outImage;
        }
    }
}
