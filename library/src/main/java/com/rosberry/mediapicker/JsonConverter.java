package com.rosberry.mediapicker;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.rosberry.mediapicker.data.PhotoParams;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dmitry on 31.08.17.
 */

final class JsonConverter {

     static String toJson(PhotoParams params) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", params.getId());
            jsonObject.put("compression", params.getCompression());
            jsonObject.put("maxSize", params.getMaxSize());
            jsonObject.put("adjustTextureSize", params.isAdjustTextureSize());
            jsonObject.put("mutable", params.isMutable());
            jsonObject.put("rotate", params.isRotate());
            jsonObject.put("pixelFormat", params.getPixelFormat().ordinal());
            jsonObject.put("bufferedUri", params.getBufferedUri().toString());
            jsonObject.put("dir", params.getDir());

            jsonObject.put("highQuality", params.isHighQuality());
            jsonObject.put("facingCamera", params.isFacingCamera());
            jsonObject.put("duration", params.getDuration());
            jsonObject.put("type", params.getType().toString());

            jsonObject.put("noGallery", params.getNoGalleryMsg());
            jsonObject.put("noCamera", params.getNoCameraMsg());
            jsonObject.put("takePhotoError", params.getTakePhotoErrorMsg());
            jsonObject.put("pickGalleryError", params.getPickGalleryErrorMsg());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public static PhotoParams fromJson(Context context, String json) {
        JSONObject jsonObject = null;

        if (json == null)
            return null;

        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        int id = jsonObject.optInt("id", 0);
        int compression = jsonObject.optInt("compression", 100);
        int maxSize = jsonObject.optInt("maxSize", -1);

        boolean adjustTextureSize = jsonObject.optBoolean("adjustTextureSize", false);
        boolean mutable = jsonObject.optBoolean("mutable", true);
        boolean rotate = jsonObject.optBoolean("rotate", false);

        boolean highQuality = jsonObject.optBoolean("highQuality", true);
        boolean facingCamera = jsonObject.optBoolean("facingCamera", false);
        long duration = jsonObject.optLong("duration", 2);
        MediaPicker.Type type = MediaPicker.Type.valueOf(jsonObject.optString("type", MediaPicker.Type.IMAGE.name()));

        Bitmap.Config pixelFormat = Bitmap.Config.values()[jsonObject.optInt("pixelFormat", 1)];
        Uri bufferedUri = Uri.parse(jsonObject.optString("bufferedUri", ""));
        String dir = jsonObject.optString("dir", "");
        CharSequence noGallery = jsonObject.optString("noGallery", context.getString(R.string.error_no_gallery_application));
        CharSequence noCamera = jsonObject.optString("noCamera", context.getString(R.string.error_no_camera_application));
        CharSequence takePhotoError =  jsonObject.optString("takePhotoError", context.getString(R.string.error_cannot_take_image));
        CharSequence pickGalleryError = jsonObject.optString("pickGalleryError", context.getString(R.string.error_cannot_pick_image));

        return new PhotoParams.Builder()
                .id(id)
                .type(type)
                .adjustTextureSize(adjustTextureSize)
                .rotate(rotate)
                .compression(compression)
                .maxSize(maxSize)
                .pixelFormat(pixelFormat)
                .mutable(mutable)
                .uri(bufferedUri)
                .dir(dir)

                .duration(duration)
                .facing(facingCamera)
                .highQuality(highQuality)

                .noGalleryError(noGallery)
                .noCameraError(noCamera)
                .takePhotoError(takePhotoError)
                .pickGalleryError(pickGalleryError)
                .build();
    }

}
