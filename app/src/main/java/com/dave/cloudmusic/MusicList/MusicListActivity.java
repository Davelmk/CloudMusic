package com.dave.cloudmusic.MusicList;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dave.cloudmusic.Adapter.MyAdapter;
import com.dave.cloudmusic.Bean.Song;
import com.dave.cloudmusic.R;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MusicListActivity extends AppCompatActivity {
    private Toolbar toolbar;

    //底部播放栏
    private ImageView song_icon;
    private TextView song_name;
    private ImageView play_icon;
    private boolean isPlaying;

    //歌单List
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private List<Song> songList;

    MyHandler myHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);
        //初始化bmob
        Bmob.initialize(this,"60ab95fd51a8da2ba14d5d044f58e17f");

        toolbar = findViewById(R.id.toolbar_list);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("我的歌单");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.back);
        }

        //底部播放栏相关事件
        song_icon=findViewById(R.id.play_icon);
        song_name=findViewById(R.id.song_name);
        play_icon=findViewById(R.id.play_icon);
        play_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlaying){
                    play_icon.setImageResource(R.mipmap.play_now);
                }else {
                    play_icon.setImageResource(R.mipmap.play_stop);
                }
            }
        });

        //获取歌单数据
        myHandler=new MyHandler();
        initData();
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
                        songList.add(new Song(song.getName(),song.getUrl()));
                    }
                    myHandler.sendEmptyMessage(0);
                } else {
                    Log.e("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }
    private void initMusicList(){
        Log.d("dave",songList.size()+"");
        //加载歌单
        recyclerView=findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        myAdapter=new MyAdapter(this,songList);
        recyclerView.setAdapter(myAdapter);
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    initMusicList();
                    break;
                default:
                    Log.e("dave","消息出错...");
                    break;
            }
            super.handleMessage(msg);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.search:
                Toast.makeText(MusicListActivity.this, item.getTitle(),
                        Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return true;
    }
}
