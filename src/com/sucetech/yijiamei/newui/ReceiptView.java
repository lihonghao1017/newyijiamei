package com.sucetech.yijiamei.newui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.sucetech.yijiamei.R;
import com.sucetech.yijiamei.manager.EventStatus;
import com.sucetech.yijiamei.view.BaseView;

/**
 * Created by lihh on 2018/10/18.
 */

public class ReceiptView extends BaseView{

    public ReceiptView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void updata(EventStatus status, Object obj) {

    }

    @Override
    public void initView(Context context) {
        View view= LayoutInflater.from(context).inflate(R.layout.receipt_view_layout,null);
        addView(view);

    }
}
