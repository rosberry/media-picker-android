package com.rosberry.mediapicker.util;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Environment;

/**
 * Created by neestell on 28.11.15.
 */
public final class FeatureUtils {

    public static boolean isSmallScreen(Resources resources) {
        int screenLayout = resources.getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        return screenLayout == Configuration.SCREENLAYOUT_SIZE_SMALL;
    }


    public static boolean isSdAvailable() {
        if (!Environment.isExternalStorageRemovable())
            return true;
        String state = Environment.getExternalStorageState();
        boolean storageAvailable = false;
        boolean storageWritable = false;

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            storageAvailable = storageWritable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            storageAvailable = true;
            storageWritable = false;
        } else {
            storageAvailable = storageWritable = false;
        }

        if (storageAvailable && storageWritable) {
            return true;
        } else {
            return false;
        }
    }

}
