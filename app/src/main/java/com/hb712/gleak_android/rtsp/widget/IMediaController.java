package com.hb712.gleak_android.rtsp.widget;

import android.view.View;
import android.widget.MediaController;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/11/9 10:46
 */
public interface IMediaController {
    void hide();

    boolean isShowing();

    void setAnchorView(View view);

    void setEnabled(boolean enabled);

    void setMediaPlayer(MediaController.MediaPlayerControl player);

    void show(int timeout);

    void show();

    void showOnce(View view);
}
