package com.example.mediaplayer;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.mediaplayer.widget.LyricsView;

public class MainActivity extends AppCompatActivity {

    private LyricsView mLyricsView;
    private Button btnStart;
    private SeekBar mSeekBar;
    private TextView tvCurrent;
    private TextView tvTotal;
    private int mCurrentProgress = 0;
    private int mTotalProgress = 0;
    private int mTmpProgress = 0;
    private boolean isTouch = false;
    private static final int MSG_UPDATE = 0x04;

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
                    if(!isTouch) {
                        mSeekBar.setProgress(mCurrentProgress);
                    }
                    break;

                default:
                    break;
            }
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = findViewById(R.id.btn_main_start);
        mLyricsView = findViewById(R.id.id_lyrics);
        mSeekBar = findViewById(R.id.main_seekbar);
        tvCurrent = findViewById(R.id.tv_main_current);
        tvTotal = findViewById(R.id.tv_main_total);

        mLyricsView.setLrcPath("");

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.removeMessages(MSG_UPDATE);
                mTotalProgress = 233770;
                mCurrentProgress = 0;
                mSeekBar.setMax(mTotalProgress);
                mSeekBar.setProgress(mCurrentProgress);
                mLyricsView.setCurrentDuration(0);
                mHandler.sendEmptyMessageDelayed(MSG_UPDATE, 1000);
            }
        });

        initSeekBar();
    }

    private void initSeekBar(){
        mTotalProgress = 233770;
        mCurrentProgress = 0;
        mSeekBar.setMax(mTotalProgress);
        mSeekBar.setProgress(mCurrentProgress);
        mHandler.sendEmptyMessageDelayed(MSG_UPDATE, 1000);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                System.out.println("--------------->fromUser=" + fromUser);
                if(fromUser) {
                    mTmpProgress = progress;
                    isTouch = true;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mHandler.removeMessages(MSG_UPDATE);
                isTouch = false;
                mCurrentProgress = mTmpProgress;
                mSeekBar.setProgress(mCurrentProgress);
                updateCurrentTime();
                mHandler.sendEmptyMessageDelayed(MSG_UPDATE, 1000);
                mLyricsView.setCurrentDuration(mCurrentProgress);
            }
        });
    }

    private void updateCurrentTime(){
        int second = mCurrentProgress / 1000 % 60;
        int minute = mCurrentProgress / 1000 / 60;

        String minuteStr = minute < 10 ? ("0" + minute) : minute+"";
        String secondStr = second < 10 ? ("0" + second) : second+"";
        tvCurrent.setText(minuteStr + ":" + secondStr);
    }
}
