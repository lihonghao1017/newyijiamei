package com.sucetech.yijiamei.newui;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sucetech.yijiamei.MainActivity;
import com.sucetech.yijiamei.R;
import com.sucetech.yijiamei.UserMsg;
import com.sucetech.yijiamei.bean.CommitDataBean;
import com.sucetech.yijiamei.bean.FormImage;
import com.sucetech.yijiamei.bean.RecycleMaterial;
import com.sucetech.yijiamei.bean.RecycleMaterialDto;
import com.sucetech.yijiamei.manager.EventManager;
import com.sucetech.yijiamei.manager.EventStatus;
import com.sucetech.yijiamei.utils.FileUtils;
import com.sucetech.yijiamei.utils.TaskManager;
import com.sucetech.yijiamei.view.BaseView;
import com.zxing.activity.CaptureActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by lihh on 2018/10/19.
 */

public class CommitView extends BaseView implements View.OnClickListener {
    private EditText editText;
    private ImageView paizhao,listenImg;
    private CommitDataBean commitDataBean;
    private RecycleMaterialDto recycleMaterialDto = new RecycleMaterialDto();

    public CommitView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void updata(EventStatus status, Object obj) {
        switch (status) {
            case imgMsg:
                FormImage formImage = (FormImage) obj;
                if (formImage.id == R.id.shoujupaizhaoLayout) {
                    listenImg.setImageBitmap(formImage.mBitmap);
                    listenImg.setTag(formImage);
                }
                break;
            case commitData:
                commitDataBean = (CommitDataBean) obj;
                this.setVisibility(View.VISIBLE);
                recycleMaterialDto.details = commitDataBean.datas;
                break;
            case commitDataOk:
                this.setVisibility(View.GONE);
                editText.setText("");
                listenImg.setImageDrawable(null);
                break;
        }
    }

    @Override
    public void initView(Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.commit_layout, null);
        addView(v);
        v.findViewById(R.id.back).setOnClickListener(this);
        v.findViewById(R.id.clear).setOnClickListener(this);
        v.findViewById(R.id.nextAction).setOnClickListener(this);
        v.findViewById(R.id.shoujupaizhaoLayout).setOnClickListener(this);
        v.findViewById(R.id.saoyisaoLayout).setOnClickListener(this);
        paizhao = v.findViewById(R.id.paizhao);
        listenImg=v.findViewById(R.id.listenImg);
        editText = v.findViewById(R.id.inputET);
        editText.setInputType(EditorInfo.TYPE_CLASS_PHONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                this.setVisibility(View.GONE);
                editText.setText("");
                listenImg.setImageDrawable(null);
                break;
            case R.id.clear:
                editText.setText("");
                break;
            case R.id.nextAction:
                recycleMaterialDto.material = new RecycleMaterial();
                recycleMaterialDto.material.licenseNumber = editText.getText().toString();
                final FormImage formImage = (FormImage) listenImg.getTag();
                if (formImage != null) {
                    TaskManager.getInstance().addTask(new Runnable() {
                        @Override
                        public void run() {
                            test(formImage.mFileName);
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "请拍照收据", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.shoujupaizhaoLayout:
                ((MainActivity) getContext()).requestPicture(R.id.shoujupaizhaoLayout);
                break;
            case R.id.saoyisaoLayout:
                Intent openCameraIntent = new Intent(getContext(), CaptureActivity.class);
                ((MainActivity) getContext()).startActivityForResult(openCameraIntent, R.id.saoyisaoLayout);
                break;
        }
    }
    public void setDanjuHao(String nub){
        if (nub!=null&&!nub.equals("")){
            editText.setText(nub);
        }
    }

    public Response test(String license) {


        Gson gson = new Gson();
        String data = gson.toJson(recycleMaterialDto);

//        File audioFile = new File(audio);
        File lictence = new File(license);
        try {
            RequestBody requestBody1 = RequestBody.create(MediaType.get("application/json"), data);
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            builder.addFormDataPart("data", null, requestBody1);
            if (commitDataBean != null && commitDataBean.imgs != null && commitDataBean.imgs.size() > 0) {
                for (int i = 0; i < commitDataBean.imgs.size(); i++) {
                    builder.addFormDataPart("faileds", "failedImg01.png",
                            RequestBody.create(MediaType.get("image/jpg"), FileUtils.getFile(commitDataBean.imgs.get(i).mFileName)));
                }
                if (commitDataBean.audioFile!=null){
                    File audioFile=new File(commitDataBean.audioFile);
                    if (audioFile.exists()){
                        builder.addFormDataPart("audio", audioFile.getName(),
                                RequestBody.create(MediaType.get("audio/amr"), FileUtils.getFile(commitDataBean.audioFile)));
                    }
                }
            }
            builder.addFormDataPart("licenses", lictence.getName(),
                    RequestBody.create(MediaType.get("image/jpg"), FileUtils.getFile(license)));
            String url = "http://60.205.139.90:81/api/v1/yijiamei/recycle";
            Request request = new Request.Builder()
                    .url(url)
                    .header("Authorization", UserMsg.getToken())
                    .post(builder.build())
                    .build();
            Response response = ((MainActivity) getContext()).client.newCall(request).execute();
            if (response.isSuccessful()) {
                this.post(new Runnable() {
                    @Override
                    public void run() {
                        EventManager.getEventManager().notifyObservers(EventStatus.commitDataOk,null);
                        Toast.makeText(getContext(), "提交成功", Toast.LENGTH_LONG).show();
                    }
                });
                Log.e("LLL", "isSuccessful");
            } else {
                this.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "提交失败", Toast.LENGTH_LONG).show();
                    }
                });
                Log.e("LLL", "failed---");
            }

            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
