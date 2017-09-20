package com.rosberry.mediapicker.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;

import com.rosberry.mediapicker.data.MediaResult;
import com.rosberry.mediapicker.data.PhotoParams;
import com.rosberry.mediapicker.MediaPicker;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by dmitry on 29.08.17.
 */

public abstract class ApplicationPicker {

    public static final String EXTERNAL_PATH = "my_images";
    protected Intent intent = new Intent();
    protected MediaPicker.OnMediaListener mediaResultListener;

    public abstract Uri start(Activity activity, MediaResult mediaResult, PhotoParams photoParams);

    boolean hasPicker(Context activity) {
        return intent.resolveActivity(activity.getPackageManager()) != null;
    }

    public void setMediaResultListener(MediaPicker.OnMediaListener mediaResultListener) {
        this.mediaResultListener = mediaResultListener;
    }

    @NonNull
    private static String getFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Calendar.getInstance().getTime());
        return String.format("%s", timeStamp);
    }

    public static Uri createExternalUri(Context ctx) {
        Uri targetUri = null;

        File dir = ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photoFile = new File(dir, getFileName());

        targetUri = FileProvider.getUriForFile(ctx, ctx.getPackageName() + ".provider", photoFile);

        return targetUri;
    }

}
