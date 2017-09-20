package com.rosberry.mediapicker.data;

import android.graphics.Rect;

/**
 * Created by dmitry on 19.09.17.
 */

public class PhotoOptions {
    private Rect size;
    private String type;

    public PhotoOptions(Rect size, String type) {
        this.size = size;
        this.type = type;
    }

    public Rect getSize() {
        return size;
    }

    public String getType() {
        return type;
    }
}
