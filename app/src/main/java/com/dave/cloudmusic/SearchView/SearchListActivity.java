package com.dave.cloudmusic.SearchView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.dave.cloudmusic.Adapter.MyAdapter;
import com.dave.cloudmusic.Bean.Song;
import com.dave.cloudmusic.R;
import com.dave.cloudmusic.Utils.DataBaseHelper;

import java.util.ArrayList;
import java.util.List;

public class SearchListActivity extends AppCompatActivity {
    private EditText editText;
    private ImageButton imageButton;
    private RecyclerView search_result;

    //SQLite
    private SQLiteDatabase db;
    private MyAdapter myAdapter;
    private List<Song> songList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.back);
        }
        editText=findViewById(R.id.search_key);
        imageButton=findViewById(R.id.search_btn);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText().toString().equals("")){
                    Toast.makeText(SearchListActivity.this, "关键字为空",
                            Toast.LENGTH_SHORT).show();
                    songList.clear();
                    myAdapter.notifyDataSetChanged();
                }else {
                    getSearchResultFromSQL(editText.getText().toString());
                    if (songList.size()==0){
                        Toast.makeText(SearchListActivity.this, "未搜索到相关歌曲",
                                Toast.LENGTH_SHORT).show();
                    }
                    myAdapter.notifyDataSetChanged();
                }
            }
        });
        //初始化列表
        search_result=findViewById(R.id.search_result);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        search_result.setLayoutManager(layoutManager);
        myAdapter=new MyAdapter(this,songList);
        search_result.setAdapter(myAdapter);
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

    private void getSearchResultFromSQL(String key){
        songList.clear();
        //从数据库加载数据
        DataBaseHelper dbHelper = new DataBaseHelper(this, "Music.db", null, 1);
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT  * FROM songList " +" where name"+" like '%"+key+"%'",
                null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String id = cursor.getString(cursor.getColumnIndex("id"));
                songList.add(new Song(id,name));
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
}
