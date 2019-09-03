package com.jack.tag;

import android.app.Application;

import com.blankj.utilcode.util.Utils;

public class IApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
    }
}
