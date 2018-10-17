package com.sucetech.yijiamei;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;

import com.sucetech.yijiamei.bean.FormImage;
import com.sucetech.yijiamei.manager.EventManager;
import com.sucetech.yijiamei.manager.EventStatus;
import com.sucetech.yijiamei.newui.BluthHopitalView;
import com.sucetech.yijiamei.utils.BitmapUtils;
import com.sucetech.yijiamei.utils.PhotoUtils;
import com.sucetech.yijiamei.view.ConBluthView;
import com.sucetech.yijiamei.view.ProgressDailogView;
import com.sucetech.yijiamei.view.ToastView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class MainActivity extends Activity {
    public BluthHopitalView mConBluthView;
    private ProgressDailogView progressDailogView;
    private int REQUEST_CAMERA = 0x001;
    private File file;
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    private static final int CAMERA_PERMISSIONS_REQUEST_CODE = 0x03;
    private static final int STORAGE_PERMISSIONS_REQUEST_CODE = 0x04;
    private File fileUri;
    private File fileCropUri;
    private Uri imageUri;
    private Uri cropImageUri;
    private static final int OUTPUT_X = 480;
    private static final int OUTPUT_Y = 480;
    public final OkHttpClient client = new OkHttpClient().newBuilder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .build();

    //    private ToastView toastView;
    TextView locationg;
    private LocationService locationService;
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showContacts();
         getPersimmions();
        initLogs();
        setContentView(R.layout.activity_main);
        mConBluthView = (BluthHopitalView) findViewById(R.id.connectBloth);
        progressDailogView = (ProgressDailogView) findViewById(R.id.progressDailogView);
//        toastView= (ToastView) findViewById(R.id.myToast);
         locationg= (TextView) findViewById(R.id.locationg);


         Window window = this.getWindow();
         //取消状态栏透明
         window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
         //添加Flag把状态栏设为可绘制模式
         window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
         //设置状态栏颜色
         window.setStatusBarColor(0xff07923A);
    }
    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        // -----------location config ------------
        locationService = ((App) getApplication()).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);
        //注册监听

        LocationClientOption mOption = new LocationClientOption();
        mOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        mOption.setScanSpan(5000);
        mOption.setCoorType("bd09ll");
        mOption.setIsNeedAddress(true);
        mOption.setOpenGps(true);
        int type = getIntent().getIntExtra("from", 0);
        if (type == 0) {
            locationService.setLocationOption(mOption);
        } else if (type == 1) {
            locationService.setLocationOption(locationService.getOption());
        }
        locationService.start();// 定位SDK
//        startLocation.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                if (startLocation.getText().toString().equals(getString(R.string.startlocation))) {
//                    locationService.start();// 定位SDK
//                    // start之后会默认发起一次定位请求，开发者无须判断isstart并主动调用request
//                    startLocation.setText(getString(R.string.stoplocation));
//                } else {
//                    locationService.stop();
//                    startLocation.setText(getString(R.string.startlocation));
//                }
//            }
//        });
    }
    /*****
     *
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                final StringBuffer sb = new StringBuffer(256);
                sb.append("time : ");
                /**
                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
                 */
                sb.append(location.getTime());
                sb.append("\nlocType : ");// 定位类型
                sb.append(location.getLocType());
                sb.append("\nlocType description : ");// *****对应的定位类型说明*****
                sb.append(location.getLocTypeDescription());
                sb.append("\nlatitude : ");// 纬度
                sb.append(location.getLatitude());
                sb.append("\nlontitude : ");// 经度
                sb.append(location.getLongitude());
                sb.append("\nradius : ");// 半径
                sb.append(location.getRadius());
                sb.append("\nCountryCode : ");// 国家码
                sb.append(location.getCountryCode());
                sb.append("\nCountry : ");// 国家名称
                sb.append(location.getCountry());
                sb.append("\ncitycode : ");// 城市编码
                sb.append(location.getCityCode());
                sb.append("\ncity : ");// 城市
                sb.append(location.getCity());
                sb.append("\nDistrict : ");// 区
                sb.append(location.getDistrict());
                sb.append("\nStreet : ");// 街道
                sb.append(location.getStreet());
                sb.append("\naddr : ");// 地址信息
                sb.append(location.getAddrStr());
                sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
                sb.append(location.getUserIndoorState());
                sb.append("\nDirection(not all devices have value): ");
                sb.append(location.getDirection());// 方向
                sb.append("\nlocationdescribe: ");
                sb.append(location.getLocationDescribe());// 位置语义化信息
                sb.append("\nPoi: ");// POI信息
                if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
                    for (int i = 0; i < location.getPoiList().size(); i++) {
                        Poi poi = (Poi) location.getPoiList().get(i);
                        sb.append(poi.getName() + ";");
                    }
                }
                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    sb.append("\nspeed : ");
                    sb.append(location.getSpeed());// 速度 单位：km/h
                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());// 卫星数目
                    sb.append("\nheight : ");
                    sb.append(location.getAltitude());// 海拔高度 单位：米
                    sb.append("\ngps status : ");
                    sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    // 运营商信息
                    if (location.hasAltitude()) {// *****如果有海拔高度*****
                        sb.append("\nheight : ");
                        sb.append(location.getAltitude());// 单位：米
                    }
                    sb.append("\noperationers : ");// 运营商信息
                    sb.append(location.getOperators());
                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                }
//                logMsg(sb.toString());
                if (sb.toString() != null&&!sb.toString().equals("")) {
                    locationg.post(new Runnable() {
                        @Override
                        public void run() {
                            locationg.setText(sb.toString());
                        }
                    });
                }
            }
        }

    };

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
                UserMsg.saveMac(scanResult);
                mConBluthView.startBlouth(scanResult);
            } else if (requestCode == CODE_CAMERA_REQUEST) {
//                Bitmap bitmap = PhotoUtils.getBitmapFromUri(imageUri, this);
                String newFile = fileUri.getParent() + "yijiamei_" + fileUri.getName();
                Bitmap bitmap = BitmapUtils.getSmallBitmap(fileUri.getPath(), 480, 480, new File(newFile));
//                img_src.setImageBitmap(smallBitmap);
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inSampleSize = 32;
//                options.outWidth = 120;
//                options.outHeight = 120;
//                Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
                FormImage formImage = new FormImage();
                formImage.mBitmap = bitmap;
                formImage.mFileName = newFile;
                formImage.id = id;
//                if (mConBluthView != null) {
//                    mConBluthView.showImages(bitmap, fileUri,id);
//                }
                EventManager.getEventManager().notifyObservers(EventStatus.imgMsg, formImage);
//                fileCropUri=creatFile();
//                cropImageUri = Uri.fromFile(fileCropUri);
//                PhotoUtils.cropImageUri(this, imageUri, cropImageUri, 1, 1, OUTPUT_X, OUTPUT_Y, CODE_RESULT_REQUEST);
            } else if (requestCode == CODE_RESULT_REQUEST) {
                Log.e("LLL", "-----requestCode--cropImageUri--" + cropImageUri.toString());
//                    Bitmap bitmap = PhotoUtils.getBitmapFromUri(cropImageUri, this);
//                    if (mConBluthView != null) {
//                        mConBluthView.showImages(bitmap);
//                    }
            }
        }

    }
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onStop();
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

}
