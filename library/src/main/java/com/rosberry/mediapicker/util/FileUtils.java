package com.rosberry.mediapicker.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

/**
 * Created by neestell on 21.04.15.
 */
public final class FileUtils {

    public static long deleteFolder(File folder) {
        if (!folder.exists()) {
            return 0;
        } else {
            long result = 0;
            File[] list = folder.listFiles();
            if (list != null)
                for (int i = 0; i < list.length; i++) {
                    if (list[i].isFile()) {
                        result += list[i].length();
                        list[i].delete();
                    } else {
                        result += deleteFolder(list[i]);
                    }
                }
            folder.delete();
            return result;
        }

    }

    public static long getFolderSize(File dir) {
        long size = 0;
        if (dir.listFiles() != null)
            for (File file : dir.listFiles()) {
                if (file.isFile()) {
                    size += file.length();
                } else
                    size += getFolderSize(file);
            }

        return size;
    }

    public static boolean bitmapToFile(Bitmap bitmap, String path, int quality, Bitmap.CompressFormat format) {
        boolean result = true;
        try {
            OutputStream out = new FileOutputStream(new File(path));
            result = bitmap.compress(format, quality, out);
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }

        return result;
    }

}
