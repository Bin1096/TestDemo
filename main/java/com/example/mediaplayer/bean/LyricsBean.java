package com.example.mediaplayer.bean;

/**
 * Created by Administrator on 2018/3/29.
 */

public class LyricsBean {

    public String content;
    public int sleepTime;
    public int timePoint;

    @Override
    public String toString() {
        return "LyricsBean{" +
                "content='" + content + '\'' +
                ", sleepTime=" + sleepTime +
                ", timePoint=" + timePoint +
                '}';
    }
}
