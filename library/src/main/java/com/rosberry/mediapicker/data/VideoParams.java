package com.rosberry.mediapicker.data;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.IntRange;

/**
 * Created by dmitry on 29.08.17.
 */

public class VideoParams {

    private int id;
    private boolean highQuality;
    private boolean facingCamera;
    private int duration;

    private String uri;
    private String bufferedUri;
    private String dir;

    private CharSequence noGalleryMsg;
    private CharSequence noCameraMsg;
    private CharSequence takeErrorMsg;
    private CharSequence pickGalleryErrorMsg;

    private VideoParams() {}

    public int getId() {
        return id;
    }


    public CharSequence getNoGalleryMsg() {
        return noGalleryMsg;
    }

    public CharSequence getNoCameraMsg() {
        return noCameraMsg;
    }

    public CharSequence getTakeErrorMsg() {
        return takeErrorMsg;
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


    public static class Builder {

        private VideoParams params = new VideoParams();

        public Builder id(int id) {
            params.id = id;

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
            params.takeErrorMsg = text;

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

        public VideoParams build() {

            return params;
        }


    }

}
