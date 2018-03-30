package com.example.mediaplayer;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2018/3/30.
 */

public class VideoActivity extends AppCompatActivity {

    private GestureDetector mGestureDetector;
    private AudioManager mAudioManager;
    private int mMaxVolume;
    private int mScreenWidth;
    private int mScreenHeight;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = getResources().getDisplayMetrics().heightPixels;
        System.out.println("---------------->mScreenWidth=" + mScreenWidth);
        System.out.println("---------------->mScreenHeight=" + mScreenHeight);

        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//                System.out.println("---------------->distanceX=" + distanceX);

                System.out.println("---------------->distanceY=" + (distanceY * mMaxVolume / mScreenHeight));

                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override
            public boolean onDown(MotionEvent e) {
                System.out.println("---------------->onDown");
                return super.onDown(e);
            }
        });

    }


    private int firstX = 0;
    private int firstY = 0;
    private int lastX = 0;
    private int lastY = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()){
//            case MotionEvent.ACTION_DOWN:
//                firstX = (int) event.getX();
//                firstY = (int) event.getY();
//                break;
//
//            case MotionEvent.ACTION_MOVE:
//                lastX = (int) event.getX();
//                lastY = (int) event.getY();
//
//                System.out.println("---------------->distanceY=" + (lastY - firstY));
//                break;
//
//            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_CANCEL:
//                break;
//
//        }
        return mGestureDetector.onTouchEvent(event);
    }
}
