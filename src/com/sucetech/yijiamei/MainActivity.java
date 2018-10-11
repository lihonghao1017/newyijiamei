package com.sucetech.yijiamei;

import android.Manifest;
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
import android.widget.Toast;

import com.sucetech.yijiamei.bean.FormImage;
import com.sucetech.yijiamei.manager.EventManager;
import com.sucetech.yijiamei.manager.EventStatus;
import com.sucetech.yijiamei.utils.BitmapUtils;
import com.sucetech.yijiamei.utils.PhotoUtils;
import com.sucetech.yijiamei.view.ConBluthView;
import com.sucetech.yijiamei.view.ProgressDailogView;
import com.sucetech.yijiamei.view.ToastView;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class MainActivity extends Activity {
    public ConBluthView mConBluthView;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showContacts();
        initLogs();
        setContentView(R.layout.activity_main);
        mConBluthView = (ConBluthView) findViewById(R.id.connectBloth);
        progressDailogView = (ProgressDailogView) findViewById(R.id.progressDailogView);
//        toastView= (ToastView) findViewById(R.id.myToast);
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
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "没有权限,请手动开启定位权限", Toast.LENGTH_SHORT).show();
            // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 333);
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

}
