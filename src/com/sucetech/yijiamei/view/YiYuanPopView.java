package com.sucetech.yijiamei.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.sucetech.yijiamei.R;
import com.sucetech.yijiamei.UserMsg;
import com.sucetech.yijiamei.bean.WuLiaoBean;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

/**
 * Created by lihh on 2018/10/4.
 */

public class YiYuanPopView extends PopupWindow {
    private LayoutInflater inflater;
    private ListView mListView;
    private MyAdapter mAdapter;
    private OnYiTuanClick clickListener;
    private JSONArray array;

    public YiYuanPopView(Context context, OnYiTuanClick clickListener) {
        super(context);
        inflater = LayoutInflater.from(context);
        String  yiyuan=UserMsg.getYiyuan();
        if (yiyuan!=null&&!yiyuan.equals("")){
            try {
                array=new JSONArray(UserMsg.getYiyuan());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        init();
        this.clickListener = clickListener;
    }
    public void updataData(){
        mAdapter.notifyDataSetChanged();
    }

    private void init() {
        View view = inflater.inflate(R.layout.spiner_window_layout, null);
        setContentView(view);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x00);
        setBackgroundDrawable(dw);
        mListView = (ListView) view.findViewById(R.id.listview);
        mListView.setAdapter(mAdapter = new MyAdapter());
    }


    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return array.length();
        }

        @Override
        public Object getItem(int position) {
            try {
                return array.getJSONObject(position);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.navi_suggist_item_layout, null);
                holder.tvName = (TextView) convertView.findViewById(R.id.text1);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            try {
                holder.tvName.setText(array.getJSONObject(position).getString("name"));
                holder.tvName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (clickListener != null) {
                            try {
                                clickListener.OnYiTuanckItemClick(array.getJSONObject(position).getInt("id"),array.getJSONObject(position).getString("name"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return convertView;
        }
    }

    public interface OnYiTuanClick {
        public void OnYiTuanckItemClick(int id, String str);
    }

    private class ViewHolder {
        private TextView tvName;
    }
}
