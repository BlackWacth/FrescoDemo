package com.hua.frescodemo.application;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by ZHONG WEI  HUA on 2016/3/14.
 */
public class MApplication extends Application {

    public static final String SHOW_TYPE = "SHOW_TYPE";
    public static final String SHOW_IMAGE = "SHOW_IMAGE";
    public static final String SHOW_GIF = "SHOW_GIF";

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
