package com.qin.imagezxlingdemo;


import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.king.zxing.CaptureActivity;
import com.qin.imagezxlingdemo.util.StatusBarUtils;

public class EasyCaptureActivity  extends CaptureActivity {

    @Override
    public int getLayoutId() {
        return R.layout.easy_capture_activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        Toolbar toolbar = findViewById(R.id.toolbar);
        StatusBarUtils.immersiveStatusBar(this,toolbar,0.2f);
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(getIntent().getStringExtra(MainActivity.KEY_TITLE));
        getCaptureHelper()
//                .decodeFormats(DecodeFormatManager.QR_CODE_FORMATS)//设置只识别二维码会提升速度
                .playBeep(true)
                .vibrate(true);
    }



    public void onClick(View v){
        switch (v.getId()){
            case R.id.ivLeft:
                onBackPressed();
                break;
        }
    }


}
