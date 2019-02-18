package com.rosberry.mediapicker;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.rosberry.mediapicker.data.PhotoOptions;
import com.rosberry.mediapicker.data.PhotoParams;
import com.rosberry.mediapicker.app.ApplicationPicker;
import com.rosberry.mediapicker.util.ExifUtils;
import com.rosberry.mediapicker.util.FileUtils;
import com.rosberry.mediapicker.util.PhotoUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Locale;

/**
 * Created by dmitry on 31.08.17.
 */

final class MediaPhotoProcessor extends AsyncTaskLoader<String> {

    private PhotoParams photoParams;

    MediaPhotoProcessor(Context context, PhotoParams photoParams) {
        super(context);
        this.photoParams = photoParams;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public String loadInBackground() {
        Uri uri = photoParams.getUri();

        boolean remotePhoto = remotePhoto(uri);

        PhotoOptions originalPhotoOptions = null;
        Rect targetBounds = null;
        String originalPath = null;
        Bitmap targetBitmap = null;

        int originalRotation = PhotoUtils.getRotation(getContext(), uri,
                                                      PhotoUtils.getRealPathFromURI(getContext(), uri));

        if (remotePhoto) {
            originalPhotoOptions = PhotoUtils.loadBoundFromCloud(getContext(), uri);
        } else {
            originalPath = PhotoUtils.getRealPathFromURI(getContext(), uri);
            if (originalPath == null)
                originalPath = uri.getPath();

            originalPhotoOptions = PhotoUtils.getBitmapBounds(originalPath);
        }
        targetBounds = new Rect(originalPhotoOptions.getSize());

        File externalPhoto = getExternalPhoto();

        File cachedPhoto = new File(photoParams.getDir(), externalPhoto.getName()
                + String.format(Locale.US, ".%s", originalPhotoOptions.getType()));
        boolean photoSuccessfullySaved = false;

        if (!"gif".equals(originalPhotoOptions.getType())) {
            if (photoParams.isMutable()) {
                if (photoParams.getMaxSize() != Integer.MAX_VALUE) {
                    if (Math.max(targetBounds.width(), targetBounds.height()) > photoParams.getMaxSize()) {
                        targetBounds = PhotoUtils.createCompatibleBounds(targetBounds, photoParams.getMaxSize());
                    }
                }
                if (photoParams.isAdjustTextureSize()) {
                    targetBounds = PhotoUtils.createCompatibleBounds(targetBounds,
                                                                     PhotoParams.HIGH_IMAGE_QUALITY_SIZE,
                                                                     PhotoParams.LOW_IMAGE_QUALITY_SIZE);
                }
            }

            targetBitmap = PhotoUtils.decodeBitmap(getContext(),
                                                   uri,
                                                   remotePhoto, originalPhotoOptions.getSize(),
                                                   photoParams.isRotate() ? originalRotation : 0,
                                                   targetBounds.width(),
                                                   targetBounds.height(),
                                                   photoParams.getPixelFormat());



           photoSuccessfullySaved = FileUtils.bitmapToFile(targetBitmap,     cachedPhoto.getPath(),
                                                     photoParams.getCompression(),
                                                     getCompressType(originalPhotoOptions));
        }else {
            try {
                FileUtils.copy(new FileInputStream(new File(PhotoUtils.getRealPathFromURI(getContext(), uri))), cachedPhoto);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (!externalPhoto.getParent().equals(cachedPhoto.getParent()))
            externalPhoto.delete();

        if (photoSuccessfullySaved) {
            if (!photoParams.isRotate())
                ExifUtils.setExifOrientation(cachedPhoto.getPath(), originalRotation);

            return cachedPhoto.getPath();
        } else
            return null;
    }

    private Bitmap.CompressFormat getCompressType(PhotoOptions originalPhotoOptions) {
        if (originalPhotoOptions.getType().length() > 0) {
            String type = originalPhotoOptions.getType().toLowerCase();

            if (type.equals("jpg") || type.equals("jpeg")) {
                return Bitmap.CompressFormat.JPEG;
            } else if (type.equals("png")) {
                return Bitmap.CompressFormat.PNG;
            } else if (type.equals("webp")) {
                return Bitmap.CompressFormat.WEBP;
            }
        }
        return Bitmap.CompressFormat.JPEG;
    }

    @NonNull
    private File getExternalPhoto() {
        String bufferedUriFullPath = photoParams.getBufferedUri().getPath();

        File externalPhoto;
        if (bufferedUriFullPath.contains(ApplicationPicker.EXTERNAL_PATH)) {
            String buffered = bufferedUriFullPath.substring(bufferedUriFullPath.indexOf(
                    ApplicationPicker.EXTERNAL_PATH) + ApplicationPicker.EXTERNAL_PATH.length());
            externalPhoto = new File(Environment.getExternalStorageDirectory(), buffered);
        } else {
            externalPhoto = new File(PhotoUtils.getRealPathFromURI(getContext(), photoParams.getBufferedUri()));
        }
        return externalPhoto;
    }

    private boolean remotePhoto(Uri uri) {
        String path = uri.toString();
        return path.startsWith(PhotoUtils.GOOGLE_CLOUD_URL) || path.contains(PhotoUtils.GALLERY_CLOUD_URL);
    }
}
