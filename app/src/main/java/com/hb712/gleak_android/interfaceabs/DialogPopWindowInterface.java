package com.hb712.gleak_android.interfaceabs;

/**
 * dialog和PopWindow的基类,涉及到其他类型的dialog,最好实现这个接口
 */
public interface DialogPopWindowInterface extends HttpInterface {

    boolean isShowing();

    void show();

    void dismiss();
}
