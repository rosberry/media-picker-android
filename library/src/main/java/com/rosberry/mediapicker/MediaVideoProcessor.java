package com.rosberry.mediapicker;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.rosberry.mediapicker.app.ApplicationPicker;
import com.rosberry.mediapicker.data.PhotoOptions;
import com.rosberry.mediapicker.data.PhotoParams;
import com.rosberry.mediapicker.util.FileUtils;
import com.rosberry.mediapicker.util.PhotoUtils;
import com.rosberry.mediapicker.util.VideoUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;

/**
 * Created by dmitry on 31.08.17.
 */

final class MediaVideoProcessor extends AsyncTaskLoader<String> {

    private PhotoParams photoParams;

    MediaVideoProcessor(Context context, PhotoParams photoParams) {
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

        boolean remotePhoto = isRemote(uri);

        String originalPath = null;
        PhotoOptions originalPhotoOptions = VideoUtils.getVideoOptions(getContext(), uri);

        File externalVideo = getExternalVideo();
        File cachedPhoto = new File(photoParams.getDir(), externalVideo.getName()
                + String.format(Locale.US, ".%s", originalPhotoOptions.getType()));

        if (remotePhoto) {

            try {
                FileUtils.copy(getContext().getContentResolver().openInputStream(uri), cachedPhoto);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            return cachedPhoto.getPath();

        } else {

            originalPath = FileUtils.getPath(getContext(), uri);
            if (originalPath == null)
                originalPath = uri.getPath();

            boolean hasExtension;

            hasExtension = VideoUtils.isVideo(originalPath);
            if (!hasExtension) {
                new File(originalPath).renameTo(cachedPhoto);
                originalPath = cachedPhoto.getPath();
            }

            return originalPath;
        }

    }



    @NonNull
    private File getExternalVideo() {
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

    private boolean isRemote(Uri uri) {

        String path = uri.toString();

        return path.startsWith(PhotoUtils.GOOGLE_CLOUD_URL)
                || path.contains(PhotoUtils.GALLERY_CLOUD_URL)
                || uri.getAuthority().equals("com.google.android.apps.docs.storage")
                || uri.getAuthority().equals("com.dropbox.android.FileCache");
    }
}
