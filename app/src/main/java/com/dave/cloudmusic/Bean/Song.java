package com.dave.cloudmusic.Bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class Song extends BmobObject{
    private String name;
    private String url;
    private String picture;
    private BmobFile songFile;
    private transient String id;

    public Song() {}

    public Song(String id, String name, String url, String picture) {
        this.id = id;
        this.name = name;
        this.url=url;
        this.picture=picture;
    }
    public Song(String id,String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public BmobFile getSongFile() {
        return songFile;
    }

    public void setSongFile(BmobFile songFile) {
        this.songFile = songFile;
    }
}
