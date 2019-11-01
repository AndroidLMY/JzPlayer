package com.androidlmy.jzplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import cn.jzvd.JzvdStd;

/**
 * @功能:
 * @Creat 2019/11/1 15:11
 * @User Lmy
 * @Compony zaituvideo
 */
public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.ViewHolder> {
    private Context context;

    public PlayerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(LayoutInflater.from(
                context).inflate(R.layout.item_recyclerview, parent,
                false));
        return holder;
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.videoplayer.setUp("http://jzvd.nathen.cn/6ea7357bc3fa4658b29b7933ba575008/fbbba953374248eb913cb1408dc61d85-5287d2089db37e62345123a1be272f8b.mp4",
                "", JzvdStd.SCREEN_NORMAL);
        holder.videoplayer.setVideoImageDisplayType(1);
        holder.videoplayer.thumbImageView.setScaleType(ImageView.ScaleType.FIT_XY);
//        holder.videoplayer.startVideo();//自动播放 在recyclerview有bug
        Glide.with(holder.videoplayer.getContext()).load("http://jzvd-pic.nathen.cn/jzvd-pic/00b026e7-b830-4994-bc87-38f4033806a6.jpg").
                into(holder.videoplayer.thumbImageView);
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private MyJzvdStd videoplayer;

        public ViewHolder(View itemView) {
            super(itemView);
            videoplayer = itemView.findViewById(R.id.videoplayer);
        }
    }
}
