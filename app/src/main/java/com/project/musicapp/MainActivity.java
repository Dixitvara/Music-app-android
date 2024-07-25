package com.project.musicapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.project.musicapp.singleton.MyMediaPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    TextView songNameTv, currentTimeTv, totalTimeTv;
    ImageButton previousSongBtn, nextSongBtn, playSongBtn, shuffle, showSongBtn;
    SeekBar seekBar;
    Handler handler;

    AudioModel currentSong;
    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();

    ArrayList<AudioModel> songList;
    LottieAnimationView animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songNameTv = findViewById(R.id.songName);
        totalTimeTv = findViewById(R.id.totalTimeTv);
        currentTimeTv = findViewById(R.id.currentTimeTV);
        previousSongBtn = findViewById(R.id.prevSongBtn);
        playSongBtn = findViewById(R.id.playBtn);
        nextSongBtn = findViewById(R.id.nextSongBtn);
        showSongBtn = findViewById(R.id.viewSongs);
        shuffle = findViewById(R.id.shuffle);
        seekBar = findViewById(R.id.seekBar);
        animationView = findViewById(R.id.animationView);

        handler = new Handler();

        songList = (ArrayList<AudioModel>) getIntent().getSerializableExtra("SONGS");

        songNameTv.setSelected(true);
        setResourcesWithMusic();
        playSong();
        animationView.playAnimation();

        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    currentTimeTv.setText(milliSecondsToMinutes(mediaPlayer.getCurrentPosition() + ""));
                }
                new Handler().postDelayed(this, 100);
                if (mediaPlayer.isPlaying())
                    playSongBtn.setImageResource(R.drawable.pause);
                else
                    playSongBtn.setImageResource(R.drawable.play);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    mediaPlayer.seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        playSongBtn.setOnClickListener(v -> pausePlay());
        nextSongBtn.setOnClickListener(v -> nextSong());
        previousSongBtn.setOnClickListener(v -> prevSong());
        shuffle.setOnClickListener(v -> shuffleSong());
        showSongBtn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), SongListActivity.class)));
    }

    void setResourcesWithMusic() {
        currentSong = songList.get(MyMediaPlayer.currentIndex);
        songNameTv.setText(currentSong.getTitle());
        totalTimeTv.setText(milliSecondsToMinutes(currentSong.getDuration()));
    }

    String milliSecondsToMinutes(String duration) {
        Long milliSeconds = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) % TimeUnit.MINUTES.toSeconds(1));
    }

    private void playSong() {
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            animationView.animate();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void pausePlay() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            animationView.pauseAnimation();
            playSongBtn.setImageResource(R.drawable.play);
        } else {
            mediaPlayer.start();
            animationView.playAnimation();
            playSongBtn.setImageResource(R.drawable.pause);
        }
    }

    private void nextSong() {
        if (MyMediaPlayer.currentIndex == songList.size() - 1) return;
        MyMediaPlayer.currentIndex += 1;
        mediaPlayer.reset();
        setResourcesWithMusic();
        playSong();
        animationView.playAnimation();
    }

    private void prevSong() {
        if (MyMediaPlayer.currentIndex == 0) return;
        MyMediaPlayer.currentIndex -= 1;
        mediaPlayer.reset();
        setResourcesWithMusic();
        playSong();
        animationView.playAnimation();
    }

    private void shuffleSong() {
        Random random = new Random();
        int randomNum = random.nextInt(songList.size());
        MyMediaPlayer.currentIndex = randomNum;
        mediaPlayer.reset();
        setResourcesWithMusic();
        playSong();
        animationView.playAnimation();
    }
}