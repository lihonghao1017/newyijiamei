package com.sucetech.yijiamei;

import android.app.Application;

import com.mapbar.scale.ScaleCalculator;

/**
 * Created by lihh on 2018/9/19.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ScaleCalculator.init(this, 0, 1024, 600, 1.5f);
       UserMsg.initUserMsg(this);
    }
}
