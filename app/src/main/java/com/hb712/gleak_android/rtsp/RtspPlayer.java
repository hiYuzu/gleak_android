package com.hb712.gleak_android.rtsp;

import android.app.Activity;
import android.os.Environment;

import com.hb712.gleak_android.R;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer;

import com.hb712.gleak_android.rtsp.widget.IjkVideoView;
import com.hb712.gleak_android.util.DateUtil;
import com.hb712.gleak_android.util.GlobalParam;
import com.hb712.gleak_android.util.LogUtil;

import java.io.File;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/11/9 11:35
 */
public class RtspPlayer {

    private static final String TAG = RtspPlayer.class.getSimpleName();
    private IjkVideoView mVideoView;
    private BaseLoadingView mLoadingView;

    private String videoPath;

    private boolean recording = false;

    static {
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
    }

    public void init(Activity activity, BaseLoadingView loadingView) {
        mLoadingView = loadingView;

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
    /// 截图暂时弃用
    /*
    public void getScreenshots() {
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
            String path = GlobalParam.SNAPSHOT_PATH;
            File fileDir = new File(path);
            if (!fileDir.exists()) {
                if (fileDir.mkdirs()) {
                    LogUtil.debugOut(TAG, "截图文件夹创建失败");
                }
            }

            File file = new File(
                    fileDir.getPath()
                            + "/"
                            + DateUtil.getCurrentTime(DateUtil.TIME_SERIES)
                            + ".jpg");
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
    */
    public synchronized int startRecord() {
        if (!mVideoView.isPlaying()) {
            LogUtil.infoOut(TAG, "无视频信号");
            return -1;
        }
        File fileDir = new File(GlobalParam.VIDEO_RECORD_PATH);
        if (!fileDir.exists()) {
            if (!fileDir.mkdirs()) {
                LogUtil.warnOut(TAG, null, "视频文件夹创建失败");
                return -1;
            }
        }
        videoPath = GlobalParam.VIDEO_RECORD_PATH
                + File.separator
                + DateUtil.getCurrentTime(DateUtil.TIME_SERIES)
                + ".mp4";
        int result = mVideoView.startRecord(videoPath);
        if (result == 0) {
            recording = true;
            LogUtil.infoOut(TAG, "开始录制");
        }
        return result;
    }

    public void stopRecord() {
        int result = mVideoView.stopRecord();
        recording = false;
        LogUtil.infoOut(TAG, "结束录制:" + result);
    }

    public String getVideoPath() {
         return videoPath;
    }

    public boolean isRecording() {
        return recording;
    }

    public abstract static class BaseLoadingView {
        public abstract void showLoading();

        public abstract void dismissLoading();
    }
}
