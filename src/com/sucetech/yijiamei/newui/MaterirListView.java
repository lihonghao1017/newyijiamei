package com.sucetech.yijiamei.newui;

import android.content.Context;
import android.view.ViewGroup;

import com.sucetech.yijiamei.manager.EventStatus;
import com.sucetech.yijiamei.view.BaseView;

/**
 * Created by lihh on 2018/10/17.
 */

public class MaterirListView extends BaseView{
    public MaterirListView(Context context, ViewGroup ParentView) {
        super(context, ParentView);
    }

    @Override
    public void updata(EventStatus status, Object obj) {
        if (status==EventStatus.materirList){


        }

    }

    @Override
    public void initView(Context context) {

    }
}
