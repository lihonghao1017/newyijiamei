package com.sucetech.yijiamei;

import android.app.Application;
import android.app.Service;
import android.os.Vibrator;

import com.baidu.mapapi.SDKInitializer;
import com.mapbar.scale.ScaleCalculator;

/**
 * Created by lihh on 2018/9/19.
 */

public class App extends Application {
    public LocationService locationService;
    public Vibrator mVibrator;
    @Override
    public void onCreate() {
        super.onCreate();
        ScaleCalculator.init(this, 0, 2199, 1080, 2f);
       UserMsg.initUserMsg(this);
        locationService = new LocationService(getApplicationContext());
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        SDKInitializer.initialize(getApplicationContext());
    }
}
