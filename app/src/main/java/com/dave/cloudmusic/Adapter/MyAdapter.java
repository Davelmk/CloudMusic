package com.dave.cloudmusic.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dave.cloudmusic.Bean.Song;
import com.dave.cloudmusic.R;

import java.util.List;

public class MyAdapter  extends RecyclerView.Adapter <MyAdapter.ViewHolder>{
    private MyItemClickListener mItemClickListener;
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
    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView item_index;
        TextView item_name;
        ImageView item_more;
        MyItemClickListener mListener;

        public ViewHolder(View itemView) {
            super(itemView);
            item_index=itemView.findViewById(R.id.item_index);
            item_name=itemView.findViewById(R.id.item_name);
            item_more=itemView.findViewById(R.id.item_more);
            this.mListener=mItemClickListener;
            item_name.setOnClickListener(this);
            item_more.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClick(v, getPosition());
            }
        }
    }
    public interface MyItemClickListener {
        void onItemClick(View view, int position);
    }
    public void setItemClickListener(MyItemClickListener myItemClickListener) {
        this.mItemClickListener = myItemClickListener;
    }
}
