package com.hb712.gleak_android.interfaceabs;

import android.os.Bundle;

public abstract class OKHttpListener {

    public abstract void onSuccess(Bundle bundle);

    public abstract void onServiceError(Bundle bundle);

    public abstract void onNetworkError(Bundle bundle);

    //此方法必走
    public void onNext(Bundle bundle) {
    }

}