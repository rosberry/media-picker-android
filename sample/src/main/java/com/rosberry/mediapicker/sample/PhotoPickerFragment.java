package com.rosberry.mediapicker.sample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.rosberry.mediapicker.MediaPicker;
import com.rosberry.mediapicker.data.MediaResult;
import com.rosberry.mediapicker.data.PhotoParams;

/**
 * Created by dmitry on 02.10.17.
 */

public class PhotoPickerFragment extends Fragment implements View.OnClickListener, MediaPicker.OnMediaListener{

    ImageView contentImageView;
    ProgressBar progressBar;

    MediaPicker mediaPicker;
    PhotoParams photoParamsOptimized;
    PhotoParams photoParamsUnmutable;

    public static Fragment newInstance() {
        return new PhotoPickerFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_photo_picker, container, false);

        view.findViewById(R.id.button_photo_pick_gallery).setOnClickListener(this);
        view.findViewById(R.id.button_photo_pick_camera).setOnClickListener(this);
        contentImageView = view.findViewById(R.id.image_photo_content);
        progressBar = view.findViewById(R.id.progress_circle);

        mediaPicker = MediaPicker.from(getActivity()).to(this);

        photoParamsOptimized = new PhotoParams.Builder()
                .mutable(true)
                .type(MediaPicker.Type.IMAGE)
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
                .type(MediaPicker.Type.IMAGE)
                .mutable(false)
                .build();

        return view;
    }

    @Override
    public void onClick(View view) {


        MainActivity mainActivity = (MainActivity) getActivity();
        switch (view.getId()) {
            case R.id.button_photo_pick_gallery:
                if (mainActivity.checkPermissions(MainActivity.REQUEST_CODE_GALLERY, Manifest.permission.WRITE_EXTERNAL_STORAGE))

                    mediaPicker.with(photoParamsOptimized).pick();
                break;
            case R.id.button_photo_pick_camera:
                if (mainActivity.checkPermissions(MainActivity.REQUEST_CODE_CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE))

                    mediaPicker.with(photoParamsOptimized).take();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mediaPicker.process(requestCode, resultCode, data);
    }

    @Override
    public void onPickMediaStateChanged(boolean inProgress) {
        progressBar.setVisibility(inProgress ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onPickMediaResult(@NonNull MediaResult result, @Nullable CharSequence errorMsg) {
        Toast.makeText(getActivity(), "Media path: " + result.getPath(), Toast.LENGTH_SHORT).show();

        if (errorMsg == null) {
            contentImageView.setImageBitmap(BitmapFactory.decodeFile(result.getPath()));
        } else {
            Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MainActivity.REQUEST_CODE_GALLERY:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mediaPicker.with(photoParamsOptimized).pick();
                } else {
                    Toast.makeText(getActivity(), String.format("Access denied STORAGE "),
                                   Toast.LENGTH_SHORT).show();
                }
                break;
            case MainActivity.REQUEST_CODE_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mediaPicker.with(photoParamsOptimized).take();
                } else {
                    Toast.makeText(getActivity(), String.format("Access denied CAMERA"),
                                   Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                                                 grantResults);
        }
    }


}
