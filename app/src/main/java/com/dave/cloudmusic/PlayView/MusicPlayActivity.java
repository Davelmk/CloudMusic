package com.dave.cloudmusic.PlayView;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.dave.cloudmusic.Bean.Song;
import com.dave.cloudmusic.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MusicPlayActivity extends AppCompatActivity {
    private Button btn_stop_play;
    private Button btn_restart_play;
    private int postion;
    private List<Song> songList;
    MyHandler myHandler = null;

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);
        Intent intent=getIntent();
        postion=intent.getIntExtra("position",0);
        myHandler=new MyHandler();
        initData();
        btn_stop_play=findViewById(R.id.btn_stop_play);
        btn_stop_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.pause();
            }
        });
        btn_restart_play=findViewById(R.id.btn_restart_play);
        btn_restart_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
            }
        });
    }

    //歌单数据初始化
    private void initData(){
        songList=new ArrayList<>();
        BmobQuery<Song> query=new BmobQuery<>();
        query.findObjects(new FindListener<Song>() {
            @Override
            public void done(List<Song> object, BmobException e) {
                if (e == null) {
                    for (Song song : object) {
                        songList.add(new Song(song.getName(),song.getUrl(),song.getObjectId()));
                    }
                    myHandler.sendEmptyMessage(0);
                } else {
                    Log.e("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    private void playSong(int postion){
        Log.d("dave","running");
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(songList.get(postion).getUrl());
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    playSong(postion);
                    break;
                default:
                    Log.e("dave","消息出错...");
                    break;
            }
            super.handleMessage(msg);
        }
    }
}
