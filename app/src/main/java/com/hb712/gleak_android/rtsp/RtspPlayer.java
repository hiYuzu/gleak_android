package com.hb712.gleak_android.rtsp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.hb712.gleak_android.R;
import com.hb712.gleak_android.rtsp.widget.IjkVideoView;
import com.hb712.gleak_android.util.GlobalParam;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/11/9 11:35
 */
public class RtspPlayer {

    private static final String TAG = RtspPlayer.class.getSimpleName();
    private IjkVideoView mVideoView;
    private BaseLoadingView mLoadingView;

    public void init(Activity activity, BaseLoadingView loadingView) {
        mLoadingView = loadingView;
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        mVideoView = activity.findViewById(R.id.videoView);
        mVideoView.setOnCompletionListener(iMediaPlayer -> mVideoView.resume());
        mVideoView.setOnInfoListener((iMediaPlayer, i, i1) -> {
            if (i == IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                mLoadingView.dismissLoading();
            }
            return true;
        });
    }

    public void startPlay() {
        mLoadingView.showLoading();
        mVideoView.setVideoPath(GlobalParam.VIDEO_URL);
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
