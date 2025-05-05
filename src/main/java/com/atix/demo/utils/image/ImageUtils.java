package com.atix.demo.utils.image;

import com.j256.simplemagic.ContentInfoUtil;
import com.j256.simplemagic.ContentType;

/**
 * contains various utilities for dealing with images
 */
public class ImageUtils {

    /**
     * Gets the correct image format for some file types
     *
     * @param filename the name of the file
     * @return the appropiate format. Null if the file type is not supported
     */
    public static String getImageFormat(String filename) {
        ContentType type = ContentInfoUtil.findExtensionMatch(filename).getContentType();
        if (type == ContentType.PDF || type == ContentType.PNG) {
            return "png";
        } else if (type == ContentType.JPEG) {
            return "jpg";
        } else if (type == ContentType.TIFF) {
            return "tiff";
        } else {
            return null;
        }
    }
}