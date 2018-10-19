package com.sucetech.yijiamei.newui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.sucetech.yijiamei.MainActivity;
import com.sucetech.yijiamei.R;
import com.sucetech.yijiamei.bean.FormImage;
import com.sucetech.yijiamei.manager.EventStatus;
import com.sucetech.yijiamei.view.BaseView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lihh on 2018/10/18.
 */

public class NoOkMaterirView extends BaseView implements View.OnClickListener {
    private ImageView img01delete, img03delete, img02delete;
    private ImageView img01, img03, img02;
    private RecoveryMaterirView recoveryMaterirView;
    private List<FormImage> datas;

    public NoOkMaterirView(Context context, ViewGroup ParentView, RecoveryMaterirView recoveryMaterirView) {
        super(context, ParentView);
        this.recoveryMaterirView = recoveryMaterirView;
    }

    @Override
    public void updata(EventStatus status, Object obj) {
        switch (status) {
            case imgMsg:
                FormImage formImage = (FormImage) obj;
                if (formImage.id == R.id.paizhaoLayout) {
                    setPhotos(formImage);
                    datas.add(formImage);
                }
                break;
        }
    }

    @Override
    public void initView(Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.no_ok_materir_layout, null);
        addView(v);
        v.findViewById(R.id.back).setOnClickListener(this);
        v.findViewById(R.id.nextAction).setOnClickListener(this);
        v.findViewById(R.id.paizhaoLayout).setOnClickListener(this);
        img01 = v.findViewById(R.id.img01);
        img03 = v.findViewById(R.id.img03);
        img02 = v.findViewById(R.id.img02);
        img01delete = v.findViewById(R.id.img01delete);
        img03delete = v.findViewById(R.id.img03delete);
        img02delete = v.findViewById(R.id.img02delete);
        img01delete.setOnClickListener(this);
        img03delete.setOnClickListener(this);
        img02delete.setOnClickListener(this);
        datas = new ArrayList<>();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img01delete:
                img01.setImageDrawable(null);
                img01.setTag(null);
                img01delete.setVisibility(View.GONE);
                break;
            case R.id.img02delete:
                img02.setImageDrawable(null);
                img02.setTag(null);
                img02delete.setVisibility(View.GONE);
                break;
            case R.id.img03delete:
                img03.setImageDrawable(null);
                img03.setTag(null);
                img03delete.setVisibility(View.GONE);
                break;
            case R.id.back:
                prentView.removeView(this);
                prentView.setVisibility(View.GONE);
                break;
            case R.id.nextAction:
                if (datas.size() > 0) {
                    recoveryMaterirView.setNoOKItem(datas);
                    this.setVisibility(View.GONE);
                }else{
                    Toast.makeText(getContext(), "请拍照不合格证据", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.paizhaoLayout:
                if (img01.getTag() == null || img02.getTag() == null || img03.getTag() == null)
                    ((MainActivity) getContext()).requestPicture(R.id.paizhaoLayout);
                else
                    Toast.makeText(getContext(), "请删除", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void setPhotos(FormImage formImage) {
        if (img01.getTag() == null) {
            img01.setImageBitmap(formImage.mBitmap);
            img01.setTag(formImage);
            img01delete.setVisibility(View.VISIBLE);
        } else if (img02.getTag() == null) {
            img02.setImageBitmap(formImage.mBitmap);
            img02.setTag(formImage);
            img02delete.setVisibility(View.VISIBLE);
        } else if (img03.getTag() == null) {
            img03.setImageBitmap(formImage.mBitmap);
            img03.setTag(formImage);
            img03delete.setVisibility(View.VISIBLE);
        }

    }
}
