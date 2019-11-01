package com.androidlmy.jzplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import cn.jzvd.Jzvd;

import static cn.jzvd.Jzvd.STATE_PAUSE;
import static cn.jzvd.Jzvd.STATE_PLAYING;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PlayerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycler_view);
        adapter = new PlayerAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                Jzvd jzvd = view.findViewById(R.id.videoplayer);
                if (jzvd != null && Jzvd.CURRENT_JZVD != null &&
                        jzvd.jzDataSource.containsTheUrl(Jzvd.CURRENT_JZVD.jzDataSource.getCurrentUrl())) {
                    if (Jzvd.CURRENT_JZVD != null && Jzvd.CURRENT_JZVD.screen != Jzvd.SCREEN_FULLSCREEN) {
                        Jzvd.releaseAllVideos();
                    }
                }
            }
        });
        recyclerView.addOnScrollListener(new AutoPlayScrollListener(this));
    }

    /**
     * 监听recycleView滑动状态，
     * 自动播放可见区域内的第一个视频
     */
    private static class AutoPlayScrollListener extends RecyclerView.OnScrollListener {
        private int firstVisibleItem = 0;
        private int lastVisibleItem = 0;
        private int visibleCount = 0;
        private Context context;

        public AutoPlayScrollListener(Context context) {
            this.context = context;
        }

        /**
         * 被处理的视频状态标签
         */
        private enum VideoTagEnum {

            /**
             * 自动播放视频
             */
            TAG_AUTO_PLAY_VIDEO,

            /**
             * 暂停视频
             */
            TAG_PAUSE_VIDEO
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            switch (newState) {
                case RecyclerView.SCROLL_STATE_IDLE://停止滚动
                    autoPlayVideo(recyclerView, VideoTagEnum.TAG_AUTO_PLAY_VIDEO);
                default:
                    autoPlayVideo(recyclerView, VideoTagEnum.TAG_PAUSE_VIDEO);
                    break;
            }

        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                firstVisibleItem = linearManager.findFirstVisibleItemPosition();
                lastVisibleItem = linearManager.findLastVisibleItemPosition();
                visibleCount = lastVisibleItem - firstVisibleItem;
            }

        }

        /**
         * 循环遍历 可见区域的播放器
         * 然后通过 getLocalVisibleRect(rect)方法计算出哪个播放器完全显示出来
         * <p>
         * getLocalVisibleRect相关链接：http://www.cnblogs.com/ai-developers/p/4413585.html
         *
         * @param recyclerView
         * @param handleVideoTag 视频需要进行状态
         */
        private void autoPlayVideo(RecyclerView recyclerView, VideoTagEnum handleVideoTag) {

            if (isWifi(context)) {
                for (int i = 0; i < visibleCount; i++) {
                    if (recyclerView != null && recyclerView.getChildAt(i) != null && recyclerView.getChildAt(i).findViewById(R.id.videoplayer) != null) {
                        MyJzvdStd homeGSYVideoPlayer = (MyJzvdStd) recyclerView.getChildAt(i).findViewById(R.id.videoplayer);
                        Rect rect = new Rect();
                        homeGSYVideoPlayer.getLocalVisibleRect(rect);
                        int videoheight = homeGSYVideoPlayer.getHeight();
                        if (rect.top == 0 && rect.bottom == videoheight) {
                            handleVideo(handleVideoTag, homeGSYVideoPlayer);
                            // 跳出循环，只处理可见区域内的第一个播放器
                            break;
                        }
                    }
                }
            } else {

            }
        }

        private static boolean isWifi(Context mContext) {
            ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetInfo != null
                    && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
            return false;
        }

        /**
         * 视频状态处理
         *
         * @param handleVideoTag     视频需要进行状态
         * @param homeGSYVideoPlayer JZVideoPlayer播放器
         */
        private void handleVideo(VideoTagEnum handleVideoTag, MyJzvdStd homeGSYVideoPlayer) {
            switch (handleVideoTag) {
                case TAG_AUTO_PLAY_VIDEO:
                    if ((homeGSYVideoPlayer.state != STATE_PLAYING)) {
                        // 进行播放
                        homeGSYVideoPlayer.startVideo();
                    }
                    break;
                case TAG_PAUSE_VIDEO:
                    if ((homeGSYVideoPlayer.state != STATE_PAUSE)) {
                        // 模拟点击,暂停视频
                        homeGSYVideoPlayer.startButton.performClick();
                    }
                    break;
                default:
                    break;
            }
        }


    }

    /**
     * 拦截返回键 返回不退出程序
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Jzvd.backPress()) {
            return true;
        } else {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                moveTaskToBack(true);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}

