package com.dave.cloudmusic.PlayView;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dave.cloudmusic.Bean.Song;
import com.dave.cloudmusic.R;
import com.dave.cloudmusic.Utils.BlurUtil;
import com.dave.cloudmusic.Utils.MergeImageUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MusicPlayActivity extends AppCompatActivity {
    private int postion;
    private List<Song> songList;
    MyHandler myHandler = null;

    private MediaPlayer mediaPlayer;

    private Toolbar toolbar;
    private ImageView image_dic;
    private LinearLayout linearLayout;

    private ObjectAnimator animator;

    private ImageView play;
    private ImageView next_song;
    private ImageView last_song;
    private ImageView play_needle;
    private boolean isPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);
        toolbar=findViewById(R.id.toolBar_play);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Name");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.back);
        }

        Intent intent=getIntent();
        postion=intent.getIntExtra("position",0);
        myHandler=new MyHandler();
        initData();

        image_dic=findViewById(R.id.image_dic);
        Bitmap discBitmap= BitmapFactory.decodeResource(this.getResources(),R.drawable.dic);
        Bitmap albumBitmap= BitmapFactory.decodeResource(this.getResources(),R.drawable.album);
        Bitmap bmp= MergeImageUtil.mergeThumbnailBitmap(discBitmap,albumBitmap);
        image_dic.setImageBitmap(bmp);
        //Bitmap bgbm = BlurUtil.startBlur(albumBitmap,8,false);
        Bitmap bgbm = BlurUtil.doBlur(albumBitmap,7,100);
        linearLayout=findViewById(R.id.layout_play);
        Drawable drawable=new BitmapDrawable(bgbm);
        linearLayout.setBackground(drawable);

        animator=ObjectAnimator.ofFloat(image_dic,
                "rotation", 0f, 360.0f);
        animator.setDuration(10000);
        animator.setRepeatCount(-1);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setInterpolator(new LinearInterpolator());


        play=findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlaying){
                    mediaPlayer.pause();
                    animator.pause();
                    play.setImageResource(R.drawable.stop_play);
                    RotateAnimation rotateAnimation=new RotateAnimation(0f,-30.0f,
                            Animation.RELATIVE_TO_SELF,0.25f,
                            Animation.RELATIVE_TO_SELF,0f);
                    rotateAnimation.setDuration(1000);
                    rotateAnimation.setFillAfter(true);
                    play_needle.startAnimation(rotateAnimation);
                    isPlaying=false;
                }else {
                    mediaPlayer.start();
                    animator.resume();
                    play.setImageResource(R.drawable.start_play);
                    RotateAnimation rotateAnimation=new RotateAnimation(-30.0f,0f,
                            Animation.RELATIVE_TO_SELF,0.25f,
                            Animation.RELATIVE_TO_SELF,0f);
                    rotateAnimation.setDuration(1000);
                    rotateAnimation.setFillAfter(true);
                    play_needle.startAnimation(rotateAnimation);
                    isPlaying=true;
                }
            }
        });
        next_song=findViewById(R.id.next_song);
        next_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MusicPlayActivity.this,
                        "下一首",Toast.LENGTH_SHORT).show();
            }
        });
        last_song=findViewById(R.id.last_song);
        last_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MusicPlayActivity.this,
                        "上一首",Toast.LENGTH_SHORT).show();
            }
        });
        play_needle=findViewById(R.id.play_needle);
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
                    isPlaying=true;
                    animator.start();
                    play.setImageResource(R.drawable.start_play);
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.reset();
                    mediaPlayer.start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
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
