package com.qin.imagezxlingdemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.king.zxing.Intents;
import com.king.zxing.util.CodeUtils;
import com.qin.imagezxlingdemo.util.UriUtils;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{



    public static final String KEY_TITLE = "key_title";
    public static final String KEY_IS_QR_CODE = "key_code";
    public static final String KEY_IS_CONTINUOUS = "key_continuous_scan";

    public static final int REQUEST_CODE_SCAN = 0X01;
    public static final int REQUEST_CODE_PHOTO = 0X02;

    public static final int RC_CAMERA = 0X01;

    public static final int RC_READ_PHOTO = 0X02;

    private Class<?> cls;
    private String title;
    private boolean isContinuousScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    public void onClick(View v){
        isContinuousScan = false;
        switch (v.getId()){
            case R.id.btn0:
                this.cls = EasyCaptureActivity.class;
                this.title = ((Button)v).getText().toString();
                checkCameraPermissions();
                break;

        }

    }

    /*
    *
    * 回调显示返回的结果
    * */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data!=null){
            switch (requestCode){
                case REQUEST_CODE_SCAN:
                    String result = data.getStringExtra(Intents.Scan.RESULT);
                    Toast.makeText(this,result,Toast.LENGTH_SHORT).show();
                    break;
                case REQUEST_CODE_PHOTO:
                    parsePhoto(data);
                    break;
            }

        }
    }

    private void parsePhoto(Intent data){
        final String path = UriUtils.INSTANCE.getImagePath(this,data);
        Log.d("Jenly","path:" + path);
        if(TextUtils.isEmpty(path)){
            return;
        }
        //异步解析
        asyncThread(new Runnable() {
            @Override
            public void run() {
                final String result = CodeUtils.parseCode(path);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Jenly","result:" + result);
                        Toast.makeText(getContext(),result,Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }

    private void asyncThread(Runnable runnable){
        new Thread(runnable).start();
    }

    private Context getContext(){
        return this;
    }


    /**
     * 检测拍摄权限
     */
    @AfterPermissionGranted(RC_CAMERA)
    private void checkCameraPermissions(){
        String[] perms = {Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {//有权限
            startScan(cls,title);
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.permission_camera),
                    RC_CAMERA, perms);
        }
    }
    /*
    *
    * 检测读写权限
    * */
    @AfterPermissionGranted(RC_READ_PHOTO)
    private void checkExternalStoragePermissions(){
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {//有权限
            startPhotoCode();
        }else{
            EasyPermissions.requestPermissions(this, getString(R.string.permission_external_storage),
                    RC_READ_PHOTO, perms);
        }
    }

    /*
    * easypermisson 方法重写
    *
    * */

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Some permissions have been granted

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Some permissions have been denied
        // ...
    }

    /**
     * 扫码
     * @param cls
     * @param title
     */
    private void startScan(Class<?> cls,String title){
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeCustomAnimation(this,R.anim.in,R.anim.out);
        Intent intent = new Intent(this, cls);
        intent.putExtra(KEY_TITLE,title);
        intent.putExtra(KEY_IS_CONTINUOUS,isContinuousScan);
        ActivityCompat.startActivityForResult(this,intent,REQUEST_CODE_SCAN,optionsCompat.toBundle());
    }

    /*
    * 生成二维码
    *
    * */
    private void startPhotoCode(){
        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(pickIntent, REQUEST_CODE_PHOTO);
    }


}
