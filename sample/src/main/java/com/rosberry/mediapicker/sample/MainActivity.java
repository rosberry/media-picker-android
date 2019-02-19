package com.rosberry.mediapicker.sample;

import android.content.Intent;
import android.content.pm.PackageManager;

import android.os.Build;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_GALLERY = 123;
    public static final int REQUEST_CODE_CAMERA = 124;
    RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showPhotoMode();

        radioGroup =  findViewById(R.id.radio_group_mode);

        radioGroup.setOnCheckedChangeListener(
                (radioGroup, i) -> {

                    switch (i) {
                        case R.id.radio_photo:
                            showPhotoMode();
                            break;
                        case R.id.radio_video:
                            showVideoMode();
                            break;
                    }

                });
    }

    private void showVideoMode() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_view, VideoPickerFragment.newInstance())
                .commit();
    }

    private void showPhotoMode() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_view, PhotoPickerFragment.newInstance())
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getSupportFragmentManager()
                .getFragments()
                .get(0)
                .onActivityResult(requestCode, resultCode, data);
    }

    public boolean checkPermissions(int requestCode, String... permissions) {
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                    ActivityCompat.requestPermissions(this, permissions, requestCode);
                } else {
                    ActivityCompat.requestPermissions(this, permissions, requestCode);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

}
