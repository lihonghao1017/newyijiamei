package com.sucetech.yijiamei.newui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sucetech.yijiamei.R;
import com.sucetech.yijiamei.manager.EventStatus;
import com.sucetech.yijiamei.view.BaseView;

/**
 * Created by admin on 2018/10/16.
 */

public class MaterielItemView extends BaseView {

    public MaterielItemView(Context context, ViewGroup ParentView) {
        super(context, ParentView);
    }

    @Override
    public void updata(EventStatus status, Object obj) {

    }

    @Override
    public void initView(Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.wuliao_item_layout, null);
        this.addView(v, -1, -1);
        name = (TextView) v.findViewById(R.id.name);
        name.setOnClickListener(this);
        beishu = (TextView) v.findViewById(R.id.beishu);
        weight = (TextView) v.findViewById(R.id.weight);
        price = (TextView) v.findViewById(R.id.wuliaoPrice);
        volume = (TextView) v.findViewById(R.id.volume);
        ok = (TextView) v.findViewById(R.id.ok);
        ok.setOnClickListener(this);
    }
}
