package com.rosberry.mediapicker.app;

import com.rosberry.mediapicker.app.ApplicationPicker;
import com.rosberry.mediapicker.app.CameraPicker;
import com.rosberry.mediapicker.app.GalleryPicker;

/**
 * Created by dmitry on 29.08.17.
 */

public final class PickFactory {

     public static ApplicationPicker getInstance(int id) {

        switch (id) {
            default:
                return new GalleryPicker();

            case CameraPicker.REQUEST_CODE:

                return new CameraPicker();
        }
    }
}
