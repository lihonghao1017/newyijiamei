package com.sucetech.yijiamei.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sucetech.yijiamei.MainActivity;
import com.sucetech.yijiamei.R;
import com.sucetech.yijiamei.UserMsg;
import com.sucetech.yijiamei.bean.yiyaunBean;
import com.sucetech.yijiamei.manager.EventStatus;
import com.sucetech.yijiamei.utils.TaskManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by lihh on 2018/9/19.
 */

public class LoginView extends BaseView implements View.OnClickListener {
    private EditText user, pwd;
    private View commit;

    public LoginView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoginView(Context context) {
        super(context);
    }

    @Override
    public void updata(EventStatus status, Object obj) {

    }

    @Override
    public void initView(Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.activity_login, null);
        user = (EditText) v.findViewById(R.id.username);
        pwd = (EditText) v.findViewById(R.id.pwd);
        commit = v.findViewById(R.id.commit);
        commit.setOnClickListener(this);
        this.addView(v, -1, -1);
        user.setText(UserMsg.getUserName());
        pwd.setText(UserMsg.getPwd());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.commit) {
            ((MainActivity) getContext()).showProgressDailogView("登陆中...");
            UserMsg.saveUserName(user.getText().toString());
            UserMsg.savePwd(pwd.getText().toString());
            TaskManager.getInstance().addTask(new Runnable() {
                @Override
                public void run() {
                    requestLoing2();
                }
            });

        }
    }

    private String TAG = "LLL";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private String requestLoing2() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", UserMsg.getUserName());
            jsonObject.put("password", UserMsg.getPwd());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));
        Request request = new Request.Builder()
                .url("http://60.205.139.90:81/api/v1/yijiamei/login")
                .post(body)
                .build();
        try {
            final Response response = ((MainActivity) getContext()).client.newCall(request).execute();
            if (response.isSuccessful()) {
                UserMsg.saveToken(response.header("Authorization"));
                requestYiyuan();
//                this.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        ((MainActivity)getContext()).hideProgressDailogView();
//                        mEventManager.notifyObservers(EventStatus.logined,null);
//                        LoginView.this.setVisibility(View.GONE);
////                        mEventManager.notifyObservers(EventStatus.logined,null);
//                        Toast.makeText(getContext(),"chengong -->",Toast.LENGTH_LONG);
//                    }
//                });
//
                return response.body().string();
            } else {
                Log.e("LLL", "shibai--->");

                this.post(new Runnable() {
                    @Override
                    public void run() {
                        ((MainActivity) getContext()).hideProgressDailogView();
                        Toast.makeText(getContext(), "chengong -->", Toast.LENGTH_LONG);
                    }
                });
//                Toast.makeText(getContext(),"shibai -->"+response.message(),Toast.LENGTH_LONG);
                throw new IOException("Unexpected code " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void requestYiyuan() {


//        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));
        Request request = new Request.Builder()
                .header("Authorization", UserMsg.getToken())
                .addHeader("Accept", "application/json")
                .url("http://60.205.139.90:81/api/v1/yijiamei/medical/")
                .get()
                .build();
        try {
            final Response response = ((MainActivity) getContext()).client.newCall(request).execute();
            if (response.isSuccessful()) {
                Log.e("LLL", "chenggong--requestYiyuan->");
//                Log.e("LLL","chenggong--requestYiyuan22->"+response.body().string());
//                UserMsg.saveYiyuan(response.body().string());
                final List<yiyaunBean> data= new Gson().fromJson(response.body().string(), new TypeToken<List<yiyaunBean>>(){}.getType());//把JSON字符串转为对象

                Log.e("LLL", "chenggong--requestYiyuan->" + UserMsg.getYiyuan());
                this.post(new Runnable() {
                    @Override
                    public void run() {
                        ((MainActivity) getContext()).hideProgressDailogView();
                        mEventManager.notifyObservers(EventStatus.hospitalData, data);
                        LoginView.this.setVisibility(View.GONE);
//                        mEventManager.notifyObservers(EventStatus.logined,null);
                        Toast.makeText(getContext(), "chengong -->", Toast.LENGTH_LONG);
                    }
                });
//
//                return response.body().string();
            } else {
                Log.e("LLL", "shibai---requestYiyuan>");
//                ((MainActivity)getContext()).hideProgressDailogView();
                this.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "shibai --requestYiyuan>", Toast.LENGTH_LONG);
                    }
                });
//                Toast.makeText(getContext(),"shibai -->"+response.message(),Toast.LENGTH_LONG);
                throw new IOException("Unexpected code " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
