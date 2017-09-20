package com.rosberry.mediapicker.app;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import com.rosberry.mediapicker.util.FeatureUtils;
import com.rosberry.mediapicker.data.MediaResult;
import com.rosberry.mediapicker.data.PhotoParams;
import com.rosberry.mediapicker.R;

import java.util.List;

/**
 * Created by dmitry on 29.08.17.
 */

public class CameraPicker extends ApplicationPicker {

    public static final int REQUEST_CODE = 100;

    public CameraPicker() {
        super();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
    }

    @Override
    public Uri start(Activity activity, MediaResult mediaResult, PhotoParams photoParams) {
        Log.d(getClass().getName(), "pick camera");

        if (hasPicker(activity)) {
            if (FeatureUtils.isSdAvailable()) {
                Uri externalUri = createExternalUri(activity.getApplicationContext());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, externalUri);

                grantReadWritePermission(activity, externalUri);
                activity.startActivityForResult(intent, mediaResult.getRequestCode());

                return externalUri;
            } else {
                mediaResultListener.onPickMediaResult(mediaResult,
                                                      activity.getString(R.string.error_please_plug_sd_card));

            }
        } else {
            mediaResultListener.onPickMediaResult(mediaResult, photoParams.getNoGalleryMsg());
        }

        return Uri.EMPTY;
    }

    private void grantReadWritePermission(Activity activity, Uri externalUri) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                            List<ResolveInfo> resInfoList = activity.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                            for (ResolveInfo resolveInfo : resInfoList) {
                                String packageName = resolveInfo.activityInfo.packageName;
                                activity.grantUriPermission(packageName, externalUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            }
        }
    }

}
