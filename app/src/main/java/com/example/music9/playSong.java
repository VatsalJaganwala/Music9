package com.example.music9;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class playSong extends AppCompatActivity {
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    TextView songName;
    TextView artistName;
    TextView currentTimeline;
    TextView finalTimeline;
    ImageButton playpause;
    ImageButton previous;
    ImageButton next;
    ArrayList<File> songs;
    MediaPlayer mediaPlayer;
    String name;
    int position;
    Thread updateSeek;
    Thread updateTimeline;
    SeekBar seekBar;
    String textContent;
    MediaMetadataRetriever metadata = new MediaMetadataRetriever();
    public Boolean plus;
    ImageView thumbnail;


    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        songName = findViewById(R.id.songName);
        artistName = findViewById(R.id.artistName);
        currentTimeline= findViewById(R.id.currentTimeline);
        finalTimeline = findViewById(R.id.finalTimeline);
        playpause = findViewById(R.id.playpause);
        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);
        seekBar = findViewById(R.id.seekBar);
        thumbnail= findViewById(R.id.thumbnail);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        plus=true;
        songs = (ArrayList) bundle.getParcelableArrayList("songList");
        name = intent.getStringExtra("currentSong");
        songName.setText(name);
        position = intent.getIntExtra("position",0);
        artistName.setText(getArtist(position));
        updateThumbnail(position);
        songName.setSelected(true);

        Uri uri = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer.start();
        seekBar.setMax(mediaPlayer.getDuration());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());

            }
        });
        playpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    playpause.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                }
                else{
                    playpause.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
//                    updateSeek.interrupt();
                }

            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
//                updateSeek.interrupt();
                if(position!=0){
                    position--;
                }
                else {
                    position = songs.size() - 1;
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
                mediaPlayer.start();
//                updateSeek.start();
//                updateSeek.run();
                seekBar.setMax(mediaPlayer.getDuration());
                textContent=songs.get(position).getName().toString();
                songName.setText(textContent);
                artistName.setText(getArtist(position));
                playpause.setImageResource(R.drawable.pause);
                updateThumbnail(position);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
//                updateSeek.interrupt();
                if(position!=songs.size()-1){
                    position++;
                }
                else {
                    position = 0;
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
                mediaPlayer.start();
//                updateSeek.start();
//                updateSeek.run();
                seekBar.setMax(mediaPlayer.getDuration());
                textContent=songs.get(position).getName().toString();
                songName.setText(textContent);
                artistName.setText(getArtist(position));
                playpause.setImageResource(R.drawable.pause);
                updateThumbnail(position);

            }
        });

        updateSeek = new Thread(){
            @Override
            public void run() {
                int currentPosition = 0;
                try{
                    while(currentPosition<mediaPlayer.getDuration()){
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);

                        sleep(1000);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        updateSeek.start();
//        updateTimeline = new Thread(){
//            @Override
//            public void run() {
//                int currentPosition = mediaPlayer.getCurrentPosition();
//                currentTimeline.setText(formatTime(currentPosition));
//
//            }
//        };
//        updateTimeline.start();
        finalTimeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plus= !plus;
            }
        });
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // start timer to update current time every second
                new Timer().scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        int duration = mediaPlayer.getDuration();
                        int currentPosition = mediaPlayer.getCurrentPosition();
                        // update current time TextView on UI thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                    currentTimeline.setText(formatTime(currentPosition));

                                if(plus){
                                    finalTimeline.setText(formatTime(duration));
                                }
                                else{

                                    finalTimeline.setText("-"+formatTime(duration-currentPosition));
                                }
                            }
                        });
                    }
                }, 0, 500);
            }
        });





    }
    public void updateThumbnail(int position){
        metadata.setDataSource(songs.get(position).getAbsolutePath());

        byte [] data = metadata.getEmbeddedPicture();
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
    }
    private String formatTime(int millis) {
        int seconds = millis / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public String getArtist(int position) {
        metadata.setDataSource(songs.get(position).getAbsolutePath());
        String artist =  metadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

        if(artist!="")
        {
            return artist;
        }
        else return "No artist";

    }

}