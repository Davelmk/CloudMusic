package com.dave.cloudmusic.RecommendList;

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
import android.widget.Toast;

import com.dave.cloudmusic.Adapter.SearchAdapter;
import com.dave.cloudmusic.Bean.SearchSong;
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

public class RecommendActivity extends AppCompatActivity {
    //设置List加载长度，只显示50首，后续不处理了
    private final int ListSize=50;

    private Toolbar toolbar;
    private RecyclerView search_result;
    private List<SearchSong> resList;
    private SearchAdapter searchAdapter;

    private String jsonString;
    private URL url;
    MyHandler myHandler = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);
        toolbar = findViewById(R.id.toolbar_rec);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("推荐歌单");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.back);
        }
        resList = new ArrayList<>();
        myHandler = new MyHandler();
        search_result = findViewById(R.id.recyclerView_rec);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        search_result.setLayoutManager(layoutManager);
        searchAdapter = new SearchAdapter(this, resList);
        searchAdapter.setItemClickListener(new SearchAdapter.MyItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (view.getId()) {
                    case R.id.item_name:
                        Toast.makeText(RecommendActivity.this,
                                "和searchActivity类似，懒得写了",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.item_favorite:
                        Toast.makeText(RecommendActivity.this,
                                "和searchActivity类似，懒得写了",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        });
        search_result.setAdapter(searchAdapter);
        setUrl();
        getJsonData();
    }
    private void setUrl() {
        //默认设置返回5条搜索结果
        try {
            url=new URL("https://api.imjad.cn/cloudmusic/?type=playlist&id=2015408138");
        } catch (MalformedURLException e) {
            Log.d("dave", e.getMessage());
            Toast.makeText(this, "URL出了一点问题", Toast.LENGTH_SHORT).show();
        }
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
                    myHandler.sendEmptyMessage(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    private void parseJsonData_key(String string) {
        String id;
        String name;
        String pic;
        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONArray jsonArray = jsonObject.getJSONObject("playlist").getJSONArray("tracks");
            for (int i = 0; i < ListSize; i++) {
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
    private void refreshList() {
        searchAdapter.notifyDataSetChanged();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    parseJsonData_key(jsonString);
                    break;
                default:
                    Log.e("dave", "消息出错...");
                    break;
            }
            super.handleMessage(msg);
        }
    }
}
