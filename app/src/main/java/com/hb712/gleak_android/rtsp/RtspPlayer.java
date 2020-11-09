package com.hb712.gleak_android.rtsp;

import android.annotation.SuppressLint;
import android.app.Activity;

import com.hb712.gleak_android.R;
import com.hb712.gleak_android.rtsp.listener.IjkPlayerListener;
import com.hb712.gleak_android.rtsp.widget.IjkVideoView;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/11/9 11:35
 */
public class RtspPlayer {

    private static final String TAG = RtspPlayer.class.getSimpleName();
    @SuppressLint("AuthLeak")
    private static final String URL = "rtsp://admin:tjtcb712@192.168.1.100:554/h264/ch1/main/av_stream";
    private IjkMediaPlayer mIjkMediaPlayer;
    private IjkVideoView mVideoView;
    private BaseLoadingView mLoadingView;

    public void init(Activity activity, BaseLoadingView loadingView) {
        mLoadingView = loadingView;
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        mVideoView = activity.findViewById(R.id.videoView);
        mVideoView.setIjkPlayerListener(new IjkPlayerListener() {
            @Override
            public void onIjkPlayer(IjkMediaPlayer ijkMediaPlayer) {
                mIjkMediaPlayer = ijkMediaPlayer;
            }
        });
        mVideoView.setOnCompletionListener(iMediaPlayer -> mVideoView.resume());
        mVideoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
                if (i == IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    mLoadingView.dismissLoading();
                }
                return true;
            }
        });
    }

    public void startPlay() {
        mLoadingView.showLoading();
        mVideoView.setVideoPath(URL);
        mVideoView.start();
    }

    public void stopPlay() {
        mVideoView.stopPlayback();
    }

    public void release() {
        mVideoView.stopPlayback();
        mVideoView.release(true);
        IjkMediaPlayer.native_profileEnd();
    }

    public abstract static class BaseLoadingView {
        public abstract void showLoading();

        public abstract void dismissLoading();
    }
}
