package com.sucetech.yijiamei.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.toolbox.Volley;
import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.sucetech.yijiamei.MainActivity;
import com.sucetech.yijiamei.R;
import com.sucetech.yijiamei.UserMsg;
import com.sucetech.yijiamei.manager.EventStatus;
import com.sucetech.yijiamei.request.LoginRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
        user =(EditText) v.findViewById(R.id.username);
        pwd = (EditText)v.findViewById(R.id.pwd);
        commit = v.findViewById(R.id.commit);
        commit.setOnClickListener(this);
        this.addView(v,-1,-1);
        user.setText(UserMsg.getUserName());
        pwd.setText(UserMsg.getPwd());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.commit) {
            ((MainActivity)getContext()).showProgressDailogView("登陆中...");
            UserMsg.saveUserName(user.getText().toString());
            UserMsg.savePwd(pwd.getText().toString());
            requestLogin();
        }
    }
    private String TAG="LLL";

    private void  requestLogin(){
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("username",UserMsg.getUserName());
            jsonObject.put("password",UserMsg.getPwd());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LoginRequest jsonRequest = new LoginRequest(Request.Method.POST,"http://60.205.139.90:81/api/v1/yijiamei/login", jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            ((MainActivity)getContext()).hideProgressDailogView();
                            UserMsg.saveToken(response.getString("Authorization"));
                            mEventManager.notifyObservers(EventStatus.logined,null);
                            LoginView.this.setVisibility(View.GONE);
//                            String token=response.getString("Authorization").replace("Bearer","");
//                            JWT jwt = new JWT(token);
//                            Claim claim=jwt.getClaim("userInfo");
//                            Log.e(TAG, "claim.asString() -> " + claim.asString());
//                            Date expiresAt = jwt.getExpiresAt();
//                            Log.e(TAG, "claim.asString() -> " + expiresAt.toString());
//                            boolean isExpired = jwt.isExpired(10); // 10 seconds leeway
//                            Log.e(TAG, "claim.isExpired() -> " + isExpired);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.getMessage(), error);
            }
        })
        {
            //注意此处override的getParams()方法,在此处设置post需要提交的参数根本不起作用
            //必须象上面那样,构成JSONObject当做实参传入JsonObjectRequest对象里
            //所以这个方法在此处是不需要的
//    @Override
//    protected Map<String, String> getParams() {
//          Map<String, String> map = new HashMap<String, String>();
//            map.put("name1", "value1");
//            map.put("name2", "value2");

//        return params;
//    }

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json; charset=UTF-8");

                return headers;
            }
        };
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                10 * 1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonRequest);
    }


}
