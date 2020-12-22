package com.hb712.gleak_android.dialog;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.VideoView;

import com.hb712.gleak_android.R;
import com.hb712.gleak_android.util.PermissionsUtil;
import com.hb712.gleak_android.util.ToastUtil;

import java.io.File;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/12/22 11:34
 */
public class VideoDialog {

    private final Context context;
    private final Activity activity;
    private File video;
    private final View videoDialogView;

    @SuppressLint("InflateParams")
    public VideoDialog(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        videoDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_video, null);
    }

    private void showVideo() {
        VideoView videoView = videoDialogView.findViewById(R.id.historyVideoView);
        videoView.setVideoPath(video.getAbsolutePath());
        System.out.println(video.getAbsolutePath());
        new AlertDialog.Builder(context)
                .setView(videoDialogView)
                .create()
                .show();
    }

    public void showVideo(String videoPath) {
        String[] permissions = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        PermissionsUtil.requestPermissions(context, activity, permissions);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            video = new File(videoPath);
            if (!video.isDirectory() && video.exists()) {
                showVideo();
            } else {
                ToastUtil.toastWithoutLog("视频不存在");
            }
        } else {
            ToastUtil.toastWithoutLog("权限不足");
        }
    }
}
