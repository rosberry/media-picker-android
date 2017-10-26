package com.rosberry.mediapicker.util;

import android.content.Context;
import android.graphics.Rect;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import com.rosberry.mediapicker.MediaPicker;
import com.rosberry.mediapicker.data.MediaOptions;
import com.rosberry.mediapicker.data.PhotoOptions;

/**
 * Created by dmitry on 04.10.17.
 */

public final class VideoUtils {
    public static final String[] MIMES = new String[]{"mp4", "mpeg","mpeg4", "m4v", "mkv", "webm", "avi", "3gp", "gif"};

    public static PhotoOptions getVideoOptions(Context context, Uri uri) {
        String type = PhotoUtils.parseType(context.getContentResolver().getType(uri));
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(context, uri);
        int videoHeight = 0;
        int videoWidth = 0;
        try {
            videoHeight = Integer.parseInt(
                    metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            videoWidth = Integer.parseInt(
                    metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        } catch (Exception e) {
            e.printStackTrace();
        }
        metaRetriever.release();

        return new PhotoOptions(new Rect(0, 0, videoWidth, videoHeight), type);
    }

    public static boolean isVideo(String originalPath) {
        for (String item : MIMES) {
            if (originalPath.toLowerCase().endsWith(item.toLowerCase()))
                return true;
        }
        return false;
    }

}
