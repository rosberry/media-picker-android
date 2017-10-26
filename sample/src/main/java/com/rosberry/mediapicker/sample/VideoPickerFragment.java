package com.rosberry.mediapicker.sample;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.rosberry.mediapicker.MediaPicker;
import com.rosberry.mediapicker.data.MediaResult;
import com.rosberry.mediapicker.data.PhotoParams;

/**
 * Created by dmitry on 02.10.17.
 */

public class VideoPickerFragment extends Fragment implements View.OnClickListener, MediaPicker.OnMediaListener {

    private MediaPicker mediaPicker;

    public static Fragment newInstance() {
        return new VideoPickerFragment();
    }

    VideoView videoView;
    ProgressBar progressBar;

    PhotoParams params;

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
        View view = inflater.inflate(R.layout.f_video_picker, container, false);

        view.findViewById(R.id.button_video_pick_gallery).setOnClickListener(this);
        view.findViewById(R.id.button_video_pick_camera).setOnClickListener(this);
        videoView = view.findViewById(R.id.video_content);
        progressBar = view.findViewById(R.id.progress_circle);

        mediaPicker = MediaPicker.from(getActivity()).to(this);

        params = new PhotoParams.Builder()
                .type(MediaPicker.Type.VIDEO)
                .duration(5)
                .facing(false)
                .highQuality(true)
                .build();


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mediaPicker.process(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        MainActivity mainActivity = (MainActivity) getActivity();
        switch (view.getId()) {
            case R.id.button_video_pick_gallery:
                if (mainActivity.checkPermissions(MainActivity.REQUEST_CODE_GALLERY, Manifest.permission.WRITE_EXTERNAL_STORAGE))

                    mediaPicker.with(params).pick();
                break;
            case R.id.button_video_pick_camera:
                if (mainActivity.checkPermissions(MainActivity.REQUEST_CODE_CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE))

                    mediaPicker.with(params).take();
                break;
        }
    }

    @Override
    public void onPickMediaStateChanged(boolean inProgress) {
        progressBar.setVisibility(inProgress ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onPickMediaResult(@NonNull MediaResult result, @Nullable CharSequence errorMsg) {
        Toast.makeText(getActivity(), "Media path: " + result.getPath(), Toast.LENGTH_SHORT).show();

        if (errorMsg == null) {
            videoView.setVideoPath(result.getPath());
            videoView.setOnPreparedListener(mediaPlayer -> {
                mediaPlayer.setLooping(false);
                mediaPlayer.start();
            });
        } else {
            Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
        }
    }
}
