package com.negiwang.android.Intro;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.negiwang.android.MainActivity;
import com.negiwang.android.Users.LoginActivity;
import com.negiwang.android.App.AppConst;
import com.negiwang.android.App.AppController;
import com.negiwang.android.R;
import com.negiwang.android.helper.M;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class SplashActivity extends Activity {

    public static Activity activity;


    private final int version = Build.VERSION.SDK_INT;
    private View decorView;
    private ImageView imageView;
    private String img_sp, Ad_main_YN, Ad_main, login_all;
    private int splash_time;
    private static InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(M.getPermitCheck(SplashActivity.this).equals("checked")){


            activity = SplashActivity.this;


            // 앱 권한 사전 동의 완료시
            setContentView(R.layout.activity_splash);

            /** 레이아웃 **/
            init();

            /** 서버 API 정보 추출 **/
            getInfo();

            badge_count_reset();

        } else {

            // 앱 권한 동의 액티비티로 이동
            goToPermitActivity();

        }

    }

    public void badge_count_reset(){

        updateIconBadgeCount(SplashActivity.this, 0);
        M.setBadge("0", SplashActivity.this);

    }

    public void updateIconBadgeCount(Context context, int count) {

        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");

        intent.putExtra("badge_count_package_name", context.getPackageName());
        intent.putExtra("badge_count_class_name", getLauncherClassName(context));

        intent.putExtra("badge_count", count);

        sendBroadcast(intent);
    }

    private String getLauncherClassName(Context context) {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setPackage(getPackageName());

        List<ResolveInfo> resolveInfoList = getPackageManager().queryIntentActivities(intent, 0);
        if (resolveInfoList != null && resolveInfoList.size() > 0) {
            return resolveInfoList.get(0).activityInfo.name;
        }
        return "";
    }

    public void init(){

        decorView = getWindow().getDecorView();
        if (version >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
        if (version >= Build.VERSION_CODES.JELLY_BEAN) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
        if (version >= Build.VERSION_CODES.KITKAT) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
        imageView = (ImageView) findViewById(R.id.splash_img);
        imageView.setBackgroundColor(Color.BLACK);

    }


    public void getInfo() {

        String url = M.parseURL(getString(R.string.Domain_url) + AppConst.GET_SPLASH_INFO, SplashActivity.this);
        newRequest(url);

    }


    public void newRequest(String url) {

        M.showLoadingDialog(SplashActivity.this);

        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                if (response != null) {
                    M.hideLoadingDialog();
                    parseJson(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SplashActivity.this, "서버 연결 오류", Toast.LENGTH_SHORT).show();
                M.hideLoadingDialog();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonReq);
    }


    public void parseJson(JSONObject response) {

        try {

            JSONObject profile = response.getJSONObject("profile");

            // Splash 이미지
            img_sp = profile.getString("img_sp");
            // 로그인 배경 이미지
            String img_login = profile.getString("img_login");
            M.setMImg_login(img_login, SplashActivity.this);

            // 회원가입 배경 이미지
            String img_join = profile.getString("img_join");
            M.setMImg_join(img_join, SplashActivity.this);

            // Splash -> 로그인 -> 메인
            login_all = profile.getString("login_all");
            // 로그인 카톡간편로그인 버튼 출력
            String login_katalk = profile.getString("login_katalk");
            M.setMLogin_katalk(login_katalk, SplashActivity.this);

            // 로그인 페이스북간편로그인 버튼 출력
            String login_facebook = profile.getString("login_facebook");
            M.setMLogin_facebook(login_facebook, SplashActivity.this);

            // 비회원 로그인 버튼 출력
            String login_guest = profile.getString("login_guest");
            M.setMLogin_guest(login_guest, SplashActivity.this);

            // 애드몹 전면 광고 키값
            Ad_main = profile.getString("Ad_main");
            M.setMAd_main(Ad_main, SplashActivity.this);

            // 애드몹 배너 광고 키값
            String Ad_foot = profile.getString("Ad_foot");
            M.setMAd_foot(Ad_foot, SplashActivity.this);

            // 애드몹 전면 광고 출력 유무
            Ad_main_YN = profile.getString("Ad_main_YN");
            M.setMAd_Main_YN(Ad_main_YN, SplashActivity.this);

            // 애드몹 배너 광고 출력 유무
            String Ad_foot_YN = profile.getString("Ad_foot_YN");
            M.setMAd_foot_YN(Ad_foot_YN, SplashActivity.this);


            // 메인 팝업 사용 유무
            String pop_YN = profile.getString("pop_YN");
            M.setMPop_YN(pop_YN, SplashActivity.this);

            // 팝업 이미지 경로
            String pop_img_src = profile.getString("pop_img_src");
            M.setMPop_img_src(pop_img_src, SplashActivity.this);

            // 팝업 타겟 경로
            String pop_t_url = profile.getString("pop_t_url");
            M.setMPop_t_url(pop_t_url, SplashActivity.this);

            // 팝업 타겟 출력 브라우저
            String pop_browser = profile.getString("pop_browser");
            M.setMPop_browser(pop_browser, SplashActivity.this);





            // 카카오 마케팅 사용 유무
            String mkakao_YN = profile.getString("mkakao_YN");
            M.setMKakao_YN(mkakao_YN, SplashActivity.this);

            // 카카오 마케팅 텍스트
            String mkakao_txt = profile.getString("mkakao_txt");
            M.setMKakao_txt(mkakao_txt, SplashActivity.this);

            // 카카오 마케팅 이미지 경로
            String mkakao_img_src = profile.getString("mkakao_img_src");
            M.setMKakao_img_src(mkakao_img_src, SplashActivity.this);


            // 앱 종료 타입
            String end_type = profile.getString("end_type");
            M.setMEnd_type(end_type, SplashActivity.this);

            // 전면 이미지 & 뒤로가기 두번 종료 퍼센트 설정
            String end_percent = profile.getString("end_percent");
            M.setMEnd_percent(end_percent, SplashActivity.this);


            //팝업 이미지 경로
            String end_img_src = profile.getString("end_img_src");
            M.setMEndImgSrc(end_img_src, SplashActivity.this);

            //팝업 타겟 경로
            String end_img_t_url = profile.getString("end_img_t_url");
            M.setMEndImgTurl(end_img_t_url, SplashActivity.this);

            //팝업 타겟 브라우저
            String end_img_brwser = profile.getString("end_img_brwser");
            M.setMEndImgBrwser(end_img_brwser, SplashActivity.this);


            /** 서버 API 정보 수신 후 Splash 이미지 출력 **/
            show_splash();

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(SplashActivity.this, "API 연결 오류", Toast.LENGTH_SHORT).show();
        }
    }



    public void show_splash() {

        Glide.with(SplashActivity.this)
                .load(img_sp)
                .into(imageView);



        if (Ad_main_YN.equals("1")) {
            // 애드몹 전면 광고 사용시
            loadAdMob();
        } else {
            // 애드몹 전면 광고 사용 안할시
            goToMain(0);
        }

    }

    public void loadAdMob() {

        mInterstitialAd = new InterstitialAd(SplashActivity.this);
        if (!Ad_main.equals("null")) {
            mInterstitialAd.setAdUnitId(Ad_main);
        } else {
            mInterstitialAd.setAdUnitId("null");
        }


        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mInterstitialAd.loadAd(adRequest);


        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                // 애드몹 로딩 실패 후 메인 이동
                goToMain(0);
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                // 애드몹 로딩 성공 후 메인 이동
                goToMain(1);
            }
        });
    }

    public void goToMain(int admob) {

            if (admob == 0) {
                // 애드몹 로딩 실패시 2초간 대기 후 액티비티 이동
                splash_time = 2000;
            } else {
                // 애드몹 로딩 성공시 1초간 대기 후 액티비티 이동(애드몹 로딩시간 + 1초)
                splash_time = 1000;
            }


        Intent intent = getIntent();
        String URL = intent.getExtras().getString("target_url", "null");


        if(URL.equals("null")) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (login_all.equals("1")) {
                        // Splash -> 로그인 -> 메인일 경우

                        if (M.getToken(SplashActivity.this).equals("null")) {
                            // 로그인 토큰 값이 없을 경우
                            goToLoginActivity();
                        } else {
                            // 로그인 토큰 값이 존재할 경우
                            goToMainActivity();
                        }
                    } else {
                        // Splash -> 메인일 경우
                        goToMainActivity();
                    }

                }
            }, splash_time);
        } else {
            goToMainActivity_TargetURL(URL);
        }

    }


    public void goToPermitActivity(){

        Intent splash_intent = new Intent(SplashActivity.this, PermitActivity.class);
        startActivity(splash_intent);
        finish();
        overridePendingTransition(0,0);

    }

    public void goToLoginActivity(){

        Intent splash_intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(splash_intent);
        finish();
        overridePendingTransition(0,0);

    }

    public void goToMainActivity(){
        Intent splash_intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(splash_intent);
        finish();
        overridePendingTransition(0,0);
    }

    public void goToMainActivity_TargetURL(String URL){
        Intent splash_intent = new Intent(SplashActivity.this, MainActivity.class);
        splash_intent.putExtra("CALLBACK_URL", URL);
        startActivity(splash_intent);
        finish();
        overridePendingTransition(0,0);
    }


    // 다른 액티비티에서 Splash에서 호출된 애드몹 전면광고 출력 요청
    public static void ad_go() {
        if (!mInterstitialAd.getAdUnitId().equals("null")) {
            mInterstitialAd.show();
        }
    }

}
