package com.sucetech.yijiamei.view;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.sucetech.yijiamei.R;
import com.sucetech.yijiamei.UserMsg;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by lihh on 2018/10/5.
 */

public class BluthPopView extends PopupWindow {
    private LayoutInflater inflater;
    private ListView mListView;
    private MyAdapter mAdapter;
    private OnSelectedBluthClick clickListener;
    private List<BluetoothDevice> deviceList;

    public BluthPopView(Context context, OnSelectedBluthClick clickListener) {
        super(context);
        inflater = LayoutInflater.from(context);
        Set<BluetoothDevice> devices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        if (devices.size() > 0) {
            deviceList = new ArrayList<>();
            for (BluetoothDevice device : devices) {
                Log.e("LLL", "device.getName()" + device.getName());
                Log.e("LLL", "device.getAddress()" + device.getAddress());
                deviceList.add(device);
//                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
        init();
        this.clickListener = clickListener;
    }

    public void updataData() {
        mAdapter.notifyDataSetChanged();
    }

    private void init() {
        View view = inflater.inflate(R.layout.spiner_window_layout, null);
        setContentView(view);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x000000);
        setBackgroundDrawable(dw);
        mListView = (ListView) view.findViewById(R.id.listview);
        mListView.setAdapter(mAdapter = new MyAdapter());
    }


    private class MyAdapter extends BaseAdapter {
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
            holder.tvName.setText(deviceList.get(position).getName() + "--" + deviceList.get(position).getAddress());
            holder.tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        clickListener.OnBluthItemClick(deviceList.get(position).getAddress());
                    }
                }
            });
            return convertView;
        }
    }

    public interface OnSelectedBluthClick {
        public void OnBluthItemClick(String mac);
    }

    private class ViewHolder {
        private TextView tvName;
    }
}
