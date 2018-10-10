package com.sucetech.yijiamei.request;

import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.ParseError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.sucetech.yijiamei.bean.FormImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static java.net.Proxy.Type.HTTP;

/**
 * Created by lihh on 2018/9/23.
 */

public class PostUploadRequest  extends Request<JSONObject> {

    private Map<String, String> sendHeader = new HashMap<String, String>();

    // 正确数据的时候回掉用
    private Response.Listener<JSONObject> mListener;

    // 请求 数据通过参数的形式传入
    private String content;
    private FormImage mImage;

    // 数据分隔线
    private String BOUNDARY = "----CloudLifeUpLoadImage";
    private String MULTIPART_FORM_DATA = "multipart/form-data";

    public PostUploadRequest(String url, String text, FormImage Item, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {

        super(Method.POST, url, errorListener);
        this.mListener = listener;
        setShouldCache(false);
        mImage = Item;
        content = text;

        // 设置请求的响应事件，因为文件上传需要较长的时间，所以在这里加大了，设为5秒
        setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    /*
     * 这里开始解析数据
     * @param response
     *            Response from the network
     * @return
     * */
    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            // 防止中文乱码
            @SuppressWarnings("deprecation")
            String jsonString = new String(response.data);

            JSONObject jsonObject = new JSONObject(jsonString);

            Log.e("upLoad", "jsonObject " + jsonObject.toString());

            return Response.success(jsonObject, HttpHeaderParser.parseCacheHeaders(response));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    /*
     * 回调正确的数据
     * @param response
     *            The parsed response returned by
     * */
    @Override
    protected void deliverResponse(JSONObject response) {
        mListener.onResponse(response);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return sendHeader;
    }

    public void setSendCookie(String cookie) {
        sendHeader.put("Cookie", cookie);
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        StringBuffer sb = new StringBuffer();

        if (content == null) {
            /**
             * 图片
             */
            /* 第一行 */
            // `"--" + BOUNDARY + "\r\n"`
            sb.append("--" + BOUNDARY + "\r\n");

            /* 第二行 */
            // Content-Disposition: form-data; name="参数的名称"; filename="上传的文件名" +
            // "\r\n"
            sb.append("Content-Disposition: form-data;");
            sb.append(" name=\"");
            sb.append(mImage.getName());
            sb.append("\"");
            sb.append("; filename=\"");
            sb.append(mImage.getFileName());
            sb.append("\"");
            sb.append("\r\n");

            /* 第三行 */
            // Content-Type: 文件的 mime 类型 + "\r\n"
            sb.append("Content-Type: ");
            sb.append(mImage.getMime());
            sb.append("\r\n");

            /* 第四行 */
            // "\r\n" 空白的一行
            sb.append("\r\n");

            try {
                bos.write(sb.toString().getBytes("utf-8"));
                /* 第五行 */
                // 文件的二进制数据 + "\r\n"
                bos.write(mImage.getValue());
                bos.write("\r\n".getBytes("utf-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            /* 结尾行 */
            // `"--" + BOUNDARY + "--" + "\r\n"`
            String endLine = "--" + BOUNDARY + "--" + "\r\n";
            try {
                bos.write(endLine.toString().getBytes("utf-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.v("upLoad", "=====formImage====\n" + bos.toString());
            return bos.toByteArray();
        }
        /**
         * 文字
         */
        /* 第一行 */
        // `"--" + BOUNDARY + "\r\n"`
        sb.append("--" + BOUNDARY + "\r\n");

        /* 第二行 */
        // Content-Disposition: form-data; name="text" + "\r\n"
        sb.append("Content-Disposition: form-data;");
        sb.append(" name=\"");
        sb.append("text");
        sb.append("\"");
        sb.append("\r\n");

        /* 第三行 */
        // "\r\n" 空白的一行
        sb.append("\r\n");

        /* 第四行 */
        // 文本内容
        sb.append(content);
        sb.append("\r\n");

        if (mImage == null) {
            /* 结尾行 */
            // `"--" + BOUNDARY + "--" + "\r\n"`
            String endLine = "--" + BOUNDARY + "--" + "\r\n";
            try {
                bos.write(sb.toString().getBytes("utf-8"));
                bos.write(endLine.toString().getBytes("utf-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.v("upLoad", "=====formImage====\n" + bos.toString());
            return bos.toByteArray();
        } else {
            /**
             * 图片
             */
            /* 第一行 */
            // `"--" + BOUNDARY + "\r\n"`
            sb.append("--" + BOUNDARY + "\r\n");

            /* 第二行 */
            // Content-Disposition: form-data; name="参数的名称"; filename="上传的文件名" +
            // "\r\n"
            sb.append("Content-Disposition: form-data;");
            sb.append(" name=\"");
            sb.append(mImage.getName());
            sb.append("\"");
            sb.append("; filename=\"");
            sb.append(mImage.getFileName());
            sb.append("\"");
            sb.append("\r\n");

            /* 第三行 */
            // Content-Type: 文件的 mime 类型 + "\r\n"
            sb.append("Content-Type: ");
            sb.append(mImage.getMime());
            sb.append("\r\n");

            /* 第四行 */
            // "\r\n" 空白的一行
            sb.append("\r\n");

            try {
                bos.write(sb.toString().getBytes("utf-8"));
                /* 第五行 */
                // 文件的二进制数据 + "\r\n"
                bos.write(mImage.getValue());
                bos.write("\r\n".getBytes("utf-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        /* 结尾行 */
        // `"--" + BOUNDARY + "--" + "\r\n"`
        String endLine = "--" + BOUNDARY + "--" + "\r\n";
        try {
            bos.write(endLine.toString().getBytes("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.v("upLoad", "=====formImage====\n" + bos.toString());
        return bos.toByteArray();
    }

    // Content-Type: multipart/form-data; boundary=----------8888888888888
    @Override
    public String getBodyContentType() {
        return MULTIPART_FORM_DATA + "; boundary=" + BOUNDARY;
    }
}
