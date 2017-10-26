package com.rosberry.mediapicker.data;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.IntRange;

import com.rosberry.mediapicker.MediaPicker;

/**
 * Created by dmitry on 29.08.17.
 */

public class PhotoParams {

    public static final int HIGH_IMAGE_QUALITY_SIZE = 4096;
    public static final int LOW_IMAGE_QUALITY_SIZE = 2048;

    //photo
    private int id;
    private int maxSize;
    private int compression;
    private boolean adjustTextureSize;
    private boolean rotate;
    private boolean mutable;

    private Bitmap.Config pixelFormat;

    //video
    private boolean highQuality;
    private boolean facingCamera;
    private long duration;

    //general
    private MediaPicker.Type type;

    private String uri;
    private String bufferedUri;
    private String dir;

    private CharSequence noGalleryMsg;
    private CharSequence noCameraMsg;
    private CharSequence takePhotoErrorMsg;
    private CharSequence pickGalleryErrorMsg;

    private PhotoParams() {
        maxSize = Integer.MAX_VALUE;
        compression = 100;
        pixelFormat = Bitmap.Config.ARGB_8888;
        mutable = true;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public int getCompression() {
        return compression;
    }

    public int getId() {
        return id;
    }

    public boolean isAdjustTextureSize() {
        return adjustTextureSize;
    }

    public boolean isRotate() {
        return rotate;
    }

    public boolean isMutable() {
        return mutable;
    }

    public Bitmap.Config getPixelFormat() {
        return pixelFormat;
    }

    public CharSequence getNoGalleryMsg() {
        return noGalleryMsg;
    }

    public CharSequence getNoCameraMsg() {
        return noCameraMsg;
    }

    public CharSequence getTakePhotoErrorMsg() {
        return takePhotoErrorMsg;
    }

    public CharSequence getPickGalleryErrorMsg() {
        return pickGalleryErrorMsg;
    }

    public void setUri(Uri uri) {
        this.uri = uri.toString();
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getDir() {
        return dir;
    }

    public void setBufferedUri(Uri uri) {
        this.bufferedUri = uri.toString();
    }

    public Uri getUri() {
        return Uri.parse(uri == null ? "" : uri);
    }

    public Uri getBufferedUri() {
        return Uri.parse(bufferedUri == null ? "" : bufferedUri);
    }

    public boolean isHighQuality() {
        return highQuality;
    }

    public boolean isFacingCamera() {
        return facingCamera;
    }

    public long getDuration() {
        return duration;
    }

    public MediaPicker.Type getType() {
        return type;
    }

    public static class Builder {

        private PhotoParams params = new PhotoParams();

        public Builder id(int id) {
            params.id = id;

            return this;
        }

        public Builder type(MediaPicker.Type type) {
            params.type = type;

            return this;
        }

        public Builder duration(long duration) {
            params.duration = duration;

            return this;
        }

        public Builder facing(boolean facing) {
            params.facingCamera = facing;

            return this;
        }

        public Builder highQuality(boolean b) {
            params.highQuality = b;

            return this;
        }

        public Builder maxSize(@IntRange(from = 160, to = 8192) int size) {
            params.maxSize = size;

            return this;
        }

        public Builder pixelFormat(Bitmap.Config pixelFormat) {
            params.pixelFormat = pixelFormat;

            return this;
        }

        public Builder compression(@IntRange(from = 1, to = 100) int value) {
            params.compression = value;

            return this;
        }

        public Builder adjustTextureSize(boolean b) {
            params.adjustTextureSize = b;

            return this;
        }

        public Builder rotate(boolean b) {
            params.rotate = b;

            return this;
        }

        public Builder mutable(boolean b) {
            params.mutable = b;

            return this;
        }

        public Builder noGalleryError(CharSequence text) {
            params.noGalleryMsg = text;

            return this;
        }

        public Builder noCameraError(CharSequence text) {
            params.noCameraMsg = text;

            return this;
        }

        public Builder takePhotoError(CharSequence text) {
            params.takePhotoErrorMsg = text;

            return this;
        }

        public Builder pickGalleryError(CharSequence text) {
            params.pickGalleryErrorMsg = text;

            return this;
        }

        public Builder uri(Uri uri) {
            params.bufferedUri = uri.toString();

            return this;
        }

        public Builder dir(String dir) {
            params.dir = dir;

            return this;
        }

        public PhotoParams build() {

            return params;
        }


    }

}
