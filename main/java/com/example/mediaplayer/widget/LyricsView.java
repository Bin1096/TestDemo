package com.example.mediaplayer.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.mediaplayer.bean.LyricsBean;
import com.example.mediaplayer.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/3/29.
 */

public class LyricsView extends TextView {

    private List<LyricsBean> mList;
    private Pattern mPattern;
    //画笔
    private Paint mPaint;
    private int mPadding = 12;
    private int mGrap = 24;
    private Rect rect;
    private int mCurrentLine = 0;
    private static final int MSG_NEXT = 0x01;

    private int mCurrentDuration = 0;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_NEXT:
                    mCurrentLine++;
                    if(mCurrentLine < mList.size()){

                        invalidate();
                    }else {
                        mHandler.removeMessages(MSG_NEXT);
                        mCurrentLine = 0;
                    }
                    break;
            }
            return true;
        }
    });


    public LyricsView(Context context) {
        super(context);
        init();
    }

    public LyricsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LyricsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mPattern = Pattern.compile("\\[\\d+:\\d{2}\\.\\d{2}");
        rect = new Rect();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(sp2px(18));
    }

    public void setLrcPath(String path){
        if(mList == null) {
            mList = new ArrayList<>();
        }else{
            mList.clear();
        }

        try {
            InputStream is = getContext().getAssets().open("test.lrc");
            if(is == null)
                return;

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));

            String tmpContent = null;
            while ((tmpContent = bufferedReader.readLine()) != null){
                dealLineContent(tmpContent);
            }


            Collections.sort(mList, new Comparator<LyricsBean>() {
                @Override
                public int compare(LyricsBean o1, LyricsBean o2) {
                    return o1.timePoint - o2.timePoint;
                }
            });

            //计算播放时间
            LyricsBean current = null;
            LyricsBean next = null;
            for(int i = 0;  i < mList.size() - 1; i++){
                current = mList.get(i);
                next = mList.get(i + 1);
                current.sleepTime = next.timePoint - current.timePoint;
            }

            //获取字体的高度
            mPaint.getTextBounds(mList.get(0).content, 0, mList.get(0).content.length(), rect);
            setHeight(mList.size() * rect.height());
            getLayoutParams().height = mList.size() * rect.height();



            System.out.println("--------------->mList="+mList);
            System.out.println("--------------->mList="+mList.get(mList.size() - 1));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dealLineContent(String line){
        int pos1 = line.indexOf("[");
        int pos2 = line.lastIndexOf("]");
        if(pos2 > pos1){

            String[] tmp = line.split("]");
//            System.out.println("---------->tmp.length=" + tmp.length + "  pattern = " + mPattern.matcher(tmp[0]).matches());
            //tmp数组的最后一项
            String finalTmp = tmp[tmp.length - 1];
            for(int i = 0; i < tmp.length; i++){
                if(mPattern.matcher(tmp[i]).matches()){
                    LyricsBean lyricsBean = new LyricsBean();

                    //计算歌词时间点
                    lyricsBean.timePoint = getTimePoint(tmp[i].substring(1));

                    //保存歌词
                    if(!mPattern.matcher(finalTmp).matches()){
                        lyricsBean.content = finalTmp;
                    }else{
                        //该时间点没有歌词
                        lyricsBean.content = "";
                        return;
                    }

                    mList.add(lyricsBean);
                }
            }
        }
    }

    private int getTimePoint(String time){
        String minuteStr = time.substring(0, time.indexOf(":"));
        String secondStr = time.substring(time.indexOf(":")+1, time.indexOf("."));
        String millisecondStr = time.substring(time.indexOf(".")+1);

//        System.out.println("------------->minuteStr="+minuteStr);
//        System.out.println("------------->secondStr="+secondStr);
//        System.out.println("------------->millisecondStr="+millisecondStr);
        int minute = 0;
        int second = 0;
        int millisecond = 0;

        //计算分钟，转换为毫秒
        if(minuteStr.startsWith("0") && minuteStr.length() > 1){
            minuteStr = minuteStr.substring(1);
        }
        minute = Integer.parseInt(minuteStr) * 60 * 1000;

        //计算秒，转换为毫秒
        if(secondStr.startsWith("0") && secondStr.length() > 1){
            secondStr = secondStr.substring(1);
        }
        second = Integer.parseInt(secondStr) * 1000;

        //计算毫秒
        if(millisecondStr.startsWith("0") && millisecondStr.length() > 1){
            millisecondStr = millisecondStr.substring(1);
        }
        millisecond = Integer.parseInt(millisecondStr) * 10;

        return minute + second + millisecond;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(mList != null && mList.size() > 0){
            if(mCurrentDuration != 0){
                mHandler.sendEmptyMessageDelayed(MSG_NEXT, mList.get(mCurrentLine + 1).timePoint - mCurrentDuration);
                mCurrentDuration = 0;
            }else {
                mHandler.sendEmptyMessageDelayed(MSG_NEXT, mList.get(mCurrentLine).sleepTime);
            }
            mPaint.setColor(getResources().getColor(R.color.gray));

            //绘制播放过的歌词
            for(int i = mCurrentLine - 1; i >= 0; i--){

                float y = (getHeight() / 2 - (mGrap + rect.height()) * (mCurrentLine - i));
                if(y < 0){
                    break;
                }
                canvas.drawText(mList.get(i).content, getWidth() / 2, y, mPaint);

            }

            //绘制当前歌词
            mPaint.setColor(getResources().getColor(R.color.highlight));
            canvas.drawText(mList.get(mCurrentLine).content, getWidth() / 2, getHeight() / 2 , mPaint);

            //绘制未播放的歌词
            mPaint.setColor(getResources().getColor(R.color.gray));
            for(int i = mCurrentLine + 1; i < mList.size(); i++){
                float y = (getHeight() / 2 + (mGrap + rect.height()) * (i - mCurrentLine));
                if(y > getHeight()){
                    break;
                }
                canvas.drawText(mList.get(i).content, getWidth() / 2, y, mPaint);
            }

        }else{
            canvas.drawText("暂无歌词", getWidth() / 2, (getHeight() / 2), mPaint);
        }
    }

    //当通过SeekBar改变播放进度时，调用该方法
    public void setCurrentDuration(int duration){
        if(mList == null || mList.size() < 1)
            return;
        mHandler.removeMessages(MSG_NEXT);
        LyricsBean lyricsBean = null;

        for(int i = 0; i < mList.size(); i++){
            lyricsBean = mList.get(i);
            //找到目标歌词
            if(lyricsBean.timePoint >= duration){
                mCurrentLine = i;
                mCurrentDuration = duration;
                break;
            }
        }

        //重绘
        invalidate();
    }

    private int sp2px(int value){
        float fontScale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (value * fontScale + 0.5f);
    }
}
