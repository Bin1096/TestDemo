package com.example.mediaplayer.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mediaplayer.R;
import com.example.mediaplayer.bean.VideoBean;

import org.w3c.dom.Text;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2018/4/3.
 */

public class VideoAdapter extends BaseAdapter {

    private List<VideoBean> mList;
    private LayoutInflater mInflater;
    private Context mContext;

    public VideoAdapter(Context context, List<VideoBean> list){
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(context);

    }


    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public VideoBean getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_video, null);
            viewHolder.ivVideo = convertView.findViewById(R.id.iv_video);
            viewHolder.tvTitle = convertView.findViewById(R.id.tv_video_title);
            viewHolder.tvTime = convertView.findViewById(R.id.tv_video_time);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        VideoBean videoBean = getItem(position);
        Glide.with(mContext).load(Uri.fromFile(new File(videoBean.path))).into(viewHolder.ivVideo);
        viewHolder.tvTitle.setText(videoBean.title);
        viewHolder.tvTime.setText(videoBean.totalTime);

        return convertView;
    }

    private class ViewHolder{
        public ImageView ivVideo;
        public TextView tvTitle;
        public TextView tvTime;
    }


}
