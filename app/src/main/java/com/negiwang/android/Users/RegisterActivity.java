package com.negiwang.android.Users;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.negiwang.android.App.AppConst;
import com.negiwang.android.App.AppController;
import com.negiwang.android.App.GlobalFunctions;
import com.negiwang.android.R;
import com.negiwang.android.helper.M;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


public class RegisterActivity extends Activity implements View.OnClickListener {
    String mb_id, mb_email, mb_password, mb_name, mb_nick, mb_hp, mb_passwordConfirm;
    EditText input_signupid, input_signuppw, input_signuppassword, input_signupname;
    Button signup_cancle;
    TextView signup;
    public static Handler MyHandler;
    private String selectedImagePath = null;

    private ImageView register_img;
    private Intent mIntent;
    private String path;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

       init();

       ShowRegister_img();

    }


    public void ShowRegister_img(){
        Glide.with(RegisterActivity.this)
                .load(M.getMImg_join(RegisterActivity.this))
                .into(register_img);
    }
    public void init(){

        register_img = (ImageView) findViewById(R.id.register_img);
        input_signupid = (EditText) findViewById(R.id.cl_id);
        input_signupname = (EditText) findViewById(R.id.cl_name);
        input_signuppw = (EditText) findViewById(R.id.cl_pw);
        input_signuppassword = (EditText) findViewById(R.id.cl_pw_check);


        TextView signup_detail = (TextView) findViewById(R.id.signup_detail);

        signup_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertScrollView_privacy01();
            }
        });

        signup = (TextView) findViewById(R.id.signup);
        signup.setOnClickListener(this);

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

                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
                return true;

            }
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.signup) {
            mb_id = input_signupid.getText().toString().trim();
            mb_password = input_signuppw.getText().toString().trim();
            mb_passwordConfirm = input_signuppassword.getText().toString().trim();
            mb_name = input_signupname.getText().toString().trim();
            mb_nick = input_signupname.getText().toString().trim();
            mb_email = GlobalFunctions.onesignalID(RegisterActivity.this);
            mb_hp = GlobalFunctions.onesignalID(RegisterActivity.this);

            String mb_ip  = getLocalIpAddress(1);
            if (mb_id.length() <= 3) {
                input_signupid.setError("아이디는 최소 4자 이상입니다.");
            } else if (mb_password.length() <= 3) {
                input_signuppw.setError("비밀번호는 최소 4자 이상입니다.");
            } else if (!mb_password.equals(mb_passwordConfirm)) {
                input_signuppassword.setError("비밀번호가 일치하지 않습니다.");
            } else if (mb_name.length() <= 1) {
                input_signupname.setError("이름을 입력해주세요.");


            } else {
                final Intent intent = new Intent(RegisterActivity.this, LoginControl.class);
                intent.putExtra("mode", "email");
                intent.putExtra("id", mb_id);
                intent.putExtra("name", mb_name);
                intent.putExtra("password", mb_password);
                intent.putExtra("email", GlobalFunctions.onesignalID(RegisterActivity.this));
                intent.putExtra("profileImgUrl", "");
                intent.putExtra("profileImgUrlThumb", "");
                startActivity(intent);
                finish();

            }

        }
    }



    public final static int INET4ADDRESS = 1;
    public final static int INET6ADDRESS = 2;

    public static String getLocalIpAddress(int type) {
        try {
            for (Enumeration en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                for (Enumeration enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        switch (type) {
                            case INET6ADDRESS:
                                if (inetAddress instanceof Inet6Address) {
                                    return inetAddress.getHostAddress().toString();
                                }
                                break;
                            case INET4ADDRESS:
                                if (inetAddress instanceof Inet4Address) {
                                    return inetAddress.getHostAddress().toString();
                                }
                                break;
                        }

                    }
                }
            }
        } catch (SocketException ex) {
        }
        return null;
    }






    private class SubmitOnClickListener implements View.OnClickListener {
        Intent intent;

        public void onClick(View v){
            intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
        }
    }
    private class loginbackClickListener implements View.OnClickListener {
        Intent intent;

        public void onClick(View v){
            intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition (0, 0);
        }
    }



    public void alertScrollView_privacy01() {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View myScrollView = inflater.inflate(R.layout.scroll_privacy, null, false);


        final TextView tv = (TextView) myScrollView
                .findViewById(R.id.textViewWithScroll);

        M.showLoadingDialog(this);
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                getString(R.string.Domain_url) + AppConst.PRIVACY_URL, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        M.hideLoadingDialog();
                        if (response != null) {

                            String dis;
                            try {
                                dis = response.getString("cf_stipulation");

                                tv.setText(Html.fromHtml(dis));
                            } catch (JSONException e) {
                                tv
                                        .setText("정보를 불러오는데 실패하였습니다.");
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        AppController.getInstance().addToRequestQueue(jsonReq);


        new AlertDialog.Builder(RegisterActivity.this).setView(myScrollView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @TargetApi(11)
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }

                }).show();
    }



}


