package com.hb712.gleak_android.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.VideoView;

import com.hb712.gleak_android.R;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/12/22 11:34
 */
public class VideoDialog {

    private final Context context;
    private final String videoPath;
    private final View videoDialogView;

    @SuppressLint("InflateParams")
    public VideoDialog(Context context, String videoPath) {
        this.context = context;
        this.videoPath = videoPath;
        videoDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_video, null);
    }

    public void showVideo() {
        VideoView videoView = videoDialogView.findViewById(R.id.historyVideoView);
        videoView.setVideoPath(videoPath);
        new AlertDialog.Builder(context)
                .setView(videoDialogView)
                .create()
                .show();
    }
}
