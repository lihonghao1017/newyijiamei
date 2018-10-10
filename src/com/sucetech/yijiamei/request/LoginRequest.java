package com.sucetech.yijiamei.request;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.ParseError;
import com.android.volley.request.JsonRequest;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by lihh on 2018/9/19.
 */

public class LoginRequest extends JsonRequest<JSONObject> {
    public LoginRequest(int method, String url, String requestBody, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, requestBody, listener, errorListener);
    }

    public LoginRequest(String url, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(0, url, (String)null, listener, errorListener);
    }

    public LoginRequest(int method, String url, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, (String)null, listener, errorListener);
    }

    public LoginRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest == null?null:jsonRequest.toString(), listener, errorListener);
    }

    public LoginRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        this(jsonRequest == null?0:1, url, jsonRequest, listener, errorListener);
    }

    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            if(response!=null&&response.headers.size()>0){
                Map<String,String> hh=response.headers;
//                Iterator<Map.Entry<String, String>> entries = hh.entrySet().iterator();
                if (hh.containsKey("Authorization")){
                    JSONObject jsonObject=new JSONObject();
                    try {
                        jsonObject.put("Authorization",hh.get("Authorization"));
                        return Response.success(jsonObject, HttpHeaderParser.parseCacheHeaders(response));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


//                while (entries.hasNext()) {
//                    Map.Entry<String, String> entry = entries.next();
//
//
//
//                    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
//                }


            }

            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
            return Response.success(new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException var3) {
            return Response.error(new ParseError(var3));
        } catch (JSONException var4) {
            return Response.error(new ParseError(var4));
        }
    }
}
