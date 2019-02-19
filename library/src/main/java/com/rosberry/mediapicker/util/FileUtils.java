package com.rosberry.mediapicker.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.Locale;

/**
 * Created by neestell on 21.04.15.
 */
public final class FileUtils {
    public static final String MIME_TYPE_AUDIO = "audio/*";
    public static final String MIME_TYPE_TEXT = "text/*";
    public static final String MIME_TYPE_IMAGE = "image/*";
    public static final String MIME_TYPE_VIDEO = "video/*";
    public static final String MIME_TYPE_APP = "application/*";

    public static final String HIDDEN_PREFIX = ".";

    public static boolean isLocalStorageDocument(Context context, Uri uri) {
        String authority = context.getPackageName() + ".documents";
        return authority.equals(uri.getAuthority());
    }


    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isStorageDocument(Uri uri){
        return "com.google.android.apps.docs.storage".equals(uri.getAuthority());
    }


    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }


    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                                                        null);
            if (cursor != null && cursor.moveToFirst()) {


                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // LocalStorageProvider
            if (isLocalStorageDocument(context, uri)) {
                // The path is the id
                return DocumentsContract.getDocumentId(uri);
            }
            // ExternalStorageProvider
            else if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }



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


    public static boolean copy(InputStream in, File dst) {
        OutputStream out = null;
        boolean result = true;
        try {

            out = new FileOutputStream(dst);

            // Transfer bytes from in to out
            byte[] buf = new byte[1024 * 8];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        } finally {
            try {
                out.close();
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
                result = false;
            }
        }

        return result;
    }

}
