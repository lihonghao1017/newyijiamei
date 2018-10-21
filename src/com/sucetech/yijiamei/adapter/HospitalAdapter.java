package com.sucetech.yijiamei.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sucetech.yijiamei.R;
import com.sucetech.yijiamei.bean.yiyaunBean;

import java.util.List;

/**
 * Created by admin on 2018/10/14.
 */

public class HospitalAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<yiyaunBean> data;
    public HospitalAdapter(Context context,List<yiyaunBean> data){
        inflater=LayoutInflater.from(context);
        this.data=data;
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder=null;
        if (view==null){
            view=inflater.inflate(R.layout.hospital_item_layout,null);
            viewHolder=new ViewHolder();
            viewHolder.name=view.findViewById(R.id.name);
//            viewHolder.itemLayout=view.findViewById(R.id.itemLayout);
            view.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) view.getTag();
        }
        viewHolder.name.setText(data.get(i).name);
//        viewHolder.itemLayout.setSelected(data.get(i).isSeleted);
        return view;
    }
    class ViewHolder{
        TextView name;
        View itemLayout;
    }
}
