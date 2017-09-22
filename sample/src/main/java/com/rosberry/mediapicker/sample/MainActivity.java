package com.rosberry.mediapicker.sample;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.rosberry.mediapicker.MediaPicker;
import com.rosberry.mediapicker.data.MediaResult;
import com.rosberry.mediapicker.data.PhotoParams;

public class MainActivity extends Activity implements View.OnClickListener, MediaPicker.OnMediaListener {

    public static final int REQUEST_CODE_GALLERY = 123;
    public static final int REQUEST_CODE_CAMERA = 124;

    ImageView contentImageView;
    ProgressBar progressBar;

    MediaPicker mediaPicker;
    PhotoParams photoParamsOptimized;
    PhotoParams photoParamsUnmutable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button_pick_gallery).setOnClickListener(this);
        findViewById(R.id.button_pick_camera).setOnClickListener(this);
        contentImageView = findViewById(R.id.image_content);
        progressBar = findViewById(R.id.progress_circle);

        mediaPicker = MediaPicker.from(this).to(this);

        photoParamsOptimized = new PhotoParams.Builder()
                .mutable(true)
                .rotate(true)
                .maxSize(2000)
                .adjustTextureSize(true)
                .compression(80)
                .pixelFormat(Bitmap.Config.ARGB_8888)
                .noGalleryError("No Gallery")
                .noCameraError("No Camera")
                .takePhotoError("Take photo error")
                .pickGalleryError("Pick gallery error")
                .build();

        photoParamsUnmutable = new PhotoParams.Builder()
                .mutable(false)
                .build();

    }

    @Override
    public void onClick(View view) {



            switch (view.getId()) {
                case R.id.button_pick_gallery:
                    if (checkPermissions(REQUEST_CODE_GALLERY, Manifest.permission.WRITE_EXTERNAL_STORAGE))

                    mediaPicker.with(photoParamsOptimized).pick();
                    break;
                case R.id.button_pick_camera:
                    if (checkPermissions(REQUEST_CODE_CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE))

                    mediaPicker.with(photoParamsOptimized).take();
                    break;
            }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mediaPicker.process(requestCode, resultCode, data);
    }

    @Override
    public void onPickMediaStateChanged(boolean inProgress) {
        progressBar.setVisibility(inProgress ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onPickMediaResult(@NonNull MediaResult result, @Nullable CharSequence errorMsg) {
        Toast.makeText(this, "Media path: " + result.getPath(), Toast.LENGTH_SHORT).show();

        if (errorMsg == null) {
            contentImageView.setImageBitmap(BitmapFactory.decodeFile(result.getPath()));
        } else {
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_GALLERY:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mediaPicker.with(photoParamsOptimized).pick();
                } else {
                    Toast.makeText(this, String.format("Access denied STORAGE "),
                                   Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mediaPicker.with(photoParamsOptimized).take();
                } else {
                    Toast.makeText(this, String.format("Access denied CAMERA"),
                                   Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                                                 grantResults);
        }
    }
}
