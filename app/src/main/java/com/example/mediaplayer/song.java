package com.example.mediaplayer;

public class song {
    public String path;
    public String song_name;
    public String album_name;
    public String artist_name;


    song(String path,String song_name,String album_name,String artist_name) {
        this.path = path;
        this.album_name=album_name;
        this.artist_name=artist_name;
        this.song_name=song_name;
    }
}
