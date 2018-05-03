package com.dave.cloudmusic.Bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class Song extends BmobObject{
    private String name;
    private String url;
    private BmobFile songFile;
    private transient String id;

    public Song(String name, String url, String id) {
        this.name = name;
        this.url = url;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Song(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
