package com.dave.cloudmusic.MainView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dave.cloudmusic.MusicList.MusicListActivity;
import com.dave.cloudmusic.R;
import com.dave.cloudmusic.SearchView.SearchActivity;
import com.wpy.circleviewpager.widget.CycleView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //抽屉布局
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;

    //轮播图
    private CycleView cycleView;
    private int[] imgs;

    //私人FM，每日推荐，歌单，排行榜
    private ImageView myFM;
    private ImageView suggestion;
    private ImageView music_list;
    private ImageView ranking;

    //歌单
    private LinearLayout myList;
    private LinearLayout recommendList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //抽屉布局响应事件
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.drawer);
        }
        navigationView = findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.
                OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.mine:
                        Toast.makeText(MainActivity.this, item.getTitle(),
                                Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.deploy:
                        Toast.makeText(MainActivity.this, item.getTitle(),
                                Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.setting:
                        Toast.makeText(MainActivity.this, item.getTitle(),
                                Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.quit:
                        Toast.makeText(MainActivity.this, item.getTitle(),
                                Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(MainActivity.this, "未知menu",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
                drawerLayout.closeDrawer(navigationView);
                return true;
            }
        });

        //添加轮播图
        cycleView = findViewById(R.id.banner);
        imgs = new int[]{R.drawable.banner1, R.drawable.banner2,
                R.drawable.banner3, R.drawable.banner4, R.drawable.banner5};
        cycleView.setItems(imgs, getSupportFragmentManager(), new CycleView.OnItemClickListener() {
            @Override
            public void onItemClick(int i) {
                Toast.makeText(MainActivity.this, "未设置响应事件",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoadImage(ImageView imageView, String s) {

            }
        });

        //私人FM，每日推荐，歌单，排行榜
        myFM = findViewById(R.id.myFM);
        myFM.setOnClickListener(this);
        suggestion = findViewById(R.id.suggestion);
        suggestion.setOnClickListener(this);
        music_list = findViewById(R.id.music_list);
        music_list.setOnClickListener(this);
        ranking = findViewById(R.id.ranking);
        ranking.setOnClickListener(this);

        //歌单,点击事件
        myList = findViewById(R.id.my_music_list);
        recommendList = findViewById(R.id.recommend_list);
        myList.setOnClickListener(this);
        recommendList.setOnClickListener(this);

        //设置默认MusicList加载方式
        SharedPreferences sharedPreferences=getSharedPreferences("data",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("needGetDataFromCloud",true);
        editor.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.search:
                Intent intent=new Intent(MainActivity.this, SearchActivity.class);
                startActivityForResult(intent,1);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.myFM:
                Toast.makeText(MainActivity.this, "私人FM",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.suggestion:
                Toast.makeText(MainActivity.this, "每日推荐",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.music_list:
                Toast.makeText(MainActivity.this, "歌单",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.ranking:Toast.makeText(MainActivity.this, "排行榜",
                    Toast.LENGTH_SHORT).show();
                break;
            case R.id.recommend_list:
                Toast.makeText(MainActivity.this, "暂无推荐",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.my_music_list:
                Intent intent = new Intent(MainActivity.this, MusicListActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
