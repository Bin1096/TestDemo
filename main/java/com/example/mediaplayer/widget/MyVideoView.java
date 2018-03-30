package com.example.mediaplayer.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.VideoView;

/**
 * Created by Administrator on 2018/3/30.
 */

public class MyVideoView extends VideoView {

    private GestureDetector mGestureDetector;

    public MyVideoView(Context context) {
        super(context);
        init();
    }

    public MyVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                System.out.println("---------------->distanceX=" + distanceX);
                System.out.println("---------------->distanceY=" + distanceY);
                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override
            public boolean onDown(MotionEvent e) {
                System.out.println("---------------->onDown");
                return super.onDown(e);
            }
        });
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        return mGestureDetector.onTouchEvent(ev);
//    }
}
