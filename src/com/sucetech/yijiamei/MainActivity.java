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
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.sucetech.yijiamei.utils.PhotoUtils;
import com.sucetech.yijiamei.view.ConBluthView;
import com.sucetech.yijiamei.view.ProgressDailogView;
import com.sucetech.yijiamei.view.ToastView;

import java.io.File;

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

    //    private ToastView toastView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mConBluthView = (ConBluthView) findViewById(R.id.connectBloth);
        progressDailogView = (ProgressDailogView) findViewById(R.id.progressDailogView);
//        toastView= (ToastView) findViewById(R.id.myToast);
        isOK();
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
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 32;
                options.outWidth=120;
                options.outHeight=120;
                Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
                if (mConBluthView != null) {
                    mConBluthView.showImages(bitmap, fileUri,pictureId);
                }
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

    /**
     * 自动获取相机权限
     */
    public void autoObtainCameraPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
//                ToastUtils.showShort(this, "您已经拒绝过一次");
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, CAMERA_PERMISSIONS_REQUEST_CODE);
        } else {//有权限直接调用系统相机拍照
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
    }
    private int pictureId;
    public void takePicture(int id){
        pictureId=id;
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
    public void isOK(){
        int osVersion = Integer.valueOf(android.os.Build.VERSION.SDK);
        if (osVersion>22){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                //申请WRITE_EXTERNAL_STORAGE权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO},
                        6666);
            }else{
            }
        }else{
            //如果SDK小于6.0则不去动态申请权限
        }
//        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE)
//                != PackageManager.PERMISSION_GRANTED) {
//            //申请WRITE_EXTERNAL_STORAGE权限
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE},
//                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
//        }else{
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE},
//                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
//        }
    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
////        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
////
////            getImei();
////            Toast.makeText(getApplicationContext(),"授权成功",Toast.LENGTH_SHORT).show();
////        }else{
////            Toast.makeText(getApplicationContext(),"授权拒绝",Toast.LENGTH_SHORT).show();
////        }
//    }
}
