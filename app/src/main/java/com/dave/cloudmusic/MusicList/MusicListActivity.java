package com.dave.cloudmusic.MusicList;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

        //加载歌单
        initMusicList();
        recyclerView=findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        myAdapter=new MyAdapter(this,songList);
        recyclerView.setAdapter(myAdapter);
    }

    //歌单数据初始化
    private void initMusicList(){
        songList=new ArrayList<>();
        songList.add(new Song("韩安旭 - 多幸运",""));
        songList.add(new Song("G.E.M.邓紫棋 - 光年之外",""));
        songList.add(new Song("曲肖冰 - 离人愁",""));
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
