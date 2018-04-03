package com.example.mediaplayer;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.mediaplayer.adapter.VideoAdapter;
import com.example.mediaplayer.bean.VideoBean;
import com.example.mediaplayer.util.IntentUtils;
import com.example.mediaplayer.widget.LyricsView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

//    private LyricsView mLyricsView;
    private ListView mListView;
    private Button btnStart;
    private SeekBar mSeekBar;
    private TextView tvCurrent;
    private TextView tvTotal;
    private Context mContext;
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
        mContext = this;

        btnStart = findViewById(R.id.btn_main_start);
        mListView = findViewById(R.id.id_lyrics);
        mSeekBar = findViewById(R.id.main_seekbar);
        tvCurrent = findViewById(R.id.tv_main_current);
        tvTotal = findViewById(R.id.tv_main_total);

//        mLyricsView.setLrcPath("");

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mHandler.removeMessages(MSG_UPDATE);
//                mTotalProgress = 233770;
//                mCurrentProgress = 0;
//                mSeekBar.setMax(mTotalProgress);
//                mSeekBar.setProgress(mCurrentProgress);
//                mLyricsView.setCurrentDuration(0);
//                mHandler.sendEmptyMessageDelayed(MSG_UPDATE, 1000);

                getVideoList();
            }
        });

//        initSeekBar();
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
//                System.out.println("--------------->fromUser=" + fromUser);
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
//                mLyricsView.setCurrentDuration(mCurrentProgress);
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

    private List<VideoBean> mVideoList;
    private VideoAdapter mAdapter;

    private void getVideoList(){

        Cursor cursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        if(cursor != null){
            mVideoList = new ArrayList<>();
            VideoBean videoBean = null;

            while (cursor.moveToNext()){
                videoBean = new VideoBean();
                videoBean.id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                videoBean.title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));  //视频文件的标题内容
                videoBean.album = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.ALBUM));
                videoBean.artist = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.ARTIST));
                videoBean.displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                videoBean.mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE));
                videoBean.path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));  //
                videoBean.duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                videoBean.size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                videoBean.thumbnails = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                videoBean.totalTime = duration2Time(videoBean.duration);


                mVideoList.add(videoBean);
            }

            cursor.close();
            videoBean = null;
            System.out.println("--------------->mVideoList=" + mVideoList);

            mAdapter = new VideoAdapter(MainActivity.this, mVideoList);
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("video", mVideoList.get(position));
                    IntentUtils.startActivity(mContext, VideoActivity.class, bundle);
                }
            });
        }
    }

    //时长转换成时间
    private String duration2Time(long duration){
        duration /= 1000;
        int second = (int) (duration % 60);
        int minute = (int) (duration / 60) % 60;
        int hours = (int) (duration / 60 / 60);

        String time = "";
        //时
        if(hours < 10){
            time = "0" + hours;
        }else{
            time += hours;
        }
        time += ":";

        //分
        if(minute < 10){
            time += "0" + minute;
        }else{
            time += minute;
        }
        time += ":";

        //秒
        if(second < 10){
            time += "0" + second;
        }else{
            time += second;
        }

        return time;
    }

}
