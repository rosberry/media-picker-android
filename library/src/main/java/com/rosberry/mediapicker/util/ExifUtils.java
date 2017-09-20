package com.rosberry.mediapicker.util;

import android.media.ExifInterface;

import java.io.IOException;

/**
 * Created by dmitry on 09.09.17.
 */

public final class ExifUtils {

    public static int getExifRotation(int orientation) {
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return 0;
            case ExifInterface.ORIENTATION_ROTATE_90:
                return 90;
            case ExifInterface.ORIENTATION_ROTATE_180:
                return 180;
            case ExifInterface.ORIENTATION_ROTATE_270:
                return 270;
            default:
                return 0;
        }
    }

    private static String getExifOrientation(int rotation) {
        switch (rotation) {
            case 0:
                return String.valueOf(ExifInterface.ORIENTATION_NORMAL);
            case 90:
                return String.valueOf(ExifInterface.ORIENTATION_ROTATE_90);
            case 180:
                return String.valueOf(ExifInterface.ORIENTATION_ROTATE_180);
            case 270:
                return String.valueOf(ExifInterface.ORIENTATION_ROTATE_270);
            default:
                return String.valueOf(ExifInterface.ORIENTATION_UNDEFINED);
        }
    }

    public static String getExifOrientation(String path) {
        try {
            ExifInterface exif = new ExifInterface(path);
            return exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return String.valueOf(ExifInterface.ORIENTATION_UNDEFINED);
    }

    public static boolean setExifOrientation(String path, int orientation) {
        try {
            ExifInterface exif = new ExifInterface(path);
            exif.setAttribute(ExifInterface.TAG_ORIENTATION, getExifOrientation(orientation));
            exif.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
            return false;

        }

        return true;
    }

}
