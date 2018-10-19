package com.sucetech.yijiamei.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sucetech.yijiamei.R;

import java.util.List;

/**
 * Created by lihh on 2018/10/15.
 */

public class BluthAdapter extends BaseAdapter{
    private List<BluetoothDevice> deviceList;
    private LayoutInflater inflater;

    public BluthAdapter(Context context, List<BluetoothDevice> deviceList){
        inflater=LayoutInflater.from(context);
        this.deviceList=deviceList;
    }
    @Override
    public int getCount() {
        return deviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return deviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.bluth_item_layout, null);
            holder.name = (TextView) convertView.findViewById(R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setText(deviceList.get(position).getName() + "--" + deviceList.get(position).getAddress());
        return convertView;
    }
    class ViewHolder{
        TextView name;

    }
}
