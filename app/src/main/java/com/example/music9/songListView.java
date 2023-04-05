package com.example.music9;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.music9.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class songListView extends ArrayAdapter<String> {

    private ArrayList<File> songs;
    private Context context;
    Bitmap thumbnailImage;
    public songListView(@NonNull Context context, int resource, @NonNull ArrayList songs) {
        super(context, resource, songs);
        this.songs = songs;
        this.context = context;
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return songs.get(position).getName().replace(".mp3", "").toString();
    }
    MediaMetadataRetriever metadata = new MediaMetadataRetriever();

    public String getArtist(int position) {
        metadata.setDataSource(songs.get(position).getAbsolutePath());
        String artist =  metadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

        if(artist!="")
        {
            return artist;
        }
        else return "No artist";

    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview,parent,false);
        TextView songNameList = convertView.findViewById(R.id.songNameList);
        TextView artistName = convertView.findViewById(R.id.artistNameList);
        ImageView thumbnail = convertView.findViewById(R.id.songThumbnaiList);
        songNameList.setText(getItem(position));
        artistName.setText(getArtist(position));
        metadata.setDataSource(songs.get(position).getAbsolutePath());

        byte [] data = metadata.getEmbeddedPicture();
        //coverart is an Imageview object

        // convert the byte array to a bitmap
        if(data != null)
        {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            thumbnail.setImageBitmap(bitmap); //associated cover art in bitmap
        }
        else
        {
            thumbnail.setImageResource(R.drawable.music); //any default cover resourse folder
        }

        thumbnail.setAdjustViewBounds(true);



        return convertView;
    }
}
