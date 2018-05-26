package com.dave.cloudmusic.SearchView;

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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.dave.cloudmusic.Adapter.SearchAdapter;
import com.dave.cloudmusic.Bean.SearchSong;
import com.dave.cloudmusic.Bean.Song;
import com.dave.cloudmusic.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class SearchActivity extends AppCompatActivity {

    private EditText editText;
    private ImageButton imageButton;

    private URL url;
    private String jsonString;
    /**
     * 设置URL格式
     * true：关键字搜索
     * false：获取Music ID
     */
    private boolean searchType;
    private int pos = -1;

    MyHandler myHandler = null;

    private RecyclerView search_result;
    private List<SearchSong> resList;
    private SearchAdapter searchAdapter;

    private int ResultCode=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //初始化bmob
        Bmob.initialize(this, "60ab95fd51a8da2ba14d5d044f58e17f");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.back);
        }
        resList = new ArrayList<>();
        myHandler = new MyHandler();
        editText = findViewById(R.id.search_key);
        imageButton = findViewById(R.id.search_btn);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchType = true;
                url = setUrl(editText.getText().toString(), searchType);
                Log.d("dave", "开始获取数据");
                getJsonData();
            }
        });

        search_result = findViewById(R.id.search_result);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        search_result.setLayoutManager(layoutManager);
        searchAdapter = new SearchAdapter(this, resList);
        searchAdapter.setItemClickListener(new SearchAdapter.MyItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (view.getId()) {
                    case R.id.item_name:
                        Toast.makeText(SearchActivity.this, "收藏后可播放",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.item_favorite:
                        if (resList.get(position).isFavorite()) {
                            resList.get(position).setFavorite(false);
                        } else {
                            resList.get(position).setFavorite(true);
                            pos = position;
                            getMusicURL(resList.get(position).getId());
                        }
                        refreshList();
                        break;
                    default:
                        break;
                }
            }
        });
        search_result.setAdapter(searchAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //将收藏的歌曲数据同步云端
                syncListToCloud();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    private URL setUrl(String key, boolean type) {
        //默认设置返回5条搜索结果
        URL res_url = null;
        try {
            if (type) {
                res_url = new URL("https://api.imjad.cn/cloudmusic/?type=search&s="
                        + key + "&search_type=1&limit=5");
            } else {
                res_url = new URL("https://api.imjad.cn/cloudmusic/?type=song&br=128000&id="+key);
            }
        } catch (MalformedURLException e) {
            Log.d("dave", e.getMessage());
            Toast.makeText(this, "URL出了一点问题", Toast.LENGTH_SHORT).show();
        }
        return res_url;
    }

    private void parseJsonData_key(String string) {
        String id;
        String name;
        String pic;
        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONArray jsonArray = jsonObject.getJSONObject("result").getJSONArray("songs");
            for (int i = 0; i < jsonArray.length(); i++) {
                id = jsonArray.getJSONObject(i).get("id").toString();
                name = jsonArray.getJSONObject(i).get("name").toString()
                        + " - " + jsonArray.getJSONObject(i).getJSONArray("ar")
                        .getJSONObject(0).get("name").toString();
                pic = jsonArray.getJSONObject(i).getJSONObject("al").get("picUrl").toString();
                resList.add(new SearchSong(id, name, pic, false));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //解析完成后刷新
        refreshList();
    }

    private void parseJsonData_id(String string) {
        String music_url = null;
        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            music_url = jsonArray.getJSONObject(0).get("url").toString();
            Log.d("dave", music_url);
            resList.get(pos).setUrl(music_url);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getMusicURL(String id) {
        searchType = false;
        url=setUrl(id, searchType);
        getJsonData();
    }

    private void getJsonData() {
        new Thread() {
            @Override
            public void run() {
                try {
                    StringBuffer stringBuffer = new StringBuffer();
                    BufferedReader bufferedReader;
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    InputStream inputStream = connection.getInputStream();
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTf-8"));
                    String sread = null;
                    while ((sread = bufferedReader.readLine()) != null) {
                        stringBuffer.append(sread);
                        stringBuffer.append("\r\n");
                    }
                    jsonString = stringBuffer.toString();
                    if (searchType) {
                        myHandler.sendEmptyMessage(0);
                    } else {
                        myHandler.sendEmptyMessage(1);
                    }
                } catch (IOException e) {
                    myHandler.sendEmptyMessage(2);
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void refreshList() {
        searchAdapter.notifyDataSetChanged();
    }

    private void syncListToCloud() {
        Song song = new Song();
        for (SearchSong searchSong:resList){
            if (searchSong.isFavorite()){
                ResultCode=1;
                song.setName(searchSong.getName());
                song.setUrl(searchSong.getUrl());
                song.setPicture(searchSong.getPic());
                song.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if(e==null){
                            Log.d("bmob","创建数据成功：" + s);
                        }else{
                            Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                        }
                    }
                });
            }
        }
        myHandler.sendEmptyMessage(3);
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    parseJsonData_key(jsonString);
                    break;
                case 1:
                    parseJsonData_id(jsonString);
                    break;
                case 2:
                    Log.d("dave", "访问失败");
                    break;
                case 3:
                    //退出当前Activity
                    setResult(ResultCode);
                    finish();
                    break;
                default:
                    Log.e("dave", "消息出错...");
                    break;
            }
            super.handleMessage(msg);
        }
    }
}
