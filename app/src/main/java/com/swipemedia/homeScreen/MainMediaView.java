package com.swipemedia.homeScreen;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
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
    private SurfaceView iv;
    private SurfaceHolder holder;
    private TextView text;
    private String inputUrl;
    private Context mContext;
    private boolean firststart = false;

    private final SurfaceHolder.Callback mSurfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (mMediaPlayer != null) {
                mMediaPlayer.setDisplay(holder);
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

}
