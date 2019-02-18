package com.rosberry.mediapicker.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.rosberry.mediapicker.data.PhotoOptions;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Created by dmitry on 30.08.17.
 */

public final class PhotoUtils {
    public static final String[] MIMES = new String[]{"jpg", "jpeg", "JPG", "JPEG"};


    private static final String[] CONTENT_ORIENTATION = new String[]{
            MediaStore.Images.ImageColumns.ORIENTATION
    };

    public static final String GOOGLE_CLOUD_URL = "content://com.google.android.apps.photos.content";
    public static final String GALLERY_CLOUD_URL = "gallery3d.provider";


    public static PhotoOptions loadBoundFromCloud(Context context, Uri uri) {
        try {
            InputStream is = context.getContentResolver().openInputStream(uri);
            if (is != null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(is, null, options);
                return new PhotoOptions(new Rect(0, 0, options.outWidth, options.outHeight), parseType(options));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new PhotoOptions(new Rect(), "");
    }

    private static String parseType(BitmapFactory.Options options) {
        String type = options.outMimeType;

        return parseType(type);
    }
    static String parseType(String type) {
        if (type != null && type.contains("/")) {
            return type.substring(type.indexOf("/") + 1);
        }else return type;


    }

    public static PhotoOptions getBitmapBounds(String path) {
        Rect bounds = new Rect(0, 0, 10, 10);
        InputStream is = null;
        String type = "";
        try {
            is = new FileInputStream(new File(path));
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, options);
            bounds.right = options.outWidth;
            bounds.bottom = options.outHeight;
            type = parseType(options);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } finally {
            closeStream(is);
        }

        return new PhotoOptions(bounds, type);
    }

    private static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        String path = null;

        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(proj[0]);
            cursor.moveToFirst();
            if (cursor.getCount() != 0)
                path = cursor.getString(column_index);
        }
        if (cursor != null)
            cursor.close();

        return path;
    }

    public static int getRotation(Context context, Uri uri, String path) {
        int rotation = PhotoUtils.getMediaRotation(context.getContentResolver(), uri);

        if (rotation <= 0 && path != null)
            rotation = ExifUtils.getExifRotation(Integer.parseInt(ExifUtils.getExifOrientation(path)));

        return rotation;
    }

    public static String getExifOrientation(int orientation) {
        switch (orientation) {
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

    public static Bitmap decodeBitmap(Context context, Uri uri, boolean remote, Rect bounds, int rotation, int width,
                                      int height,
                                      Bitmap.Config config) {
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            int sampleSize = Math.max(bounds.width() / width, bounds.height() / height);
            sampleSize = Math.min(sampleSize,
                                  Math.max(bounds.width() / height, bounds.height() / width));
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = Math.max(sampleSize, 1);
            options.inPreferredConfig = config;
            options.inMutable = true;
            is = remote ? context.getContentResolver().openInputStream(uri) : new FileInputStream(
                    new File(getRealPathFromURI(context, uri)));
            bitmap = BitmapFactory.decodeStream(is, null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return bitmap;
        } finally {
            closeStream(is);
        }
        // Scale down the sampled bitmap if it's still larger than the desired dimension.
        if (bitmap != null) {
            float scale = Math.min((float) width / bitmap.getWidth(),
                                   (float) height / bitmap.getHeight());
            scale = Math.max(scale, Math.min((float) height / bitmap.getWidth(),
                                             (float) width / bitmap.getHeight()));
            if (scale < 1 || rotation != 0) {
                Matrix m = new Matrix();
                m.setScale(scale, scale);
                m.postRotate(rotation);
                Bitmap transformed = Bitmap.createBitmap(bitmap, 0, 0, (int) ((bitmap.getWidth())),
                                                         (int) ((bitmap.getHeight()) ), m, true);
                if (bitmap != transformed)
                    bitmap.recycle();

                return transformed;
            }
        }
        return bitmap;
    }

    public static Rect createCompatibleBounds(Rect original, int highQ, int lowQ) {
        Rect rect = new Rect(0, 0, original.width(), original.height());
        Rect targetRect = new Rect(0, 0, rect.width(), rect.height());

        int maxSide = Math.max(rect.width(), rect.height());
        boolean smallScreen = FeatureUtils.isSmallScreen(Resources.getSystem());

        int quality = smallScreen ? lowQ : highQ;

        if (maxSide > quality) {
            if (rect.width() == rect.height())
                targetRect = new Rect(0, 0, quality, quality);
            else
                targetRect = scaleToSide(rect, quality, rect.width() == maxSide);
        }

        return targetRect;

    }

    public static Rect createCompatibleBounds(Rect original, int maxSize) {
        Rect rect = new Rect(0, 0, original.width(), original.height());
        Rect targetRect = new Rect(0, 0, rect.width(), rect.height());

        int maxSide = Math.max(rect.width(), rect.height());

        if (rect.width() == rect.height())
            targetRect = new Rect(0, 0, maxSize, maxSize);
        else
            targetRect = scaleToSide(rect, maxSize, rect.width() == maxSide);

        return targetRect;

    }

    public static Rect scaleToSide(Rect rect, int maxSize, boolean isWidth) {
        Rect result = new Rect();
        float ratio = rect.width() / (float) rect.height();
        int sizeDiff;
        int preferedWidth = rect.width();
        int preferedHeight = rect.height();
        int scaledSide;
        int maxSide;

        if (isWidth) {
            maxSide = rect.width();
        } else {
            maxSide = rect.height();
        }
        sizeDiff = maxSide - maxSize;
        scaledSide = (isWidth ? preferedHeight : preferedWidth) - (int) (sizeDiff * (!isWidth ? ratio : 1f / ratio));
        result.right = isWidth ? maxSize : scaledSide;
        result.bottom = isWidth ? scaledSide : maxSize;

        return result;
    }

    public static int getMediaRotation(ContentResolver contentResolver, Uri uri) {
        Cursor cursor = null;

        try {
            cursor = contentResolver.query(uri, CONTENT_ORIENTATION, null, null, null);
            if (cursor == null || !cursor.moveToFirst()) {
                return -1;
            }
            return cursor.getInt(0);
        } catch (RuntimeException ignored) {
            // If the orientation column doesn't exist, assume no rotation.
            return 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}
