package com.hb712.gleak_android.rtsp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;

import com.hb712.gleak_android.R;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer;

import com.hb712.gleak_android.rtsp.widget.IjkVideoView;
import com.hb712.gleak_android.util.GlobalParam;
import com.hb712.gleak_android.util.LogUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/11/9 11:35
 */
public class RtspPlayer {

    private static final String TAG = RtspPlayer.class.getSimpleName();
    private IjkVideoView mVideoView;
    private BaseLoadingView mLoadingView;

    public boolean isRecording = false;

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

    public void getScreenshots(Context context) {
        if (!mVideoView.isPlaying()) {
            LogUtil.infoOut(TAG, "无视频信号");
            return;
        }
        int width = 1280;
        int height = 720;
        Bitmap srcBitmap = Bitmap.createBitmap(width,
                height, Bitmap.Config.ARGB_8888);
        boolean flag = mVideoView.getCurrentFrame(srcBitmap);
        if (flag) {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "/ijkplayer/snapshot";
            File fileDir = new File(path);
            if (!fileDir.exists()) {
                if (fileDir.mkdirs()) {
                    LogUtil.infoOut(TAG, "文件夹创建失败");
                }
            }

            @SuppressLint("SimpleDateFormat")
            File file = new File(
                    fileDir.getPath()
                            + "/"
                            + new SimpleDateFormat("yyyyMMddHHmmss")
                            .format(new Date()) + ".jpg");
            try {
                FileOutputStream out = new FileOutputStream(file);
                srcBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.flush();
                out.close();
                LogUtil.infoOut(TAG, "截图已保存：" + file.getPath());
            } catch (FileNotFoundException e) {
                LogUtil.errorOut(TAG, e, "文件未找到");
            } catch (IOException ex) {
                LogUtil.errorOut(TAG, ex, "IO异常");
            }

        } else {
            LogUtil.infoOut(TAG, "截图失败");
        }
    }

    public void startRecord(Context context) {
        if (!mVideoView.isPlaying()) {
            LogUtil.infoOut(TAG, "无视频信号");
            return;
        }
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "/ijkplayer/video";
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            if (!fileDir.mkdirs()) {
                LogUtil.infoOut(TAG, "文件夹创建失败");
            }
        }
        @SuppressLint("SimpleDateFormat")
        String filePath = path + "/"
                + new SimpleDateFormat("yyyyMMddHHmmss")
                .format(new Date()) + ".mp4";
        int result = mVideoView.startRecord(filePath);
        isRecording = true;
        LogUtil.infoOut(TAG, "开始录制: " + result);
    }

    public void stopRecord(Context context) {
        int result = mVideoView.stopRecord();
        isRecording = false;
        LogUtil.infoOut(TAG, "停止录制: " + result);
    }

    public abstract static class BaseLoadingView {
        public abstract void showLoading();

        public abstract void dismissLoading();
    }
}
