package com.sucetech.yijiamei.newui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.sucetech.yijiamei.R;
import com.sucetech.yijiamei.bean.CommitDataBean;
import com.sucetech.yijiamei.bean.FormImage;
import com.sucetech.yijiamei.bean.RecycleMaterialDetails;
import com.sucetech.yijiamei.manager.EventManager;
import com.sucetech.yijiamei.manager.EventStatus;
import com.sucetech.yijiamei.view.BaseView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by lihh on 2018/10/18.
 */

public class RecoveryMaterirView extends BaseView implements View.OnClickListener {
    private List<RecycleMaterialDetails> datas;
    private LinearLayout wuliaoContent;
    private RelativeLayout noOKLayout;

    public RecoveryMaterirView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void updata(EventStatus status, Object obj) {
        if (status == EventStatus.materirList) {
            datas.clear();
            datas.addAll((Collection<? extends RecycleMaterialDetails>) obj);
            wuliaoContent.removeAllViews();
            this.setVisibility(View.VISIBLE);
            for (int i = 0; i < datas.size(); i++) {
                RecoveryMaterirViewItem item = new RecoveryMaterirViewItem(getContext(), wuliaoContent);
                item.updata(EventStatus.materirItem, datas.get(i));
                item.setRecoveryMaterirView(this);
                wuliaoContent.addView(item);
            }
        }else if(status == EventStatus.commitDataOk){
            eidt();
        }
    }

    @Override
    public void initView(Context context) {
        datas = new ArrayList<>();
        View v = LayoutInflater.from(context).inflate(R.layout.materir_detail_view_layout, null);
        v.findViewById(R.id.nextAction).setOnClickListener(this);
        v.findViewById(R.id.back).setOnClickListener(this);
        v.findViewById(R.id.nohegeLayout).setOnClickListener(this);
        wuliaoContent = v.findViewById(R.id.wuliaoContent);
        noOKLayout = v.findViewById(R.id.noOKLayout);
        this.addView(v);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.nextAction) {
            CommitDataBean commitDataBean=new CommitDataBean();
            commitDataBean.datas=datas;
            if (noOkItemView!=null){
                commitDataBean.imgs=noOkItemView.imgs;
            }
            EventManager.getEventManager().notifyObservers(EventStatus.commitData,commitDataBean);
        } else if (v.getId() == R.id.back) {
            eidt();
        } else if (v.getId() == R.id.nohegeLayout) {
            noOKLayout.setVisibility(View.VISIBLE);
            noOKLayout.removeAllViews();
            noOKLayout.addView(new NoOkMaterirView(getContext(), noOKLayout, this));
        }
    }

    public void eidt() {
        this.setVisibility(View.GONE);
        wuliaoContent.removeAllViews();
        datas.clear();
    }

    private NoOkItemView noOkItemView;

    public void setNoOKItem(List<FormImage> datas) {
        if (noOkItemView != null){
            wuliaoContent.removeView(noOkItemView);
            noOkItemView=null;
        }
        noOkItemView = new NoOkItemView(getContext(), wuliaoContent);
        noOkItemView.updata(EventStatus.showNoOKIemt, datas);
        wuliaoContent.addView(noOkItemView);
    }
}
