package com.rosberry.mediapicker.data;

import android.graphics.Rect;

/**
 * Created by dmitry on 19.09.17.
 */

public class PhotoOptions extends MediaOptions{
    private Rect size;

    public PhotoOptions(Rect size, String type) {
        super(type);
        this.size = size;

    }

    public Rect getSize() {
        return size;
    }

}
