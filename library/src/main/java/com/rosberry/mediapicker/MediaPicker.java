package com.rosberry.mediapicker;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.rosberry.mediapicker.app.PickFactory;
import com.rosberry.mediapicker.data.MediaResult;
import com.rosberry.mediapicker.data.PhotoParams;
import com.rosberry.mediapicker.app.ApplicationPicker;
import com.rosberry.mediapicker.app.CameraPicker;
import com.rosberry.mediapicker.app.GalleryPicker;
import com.rosberry.mediapicker.util.PhotoUtils;

import java.io.File;

/**
 * Created by dmitry on 29.08.17.
 */

public final class MediaPicker implements LoaderManager.LoaderCallbacks<String> {

    private Context context;
    private Handler handler;
    private SharedPreferences preferences;
    private LoaderManager loaderManager;

    private PhotoParams photoParams;
    private MediaResult noResult;
    private static MediaResult generalResult;

    private OnMediaListener photoListener;
    private OnMediaListener generalListener;

    private static final String PHOTO_PARAMS_KEY = "media_picker_photo_params";

    public static final long DELAY_SCAN_RESULT = 500;

    private MediaPicker(Context context, LoaderManager loaderManager) {
        this.context = context;
        this.loaderManager = loaderManager;
        this.handler = new Handler();
        this.preferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        this.noResult = new MediaResult(-1, -1);
        this.photoParams = JsonConverter.fromJson(context, preferences.getString(PHOTO_PARAMS_KEY, null));
        this.generalListener = new OnMediaListener() {

            @Override
            public void onPickMediaStateChanged(boolean inProgress) {
                if (photoListener != null)
                    photoListener.onPickMediaStateChanged(inProgress);
            }

            @Override
            public void onPickMediaResult(@NonNull MediaResult result, @Nullable CharSequence errorMsg) {
                preferences.edit().clear().apply();
                // photoParams = null;

                onPickMediaStateChanged(false);

                if (photoListener != null)
                    photoListener.onPickMediaResult(result, errorMsg);

            }
        };
    }

    public static MediaPicker from(Activity context) {
        MediaPicker mediaPicker = new MediaPicker(context, context.getLoaderManager());

        return mediaPicker;
    }

    public void process(@IntRange(from = 100, to = 101) int requestCode, int resultCode,
                        @Nullable Intent data) {

        if (requestCode == CameraPicker.REQUEST_CODE ||
                requestCode == GalleryPicker.REQUEST_CODE) {

            Uri uri = data != null ? (data.getData() != null ? data.getData() : photoParams.getBufferedUri()) : photoParams.getBufferedUri();

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT
                    && requestCode == CameraPicker.REQUEST_CODE) {
                context.revokeUriPermission(uri,
                                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

            if (resultCode == Activity.RESULT_OK && photoParams != null) {

                generalResult = new MediaResult(photoParams.getId(), requestCode);
                generalListener.onPickMediaStateChanged(true);
                handler.postDelayed(new MediaResultCallback(requestCode, data, uri), DELAY_SCAN_RESULT);

            }
        }

    }

    public void pick() {
        chooseApplication(GalleryPicker.REQUEST_CODE);
    }

    public void take() {
        chooseApplication(CameraPicker.REQUEST_CODE);
    }

    public MediaPicker with(PhotoParams photoParams) {
        this.photoParams = photoParams;

        return this;
    }

    private void chooseApplication(int requestCode) {
        if (photoParams != null) {
            generalResult = new MediaResult(photoParams.getId(), requestCode);

            ApplicationPicker applicationPicker = PickFactory.getInstance(requestCode);
            applicationPicker.setMediaResultListener(generalListener);

            Uri uri = applicationPicker.start((Activity) context, generalResult, photoParams);
            photoParams.setBufferedUri(uri);
            File dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (dir != null) {
                dir.mkdirs();
                photoParams.setDir(dir.getPath());
            }
            preferences
                    .edit()
                    .putString(PHOTO_PARAMS_KEY, JsonConverter.toJson(photoParams))
                    .apply();

        } else {
            generalListener.onPickMediaResult(noResult,
                                              context.getString(R.string.error_no_mediapicker_params_specified));
        }
    }

    public MediaPicker to(OnMediaListener photoListener) {
        this.photoListener = photoListener;

        if (loaderManager.getLoader(0) != null)
            loaderManager.initLoader(0, null, MediaPicker.this);

        return this;
    }

    public interface OnMediaListener {

        void onPickMediaStateChanged(boolean inProgress);

        void onPickMediaResult(@NonNull MediaResult result, @Nullable CharSequence errorMsg);

    }

    private class MediaResultCallback implements Runnable {

        final int requestCode;
        final Intent intent;
        final Uri uri;

        private MediaResultCallback(int requestCode, Intent intent, Uri uri) {
            this.requestCode = requestCode;
            this.intent = intent;
            this.uri = uri;
        }

        @Override
        public void run() {

            if (Uri.EMPTY.equals(uri)) {

                generalListener.onPickMediaResult(generalResult, photoParams.getPickGalleryErrorMsg());

            } else {
                if (requestCode == CameraPicker.REQUEST_CODE)

                    scanGallery(uri);
                else
                    onPhotoUriAvailable(uri, null);
            }

        }

        public void onPhotoUriAvailable(Uri uri, String path) {
            if (uri == null && path == null)
                generalListener.onPickMediaResult(generalResult, photoParams.getPickGalleryErrorMsg());
            else {
                if (uri != null) {
                    photoParams.setUri(uri);
                } else
                    photoParams.setUri(Uri.parse(path));

                loaderManager.initLoader(0, null, MediaPicker.this);
            }
        }

        private void scanGallery(Uri uri) {
            String path = null;
            if (uri.toString().contains(ApplicationPicker.EXTERNAL_PATH)) {
                path = uri.getPath().substring(uri.getPath().indexOf(
                        ApplicationPicker.EXTERNAL_PATH) + ApplicationPicker.EXTERNAL_PATH.length());
            } else {
                path = PhotoUtils.getRealPathFromURI(context, uri);
            }

            MediaScannerConnection.scanFile(context, new String[]{new File(Environment.getExternalStorageDirectory(),
                                                                           path).getPath()},
                                            new String[]{"jpg", "jpeg", "JPG", "JPEG"},
                                            new MediaScannerConnection.OnScanCompletedListener() {

                                                public void onScanCompleted(String path, Uri uri) {
                                                    onPhotoUriAvailable(uri, path);
                                                }

                                            });

        }
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {

        return new MediaPhotoProcessor(context, photoParams);
    }

    @Override
    public void onLoadFinished(Loader loader, String path) {
        loaderManager.destroyLoader(loader.getId());
        generalResult.setPath(path);
        generalListener.onPickMediaResult(generalResult,
                                          path == null ? context.getString(R.string.error_process_image) : null);
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

}
