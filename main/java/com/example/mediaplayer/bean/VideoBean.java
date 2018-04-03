package com.example.mediaplayer.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/4/3.
 */

public class VideoBean implements Serializable{

    public int id;
    public String title;
    public String album;
    public String artist;
    public String displayName;
    public String mimeType;
    public String path;
    public long duration;
    public long size;
    public String thumbnails;
    public String totalTime;

    @Override
    public String toString() {
        return "VideoBean{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", album='" + album + '\'' +
                ", artist='" + artist + '\'' +
                ", displayName='" + displayName + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", path='" + path + '\'' +
                ", duration='" + duration + '\'' +
                ", size='" + size + '\'' +
                ", thumbnails='" + thumbnails + '\'' +
                ", totalTime='" + totalTime + '\'' +
                '}';
    }
}
