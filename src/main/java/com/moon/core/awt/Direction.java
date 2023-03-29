package com.moon.core.awt;

import java.awt.image.BufferedImage;

import static com.moon.core.enums.Const.ZERO;

/**
 * @author moonsky
 */
public enum Direction {
    VERTICAL {
        @Override
        int computeWhole(int length, int resultSize, int[] widths, int[] heights) {
            int wholeHeight = 0;
            for (int i = 0; i < length; i++) {
                int height = (int) (heights[i] * ((double) resultSize / widths[i]));
                wholeHeight += height;
                heights[i] = height;
            }
            return wholeHeight;
        }

        @Override
        int computeHeight(Maths maths, int old, int now) {
            return old + now;
        }

        @Override
        int computeWidth(Maths maths, int old, int now) {
            return maths.compute(old, now);
        }

        @Override
        void setRGB(
            BufferedImage out, int index, int width, int height,
            int[] rgbArray, int offset, int scanSize) {
            out.setRGB(ZERO, index, width, height, rgbArray, ZERO, width);
        }

        @Override
        int nextIndex(int currIndex, int width, int height) {
            return currIndex + height;
        }
    },
    HORIZONTAL {
        @Override
        int computeWhole(int length, int resultSize, int[] widths, int[] heights) {
            int wholeWidth = 0;
            for (int i = 0; i < length; i++) {
                int width = (int) (widths[i] * ((double) resultSize / heights[i]));
                wholeWidth += width;
                widths[i] = width;
            }
            return wholeWidth;
        }

        @Override
        int computeHeight(Maths maths, int old, int now) {
            return maths.compute(old, now);
        }

        @Override
        int computeWidth(Maths maths, int old, int now) {
            return old + now;
        }

        @Override
        void setRGB(
            BufferedImage out, int index, int width, int height,
            int[] rgbArray, int offset, int scanSize) {
            out.setRGB(index, ZERO, width, height, rgbArray, ZERO, width);
        }

        @Override
        int nextIndex(int currIndex, int width, int height) {
            return currIndex + width;
        }
    };

    public final static Direction V = VERTICAL;
    public final static Direction H = HORIZONTAL;

    abstract int computeWhole(int length, int resultSize, int[] widths, int[] heights);

    abstract int computeHeight(Maths maths, int old, int now);

    abstract int computeWidth(Maths maths, int old, int now);

    abstract void setRGB(
        BufferedImage out, int index, int width, int height,
        int[] rgbArray, int offset, int scanSize);

    abstract int nextIndex(int currIndex, int width, int height);
}
