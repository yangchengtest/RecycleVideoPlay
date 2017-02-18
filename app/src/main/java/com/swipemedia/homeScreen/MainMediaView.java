package com.swipemedia.homeScreen;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.swipemedia.R;

import java.io.IOException;

/**
 * Created by yangcheng830117 on 17/2/18.
 */

public class MainMediaView extends FrameLayout {
    private MediaPlayer mMediaPlayer;
    private SurfaceView iv;
    private SurfaceHolder holder;
    private TextView text;
    private String inputUrl;
    private Context mContext;
    private boolean firststart=false;

    public MainMediaView(Context context, String url) {
        super(context);
        mContext = context;
        // 加载布局
        LayoutInflater.from(context).inflate(R.layout.viewpager_main_layout, this);
        iv = (SurfaceView) findViewById(R.id.main_surface);
        holder = iv.getHolder();
        text = (TextView) findViewById(R.id.click_button);
        inputUrl = url;
    }

    //VIEWPAGER instantiateItem INIT MEDIAPLAYER
    public void initMedia() {
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(mContext, Uri.parse(inputUrl));
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                               @Override
                                               public void onPrepared(MediaPlayer mp) {
                                                   mMediaPlayer.setDisplay(holder);
                                                   //FIRST START
                                                   if (firststart) {
                                                       mMediaPlayer.start();
                                                       firststart=false;
                                                   }
                                               }
                                           }

        );
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                                            @Override
                                            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                                                Log.e("error", "error");
                                                mMediaPlayer.reset();
                                                try {
                                                    mMediaPlayer.setDataSource(mContext, Uri.parse(inputUrl));
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                return false;
                                            }
                                        }

        );
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

    public void setFirstStart()
    {
        firststart=true;
    }

}
