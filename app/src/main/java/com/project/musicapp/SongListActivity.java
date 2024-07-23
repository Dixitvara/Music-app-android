package com.project.musicapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.musicapp.adapter.SongsRecyclerViewAdapter;

import java.io.File;
import java.util.ArrayList;

public class SongListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView noSongTxtView;
    ArrayList<AudioModel> songList;
    SongsRecyclerViewAdapter adapter;
    Cursor cursor;
    MediaPlayer music;
    static int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);

        if (!checkPermission()) {
            requestPermission();
            return;
        }

        recyclerView = findViewById(R.id.songRecyclerView);
        noSongTxtView = findViewById(R.id.noSongTxtView);

        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION
        };

        songList = new ArrayList<>();
        adapter = new SongsRecyclerViewAdapter(songList, getApplicationContext());

        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        cursor = contentResolver.query(uri, projection, selection, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(0);
                String duration = cursor.getString(2);
                String path = cursor.getString(1);
                AudioModel songData = new AudioModel(name, duration, path);
                if (new File(songData.getPath()).exists())
                    songList.add(songData);
            }
            if (songList.isEmpty())
                noSongTxtView.setVisibility(View.VISIBLE);
            else {
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(adapter);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (recyclerView != null)
            recyclerView.setAdapter(new SongsRecyclerViewAdapter(songList, getApplicationContext()));
    }

    boolean checkPermission() {
        return ContextCompat.checkSelfPermission(SongListActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(SongListActivity.this, android.Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED;
    }

    void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(SongListActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE))
            Toast.makeText(SongListActivity.this, "Read permission is required", Toast.LENGTH_SHORT).show();
        else
            ActivityCompat.requestPermissions(SongListActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
    }
}