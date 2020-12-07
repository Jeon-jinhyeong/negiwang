package com.negiwang.android.Users;

//import cn.pedant.SweetAlert.SweetAlertDialog;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.negiwang.android.App.AppConst;
import com.negiwang.android.App.AppController;
import com.negiwang.android.MainActivity;
import com.negiwang.android.R;
import com.negiwang.android.helper.M;

import org.json.JSONObject;


/**
 * Created by hanshem on 16. 3. 8..
 */
public class LoginControl extends Activity {


    Activity activity = this;
    private String appUrl, token, mode, password, id, name, email, profileImgUrl, url_register;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        init();

        // SNS 로그인시 이미 존재하는 아이디인지 체크
        check_id();




    }

    public void init(){
        appUrl = getString(R.string.Domain_url);
        token = M.getToken(activity);

        mode = getIntent().getExtras().getString("mode", "");
        Log.e("mode", mode);
        M.setLoginMode(mode, activity);

        if(mode.equals("email")){
            password = getIntent().getExtras().getString("password", "");
        }

        /** 카톡 or 페북 토큰 ID 값 **/ //일반 이메일 회원가입시 ID값은 이메일
        id = getIntent().getExtras().getString("id", "");
        Log.e("id", id);

        /** 카톡 or 페북 닉네임 **/
        name = getIntent().getExtras().getString("name", "");

        /** 카톡 email **/ //페이스북은 휴대폰 번호로 생성된 경우 email 추출시 오류 -> gmail 계정으로 수집
        email = getIntent().getExtras().getString("email", "");

        /** 카톡 or 페북 기본 프로필 이미지 **/
        profileImgUrl = getIntent().getExtras().getString("profileImgUrl", "");
        Log.e("profileImgUrl", profileImgUrl);
    }



    public void check_id() {

        String url = appUrl + AppConst.POSTS_CHECK_ID_IN_PROFILE + id;


        StringRequest req = new StringRequest(Request.Method.GET, url.replace(
                ":token:", M.getToken(activity)),
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        if (response.equals("true")) {

                            Log.e("CHECK_ID", "true");

                            M.setMB_ID(id, activity);
                            M.setToken("token:", activity);
                            Intent intent_notify = new Intent(activity, MainActivity.class);
                            startActivity(intent_notify);
                            finish();
                            overridePendingTransition(0, 0);

                        } else {
                            Log.e("CHECK_ID", "false");
                            // 회원가입
                            register();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

        };
        AppController.getInstance().addToRequestQueue(req);

    }


    public void register(){
        if(mode.equals("email")) {
            url_register = appUrl + AppConst.REGISTER_URL_EMAIL + "&mode=" + mode + "&id=" + id + "&name=" + name + "&email=" + email + "&profileImgUrl=" + profileImgUrl +  "&password=" + password + "&img_path=" + profileImgUrl;
        } else {
            url_register = appUrl + AppConst.REGISTER_URL + "&mode=" + mode + "&id=" + id + "&name=" + name + "&email=" + email + "&profileImgUrl=" + profileImgUrl +  "&img_path=" + profileImgUrl;
        }
        Log.e("url_register", url_register);
        SendHttpRequestTask t = new SendHttpRequestTask();
        String[] params = new String[]{null, "1",
                "1"};
        t.execute(params);
    }

    //백그라운드 스레드
    private class SendHttpRequestTask extends AsyncTask<String, Void, String> {
        String data;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            M.showLoadingDialog(activity);
        }

        @Override
        protected String doInBackground(String... params) {
            String filePath = params[0];
            String status = params[1];
            String privacy = params[2];
            try {
                data = M.publishStatus(
                        M.parseURL(url_register, activity),
                        filePath, status, null, privacy);
                Log.e("send", "01");
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(activity, "네트워크 오류입니다", Toast.LENGTH_SHORT).show();
            }

            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {

                JSONObject response = M.stringToJSONObject(result);
                try {
                    if (response.getInt("statusCode") != 1) {
                        M.hideLoadingDialog();
                        Toast.makeText(activity, "이미 등록된 회원정보입니다", Toast.LENGTH_SHORT).show();
                    } else {
                        M.hideLoadingDialog();
                        Toast.makeText(activity, "회원가입이 완료되었습니다", Toast.LENGTH_SHORT).show();

                        M.setMB_ID(id, activity);
                        M.setNick(name, activity);
                        M.setName(name, activity);
                        M.setToken("token:", activity);

                        Intent intent_notify = new Intent(activity, MainActivity.class);
                        startActivity(intent_notify);
                        finish();
                        overridePendingTransition(0, 0);


                    }
                } catch (Exception e) {
                    M.hideLoadingDialog();
                    Toast.makeText(activity, "네트워크 오류입니다", Toast.LENGTH_SHORT).show();

                }
            } catch (Exception e){
                M.hideLoadingDialog();
                Toast.makeText(activity, "네트워크 오류입니다", Toast.LENGTH_SHORT).show();
            }
        }
    }

}



