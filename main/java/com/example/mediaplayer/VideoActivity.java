package com.example.mediaplayer;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.mediaplayer.bean.VideoBean;
import com.example.mediaplayer.widget.MyVideoView;

/**
 * Created by Administrator on 2018/3/30.
 */

public class VideoActivity extends AppCompatActivity {

    //播放控制
    private LinearLayout llController;
    //当前时间
    private TextView tvCurrent;
    //视频总时间
    private TextView tvTotal;
    //进度条
    private SeekBar mSeekBar;
    //播放控件
    private MyVideoView mVideoView;
    //是否已播放完毕
    private boolean isFinished = false;
    //当前播放进度
    private int mCurrentProgress = 0;
    //总播放进度
    private int mTotalProgress = 0;
    //临时播放进度
    private int mTmpProgress = 0;
    //是否触摸了SeekBar
    private boolean isTouchSeekBar = false;
    private VideoBean mVideoBean;

    //音量
    private LinearLayout llVolume;
    private ProgressBar pbVolume;

    //亮度
    private LinearLayout llBrightness;
    private ProgressBar pbBrightness;

    private static final int MSG_UPDATE = 0x04;
    private static final int MSG_HIDE_CONTROLLER = 0x05;
    private static final int MSG_HIDE_VOLUME = 0x06;
    private static final int MSG_HIDE_BRIGHTNESS = 0x07;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what){
                case MSG_UPDATE:
                    mCurrentProgress += 1000;
                    if(mCurrentProgress > mTotalProgress){
                        mCurrentProgress = mTotalProgress;
                    }else{
                        mHandler.sendEmptyMessageDelayed(MSG_UPDATE, 1000);
                    }
                    updateCurrentTime();
                    if(!isTouchSeekBar) {
                        mSeekBar.setProgress(mCurrentProgress);
                    }
                    break;

                //隐藏播放控制
                case MSG_HIDE_CONTROLLER:
                    llController.setVisibility(View.INVISIBLE);
                    break;

                //隐藏音量条
                case MSG_HIDE_VOLUME:
                    llVolume.setVisibility(View.INVISIBLE);
                    break;
                default:
                    break;
            }
            return true;
        }
    });

    private GestureDetector mGestureDetector;
    private AudioManager mAudioManager;
    private int mMaxVolume;
    private int mScreenWidth;
    private int mScreenHeight;





    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        mVideoView = findViewById(R.id.video_view);
        llController = findViewById(R.id.ll_video_controller);
        mSeekBar = findViewById(R.id.video_seekbar);
        tvCurrent = findViewById(R.id.tv_video_current);
        tvTotal = findViewById(R.id.tv_video_total);

        //音量
        llVolume = findViewById(R.id.ll_video_volume);
        pbVolume = findViewById(R.id.pb_video_volume);

        //亮度
        llBrightness = findViewById(R.id.ll_video_brightness);
        pbBrightness = findViewById(R.id.pb_video_brightness);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            mVideoBean = (VideoBean) bundle.getSerializable("video");
        }

        initTouch();
        initVideoView();


        if(mVideoBean != null){
            startPlay();
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(!isFinished){
            mVideoView.start();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(!isFinished){
            mVideoView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.stopPlayback();
    }

    private void startPlay(){
        mTotalProgress = (int) mVideoBean.duration;
        mCurrentProgress = 0;
        mSeekBar.setMax(mTotalProgress);
        mSeekBar.setProgress(mCurrentProgress);
        mHandler.sendEmptyMessageDelayed(MSG_UPDATE, 1000);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                System.out.println("--------------->fromUser=" + fromUser);
                if(fromUser) {
                    mTmpProgress = progress;
                    isTouchSeekBar = true;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mHandler.removeMessages(MSG_UPDATE);
                isTouchSeekBar = false;
                mCurrentProgress = mTmpProgress;
                mSeekBar.setProgress(mCurrentProgress);
                updateCurrentTime();
                mHandler.sendEmptyMessageDelayed(MSG_UPDATE, 1000);
                mVideoView.seekTo(mCurrentProgress);
            }
        });

        tvTotal.setText(mVideoBean.totalTime);
        mVideoView.setVideoPath(mVideoBean.path);
        mVideoView.start();
        isFinished = false;


    }

    //初始化VideoView控件
    private void initVideoView(){
        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                isFinished = true;
                return false;
            }
        });

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mVideoView.stopPlayback();
                isFinished = true;
            }
        });


    }

    //更新播放进度
    private void updateCurrentTime(){
        int second = mCurrentProgress / 1000 % 60;
        int minute = mCurrentProgress / 1000 / 60;

        String minuteStr = minute < 10 ? ("0" + minute) : minute+"";
        String secondStr = second < 10 ? ("0" + second) : second+"";
        tvCurrent.setText(minuteStr + ":" + secondStr);
    }

    private void initTouch(){
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        //设置当前音量
        pbVolume.setMax(mMaxVolume);
        pbVolume.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

        mScreenWidth = getWindowManager().getDefaultDisplay().getWidth();
        mScreenHeight = getWindowManager().getDefaultDisplay().getHeight();

//        System.out.println("---------------->mScreenWidth=" + mScreenWidth);
//        System.out.println("---------------->mScreenHeight=" + mScreenHeight);

        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

                float oldX = e1.getX();
                float oldY = e1.getY();
                int lastY = (int) e2.getRawY();

                if (oldX > mScreenWidth * 4.0 / 5) {
                    // 右边滑动
                    updateVolume((oldY - lastY) / mScreenHeight);
                } else if (oldX < mScreenWidth / 5.0) {
                    // 左边滑动
                    updateBrightness((oldY - lastY) / mScreenHeight);
                }

                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override
            public boolean onDown(MotionEvent e) {
                System.out.println("---------------->onDown");

                if ((e.getX() > mScreenWidth * 4.0 / 5) && mHandler.hasMessages(MSG_HIDE_VOLUME)) {
                    mHandler.removeMessages(MSG_HIDE_VOLUME);
                }
                return super.onDown(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if(llController.getVisibility() == View.VISIBLE){
                    llController.setVisibility(View.INVISIBLE);
                    mHandler.removeMessages(MSG_HIDE_CONTROLLER);
                }else{
                    llController.setVisibility(View.VISIBLE);
                    mHandler.sendEmptyMessageDelayed(MSG_HIDE_CONTROLLER, 5000);
                }
                return super.onSingleTapConfirmed(e);
            }


        });
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_UP:
                mHandler.sendEmptyMessageDelayed(MSG_HIDE_VOLUME, 2000);
                break;

        }
        return mGestureDetector.onTouchEvent(event);
    }

    //更新音量
    private void updateVolume(float percent){
        System.out.println("------------------>percent=" + percent);
        int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int tmp = (int) (mMaxVolume * percent);
        current += tmp;
        System.out.println("------------------>current=" + current);

        if(llVolume.getVisibility() != View.VISIBLE){
            llVolume.setVisibility(View.VISIBLE);
        }

        if(current > mMaxVolume){
            current = mMaxVolume;
        }

        if(current < 0){
            current = 0;
        }

        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current, 0);
        pbVolume.setProgress(current);
    }

    //更新亮度
    private void updateBrightness(float percent){
        float brightness = getWindow().getAttributes().screenBrightness;
        System.out.println("------------------>percent=" + percent);
        System.out.println("------------------>brightness=" + brightness);

    }
}
