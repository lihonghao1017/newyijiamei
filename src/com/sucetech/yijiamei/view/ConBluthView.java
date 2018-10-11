package com.sucetech.yijiamei.view;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sucetech.yijiamei.MainActivity;
import com.sucetech.yijiamei.R;
import com.sucetech.yijiamei.UserMsg;
import com.sucetech.yijiamei.manager.EventStatus;
import com.zxing.activity.CaptureActivity;

import java.util.Set;

/**
 * Created by lihh on 2018/9/18.
 */

public class ConBluthView extends BaseView implements View.OnClickListener, BluthPopView.OnSelectedBluthClick {
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice device;
    private Bluetooth_Scale mBl_Scale;
    private Handler mHandler;
    private SCALENOW scalenow = new SCALENOW();
    private Boolean b_scaleIsConnect = Boolean.FALSE;
    private Boolean bisClosed = false;
    public TextView weidthStr;
    private View speedLayout;
    private View selectBluth;
    private BluthPopView bluthPopView;

    public ConBluthView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ConBluthView(Context context) {
        super(context);
    }

    @Override
    public void initView(Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.connect_bluth_layout, null);
        weidthStr = (TextView) v.findViewById(R.id.weidthStr);
        speedLayout = v.findViewById(R.id.speedLayout);
        speedLayout.setOnClickListener(this);
        selectBluth = v.findViewById(R.id.selectBluth);
        selectBluth.setOnClickListener(this);
        addView(v, -1, -1);
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        ((MainActivity) getContext()).hideProgressDailogView();
                        Bundle bundle = msg.getData();
                        GetWeight(bundle.getByteArray("weight"));
                        if (scalenow.bOverFlag) {
                            weidthStr.setText("----");
                        } else {
                            String wei = scalenow.sformatNetWeight.trim();
                            if(wei.contains("+")){
                                wei=wei.replace("+","");
                            }
                            wei = wei.trim();

                            weidthStr.setText(wei);
                            mEventManager.notifyObservers(EventStatus.weight, wei);
                        }
                        speedLayout.setBackgroundResource(R.drawable.nav_ic_speed_limit);
                        speedLayout.setEnabled(false);
                        selectBluth.setVisibility(View.GONE);
                        break;
                    case 2:
                        ((MainActivity) getContext()).hideProgressDailogView();
                        Toast.makeText(getContext(), "蓝牙链接失败", Toast.LENGTH_SHORT).show();
                        if (msg.arg1 < 2) {
                            weidthStr.setText("----");
                            b_scaleIsConnect = Boolean.FALSE;
                            speedLayout.setEnabled(true);
                            speedLayout.setBackgroundResource(R.drawable.nav_ic_speed_limit_g);
                        } else b_scaleIsConnect = Boolean.TRUE;
                        break;
                    case 3:
                        connectBluth((String) msg.obj);
                        break;

                }
            }
        };
        mBl_Scale = new Bluetooth_Scale(getContext(), mHandler);
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
        if (mBluetoothAdapter.enable()) {
            try {
                device = mBluetoothAdapter.getRemoteDevice(bluth);
                if (device != null) {
                    mBl_Scale.connect(device);
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


    @Override
    public void updata(EventStatus status, Object obj) {
        switch (status) {
            case logined:
                this.setVisibility(View.VISIBLE);
                if (UserMsg.getMac() != null && !UserMsg.getMac().equals("")) {
                    startBlouth(UserMsg.getMac());
                } else {
                    selectBluth.setVisibility(View.VISIBLE);
                }
                break;
            case loginFail:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.speedLayout:
                Intent openCameraIntent = new Intent(getContext(), CaptureActivity.class);
                ((MainActivity) getContext()).startActivityForResult(openCameraIntent, R.id.speedLayout);
                break;
            case R.id.selectBluth:
                Set<BluetoothDevice> devices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
                if (devices.size() > 0) {
                    bluthPopView = new BluthPopView(getContext(), this);
                    bluthPopView.setWidth(this.getWidth());
                    bluthPopView.showAsDropDown(this, 0, 0);

                } else {
                    Toast.makeText(getContext(), "请先手动配对", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    public void onDestroy() {
        if (mBl_Scale != null) mBl_Scale.stop();
        bisClosed = true;
        System.exit(0);
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

    @Override
    public void OnBluthItemClick(String mac) {
        startBlouth(mac);
        bluthPopView.dismiss();
    }

    public static class SCALENOW {
        public String sformatNetWeight = "0";
        public String sUnit = "0";
        public boolean bWeiStaFlag;
        public boolean bZeroFlag;
        public boolean bOverFlag;
    }

}
