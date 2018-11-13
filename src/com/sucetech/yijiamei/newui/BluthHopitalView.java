package com.sucetech.yijiamei.newui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sucetech.yijiamei.MainActivity;
import com.sucetech.yijiamei.R;
import com.sucetech.yijiamei.UserMsg;
import com.sucetech.yijiamei.adapter.BluthAdapter;
import com.sucetech.yijiamei.adapter.HospitalAdapter;
import com.sucetech.yijiamei.bean.WuiaoYiyuanBean;
import com.sucetech.yijiamei.bean.yiyaunBean;
import com.sucetech.yijiamei.manager.EventManager;
import com.sucetech.yijiamei.manager.EventStatus;
import com.sucetech.yijiamei.view.BaseView;
import com.sucetech.yijiamei.view.Bluetooth_Scale;
import com.sucetech.yijiamei.view.ConBluthView;
import com.zxing.activity.CaptureActivity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by admin on 2018/10/13.
 */

public class BluthHopitalView extends BaseView implements View.OnClickListener, AdapterView.OnItemClickListener {
    private View userMsg, erweimaIcon, bluthIcon;
    private ListView hospitalList;
    private HospitalAdapter hospitalAdapter;
    private List<yiyaunBean> yiyuanData;
    private TextView weightStr;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice device;
    private Bluetooth_Scale mBl_Scale;
    private Handler mHandler;
    private ConBluthView.SCALENOW scalenow = new ConBluthView.SCALENOW();
    private ListView boluthList;
    private View boluthListLayout, boluthListUpdataLayout;
    private List<BluetoothDevice> deviceList;
    private String bluthMac;
    private BluthAdapter bluthAdapter;
    private BluetoothReceiver mBluetoothReceiver;

    public BluthHopitalView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WuiaoYiyuanBean wuiaoYiyuanBean;

    @Override
    public void updata(EventStatus status, Object obj) {
        switch (status) {
            case hospitalData:
                if (obj != null) {
                    if (UserMsg.getMac() != null && !UserMsg.getMac().equals("")) {
                        startBlouth(UserMsg.getMac());
                    }
                    wuiaoYiyuanBean = (WuiaoYiyuanBean) obj;
                    this.setVisibility(View.VISIBLE);
                    yiyuanData.addAll(wuiaoYiyuanBean.yiyaunBeanList);
                    hospitalAdapter.notifyDataSetChanged();
                }

                break;
        }

    }

    @Override
    public void initView(Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.bluthhospital_layout, null);
        userMsg = v.findViewById(R.id.userMsg);
        erweimaIcon = v.findViewById(R.id.erweimaIcon);
        bluthIcon = v.findViewById(R.id.bluthLayout);
        userMsg.setOnClickListener(this);
        erweimaIcon.setOnClickListener(this);
        bluthIcon.setOnClickListener(this);
        hospitalList = v.findViewById(R.id.hospitalList);
        hospitalList.setOnItemClickListener(this);
        yiyuanData = new ArrayList<>();
        hospitalAdapter = new HospitalAdapter(getContext(), yiyuanData);
        hospitalList.setAdapter(hospitalAdapter);
        this.addView(v);
        weightStr = v.findViewById(R.id.weightStr);
        weightStr.setOnClickListener(this);
        boluthList = v.findViewById(R.id.boluthList);
        boluthList.setOnItemClickListener(this);
        boluthListLayout = v.findViewById(R.id.boluthListLayout);
        boluthListUpdataLayout = v.findViewById(R.id.boluthListUpdataLayout);
        boluthListUpdataLayout.setOnClickListener(this);
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        Log.e("LLL", "1111");
                        ((MainActivity) getContext()).hideProgressDailogView();
                        Bundle bundle = msg.getData();
                        GetWeight(bundle.getByteArray("weight"));
                        if (scalenow.bOverFlag) {
                            ;
                        } else {
                            String wei = scalenow.sformatNetWeight.trim();
                            if (wei.contains("+")) {
                                wei = wei.replace("+", "");
                            }
                            wei = wei.trim();

                            weightStr.setText(wei + " KG");
                            mEventManager.notifyObservers(EventStatus.weight, wei);
                        }
                        weightStr.setVisibility(View.VISIBLE);
                        boluthListLayout.setVisibility(View.GONE);
                        break;
                    case 2:
                        if (msg.arg1 == 0) {
                            Toast.makeText(getContext(), "蓝牙链接失败", Toast.LENGTH_SHORT).show();
                        } else if (msg.arg1 == 1) {
                            Toast.makeText(getContext(), "蓝牙链接断开", Toast.LENGTH_SHORT).show();
                        } else if (msg.arg1 == 2) {
                            UserMsg.saveMac(bluthMac);
                            Toast.makeText(getContext(), "蓝牙链接成功", Toast.LENGTH_SHORT).show();
                        }
                        Log.e("LLL", "2222");

                        ((MainActivity) getContext()).hideProgressDailogView();

                        weightStr.setVisibility(View.GONE);
                        boluthListLayout.setVisibility(View.VISIBLE);
//                        if (msg.arg1 < 2) {
//                            weidthStr.setText("----");
//                            b_scaleIsConnect = Boolean.FALSE;
//                            speedLayout.setEnabled(true);
//                            speedLayout.setBackgroundResource(R.drawable.nav_ic_speed_limit_g);
//                        } else b_scaleIsConnect = Boolean.TRUE;
                        break;
                    case 3:
                        connectBluth((String) msg.obj);
                        break;

                }
            }
        };
        mBl_Scale = new Bluetooth_Scale(getContext(), mHandler);
        initBluth();
    }

    private void initBluth() {

        deviceList = new ArrayList<>();
        bluthAdapter = new BluthAdapter(getContext(), deviceList);
        boluthList.setAdapter(bluthAdapter);
        initBluthBoundData();
    }

    private void initBluthBoundData() {
        deviceList.clear();
        Set<BluetoothDevice> devices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        if (devices.size() > 0) {
            for (BluetoothDevice device : devices) {
                deviceList.add(device);
            }
            bluthAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.erweimaIcon:
                Intent openCameraIntent = new Intent(getContext(), CaptureActivity.class);
                ((MainActivity) getContext()).startActivityForResult(openCameraIntent, R.id.speedLayout);
                break;
            case R.id.bluthLayout:
                bluthIcon.setSelected(!bluthIcon.isSelected());
                if (bluthIcon.isSelected()) {
                    boluthListLayout.setVisibility(View.VISIBLE);
                } else {
                    boluthListLayout.setVisibility(View.GONE);
                }

                break;
            case R.id.boluthListUpdataLayout:
                findBluthDevice();
                break;

        }

    }

    public void onDestroy() {
        if (mBl_Scale != null) mBl_Scale.stop();
        System.exit(0);
        if (mBluetoothReceiver != null) {
            this.getContext().unregisterReceiver(mBluetoothReceiver);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView.getId() == R.id.boluthList) {
            BluetoothDevice device = deviceList.get(i);
            if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                String mac = deviceList.get(i).getAddress();
                startBlouth(mac);
            } else {
                try {
                    createBond(BluetoothDevice.class, device);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(getContext(), "kkkk", Toast.LENGTH_SHORT).show();
            }

        } else {
            for (yiyaunBean bean : yiyuanData) {
                bean.isSeleted = false;
            }
            yiyuanData.get(i).isSeleted = true;
            hospitalAdapter.notifyDataSetChanged();
            EventManager.getEventManager().notifyObservers(EventStatus.selectedHos, yiyuanData.get(i));
            EventManager.getEventManager().notifyObservers(EventStatus.wuliaoType, wuiaoYiyuanBean.wuliaoTypes);

        }

    }

    public void startBlouth(String blouth) {
        ((MainActivity) getContext()).showProgressDailogView("蓝牙链接中...");
        this.setVisibility(View.VISIBLE);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(getContext(), "此设备不支持蓝牙", Toast.LENGTH_SHORT).show();
            ((MainActivity) getContext()).hideProgressDailogView();
            return;
        }
        connectBluth(blouth);
    }

    private boolean stringIsMac(String val) {
        String trueMacAddress = "([A-Fa-f0-9]{2}-){5}[A-Fa-f0-9]{2}";
        if (val.matches(trueMacAddress)) {
            return true;
        } else {
            return false;
        }
    }

    private void connectBluth(String bluth) {
        bluthMac = bluth;
        if (mBluetoothAdapter.enable()) {
            try {
                device = mBluetoothAdapter.getRemoteDevice(bluth);
                if (device != null) {
                    if (!mBl_Scale.connect(device)) {
                        Message message = Message.obtain();
                        message.what = 3;
                        message.obj = bluth;
                        mHandler.sendMessageDelayed(message, 1000);
                    }
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), "二维码格式异常-->" + bluth, Toast.LENGTH_SHORT).show();
                ((MainActivity) getContext()).hideProgressDailogView();
            }
        } else {
            Message message = Message.obtain();
            message.what = 3;
            message.obj = bluth;
            mHandler.sendMessageDelayed(message, 1000);
        }
    }

    void GetWeight(byte[] databuf) {
        int i, j, offset = 6;
        boolean StartFalg = false;
        scalenow.bZeroFlag = true;
        scalenow.bOverFlag = false;
        scalenow.bWeiStaFlag = false;
        switch (databuf[0]) {
            case 'o':
            case 'O':
                scalenow.bOverFlag = true;
                break;
            case 'u':
            case 'U':
                scalenow.bWeiStaFlag = false;
                offset = 6;    //6
                break;
            case 's':
            case 'S':
                scalenow.bWeiStaFlag = true;
                break;
        }
        if (databuf[5] == '-') offset = 5;
        for (i = 0; i < 14; i++) {
            if (databuf[i + offset] == '\'') databuf[i + offset] = '.';
            if (StartFalg) {
                if (((databuf[i + offset] > '9') || (databuf[i + offset] < '.')) && (!((databuf[i + offset] == ' ') && (databuf[i + offset + 1] <= '9')))) {
                    break;
                }
            } else if ((databuf[i + offset] >= '0') && (databuf[i + offset] <= '9')) {
                StartFalg = true;
                if (databuf[i + offset] != '0') scalenow.bZeroFlag = false;
            }
        }
        scalenow.sformatNetWeight = new String(databuf, offset, i);


        for (j = 0; j < 6; j++) {
            if (databuf[i + j + offset] < 0x20) {
                break;
            }
        }
        scalenow.sUnit = new String(databuf, i + offset, j);
    }

//    @Override
//    public void OnBluthItemClick(String mac) {
//        startBlouth(mac);
////        bluthPopView.dismiss();
//    }

    public static class SCALENOW {
        public String sformatNetWeight = "0";
        public String sUnit = "0";
        public boolean bWeiStaFlag;
        public boolean bZeroFlag;
        public boolean bOverFlag;
    }

    private void findBluthDevice() {
        if (mBluetoothReceiver == null) registerBluthBroadCast();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        initBluthBoundData();
        if (mBluetoothAdapter.enable()) {
            mBluetoothAdapter.startDiscovery();
        }

    }

    private void registerBluthBroadCast() {
        mBluetoothReceiver = new BluetoothReceiver();
// 动态注册注册广播接收器。接收蓝牙发现讯息
        IntentFilter btFilter = new IntentFilter();
        btFilter.setPriority(1000);
        btFilter.addAction(BluetoothDevice.ACTION_FOUND);
        btFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        this.getContext().registerReceiver(mBluetoothReceiver, btFilter);
    }

    private boolean createBond(Class btClass, BluetoothDevice btDevice)
            throws Exception {
        Method createBondMethod = btClass.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }

    class BluetoothReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction(); //得到action
            Log.e("action1=", action);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {  //发现设备
                BluetoothDevice btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
               if (btDevice.getName()!=null&&!btDevice.getName().equals("")){
                   deviceList.add(btDevice);
                   bluthAdapter.notifyDataSetChanged();
               }
            }else if(BluetoothDevice.ACTION_CLASS_CHANGED .equals(action)){
                Toast.makeText(getContext(), "ACTION_CLASS_CHANGED", Toast.LENGTH_SHORT).show();
                BluetoothDevice btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                startBlouth(btDevice.getAddress());
            }else if(BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)){
                BluetoothDevice btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                try {
                    Method setPairingConfirmation = btDevice.getClass().getDeclaredMethod("setPairingConfirmation",boolean.class);
                    setPairingConfirmation.invoke(btDevice,true);
                    abortBroadcast();//如果没有将广播终止，则会出现一个一闪而过的配对框。
                    Method removeBondMethod = btDevice.getClass().getDeclaredMethod("setPin",
                            new Class[]
                                    {byte[].class});
                    Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice,
                            new Object[]
                                    {"1234".getBytes()});
                    Log.e("returnValue", "" + returnValue);

                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

            }
        }
    }

}
