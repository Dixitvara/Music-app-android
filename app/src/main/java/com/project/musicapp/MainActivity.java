package com.project.musicapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    TextView songNameTxtView;
    ImageButton previousSongBtn, nextSongBtn, playSongBtn;
    MediaPlayer music;
    SeekBar seekBar;
    Handler handler;
    Runnable updateSeekBar;

    boolean isPlaying = false;
    private static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songNameTxtView = findViewById(R.id.songName);
        previousSongBtn = findViewById(R.id.prevSongBtn);
        playSongBtn = findViewById(R.id.playBtn);
        nextSongBtn = findViewById(R.id.nextSongBtn);
        seekBar = findViewById(R.id.seekBar);

        if (!checkPermission()) {
            requestPermission();
            return;
        }

        music = MediaPlayer.create(this, R.raw.song);

        handler = new Handler();
        updateSeekBar = new Runnable() {
            @Override
            public void run() {
                if (music != null) {
                    int currentPosition = music.getCurrentPosition();
                    int totalDuration = music.getDuration();
                    seekBar.setProgress(currentPosition * 100 / totalDuration);
                    handler.postDelayed(this, 1000);
                }
            }
        };

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    int newPosition = music.getDuration() * progress / 100;
                    music.seekTo(newPosition);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (music.isPlaying())
                    music.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                music.start();
            }
        });

        playSongBtn.setOnClickListener(v -> {
            if (isPlaying) {
                music.pause();
                playSongBtn.setImageResource(R.drawable.play);
                handler.removeCallbacks(updateSeekBar);
            } else {
                music.start();
                playSongBtn.setImageResource(R.drawable.pause);
                handler.postDelayed(updateSeekBar, 1000);
            }
            isPlaying = !isPlaying;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (music != null) {
            music.release();
            music = null;
            handler.removeCallbacks(updateSeekBar);
        }
    }

    boolean checkPermission() {
        return ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
    }

    void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE))
            Toast.makeText(MainActivity.this, "Read permission is required", Toast.LENGTH_SHORT).show();
        else
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
    }
}