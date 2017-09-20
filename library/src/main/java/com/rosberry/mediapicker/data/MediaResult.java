package com.rosberry.mediapicker.data;

/**
 * Created by dmitry on 29.08.17.
 */

public final class MediaResult {
    private final int id;
    private final int requestCode;
    private String path;


    public MediaResult(int id, int requestCode) {
        this.id = id;
        this.requestCode = requestCode;
    }

    public int getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
