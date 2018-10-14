package com.sucetech.yijiamei.view;

import android.content.Context;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sucetech.yijiamei.MainActivity;
import com.sucetech.yijiamei.R;
import com.sucetech.yijiamei.UserMsg;
import com.sucetech.yijiamei.bean.FormImage;
import com.sucetech.yijiamei.bean.RecycleMaterial;
import com.sucetech.yijiamei.bean.RecycleMaterialDetails;
import com.sucetech.yijiamei.bean.RecycleMaterialDto;
import com.sucetech.yijiamei.manager.EventStatus;
import com.sucetech.yijiamei.utils.FileUtils;
import com.sucetech.yijiamei.utils.TaskManager;
import com.sucetech.yijiamei.view.voice.AudioRecorder;
import com.sucetech.yijiamei.view.voice.RecordButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by lihh on 2018/9/28.
 */

public class WuLiaoCommitView extends BaseView implements View.OnClickListener, YiYuanPopView.OnYiTuanClick {
    private View hegeBt, noHegeBt;
    private LinearLayout wuliaoItemViews, licensesLayout;
    private ImageView licensesFile, addView;
    private View commit;
    private TextView des, packager, price, selfCheck;
    private RecycleMaterialDto recycleMaterialDto;
    private NoHeGeView noHeGeView;
    private TextView yiyuan;
    private YiYuanPopView yiYuanPopView;
    private RecordButton mRecordButton;
    public TextView voiceFile;


    public WuLiaoCommitView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WuLiaoCommitView(Context context) {
        super(context);
    }

    public WuLiaoCommitView(Context context, ViewGroup ParentView) {
        super(context, ParentView);
    }

    @Override
    public void updata(EventStatus status, Object obj) {
        switch (status) {
            case imgMsg:
                FormImage formImage = (FormImage) obj;
                if (formImage.id == R.id.licensesFile) {
                    licensesFile.setImageBitmap(formImage.mBitmap);
                    licensesFile.setTag(formImage);
                }
                break;
            case logined:
                this.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void initView(Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.wuliao_commit_view, null);
        wuliaoItemViews = v.findViewById(R.id.wuliaoItemViews);
        licensesLayout = v.findViewById(R.id.licensesLayout);
        licensesFile = v.findViewById(R.id.licensesFile);
        addView = v.findViewById(R.id.addView);
        addView.setOnClickListener(this);
        yiyuan = v.findViewById(R.id.yiyuan);
        yiyuan.setOnClickListener(this);
        this.addView(v, -1, -1);
        hegeBt = v.findViewById(R.id.hegeBt);
        noHegeBt = v.findViewById(R.id.noHegeBt);
        hegeBt.setOnClickListener(this);
        noHegeBt.setOnClickListener(this);
        licensesFile.setOnClickListener(this);
        commit = v.findViewById(R.id.commit);
        commit.setOnClickListener(this);
        wuliaoItemViews.addView(new WuliaoItemView(getContext(), wuliaoItemViews));

        noHeGeView = v.findViewById(R.id.nohegeLayout);
        des = v.findViewById(R.id.des);
        packager = v.findViewById(R.id.packager);
        price = v.findViewById(R.id.price);
        selfCheck = v.findViewById(R.id.selfCheck);
        recycleMaterialDto = new RecycleMaterialDto();
        recycleMaterialDto.material = new RecycleMaterial();
        recycleMaterialDto.details = new ArrayList<>();
//        recycleMaterialDto.material.id = 1111;
//        recycleMaterialDto.material.licenseNumber = "1";


        mRecordButton = (RecordButton) v.findViewById(R.id.btn_record);
        mRecordButton.setAudioRecord(new AudioRecorder());
        //语音发送的回调
        mRecordButton.setRecordListener(new RecordButton.RecordListener() {
            @Override
            public void recordEnd(String filePath, float time) {
                voiceFile.setText(filePath);
                Log.e("LLL", "filePath-->" + filePath);
                Log.e("LLL", "time-->" + time);//秒
            }
        });
        voiceFile = (TextView) v.findViewById(R.id.voiceFile);
    }

    @Override
    public void OnYiTuanckItemClick(int id, String str) {
        yiyuan.setText(str);
        recycleMaterialDto.material.medicalId = id;
        yiyuan.setTag(id);
        yiYuanPopView.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.yiyuan:
                String yiyuan = UserMsg.getYiyuan();
                if (yiyuan != null && !yiyuan.equals("")) {
                    yiYuanPopView = new YiYuanPopView(getContext(), this);
                    yiYuanPopView.setWidth(this.getWidth());
//                    yiYuanPopView.showAsDropDown(((MainActivity) getContext()).mConBluthView.weidthStr, 0, 0);
                    return;
                }
                Toast.makeText(getContext(), "医院数据异常", Toast.LENGTH_LONG).show();
                break;
            case R.id.addView:
                wuliaoItemViews.addView(new WuliaoItemView(getContext(), wuliaoItemViews));
                break;
            case R.id.hegeBt:
                noHeGeView.setVisibility(View.GONE);
                break;
            case R.id.noHegeBt:
                noHeGeView.setVisibility(View.VISIBLE);
                break;
            case R.id.licensesFile:
                ((MainActivity) getContext()).requestPicture(R.id.licensesFile);
                break;
            case R.id.commit:
//                if (price.getText().toString() == null || price.getText().toString().equals("")) {
//                    Toast.makeText(getContext(), "价格错误", Toast.LENGTH_LONG).show();
//                    return;
//                }
//                recycleMaterialDto.material.price = Float.parseFloat(price.getText().toString());
//                if (packager.getText().toString() == null || packager.getText().toString().equals("")) {
//                    Toast.makeText(getContext(), "打包人错误", Toast.LENGTH_LONG).show();
//                    return;
//                }
//                recycleMaterialDto.material.packager = packager.getText().toString();
//                if (des.getText().toString() == null || des.getText().toString().equals("")) {
//                    Toast.makeText(getContext(), "描述错误", Toast.LENGTH_LONG).show();
//                    return;
//                }
//                recycleMaterialDto.material.description = des.getText().toString();
//                if (selfCheck.getText().toString() == null || selfCheck.getText().toString().equals("")) {
//                    Toast.makeText(getContext(), "自检错误", Toast.LENGTH_LONG).show();
//                    return;
//                }
                recycleMaterialDto.material.selfCheck = selfCheck.getText().toString();
                if (licensesFile.getTag() == null) {
                    Toast.makeText(getContext(), "收据照片异常", Toast.LENGTH_LONG).show();
                    return;
                }
                if (recycleMaterialDto.material.medicalId < 1) {
                    Toast.makeText(getContext(), "请选择医院", Toast.LENGTH_LONG).show();
                    return;
                }
                int childCount = wuliaoItemViews.getChildCount();
                recycleMaterialDto.details.clear();
                if (childCount > 0) {
                    for (int i = 0; i < childCount; i++) {
                        WuliaoItemView item = (WuliaoItemView) wuliaoItemViews.getChildAt(i);
                        if (!item.isOK) {
                            Toast.makeText(getContext(), "第" + i + "个item异常", Toast.LENGTH_LONG).show();
                            return;
                        }
                        recycleMaterialDto.details.add(item.getItemData());
                    }
                } else {
                    Toast.makeText(getContext(), "未称重", Toast.LENGTH_LONG).show();
                    return;
                }
                if (licensesFile.getTag() == null) {
                    Toast.makeText(getContext(), "票据照片异常", Toast.LENGTH_LONG).show();
                    return;
                }
                String nohegeImg01 = null, nohegeImg02 = null, nohegeImg03 = null;
                if (noHeGeView.img01.getTag() != null) {
                    nohegeImg01 = ((FormImage) noHeGeView.img01.getTag()).mFileName;
                }
                if (noHeGeView.img02.getTag() != null) {
                    nohegeImg02 = ((FormImage) noHeGeView.img02.getTag()).mFileName;
                }
                if (noHeGeView.img03.getTag() != null) {
                    nohegeImg03 = ((FormImage) noHeGeView.img03.getTag()).mFileName;
                }
                final String noImg01 = nohegeImg01;
                final String noImg02 = nohegeImg02;
                final String noImg03 = nohegeImg03;
                final String voidceFile = voiceFile.getText().toString();
                TaskManager.getInstance().addTask(new Runnable() {
                    @Override
                    public void run() {
                        test(((FormImage) licensesFile.getTag()).mFileName, voidceFile, noImg01, noImg02, noImg03);
                    }
                });
                break;
        }
    }

    public Response test(String license, String audio, String failedImg01, String failedImg02, String failedImg03) {

        Gson gson = new Gson();
        String data = gson.toJson(recycleMaterialDto);

        File audioFile = new File(audio);
        File lictence = new File(license);
        try {
            RequestBody requestBody1 = RequestBody.create(MediaType.get("application/json"), data);
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            builder.addFormDataPart("data", null, requestBody1);
            if (failedImg01 != null) {
                builder.addFormDataPart("faileds", "failedImg01.png",
                        RequestBody.create(MediaType.get("image/jpg"), FileUtils.getFile(failedImg01)));
            }
            if (failedImg02 != null) {
                builder.addFormDataPart("faileds", "failedImg02.pngs",
                        RequestBody.create(MediaType.get("image/jpg"),FileUtils.getFile(failedImg02)));
            }
            if (failedImg03 != null) {
                builder.addFormDataPart("faileds", "failedImg03.png",
                        RequestBody.create(MediaType.get("image/jpg"),  FileUtils.getFile(failedImg03)));
            }
            builder.addFormDataPart("licenses", lictence.getName(),
                    RequestBody.create(MediaType.get("image/jpg"),  FileUtils.getFile(license)));
            if (audio != null&&!audio.equals("")) {
                builder.addFormDataPart("audio", audioFile.getName(),
                        RequestBody.create(MediaType.get("audio/amr"), FileUtils.getFile(audio)));
            }
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
