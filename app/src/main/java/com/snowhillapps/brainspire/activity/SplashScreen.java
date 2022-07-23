package com.snowhillapps.brainspire.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.VideoView;

import com.snowhillapps.brainspire.R;

public class SplashScreen extends AppCompatActivity {
        private VideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        videoView = findViewById(R.id.videoView);


        String video_path = new StringBuilder("android.resource://")
                .append(getPackageName())
                .append("/raw/video_splash")
                .toString();

        videoView.setVideoPath(video_path);


        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {


               new Handler().postDelayed(new Runnable() {
                   @Override
                   public void run() {

                Intent intent = new Intent(getApplicationContext() , LoginActivity.class);
                startActivity(intent);
                finish();

                   }
               },3000);
            }
        });

            videoView.start();

    }
}