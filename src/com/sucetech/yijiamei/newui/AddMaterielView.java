package com.sucetech.yijiamei.newui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sucetech.yijiamei.R;
import com.sucetech.yijiamei.manager.EventStatus;
import com.sucetech.yijiamei.view.BaseView;

/**
 * Created by lihh on 2018/10/16.
 */

public class AddMaterielView extends BaseView implements View.OnClickListener{
    private TextView nextAction;
    private LinearLayout wuliaoContent;
    public AddMaterielView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void updata(EventStatus status, Object obj) {
        if (status==EventStatus.selectedHos){
            wuliaoContent.addView(new MaterielItemView(getContext(),wuliaoContent));
        }

    }

    @Override
    public void initView(Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.add_materiel_view_layout,null);
        nextAction=v.findViewById(R.id.nextAction);
        nextAction.setOnClickListener(this);
        wuliaoContent=v.findViewById(R.id.wuliaoContent);
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.nextAction){

        }

    }
}
