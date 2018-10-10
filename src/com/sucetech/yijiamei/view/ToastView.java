package com.sucetech.yijiamei.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sucetech.yijiamei.R;


/**
 * Created by lihh on 2017/4/1.
 */

public class ToastView extends LinearLayout {

    private View myProgressDialog;
    private TextView myProgressDialogText;
    private TextView myToast;

    public ToastView(Context context) {
        super(context);
        initView(context);
    }

    public ToastView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ToastView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.navi_toastdailog_layout, null);
        myToast = (TextView) v.findViewById(R.id.myToast);
        this.addView(v);
    }
    private void resetToast() {
        removeToastTime();
        mHanler.post(timeShort);
    }

    private void removeToastTime() {
        mHanler.removeCallbacks(timeShort);
        recLen = 2;
    }
    private Handler mHanler=new Handler();
    private int recLen = 2;
    private Runnable timeShort=new Runnable() {
        @Override
        public void run() {
            recLen--;
            if (recLen < 0) {
                recLen =2;
                ToastView.this.setVisibility(View.GONE);
            } else {
                mHanler.postDelayed(this, 1000);
            }
        }
    };
    public void showToast(String des) {
        resetToast();
        myToast.setText(des);
        if(this.getVisibility()== View.GONE){
            this.setVisibility(View.VISIBLE);
        }
    }
}
