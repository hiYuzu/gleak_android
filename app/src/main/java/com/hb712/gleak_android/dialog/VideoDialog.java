package com.hb712.gleak_android.dialog;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.VideoView;

import com.hb712.gleak_android.R;
import com.hb712.gleak_android.util.ToastUtil;

import java.io.File;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/12/22 11:34
 */
public class VideoDialog {

    private final Activity activity;
    private File video;
    private final View videoDialogView;

    @SuppressLint("InflateParams")
    public VideoDialog(Activity activity) {
        this.activity = activity;
        videoDialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_video, null);
    }

    private void showVideo() {
        VideoView videoView = videoDialogView.findViewById(R.id.historyVideoView);
        videoView.setVideoPath(video.getAbsolutePath());
        new AlertDialog.Builder(activity)
                .setView(videoDialogView)
                .create()
                .show();
        videoView.start();
    }

    public void showVideo(String videoPath) {
        if (videoPath == null) {
            ToastUtil.longToastShow("视频不存在");
            return;
        }
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            video = new File(videoPath);
            if (!video.isDirectory() && video.exists()) {
                showVideo();
            } else {
                ToastUtil.longToastShow("未找到视频");
            }
        } else {
            ToastUtil.longToastShow("权限不足");
        }
    }
}
