package com.negiwang.android.Intro;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.negiwang.android.R;
import com.negiwang.android.helper.M;

import java.util.ArrayList;

public class PermitActivity extends Activity {


    private LinearLayout permit_btn;
    public static Handler MyHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_permit);

        /** 레이아웃 **/
        init();


    }

    public void init(){

        final PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {

                // 동의 완료시 SplashActivity로 이동

                M.setPermitCheck("checked", PermitActivity.this);
                goToSplash();

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {

                // 동의 거부시
                Toast.makeText(PermitActivity.this, "권한이 허용되지 않아 앱을 종료합니다", Toast.LENGTH_SHORT).show();
                finish();

            }


        };


        permit_btn = (LinearLayout) findViewById(R.id.permit_btn);

        permit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 권한 동의 다이얼러그 출력
                TedPermission.with(PermitActivity.this)
                        .setPermissionListener(permissionlistener)
                        //.setPermissions(Manifest.permission.CALL_PHONE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .setPermissions(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .check();

            }
        });


        MyHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    EXITBack = false;
                }
            }
        };
    }

    private static boolean EXITBack = false;



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {


            if (EXITBack == false) {
                Toast.makeText(this, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
                EXITBack = true;
                MyHandler.sendEmptyMessageDelayed(0, 1000 * 2);
                return true;

            }
        }

        return super.onKeyDown(keyCode, event);
    }
    
    public void goToSplash() {

        Intent intent = new Intent(PermitActivity.this, SplashActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);

    }



}
