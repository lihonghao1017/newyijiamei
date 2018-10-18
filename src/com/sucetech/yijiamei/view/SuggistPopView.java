package com.sucetech.yijiamei.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.sucetech.yijiamei.R;
import com.sucetech.yijiamei.bean.WuLiaoBean;

import java.util.List;

/**
 * Created by lihh on 2018/9/20.
 */

public class SuggistPopView extends PopupWindow {
    private LayoutInflater inflater;
    private GridView mListView;
    private List<WuLiaoBean> list;
    private MyAdapter mAdapter;
    private OnSuggistItemClick clickListener;

    public SuggistPopView(Context context, List<WuLiaoBean> list, OnSuggistItemClick clickListener) {
        super(context);
        inflater = LayoutInflater.from(context);
        this.list=list;
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
        mListView = (GridView) view.findViewById(R.id.listview);
        mListView.setAdapter(mAdapter = new MyAdapter());
        view.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }


    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
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
            holder.tvName.setText(list.get(position).name);
            holder.tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        clickListener.onSuggistItemClick(position,list.get(position).name);
                    }
                }
            });

            return convertView;
        }
    }

    public interface OnSuggistItemClick {
        public void onSuggistItemClick(int position, String str);
    }

    private class ViewHolder {
        private TextView tvName;
    }
}
