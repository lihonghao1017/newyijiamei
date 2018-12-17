package com.sucetech.yijiamei;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.sucetech.yijiamei.bean.FormImage;
import com.sucetech.yijiamei.manager.EventManager;
import com.sucetech.yijiamei.manager.EventStatus;
import com.sucetech.yijiamei.newui.AddMaterielView;
import com.sucetech.yijiamei.newui.BluthHopitalView;
import com.sucetech.yijiamei.newui.CommitView;
import com.sucetech.yijiamei.newui.MakeSureDialog;
import com.sucetech.yijiamei.newui.RecoveryMaterirView;
import com.sucetech.yijiamei.utils.BitmapUtils;
import com.sucetech.yijiamei.utils.PhotoUtils;
import com.sucetech.yijiamei.view.LoginView;
import com.sucetech.yijiamei.view.ProgressDailogView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class MainActivity extends Activity {
    private CommitView mCommitView;
    public BluthHopitalView mConBluthView;
    private LoginView loginView;
    private AddMaterielView addMaterielView;
    private RecoveryMaterirView recoveryMaterirView;
    private ProgressDailogView progressDailogView;
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    private static final int CAMERA_PERMISSIONS_REQUEST_CODE = 0x03;
    private static final int STORAGE_PERMISSIONS_REQUEST_CODE = 0x04;
    private File fileUri;
    private Uri imageUri;
    public final OkHttpClient client = new OkHttpClient().newBuilder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .build();
    TextView locationg;
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showContacts();
         getPersimmions();
        initLogs();
        setContentView(R.layout.activity_main);
        mConBluthView = (BluthHopitalView) findViewById(R.id.connectBloth);
        progressDailogView = (ProgressDailogView) findViewById(R.id.progressDailogView);
         loginView=(LoginView) findViewById(R.id.loginView);
         addMaterielView=(AddMaterielView) findViewById(R.id.addMaterielView);;
         recoveryMaterirView=(RecoveryMaterirView) findViewById(R.id.recoveryMaterirView);;
         locationg= (TextView) findViewById(R.id.locationg);
         mCommitView=findViewById(R.id.commitView);

         Window window = this.getWindow();
         //取消状态栏透明
         window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
         //添加Flag把状态栏设为可绘制模式
         window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
         //设置状态栏颜色
         window.setStatusBarColor(0xff07923A);
    }

    private File creatFile() {
        File iifile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/test/" + System.currentTimeMillis() + ".jpg");
        iifile.getParentFile().mkdirs();
        return iifile;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //处理扫描结果（在界面上显示）
        if (resultCode == RESULT_OK) {
            if (requestCode == R.id.speedLayout) {
                Bundle bundle = data.getExtras();
                String scanResult = bundle.getString("result");
                mConBluthView.startBlouth(scanResult);
            } else if(requestCode == R.id.saoyisaoLayout){
                Bundle bundle = data.getExtras();
                String scanResult = bundle.getString("result");
                mCommitView.setDanjuHao(scanResult);
            }   else if (requestCode == CODE_CAMERA_REQUEST) {
                String newFile = fileUri.getParent() + "yijiamei_" + fileUri.getName();
                Bitmap bitmap = BitmapUtils.getSmallBitmap(fileUri.getPath(), 680, 680, new File(newFile));
                FormImage formImage = new FormImage();
                formImage.mBitmap = bitmap;
                formImage.mFileName = newFile;
                formImage.id = id;
                EventManager.getEventManager().notifyObservers(EventStatus.imgMsg, formImage);
            } else if (requestCode == CODE_RESULT_REQUEST) {
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mConBluthView != null) {
            mConBluthView.onDestroy();
        }
    }

    public void showProgressDailogView(String des) {
        progressDailogView.setVisibility(View.VISIBLE);
        progressDailogView.setDes(des);
    }

    public void hideProgressDailogView() {
        progressDailogView.setVisibility(View.GONE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            //调用系统相机申请拍照权限回调
            case CAMERA_PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (hasSdcard()) {
                        imageUri = Uri.fromFile(fileUri);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            imageUri = FileProvider.getUriForFile(MainActivity.this, "com.zz.fileprovider", fileUri);//通过FileProvider创建一个content类型的Uri
                        PhotoUtils.takePicture(this, imageUri, CODE_CAMERA_REQUEST);
                    } else {
                        Toast.makeText(this, "设备没有SD卡！", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "请允许打开相机！！", Toast.LENGTH_LONG).show();
                }
                break;


            }
            //调用系统相册申请Sdcard权限回调
            case STORAGE_PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    PhotoUtils.openPic(this, CODE_GALLERY_REQUEST);
                } else {
                    Toast.makeText(this, "请允许打操作SDCard！！", Toast.LENGTH_LONG).show();
                }
                break;
            default:
        }
    }

    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    private int id;

    public void requestPicture(int id) {
        this.id = id;
        if (hasSdcard()) {
            fileUri = creatFile();
            imageUri = Uri.fromFile(fileUri);
            //通过FileProvider创建一个content类型的Uri
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                imageUri = FileProvider.getUriForFile(MainActivity.this, "com.sucetech.yijiamei", fileUri);
            }
            PhotoUtils.takePicture(this, imageUri, CODE_CAMERA_REQUEST);
        } else {
            Toast.makeText(this, "设备没有SD卡！", Toast.LENGTH_LONG).show();
        }
    }

    public void showContacts() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "没有权限,请手动开启定位权限", Toast.LENGTH_SHORT).show();
            // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO}, 333);
        } else {
            //   init();
            // getLoc();
        }
    }

    public static void initLogs() {//导航相关全日制

        String sdcardPath = "/sdcard/yijiamei/";
        File file = new File(sdcardPath);

        if (!file.exists()) {
            //通过file的mkdirs()方法创建目录中包含却不存在的文件夹
            file.mkdirs();
        }
        try {
            Time t = new Time();
            t.setToNow();
            String time = t.monthDay + "d" + t.hour + "h" + t.minute + "m"
                    + t.second;
            String logPath = sdcardPath + time + ".txt";
            Runtime.getRuntime().exec("logcat -v long -f " + logPath);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    private String permissionInfo;
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            /***
             * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
             */
            // 定位精确位置
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
			/*
			 * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
			 */
            // 读写权限
            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
            // 读取电话状态权限
            if (addPermission(permissions, Manifest.permission.READ_PHONE_STATE)) {
                permissionInfo += "Manifest.permission.READ_PHONE_STATE Deny \n";
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), 777);
            }
        }
    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (shouldShowRequestPermissionRationale(permission)){
                return true;
            }else{
                permissionsList.add(permission);
                return false;
            }

        }else{
            return true;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){

            if(mCommitView.getVisibility()==View.VISIBLE){
                mCommitView.onkeyDown();
                return true;
            }
            if (recoveryMaterirView.getVisibility()==View.VISIBLE){
                recoveryMaterirView.onkeyDown();
                return true;
            }
            if (addMaterielView.getVisibility()==View.VISIBLE){
                addMaterielView.onkeyDown();
                return true;
            }
            if (mConBluthView.getVisibility()==View.VISIBLE||loginView.getVisibility()==View.VISIBLE){
                showExit();
                return true;
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private void showExit(){
        MakeSureDialog dialog = new MakeSureDialog();
//        dialog.setContent("确认删除吗？");
        dialog.setDialogClickListener(new MakeSureDialog.onDialogClickListener() {
            @Override
            public void onSureClick() {
                finish();
            }

            @Override
            public void onCancelClick() {
            }
        });
        dialog.show(getFragmentManager(),"");
    }
}
