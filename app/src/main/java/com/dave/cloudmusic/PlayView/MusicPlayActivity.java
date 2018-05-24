package com.dave.cloudmusic.PlayView;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.dave.cloudmusic.MainView.MainActivity;
import com.dave.cloudmusic.R;
import com.dave.cloudmusic.Utils.BlurUtil;
import com.dave.cloudmusic.Utils.DataBaseHelper;
import com.dave.cloudmusic.Utils.MergeImageUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MusicPlayActivity extends AppCompatActivity implements View.OnClickListener{
    private String playNow;
    private int position;
    private List<Song> songList;
    //SQLite
    private SQLiteDatabase db;
    private MediaPlayer mediaPlayer;

    private Toolbar toolbar;
    private ActionBar actionBar;
    private ImageView image_dic;
    private LinearLayout linearLayout;

    private ObjectAnimator animator;

    private ImageView play;
    private ImageView next_song;
    private ImageView last_song;
    private ImageView play_needle;

    //favorite,download,comment,more
    private ImageView favorite;
    private ImageView download;
    private ImageView comment;
    private ImageView more_action;

    private boolean isPlaying;

    MyHandler myHandler = null;
    private Bitmap albumBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);

        //获取歌曲列表
        initData();

        //获取当前播放歌曲的ID
        Intent intent = getIntent();
        playNow = intent.getStringExtra("playNow");
        position = getPosition(playNow);
        if (position >= 0) {
            playSong(position);
        } else {
            Toast.makeText(MusicPlayActivity.this, "未查找到歌曲",
                    Toast.LENGTH_SHORT).show();
        }

        //toolbar
        toolbar = findViewById(R.id.toolBar_play);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(songList.get(position).getName());
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.back);
        }

        myHandler = new MyHandler();

        //加载默认图片
        image_dic = findViewById(R.id.image_dic);
        linearLayout = findViewById(R.id.layout_play);
        setDefaultPicture();
        //获取网络图片
        getImageFromNet();

        animator = ObjectAnimator.ofFloat(image_dic,
                "rotation", 0f, 360.0f);
        animator.setDuration(10000);
        animator.setRepeatCount(-1);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setInterpolator(new LinearInterpolator());
        //播放按钮
        play = findViewById(R.id.play);
        play.setOnClickListener(this);
        next_song = findViewById(R.id.next_song);
        next_song.setOnClickListener(this);
        last_song = findViewById(R.id.last_song);
        last_song.setOnClickListener(this);
        //圆盘指针
        play_needle = findViewById(R.id.play_needle);

        favorite=findViewById(R.id.favorite);
        favorite.setOnClickListener(this);
        download=findViewById(R.id.download);
        download.setOnClickListener(this);
        comment=findViewById(R.id.comment);
        comment.setOnClickListener(this);
        more_action=findViewById(R.id.more_action);
        more_action.setOnClickListener(this);

    }

    //歌单数据初始化
    private void initData() {
        songList = new ArrayList<>();
        //从数据库加载数据
        DataBaseHelper dbHelper = new DataBaseHelper(this, "Music.db", null, 1);
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("songList", null, null,
                null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String url = cursor.getString(cursor.getColumnIndex("url"));
                String picture = cursor.getString(cursor.getColumnIndex("picture"));
                songList.add(new Song(id, name, url, picture));
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private int getPosition(String id) {
        for (int i = 0; i < songList.size(); i++) {
            if (songList.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    private void playSong(int position) {
        Log.d("dave", "running");
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(songList.get(position).getUrl());
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                    isPlaying = true;
                    animator.start();
                    play.setImageResource(R.drawable.start_play);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //默认图片加载
    private void setDefaultPicture(){
        image_dic.setImageResource(R.drawable.abq);
        linearLayout.setBackground(this.getDrawable(R.drawable.default_background));
    }

    //加载图片
    private void initPicture() {
        if (albumBitmap != null) {
            Bitmap discBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.dic);
            Bitmap bmp = MergeImageUtil.mergeThumbnailBitmap(discBitmap, albumBitmap);
            image_dic.setImageBitmap(bmp);
            Bitmap bgbm = BlurUtil.doBlur(albumBitmap, 7, 100);
            Drawable drawable = new BitmapDrawable(bgbm);
            linearLayout.setBackground(drawable);
        }else {
            setDefaultPicture();
            Log.d("dave", "网络图片为空");
        }
    }

    private void getImageFromNet() {
        new Thread() {
            @Override
            public void run() {
                DefaultHttpClient httpclient = new DefaultHttpClient();
                HttpGet httpget = new HttpGet(songList.get(position).getPicture());
                try {
                    HttpResponse resp = httpclient.execute(httpget);
                    // 判断是否正确执行
                    if (HttpStatus.SC_OK == resp.getStatusLine().getStatusCode()) {
                        // 将返回内容转换为bitmap
                        HttpEntity entity = resp.getEntity();
                        InputStream in = entity.getContent();
                        albumBitmap = BitmapFactory.decodeStream(in);
                        Log.d("dave", "图片获取完成");
                        myHandler.sendEmptyMessage(0);
                    }else {
                        myHandler.sendEmptyMessage(1);
                    }

                } catch (Exception e) {
                } finally {
                    httpclient.getConnectionManager().shutdown();
                }
            }
        }.start();
    }

    private void playAction(){
        if (isPlaying) {
            mediaPlayer.pause();
            animator.pause();
            play.setImageResource(R.drawable.stop_play);
            RotateAnimation rotateAnimation = new RotateAnimation(0f, -30.0f,
                    Animation.RELATIVE_TO_SELF, 0.25f,
                    Animation.RELATIVE_TO_SELF, 0f);
            rotateAnimation.setDuration(1000);
            rotateAnimation.setFillAfter(true);
            play_needle.startAnimation(rotateAnimation);
            isPlaying = false;
            Log.d("dave", mediaPlayer.getCurrentPosition() + "");
        } else {
            mediaPlayer.start();
            animator.resume();
            play.setImageResource(R.drawable.start_play);
            RotateAnimation rotateAnimation = new RotateAnimation(-30.0f, 0f,
                    Animation.RELATIVE_TO_SELF, 0.25f,
                    Animation.RELATIVE_TO_SELF, 0f);
            rotateAnimation.setDuration(1000);
            rotateAnimation.setFillAfter(true);
            play_needle.startAnimation(rotateAnimation);
            isPlaying = true;
        }
    }
    private void nextSongAction(){
        mediaPlayer.stop();
        mediaPlayer=null;
        position=(position+1)%songList.size();
        actionBar.setTitle(songList.get(position).getName());
        getImageFromNet();
        initPicture();
        playSong(position);
    }
    private void lastSongAction(){
        mediaPlayer.stop();
        mediaPlayer=null;
        if(position<1){
            position=songList.size()-1;
        }else {
            position=position-1;
        }
        actionBar.setTitle(songList.get(position).getName());
        getImageFromNet();
        initPicture();
        playSong(position);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.play:
                playAction();
                break;
            case R.id.next_song:
                nextSongAction();
                break;
            case R.id.last_song:
                lastSongAction();
                break;
            case R.id.favorite:
                Toast.makeText(MusicPlayActivity.this, "暂不支持",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.download:
                Toast.makeText(MusicPlayActivity.this, "暂不支持",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.comment:
                Toast.makeText(MusicPlayActivity.this, "暂不支持",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.more_action:
                Toast.makeText(MusicPlayActivity.this, "暂不支持",
                        Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    initPicture();
                    break;
                case 1:
                    Log.d("dave", "访问失败");
                    break;
                default:
                    Log.e("dave", "消息出错...");
                    break;
            }
            super.handleMessage(msg);
        }
    }
}
