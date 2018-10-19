package com.sucetech.yijiamei.newui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sucetech.yijiamei.R;
import com.sucetech.yijiamei.bean.FormImage;
import com.sucetech.yijiamei.manager.EventStatus;
import com.sucetech.yijiamei.view.BaseView;

import java.util.List;

/**
 * Created by lihh on 2018/10/19.
 */

public class NoOkItemView extends BaseView {
    private ImageView img01, img02, img03;
    public  List<FormImage> imgs;


    public NoOkItemView(Context context, ViewGroup ParentView) {
        super(context, ParentView);
    }

    @Override
    public void updata(EventStatus status, Object obj) {
        if (status==EventStatus.showNoOKIemt){
            imgs= (List<FormImage>) obj;
            for (int i = 0; i <imgs.size() ; i++) {
                setPhotos(imgs.get(i));
            }
        }
    }

    @Override
    public void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.no_ok_item_layout, null);
        addView(view);
        img01 = view.findViewById(R.id.img01);
        img02 = view.findViewById(R.id.img02);
        img03 = view.findViewById(R.id.img03);
    }

    private void setPhotos(FormImage formImage) {
        if (img01.getTag() == null) {
            img01.setImageBitmap(formImage.mBitmap);
            img01.setTag(formImage);
        } else if (img02.getTag() == null) {
            img02.setImageBitmap(formImage.mBitmap);
            img02.setTag(formImage);
        } else if (img03.getTag() == null) {
            img03.setImageBitmap(formImage.mBitmap);
            img03.setTag(formImage);
        }
    }
}
