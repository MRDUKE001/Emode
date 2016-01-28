package com.wind.emode.testcase;

import com.wind.emode.BaseActivity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;
import com.wind.emode.R;

public class VideoTest extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_video_test);
        Uri uri = Uri.parse("android.resource://com.wind.emode/"+R.raw.xperia_hd_landscapes);
        VideoView videoView = (VideoView)this.findViewById(R.id.video_view);
        videoView.setMediaController(new MediaController(this));
        videoView.setVideoURI(uri);
        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                getWindow().getDecorView().findViewById(R.id.btn_bar).setVisibility(View.VISIBLE);
            }
        });
        videoView.requestFocus();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getWindow().getDecorView().findViewById(R.id.btn_bar).setVisibility(View.GONE);
    }
}
