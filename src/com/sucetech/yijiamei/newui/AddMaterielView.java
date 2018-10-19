package com.sucetech.yijiamei.newui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sucetech.yijiamei.R;
import com.sucetech.yijiamei.bean.RecycleMaterialDetails;
import com.sucetech.yijiamei.bean.yiyaunBean;
import com.sucetech.yijiamei.manager.EventManager;
import com.sucetech.yijiamei.manager.EventStatus;
import com.sucetech.yijiamei.view.BaseView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lihh on 2018/10/16.
 */

public class AddMaterielView extends BaseView implements View.OnClickListener {
    private TextView nextAction;
    private LinearLayout wuliaoContent;
    private TextView title;
    private yiyaunBean yiyaunBean;

    public AddMaterielView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void updata(EventStatus status, Object obj) {
        if (status == EventStatus.selectedHos) {
            wuliaoContent.removeAllViews();
            wuliaoContent.addView(new MaterielItemView(getContext(), wuliaoContent));
            yiyaunBean = (com.sucetech.yijiamei.bean.yiyaunBean) obj;
            title.setText(yiyaunBean.name);
            this.setVisibility(View.VISIBLE);
        }else if(status == EventStatus.commitDataOk){
            this.setVisibility(View.GONE);
        }
    }

    @Override
    public void initView(Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.add_materiel_view_layout, null);
        nextAction = v.findViewById(R.id.nextAction);
        nextAction.setOnClickListener(this);
        wuliaoContent = v.findViewById(R.id.wuliaoContent);
        title = v.findViewById(R.id.title);
        v.findViewById(R.id.back).setOnClickListener(this);
        this.addView(v, -1, -1);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.nextAction) {
            int count = wuliaoContent.getChildCount();
            List<RecycleMaterialDetails> datas = new ArrayList<>();
            for (int i = 0; i < count - 1; i++) {
                MaterielItemView itemView = (MaterielItemView) wuliaoContent.getChildAt(i);
                RecycleMaterialDetails item = itemView.getRecycleMaterialDetails();
                if (item != null) {
                    datas.add(item);
                } else {
                    Toast.makeText(getContext(), "请检测第" + count + "个物料信息", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            if (datas.size() > 0) {
                EventManager.getEventManager().notifyObservers(EventStatus.materirList, datas);
            }

        } else if (view.getId() == R.id.back) {
            this.setVisibility(View.GONE);

        }

    }
}
