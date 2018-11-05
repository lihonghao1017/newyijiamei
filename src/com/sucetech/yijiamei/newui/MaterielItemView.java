package com.sucetech.yijiamei.newui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.sucetech.yijiamei.MainActivity;
import com.sucetech.yijiamei.R;
import com.sucetech.yijiamei.bean.RecycleMaterialDetails;
import com.sucetech.yijiamei.bean.WuLiaoBean;
import com.sucetech.yijiamei.bean.WuliaoType;
import com.sucetech.yijiamei.manager.EventStatus;
import com.sucetech.yijiamei.view.BaseView;
import com.sucetech.yijiamei.view.SuggistPopView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2018/10/16.
 */

public class MaterielItemView extends BaseView implements View.OnClickListener, SuggistPopView.OnSuggistItemClick {
    private View delete, updata;
    private TextView weidth, type, price,beishu;
    private String weight;
    private SuggistPopView mSuggistPopView;
//    private List<WuLiaoBean> datas;
    private boolean isCreatNextItem;
    public RecycleMaterialDetails recycleMaterialDetails;
    private boolean isWeightOK;
    private List<WuliaoType> wuliaoTypes;
    public MaterielItemView(Context context, ViewGroup ParentView) {
        super(context, ParentView);
    }

    @Override
    public void updata(EventStatus status, Object obj) {
        if (status == EventStatus.weight) {
            weight = (String) obj;
            if (weight!=null&&!weight.equals("")&&!isWeightOK) {
                weidth.setText(weight);
                isWeightOK=true;
            }
        }else if(status== EventStatus.wuliaoType){
            wuliaoTypes= (List<WuliaoType>) obj;
            if (mSuggistPopView==null){
                mSuggistPopView = new SuggistPopView(getContext(), wuliaoTypes, this);
                mSuggistPopView.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {

                    }
                });
            }
        }
    }

    @Override
    public void initView(Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.materiel_item_layout, null);
        this.addView(v, -1, -1);
        delete = v.findViewById(R.id.delete);
        updata = v.findViewById(R.id.updata);
        weidth =  v.findViewById(R.id.weidth);
        weidth.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        beishu=v.findViewById(R.id.beishu);
        beishu.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        type =  v.findViewById(R.id.type);
        price = v.findViewById(R.id.price);
        price.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        delete.setOnClickListener(this);
        updata.setOnClickListener(this);
        type.setOnClickListener(this);
//        datas = new ArrayList<>();
//        for (int i = 0; i < 6; i++) {
//            WuLiaoBean wuLiaoBean = new WuLiaoBean();
//            wuLiaoBean.name = "塑料" + i;
//            wuLiaoBean.index = i;
//            datas.add(wuLiaoBean);
//        }

        recycleMaterialDetails = new RecycleMaterialDetails();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.type:
                mSuggistPopView.setWidth(this.getWidth());
                mSuggistPopView.showAsDropDown(this, 0, 0);
                break;
            case R.id.updata:
                if (weight != null) {
                    isWeightOK=true;
                    weidth.setText(weight);
//                    if (!isCreatNextItem&& !type.getText().toString().equals("") && type.getText().toString() != null) {
//                        prentView.addView(new MaterielItemView(getContext(), prentView));
//                        isCreatNextItem=true;
//                    }
                }else{
                    weidth.setText("0");
                }
                break;
            case R.id.delete:
                prentView.removeView(this);
                break;
        }

    }

    @Override
    public void onSuggistItemClick(int position, String str) {
        type.setText(str);
        recycleMaterialDetails.type =  wuliaoTypes.get(position).id;
        recycleMaterialDetails.orgId=wuliaoTypes.get(position).orgId;
        recycleMaterialDetails.typeName=wuliaoTypes.get(position).name;
        mSuggistPopView.dismiss();
//        if (!isCreatNextItem && !weidth.getText().toString().equals("") && weidth.getText().toString() != null) {
//            prentView.addView(new MaterielItemView(getContext(), prentView));
//            isCreatNextItem=true;
//        }
    }
    public RecycleMaterialDetails getRecycleMaterialDetails(){
        if ( weidth.getText().toString() == null||weidth.getText().toString().equals("")){
            Toast.makeText(getContext(),"请更新重量",Toast.LENGTH_LONG).show();
            return null;
        }
        if ( type.getText().toString() == null||type.getText().toString().equals("")){
            Toast.makeText(getContext(),"请选择类型",Toast.LENGTH_LONG).show();
            return null;
        }

        float www = Float.valueOf(0);
        if (beishu.getText().toString() != null && !beishu.getText().toString().equals("")) {
           if (weidth.getText().toString().contains("-")){
               www=0;
           }else{
               www = Float.parseFloat(weidth.getText().toString()) * Float.parseFloat(beishu.getText().toString());
               recycleMaterialDetails.multiple= Float.parseFloat(beishu.getText().toString());
           }
        } else {
            www = Float.parseFloat(weidth.getText().toString());
            recycleMaterialDetails.multiple=1;
        }
        recycleMaterialDetails.weight=www;
        if (price.getText().toString() != null && !price.getText().toString().equals("")) {
            recycleMaterialDetails.unitPrice=price.getText().toString();
        }

        return recycleMaterialDetails;
    }
}
