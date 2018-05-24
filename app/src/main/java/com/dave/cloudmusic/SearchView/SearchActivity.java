package com.dave.cloudmusic.SearchView;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.dave.cloudmusic.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SearchActivity extends AppCompatActivity {

    private EditText editText;
    private ImageButton imageButton;

    private URL url;
    private String jsonString;

    private RecyclerView search_result;

    MyHandler myHandler = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.back);
        }
        myHandler=new MyHandler();
        editText=findViewById(R.id.search_key);
        imageButton=findViewById(R.id.search_btn);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    url = new URL("https://api.imjad.cn/cloudmusic/?type=detail&id=549309116");
                }catch (Exception e){
                    e.printStackTrace();
                }
                Log.d("dave","开始获取数据");
                getJsonData();
            }
        });
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

    private void parseJsonData(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            Log.d("dave",jsonObject.getJSONArray("songs")
                    .getJSONObject(0).getJSONObject("al").get("picUrl").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getJsonData() {
        new Thread() {
            @Override
            public void run() {
                try {
                    StringBuffer stringBuffer=new StringBuffer();
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
                    jsonString=stringBuffer.toString();
                    myHandler.sendEmptyMessage(0);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("dave","获取数据失败");
                }
            }
        }.start();
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    parseJsonData(jsonString);
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
