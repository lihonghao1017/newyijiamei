package com.sucetech.yijiamei.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sucetech.yijiamei.MainActivity;
import com.sucetech.yijiamei.R;
import com.sucetech.yijiamei.bean.FormImage;
import com.sucetech.yijiamei.manager.EventStatus;
import com.sucetech.yijiamei.view.voice.AudioRecorder;
import com.sucetech.yijiamei.view.voice.RecordButton;

import java.io.File;

/**
 * Created by lihh on 2018/9/22.
 */

public class NoHeGeView extends BaseView implements View.OnClickListener {
    public ImageView img01, img02, img03;
//    private RecordButton mRecordButton;
//    private View voiceFileLayout;
//    public TextView voiceFile;
    private int index = 1;
    public boolean isOK;

    public NoHeGeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


//    public NoHeGeView(Context context, ViewGroup ParentView) {
//        super(context, ParentView);
//    }

    @Override
    public void updata(EventStatus status, Object obj) {
        switch (status) {
            case imgMsg:
                FormImage formImage = (FormImage) obj;
                if (formImage.id == R.id.img01) {
                    img01.setImageBitmap(formImage.mBitmap);
                    img01.setTag(formImage);
                    img02.setVisibility(View.VISIBLE);
                }else  if (formImage.id == R.id.img02) {
                    img02.setImageBitmap(formImage.mBitmap);
                    img02.setTag(formImage);
                    img03.setVisibility(View.VISIBLE);
                }else  if (formImage.id == R.id.img03) {
                    img03.setImageBitmap(formImage.mBitmap);
                    img03.setTag(formImage);
                }
                break;
            case logined:
                this.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void initView(Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.nohege_layout, null);
        this.addView(v);
        img01 = (ImageView) v.findViewById(R.id.img01);
        img01.setOnClickListener(this);
        img02 = (ImageView) v.findViewById(R.id.img02);
        img02.setOnClickListener(this);
        img03 = (ImageView) v.findViewById(R.id.img03);
        img03.setOnClickListener(this);
//        mRecordButton = (RecordButton) v.findViewById(R.id.btn_record);
//        mRecordButton.setAudioRecord(new AudioRecorder());
//        //语音发送的回调
//        mRecordButton.setRecordListener(new RecordButton.RecordListener() {
//            @Override
//            public void recordEnd(String filePath, float time) {
//                voiceFile.setText(filePath);
//                Log.e("LLL", "filePath-->" + filePath);
//                Log.e("LLL", "time-->" + time);//秒
//            }
//        });
//        voiceFileLayout = v.findViewById(R.id.voiceFileLayout);
//        voiceFile = (TextView) v.findViewById(R.id.voiceFile);
//        delete = v.findViewById(R.id.delete);
//        delete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.delete:
//                prentView.removeView(this);
//                prentView.setEnabled(true);
//                break;
//            case R.id.voiceFileLayout:
//                break;
            case R.id.img01:
                index = 1;
                ((MainActivity) getContext()).requestPicture(R.id.img01);
                break;
            case R.id.img02:
                index = 2;
                ((MainActivity) getContext()).requestPicture(R.id.img02);
                break;
            case R.id.img03:
                index = 3;
                ((MainActivity) getContext()).requestPicture(R.id.img03);
                break;
        }
    }

    public void showImages(Bitmap bitmap,File file,int id) {
        if (index == 1) {
            img01.setImageBitmap(bitmap);
            img01.setTag(file);
            img02.setVisibility(View.VISIBLE);
        } else if (index == 2) {
            img02.setImageBitmap(bitmap);
            img03.setVisibility(View.VISIBLE);
            img02.setTag(file);
        } else if (index == 3) {
            img03.setImageBitmap(bitmap);
            img03.setTag(file);
        }

    }


}
