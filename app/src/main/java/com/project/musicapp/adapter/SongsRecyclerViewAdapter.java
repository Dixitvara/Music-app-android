package com.project.musicapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.musicapp.AudioModel;
import com.project.musicapp.MainActivity;
import com.project.musicapp.R;
import com.project.musicapp.singleton.MyMediaPlayer;

import java.util.ArrayList;

public class SongsRecyclerViewAdapter extends RecyclerView.Adapter<SongsRecyclerViewAdapter.ViewHolder> {

    ArrayList<AudioModel> songList;
    Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;
        LinearLayout songItemLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.songNameItem);
            imageView = itemView.findViewById(R.id.songImageItem);
            songItemLayout = itemView.findViewById(R.id.songItemLayout);
        }
    }

    public SongsRecyclerViewAdapter(ArrayList<AudioModel> songList, Context context) {
        this.songList = songList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.songs_view_layout, parent, false);
        return new SongsRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AudioModel songData = songList.get(position);
        holder.textView.setText(songData.getTitle());

        if(MyMediaPlayer.currentIndex == position)
            holder.textView.setTextColor(R.color.red);
        holder.songItemLayout.setOnClickListener(v -> {
            MyMediaPlayer.getInstance().reset();
            MyMediaPlayer.currentIndex = position;
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("SONGS", songList);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }
}
