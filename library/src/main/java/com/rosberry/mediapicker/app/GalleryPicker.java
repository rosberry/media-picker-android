package com.rosberry.mediapicker.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.rosberry.mediapicker.util.FeatureUtils;
import com.rosberry.mediapicker.data.MediaResult;
import com.rosberry.mediapicker.data.PhotoParams;
import com.rosberry.mediapicker.R;

/**
 * Created by dmitry on 29.08.17.
 */

public class GalleryPicker extends ApplicationPicker {

    public static final int REQUEST_CODE = 101;

    public GalleryPicker() {
        super();
        intent.setAction(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

    }

    @Override
    public Uri start(Activity activity, MediaResult mediaResult, PhotoParams photoParams) {
        Log.d(getClass().getName(), "pick gallery");

        if (hasPicker(activity)) {
            if (FeatureUtils.isSdAvailable()) {
                activity.startActivityForResult(intent, mediaResult.getRequestCode());

                return createExternalUri(activity.getApplicationContext());
            }else {
                mediaResultListener.onPickMediaResult(mediaResult, activity.getString(R.string.error_please_plug_sd_card));

            }

        } else {
            mediaResultListener.onPickMediaResult(mediaResult, photoParams.getNoGalleryMsg());
        }

        return Uri.EMPTY;
    }

}
