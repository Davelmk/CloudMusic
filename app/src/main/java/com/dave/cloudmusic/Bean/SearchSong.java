package com.dave.cloudmusic.Bean;

public class SearchSong {
    private String id;
    private String name;
    private String pic;
    private String url;
    private boolean favorite;

    public SearchSong(String id, String name, String pic, boolean favorite) {
        this.id = id;
        this.name = name;
        this.pic = pic;
        this.favorite=favorite;
    }

    public SearchSong(String id, String name, String pic, String url, boolean favorite) {
        this.id = id;
        this.name = name;
        this.pic = pic;
        this.url = url;
        this.favorite=favorite;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
