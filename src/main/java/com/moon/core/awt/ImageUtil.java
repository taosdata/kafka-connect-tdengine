package com.moon.core.awt;

import com.moon.core.io.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;

import static com.moon.core.lang.ObjectUtil.defaultIfNull;
import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
public abstract class ImageUtil {

    private ImageUtil() { noInstanceError(); }

    public final static String imageToBase64(String imageFilePath) {
        return imageToBase64(new File(imageFilePath));
    }

    public final static String imageToBase64(String dirPath, String imageFileName) {
        return imageToBase64(new File(dirPath, imageFileName));
    }

    public final static String imageToBase64(File dirFile, String imageFileName) {
        return imageToBase64(new File(dirFile, imageFileName));
    }

    public final static String imageToBase64(File imageFile) {
        try (InputStream input = FileUtil.getInputStream(imageFile)) {
            byte[] bytes = new byte[input.available()];
            input.read(bytes);
            return Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public final static File base64ToImage(String imageBase64Codes, File dirFile, String saveImageName) {
        return base64ToImage(imageBase64Codes, new File(dirFile, saveImageName));
    }

    public final static File base64ToImage(String imageBase64Codes, String dirFile, String saveImageName) {
        return base64ToImage(imageBase64Codes, new File(dirFile, saveImageName));
    }

    public final static File base64ToImage(String imageBase64Codes, String saveImagePath) {
        return base64ToImage(imageBase64Codes, new File(saveImagePath));
    }

    public final static File base64ToImage(String imageBase64Codes, File saveImage) {
        byte[] bytes = Base64.getDecoder().decode(imageBase64Codes.getBytes());
        try (OutputStream output = FileUtil.getOutputStream(saveImage)) {
            output.write(bytes);
            return saveImage;
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static ImageJoiner joiner() {
        return joiner(null);
    }

    public static ImageJoiner joiner(ImageType type) {
        return ImageJoiner.valueOf(defaultIfNull(type, ImageType.DEFAULT).name());
    }
}
