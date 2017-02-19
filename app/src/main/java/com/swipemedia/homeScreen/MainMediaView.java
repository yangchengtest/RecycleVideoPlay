package com.swipemedia.homeScreen;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ksyun.media.player.IMediaPlayer;
import com.ksyun.media.player.KSYMediaPlayer;
import com.swipemedia.R;

import java.io.IOException;

/**
 * Created by yangcheng830117 on 17/2/18.
 */

public class MainMediaView extends FrameLayout {
    private KSYMediaPlayer mMediaPlayer;
    private FrameLayout main_view;
    private LinearLayout surface_linelayout;
    private SurfaceView iv;
    private SurfaceHolder holder;
    private TextView text;
    private String inputUrl;
    private Context mContext;
    private boolean firststart = false;
    private int surfaceHeight,surfaceWidth;


    private final SurfaceHolder.Callback mSurfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (mMediaPlayer != null) {
                mMediaPlayer.setDisplay(holder);
                MeasureSize();
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        }
    };

    public MainMediaView(Context context, String url) {
        super(context);
        mContext = context;
        // 加载布局
        LayoutInflater.from(context).inflate(R.layout.viewpager_main_layout, this);
        main_view = (FrameLayout) findViewById(R.id.main_view);
        iv = (SurfaceView) findViewById(R.id.main_surface);
        holder = iv.getHolder();
        holder.addCallback(mSurfaceCallback);
        text = (TextView) findViewById(R.id.click_button);
        inputUrl = url;
    }

    //VIEWPAGER instantiateItem INIT MEDIAPLAYER
    public void initMedia() {
        mMediaPlayer = new KSYMediaPlayer.Builder(mContext).build();
        try {
            mMediaPlayer.setDataSource(mContext, Uri.parse(inputUrl));
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
        IMediaPlayer.OnPreparedListener mOnPreparedListener = new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                mMediaPlayer.setDisplay(holder);
                MeasureSize();
                //FIRST START
                Log.e("player", "input url=" + inputUrl + "firststart stat=" + firststart);
                if (firststart) {
                    mMediaPlayer.start();
                    firststart = false;
                }
                //KSPLAYER PREPAREASYNC后就会播放,停止一下。
                else {
                    mMediaPlayer.pause();
                }
            }
        };
        mMediaPlayer.setOnPreparedListener(mOnPreparedListener);

        IMediaPlayer.OnErrorListener mOnErrorListener = new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                Log.e("error", "error");
                mMediaPlayer.reset();
                try {
                    mMediaPlayer.setDataSource(mContext, Uri.parse(inputUrl));
                    mMediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        };

        mMediaPlayer.setOnErrorListener(mOnErrorListener);
        text.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (!mMediaPlayer.isPlaying()) {
                                            mMediaPlayer.start();
                                        } else {
                                            mMediaPlayer.pause();
                                        }
                                    }
                                }

        );
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //newConfig.orientation获得当前屏幕状态是横向或者竖向
        //Configuration.ORIENTATION_PORTRAIT 表示竖向
        //Configuration.ORIENTATION_LANDSCAPE 表示横屏
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            MeasureSize();
        }
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            MeasureSize();
        }
    }

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }

    public void pause() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            }
        }
    }

    public void start() {
        if (mMediaPlayer != null) {
            if (!mMediaPlayer.isPlaying()) {
                Log.e("player", "input url=" + inputUrl);
                mMediaPlayer.start();
            }
        }
    }

    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void setFirstStart() {
        firststart = true;
    }


    public void MeasureSize() {
        int mVideoWidth = mMediaPlayer.getVideoWidth();
        int mVideoHeight = mMediaPlayer.getVideoHeight();
        if ((mVideoWidth > 0) && (mVideoHeight > 0)) {
            boolean isplay=false;
            if (mMediaPlayer.isPlaying())
            {
                isplay=true;
                mMediaPlayer.pause();
            }
//            Configuration newConfig = getResources().getConfiguration();
//            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//                int mVideotmp = mVideoWidth;
//                mVideoWidth = mVideoHeight;
//                mVideoHeight = mVideotmp;
//            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            }
            //不能通过获取FRAMELAYOUT的大小来作为最大尺寸,因为旋转屏幕的时候,FRAMELAYOUT可能还没有触发刷新。
            //通过获取去除头部的全屏来进行适配。
            getSurfaceRect();
            Log.e("change size", "width=" + surfaceWidth + " height=" + surfaceHeight);
            Log.e("change size", "video width=" + mVideoWidth + "video height=" + mVideoHeight);
            int finalwidth, finalheight = 0;
            if (mVideoWidth * surfaceHeight > surfaceWidth * mVideoHeight) {
                finalheight = surfaceWidth * mVideoHeight / mVideoWidth;
                finalwidth = surfaceWidth;
            } else if (mVideoWidth * surfaceHeight < surfaceWidth * mVideoHeight) {
                finalwidth = surfaceHeight * mVideoWidth / mVideoHeight;
                finalheight = surfaceHeight;
            } else {
                finalheight = surfaceHeight;
                finalwidth = surfaceWidth;
            }
            Log.e("change size", "final width=" + finalwidth + " final height=" + finalheight + " url=" + inputUrl);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) iv.getLayoutParams();
            layoutParams.height = finalheight;
            layoutParams.width = finalwidth;
            iv.setLayoutParams(layoutParams);
            holder.setFixedSize(mVideoWidth,mVideoHeight);

            iv.getHolder().setSizeFromLayout();
            if (isplay)
            {
                mMediaPlayer.start();
            }
        }
    }

    private void getSurfaceRect()
    {
        int height=0;
        int resourceid=mContext.getResources().getIdentifier("status_bar_height","dimen","android");
        if (resourceid>0)
        {
            height=mContext.getResources().getDimensionPixelSize(resourceid);
        }
        DisplayMetrics dm = new DisplayMetrics();
        dm = mContext.getApplicationContext().getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        surfaceHeight=screenHeight-height;
        surfaceWidth=screenWidth;
    }
}
