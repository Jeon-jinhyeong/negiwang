package com.negiwang.android.Users;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;
import com.negiwang.android.App.AppConst;
import com.negiwang.android.App.AppController;
import com.negiwang.android.App.GlobalFunctions;
import com.negiwang.android.MainActivity;
import com.negiwang.android.R;
import com.negiwang.android.helper.M;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends Activity {


    private ImageView login_img, sns_talk, sns_facebook;
    private LinearLayout sns_login_guest;
    private EditText mb_id_Input, mb_passwordInput;
    TextView regi_btn;

    private String mb_id_login, mb_password_login;
    public static Handler MyHandler;

    private SessionCallback callback;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);


        login_check();

        setContentView(R.layout.activity_login);

        init();

        /** 로그인 배경이미지 **/
        showBackgroundImg();

    }

    public void login_check() {
        // 이미 로그인 토큰값이 존재할 경우 MainActivity로 이동

        if (!M.getToken(LoginActivity.this).equals("null")) {

//            Intent mIntent = new Intent(this, MainActivity.class);
//            startActivity(mIntent);
//            finish();
//            overridePendingTransition(0, 0);

        }
    }

    public void init() {
        login_img = (ImageView) findViewById(R.id.login_img);
        login_img.setBackgroundColor(Color.BLACK);


        int edit_color = Color.parseColor("#ffffff");

        mb_id_Input = (EditText) findViewById(R.id.id_edit);
        mb_id_Input.getBackground().setColorFilter(edit_color, PorterDuff.Mode.SRC_ATOP);
        mb_id_Input.setHintTextColor(edit_color);
        mb_id_Input.setTextColor(edit_color);
        mb_passwordInput = (EditText) findViewById(R.id.pw_edit);
        mb_passwordInput.getBackground().setColorFilter(edit_color, PorterDuff.Mode.SRC_ATOP);
        mb_passwordInput.setHintTextColor(edit_color);
        mb_passwordInput.setTextColor(edit_color);

        regi_btn = (TextView) findViewById(R.id.regi_btn);
        regi_btn.setTextColor(edit_color);


        TextView Login = (TextView) findViewById(R.id.login_btn);
        Login.setOnClickListener(new LoginOnClickListener());


        sns_talk = (ImageView) findViewById(R.id.sns_talk);
        sns_facebook = (ImageView) findViewById(R.id.sns_facebook);

        if (M.getMLogin_katalk(LoginActivity.this).equals("1")) {
            sns_talk.setVisibility(View.VISIBLE);
        } else {
            sns_talk.setVisibility(View.GONE);
        }

        if (M.getMLogin_facebook(LoginActivity.this).equals("1")) {
            sns_facebook.setVisibility(View.VISIBLE);
        } else {
            sns_facebook.setVisibility(View.GONE);
        }

        regi_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(LoginActivity.this,
                        RegisterActivity.class);
                startActivity(mIntent);
                finish();
            }
        });

        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        Session.getCurrentSession().checkAndImplicitOpen();


        callbackManager = CallbackManager.Factory.create();  //로그인 응답을 처리할 콜백 관리자


        MyHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    EXITBack = false;
                }
            }
        };


        sns_login_guest = (LinearLayout) findViewById(R.id.sns_login_guest);

        if(M.getMLogin_guest(LoginActivity.this).equals("1")){
            sns_login_guest.setVisibility(View.VISIBLE);
        } else {
            sns_login_guest.setVisibility(View.GONE);
        }


        sns_login_guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mb_name = "guest";
                String mb_nick = "guest";
                String mb_email = GlobalFunctions.onesignalID(LoginActivity.this);
                String mb_id = "guest";





                //  Toast.makeText(SNS_Login.this, M.getNick(this), Toast.LENGTH_SHORT).show();

                RegisterTask t = new RegisterTask();
                String[] params = new String[]{mb_name, mb_nick, mb_id, mb_email};
                t.execute(params);
            }
        });

        sns_talk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new kakaoLoginControl(LoginActivity.this).call();
            }
        });


        sns_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //LoginManager - 요청된 읽기 또는 게시 권한으로 로그인 절차를 시작합니다.
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this,
                        Arrays.asList("public_profile", "user_friends"));
                LoginManager.getInstance().registerCallback(callbackManager,
                        new FacebookCallback<LoginResult>() {
                            @Override
                            public void onSuccess(LoginResult loginResult) {
                                Log.e("onSuccess", "onSuccess");
                                //getPosts();
                                GraphRequest request = GraphRequest.newMeRequest(
                                        loginResult.getAccessToken(),
                                        new GraphRequest.GraphJSONObjectCallback() {
                                            @Override
                                            public void onCompleted(
                                                    JSONObject object,
                                                    GraphResponse response) {


                                                // Application code
                                                try {
                                                    Log.e("fb", "try");

                                                    if (M.getToken(LoginActivity.this).equals("null")) {
                                                        String id = (String) response.getJSONObject().get("id");//페이스북 아이디값
                                                        Log.e("fb", "id");
                                                        String name = (String) response.getJSONObject().get("name");//페이스북 이름
                                                        Log.e("fb", "name");
                                                        String email = GlobalFunctions.onesignalID(LoginActivity.this);
                                                        Log.e("fb", "email");
                                                        String profileImgUrl = "https://graph.facebook.com/" + id + "/picture?type=large";
                                                        Log.e("fb", "profileImgUrl");
                                                        String profileImgUrlThumb = "https://graph.facebook.com/" + id + "/picture?type=large";
                                                        Log.e("fb", "profileImgUrlThumb");


                                                        //M.setNick(name, FacebookLoginActivity.this);
                                                        //M.setName(name, FacebookLoginActivity.this);
                                                        //M.setMail(email, FacebookLoginActivity.this);
                                                        //M.setProfile(profileImgUrl, FacebookLoginActivity.this);

                                                        redirectSignupActivityfb(id, name, email, profileImgUrl, profileImgUrlThumb);
                                                    }

                                                } catch (JSONException e) {
                                                    // TODO Auto-generated catch block
                                                    e.printStackTrace();
                                                    Log.e("fb", "catch");
                                                }


                                                // new joinTask().execute(); //자신의 서버에서 로그인 처리를 해줍니다

                                            }
                                        });
                                Bundle parameters = new Bundle();
                                parameters.putString("fields", "id,name,email,gender, birthday");
                                request.setParameters(parameters);
                                request.executeAsync();


                            }

                            @Override
                            public void onCancel() {
                                Log.e("onCancel", "onCancel");
                            }

                            @Override
                            public void onError(FacebookException exception) {
                                Log.e("onError", "onError " + exception.getLocalizedMessage());
                            }
                        });
            }
        });
    }


    public void showBackgroundImg() {
        Glide.with(LoginActivity.this)
                .load(M.getMImg_login(LoginActivity.this))
                .into(login_img);
    }


    private class RegisterTask extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {
            M.showLoadingDialog(LoginActivity.this);
        }

        String data;

        @Override
        protected String doInBackground(String... params) {

            String mb_name = params[0];
            String mb_nick = params[1];
            String mb_id = params[2];
            String mb_email = params[3];


            try {
                data = M.register_sns2(getString(R.string.Domain_url) + AppConst.REGISTER_GUEST_URL, mb_name, mb_nick, mb_id, mb_email);
                Log.e("data", data);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return data;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            JSONObject response = M.stringToJSONObject(result);
            M.hideLoadingDialog();
            try {
                if (response.getInt("statusCode") != 1) {
                    //GlobalFunctions.Toast_Bottom(SNS_Login.this, response.getString("statusMessage"));
                } else {
                    //GlobalFunctions.Toast_Bottom(SNS_Login.this, response.getString("statusMessage"));


                    String token = response.getString("token");

                    M.setMB_ID(response.getString("mb_id"), LoginActivity.this);
                    M.setNick(response.getString("mb_id"), LoginActivity.this);
                    M.setName(response.getString("mb_id"), LoginActivity.this);
                    M.setToken(token, LoginActivity.this);

                    //	Toast.makeText(KakaoLoginActivity.this, "회원가입이 가능합니다", Toast.LENGTH_SHORT).show();
                    final List<AuthType> availableAuthTypes = new ArrayList<AuthType>();
                    availableAuthTypes.add(AuthType.KAKAO_TALK);
                    Intent mIntent = new Intent(LoginActivity.this,
                            MainActivity.class);
                    mIntent.putExtra("target_url", "null");
                    startActivity(mIntent);
                    finish();


                }
            } catch (JSONException e) {
                //M.T(SNS_Login.this, "GWings");
            }
        }

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


    private class LoginOnClickListener implements View.OnClickListener {
        public void onClick(View v) {

            mb_id_login = mb_id_Input.getText().toString().trim();
            mb_password_login = mb_passwordInput.getText().toString().trim();

            if (mb_id_login.length() <= 3) {
                mb_id_Input.setError("아이디를 확인해주세요(4자이상)");

            } else if (mb_password_login.length() <= 3) {
                mb_passwordInput.setError("비밀번호를 확인해주세요(4자이상)");

            } else {
                M.showLoadingDialog(LoginActivity.this);

                StringRequest str = new StringRequest(Request.Method.POST,
                        getString(R.string.Domain_url) + AppConst.LOGIN_URL, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        M.hideLoadingDialog();
                        parseJson(M.stringToJSONObject(response));

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError v) {
                        M.hideLoadingDialog();
                        Log.e("mb_id", mb_id_login);
                        Log.e("mb_password", mb_password_login);
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {

                        Map<String, String> params = new HashMap<String, String>();
                        params.put("mb_id", mb_id_login);
                        params.put("mb_password", mb_password_login);
                        return params;
                    }
                };
                AppController.getInstance().addToRequestQueue(str);
            }
        }
    }

    public void parseJson(JSONObject response) {

        try {
            M.L(response.toString());
            boolean status = response.getBoolean("status");
            String token = null;
            String id = null;
            String mb_id_origin = null;
            String mb_nick = null;
            if (status != false) {
                token = response.getString("token");
                id = response.getString("userID");
                String mb_id = response.getString("mb_id");
                mb_nick = response.getString("mb_nick");
                M.setToken(token, this);
                M.setID(id, this);
                M.setMB_ID(mb_id, this);
                M.setNick(mb_nick, this);
                M.setProfile("null", LoginActivity.this);

                // 로그인 처리 후 액티비티 이동
                Intent mIntent = new Intent(this, MainActivity.class);
                startActivity(mIntent);
                finish();
                overridePendingTransition(0,0);

            } else {
                Toast.makeText(this, "일치한 사용자 정보가 없습니다", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
                return;
            }
        } catch (Exception e) {

        }

        try {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {

        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }

    private class SessionCallback implements ISessionCallback {

        /**
         * 로그인 세션 생성 되었을 경우
         **/
        @Override
        public void onSessionOpened() {
            requestMe();

        }

        /**
         * 로그인 세션 생성 실패할 경우
         **/
        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if (exception != null) {
                Logger.e(exception);
            }
        }
    }


    private void requestMe() {
        List<String> propertyKeys = new ArrayList<String>();
        //propertyKeys.add("kaccount_email");
        propertyKeys.add("nickname");
        propertyKeys.add("profile_image");
        propertyKeys.add("thumbnail_image");

        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);

                //redirectLoginActivity();
                Log.e("UserProfile", "onFailure");
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                //redirectLoginActivity();
                Log.e("UserProfile", "onSessionClosed");
            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                Log.e("UserProfile", "onSuccess");
                //Log.e("UserProfile", String.valueOf(userProfile));
                if (M.getToken(LoginActivity.this).equals("null")) {
                    String U_Email = GlobalFunctions.onesignalID(LoginActivity.this);
                    String U_Nick = userProfile.getNickname();
                    String U_Image = "" + userProfile.getProfileImagePath();
                    String U_Image_thumb = "" + userProfile.getThumbnailImagePath();
                    String U_ID = String.valueOf(userProfile.getId());

                    redirectSignupActivity(U_ID, U_Nick, U_Email, U_Image, U_Image_thumb);
                }


                //redirectMainActivity();
            }

            @Override
            public void onNotSignedUp() {
                Log.e("UserProfile", "onNotSignedUp");
                //showSignup();
            }
        }, propertyKeys, false);
    }


    /**
     * 카카오 로그인 세션 생성 되었을 경우
     **/
    protected void redirectSignupActivity(String id, String name, String email, String profileImgUrl, String profileImgUrlThumb) {
        final Intent intent = new Intent(this, LoginControl.class);
        intent.putExtra("mode", "kakao");
        intent.putExtra("id", id);
        intent.putExtra("name", name);
        intent.putExtra("email", email);
        intent.putExtra("profileImgUrl", profileImgUrl);
        intent.putExtra("profileImgUrlThumb", profileImgUrlThumb);
        startActivity(intent);
        finish();
        overridePendingTransition(0,0);
    }

    /**
     * 페이스북 로그인 세션 생성 되었을 경우
     **/
    protected void redirectSignupActivityfb(String id, String name, String email, String profileImgUrl, String profileImgUrlThumb) {
//        Log.e("fb", "s");
        final Intent intent = new Intent(this, LoginControl.class);
        intent.putExtra("mode", "facebook");
        intent.putExtra("id", id);
        intent.putExtra("name", name);
        intent.putExtra("email", email);
        intent.putExtra("profileImgUrl", profileImgUrl);
        intent.putExtra("profileImgUrlThumb", profileImgUrlThumb);
        startActivity(intent);
        finish();
        overridePendingTransition(0,0);
    }

}
