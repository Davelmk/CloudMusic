package com.dave.cloudmusic.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dave.cloudmusic.Bean.Song;
import com.dave.cloudmusic.PlayView.MusicPlayActivity;
import com.dave.cloudmusic.R;

import java.util.List;

public class MyAdapter  extends RecyclerView.Adapter <MyAdapter.ViewHolder>{
    public Context mContext;
    public List<Song> songsList;

    public MyAdapter(Context mContext, List<Song> songsList) {
        this.mContext = mContext;
        this.songsList = songsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song,
                parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.item_index.setText(position+1+"");
        holder.item_name.setText(songsList.get(position).getName().toString());
        holder.item_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,songsList.get(position).getName(),
                        Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(mContext, MusicPlayActivity.class);
                intent.putExtra("position",position);
                mContext.startActivity(intent);
            }
        });
        holder.item_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"暂不支持更多操作",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView item_index;
        public TextView item_name;
        public ImageView item_more;

        public ViewHolder(View view) {
            super(view);
            item_index=view.findViewById(R.id.item_index);
            item_name=view.findViewById(R.id.item_name);
            item_more=view.findViewById(R.id.item_more);
        }
    }
}
