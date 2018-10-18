package com.sucetech.yijiamei.newui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.sucetech.yijiamei.R;
import com.sucetech.yijiamei.bean.RecycleMaterialDetails;
import com.sucetech.yijiamei.bean.WuLiaoBean;
import com.sucetech.yijiamei.manager.EventStatus;
import com.sucetech.yijiamei.view.BaseView;
import com.sucetech.yijiamei.view.SuggistPopView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lihh on 2018/10/18.
 */

public class RecoveryMaterirViewItem extends BaseView implements View.OnClickListener {
    private View edit;
    private TextView weidth, type, price, beishu;
    private String weight;

    public RecoveryMaterirViewItem(Context context, ViewGroup ParentView) {
        super(context, ParentView);
    }

    @Override
    public void updata(EventStatus status, Object obj) {
        if (status == EventStatus.materirItem) {
            RecycleMaterialDetails item = (RecycleMaterialDetails) obj;
            weidth.setText(item.weight + "");
            type.setText(item.type + "");
            price.setText(item.price + "");
            beishu.setText(item.multiple + "");
        }
    }

    @Override
    public void initView(Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.recovery_materir_item_layout, null);
        this.addView(v, -1, -1);
        edit = v.findViewById(R.id.edit);
        weidth = v.findViewById(R.id.weidth);
        type = v.findViewById(R.id.type);
        price = v.findViewById(R.id.price);
        beishu = v.findViewById(R.id.beishu);
        edit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit:
                ((RecoveryMaterirView)prentView).eidt();
                break;
        }
    }
}
