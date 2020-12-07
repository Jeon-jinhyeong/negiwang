package com.negiwang.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.Profile;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;
import com.kakao.kakaostory.KakaoStoryService;
import com.kakao.kakaostory.callback.StoryResponseCallback;
import com.kakao.kakaostory.request.PostRequest;
import com.kakao.kakaostory.response.model.MyStoryInfo;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.util.KakaoParameterException;
import com.kakao.util.helper.log.Logger;
import com.negiwang.android.Dialog.ExitPopup;
import com.negiwang.android.Users.LoginActivity;
import com.negiwang.android.sdk.PaymentScheme;
import com.negiwang.android.App.GlobalFunctions;
import com.negiwang.android.Intro.SplashActivity;
import com.negiwang.android.R;
import com.negiwang.android.Users.SubRecorderActivity;
import com.negiwang.android.helper.M;

import org.apache.http.util.EncodingUtils;

import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    public static Activity MyActivity = null;
    static WebView MyWebView;
    private LinearLayout WebView_L, adView;
    private Dialog dialog;
    private String URL;
    private byte[] postData;

    private String lastMyStoryId;
    private final String execParam = "place=1111";
    private final String marketParam = "referrer=kakaostory";

    public static Handler MyHandler;

    private NativeExpressAdView mNativeExpressAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyActivity = MainActivity.this;

        init();

        popup_check();


        onesignal();


    }

    public void onesignal(){

        try {
            /** OneSignal Tag 그룹 추가 : mb_id **/
            GlobalFunctions.OneSignal_Group_add("mb_id", M.getMB_ID(MainActivity.this));
        } catch (Exception e) {

        }

        try {
            /** OneSignal Tag 그룹 추가 : Push1 **/
            GlobalFunctions.OneSignal_Group_add("Push1", "on");
        } catch (Exception e) {

        }
    }

    public void init() {
        MyWebView = (WebView) findViewById(R.id.MyWebView);

        // PG
/*
        MyWebView.setWebViewClient(new KcpWebViewClient(this, MyWebView));
        WebSettings settings = MyWebView.getSettings();
        settings.setJavaScriptEnabled(true);
*/
        WebView_L = (LinearLayout) findViewById(R.id.WebView_L);
        mNativeExpressAdView = new NativeExpressAdView(MainActivity.this);
        adView = (LinearLayout) findViewById(R.id.adView);

        if (M.getMAd_foot_YN(MainActivity.this).equals("1")) {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            int size = Math.round(50 * dm.density);
            WebView_L.setPadding(0, 0, 0, size);

            adView.setVisibility(View.VISIBLE);
            ShowAdmobBanner();


        } else {
            adView.setVisibility(View.GONE);
            WebView_L.setPadding(0, 0, 0, 0);

        }

        initializeWebView(MyWebView);



        if(M.getMKakao_YN(MainActivity.this).equals("1")){
            if(M.getLoginMode(MainActivity.this).equals("kakao")) {
                if (!M.getShare(MainActivity.this).equals("yes")) {
                    Mkakao();
                }
            }
        }

        MyHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    EXITBack = false;
                }
            }
        };
    }


    public void Mkakao(){
        String KAS_TEXT = M.getMKakao_txt(MainActivity.this);
        final String KAS_TEXT_FIX = KAS_TEXT.replace("\\n", "\n");

        share(KAS_TEXT_FIX, getString(R.string.Domain_url) + "api/kas.php");
    }

    public void share(final String message, final String linkurl) {

        requestPostLink(linkurl, message);
        M.setShare("yes", MainActivity.this);


    }


    private void requestPostLink(String scrapUrl, String linkContent) {
        try {
            KakaoStoryService.requestPostLink(new KakaoStoryResponseCallback<MyStoryInfo>() {

                @Override
                public void onSuccess(MyStoryInfo result) {
                    handleStoryPostResult(KakaoStoryService.StoryType.NOTE, result);
                }
            }, scrapUrl, linkContent, PostRequest.StoryPermission.PUBLIC, true, execParam, execParam, marketParam, marketParam);
        } catch (KakaoParameterException e) {
            Logger.e(e);
        }
    }

    private void handleStoryPostResult(KakaoStoryService.StoryType type, MyStoryInfo info) {
        if (info.getId() != null) {
            lastMyStoryId = info.getId();

            Logger.e("requestPost : %s", "succeeded to post " + type + " on KakaoStory.\nmyStoryId=" + lastMyStoryId);

        } else {
            Logger.e("requestPost : %s", "failed to post " + type + " on KakaoStory.\nmyStoryId=null");

        }
    }

    private abstract class KakaoStoryResponseCallback<T> extends StoryResponseCallback<T> {

        @Override
        public void onNotKakaoStoryUser() {
        }

        @Override
        public void onFailure(ErrorResult errorResult) {
            final String message = "MyKakaoStoryHttpResponseHandler : failure : " + errorResult;
            Logger.w(message);
        }

        @Override
        public void onSessionClosed(ErrorResult errorResult) {
        }

        @Override
        public void onNotSignedUp() {
        }
    }


    public void ShowAdmobBanner() {
        mNativeExpressAdView.setAdSize(AdSize.BANNER);
        mNativeExpressAdView.setAdUnitId(M.getMAd_foot(MainActivity.this));
        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
        adRequestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
        adView.addView(mNativeExpressAdView);
        mNativeExpressAdView.loadAd(adRequestBuilder.build());
    }

    private void initializeWebView(WebView MyWebView) {

        WebSettings webSettings = MyWebView.getSettings();
        // 콜백함수 호출


        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setLoadsImagesAutomatically(true);
        MyWebView.setVerticalScrollBarEnabled(true);
        MyWebView.setHorizontalScrollBarEnabled(true);
        MyWebView.setNetworkAvailable(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setGeolocationEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        this.registerForContextMenu(MyWebView);
        webSettings.setUseWideViewPort(true);
        webSettings.setSaveFormData(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(false);
        webSettings.setGeolocationEnabled(true);
        webSettings.setDatabasePath(getFilesDir() + "/databases/");


        if (Build.VERSION.SDK_INT >= 19) {

            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);

        }

        // PG
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.setAcceptThirdPartyCookies(MyWebView, true);
        }

        if (Build.VERSION.SDK_INT >= 16) {
            webSettings.setAllowFileAccessFromFileURLs(true);
            webSettings.setAllowUniversalAccessFromFileURLs(true);
        }

        webSettings.setDefaultTextEncodingName("utf-8");
/*
        MyWebView.clearHistory();
        MyWebView.clearCache(true);
        MyWebView.clearView();
*/
        MyWebView.addJavascriptInterface(new WebAppInterface(this), "Android");


        MyWebView.setWebViewClient(new WebViewClient() {


            public static final String INTENT_PROTOCOL_START = "intent:";
            public static final String INTENT_PROTOCOL_INTENT = "#Intent;";
            public static final String INTENT_PROTOCOL_END = ";end;";
            public static final String GOOGLE_PLAY_STORE_PREFIX = "market://details?id=";


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
/*


*/
                String mytemp=(url.split("package=")[url.split("package=").length-1]).split(";")[0];

                if (url.startsWith(INTENT_PROTOCOL_START)) {
                    final int customUrlStartIndex = INTENT_PROTOCOL_START.length();
                    final int customUrlEndIndex = url.indexOf(INTENT_PROTOCOL_INTENT);
                    if (customUrlEndIndex < 0) {
                        return false;
                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW);

                        /*PG Start*/
                        if (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("javascript:")) {
                            try {
                                intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME); //IntentURI처리
                                Uri uri = Uri.parse(intent.getDataString());

                                startActivity(new Intent(Intent.ACTION_VIEW, uri));
                                return true;
                            } catch (URISyntaxException ex) {
                                return false;
                            }
                        }
                        else {
                        /*PG END*/

                            final String customUrl = url.substring(customUrlStartIndex, customUrlEndIndex);

                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            try {
                                intent.setData(Uri.parse(customUrl));
                                getBaseContext().startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                            /*PG Start*/
                                if (intent == null) return false;

                                if (handleNotFoundPaymentScheme(intent.getScheme(), MainActivity.this, view))
                                    return true;
                            /*
                            String packageName = intent.getPackage();
                            if (packageName != null) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
                                return true;
                            }
                            */
                            /*PG END*/


                                final int packageStartIndex = customUrlEndIndex + INTENT_PROTOCOL_INTENT.length();
                                final int packageEndIndex = url.indexOf(INTENT_PROTOCOL_END);

                                final String packageName = url.substring(packageStartIndex, packageEndIndex < 0 ? url.length() : packageEndIndex);
                                //intent.setData(Uri.parse(GOOGLE_PLAY_STORE_PREFIX + packageName));
                                String mmurl = "https://play.google.com/store/apps/details?id=" + mytemp;
                                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mmurl));
                                getBaseContext().startActivity(intent);
                            }
                        }
                        return true;
                    }

                }

                String origin_url = url;
                String temp_url = origin_url.substring(origin_url.length() - 3,
                        origin_url.length());
                if (temp_url.equals("mp4")) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    Uri uri = Uri.parse(url);
                    i.setDataAndType(uri, "video/mp4");
                    startActivity(i);

                } else if (origin_url.startsWith("tel:")) {
                    Intent call_phone = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(origin_url));
                    startActivity(call_phone);
                    return true;
                }

                if (url.contains("logout")) {
                    M.setMB_ID("null", MainActivity.this);
                    M.setToken("null", MainActivity.this);
                    M.setName("null", MainActivity.this);
                    M.setNick("null", MainActivity.this);
                    M.setLoginMode("null", MainActivity.this);


                    UserManagement.requestLogout(new LogoutResponseCallback() {
                        @Override
                        public void onCompleteLogout() {
                            //redirectLoginActivity();
                        }
                    });

                    try {
                        AccessToken.setCurrentAccessToken(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        Profile.setCurrentProfile(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Intent goToSplash = new Intent(MainActivity.this, SplashActivity.class);
                    startActivity(goToSplash);
                    finish();
                    overridePendingTransition(0, 0);

                }

                if (url.contains("login")) {
                    Intent goToLogoin = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(goToLogoin);
                    finish();
                    overridePendingTransition(0, 0);
                }


                if ((url.startsWith("intent:") || url.startsWith("kakaolink:") || url.startsWith("storylink:") || url.contains("market://") || url.contains("mailto:") || url.contains("play.google") || url.contains("vid:")) == true) {
                    // Load new URL Don't override URL Link
                    view.getContext().startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

                    return true;
                } else if (url.endsWith(".mp4") || url.endsWith(".avi") || url.endsWith(".flv")) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(url), "video/mp4");
                        view.getContext().startActivity(intent);
                    } catch (Exception e) {
                        //error
                    }

                    return true;
                }
                // Toast.makeText(Page.this,  url, Toast.LENGTH_SHORT).show();
                // Return true to override url loading (In this case do nothing).
                // 웹뷰 캐시 클릭어
                // view.clearHistory();
                view.clearCache(true);
                // view.clearView();
                return super.shouldOverrideUrlLoading(view, url);


            }


            @Override
            public void onFormResubmission(WebView view, Message dontResend, Message resend) {
                resend.sendToTarget();
            }


        });

        Intent intent = getIntent();
        URL = intent.getExtras().getString("CALLBACK_URL", "null");

        if (URL.equals("null")) {
            URL = getString(R.string.Domain_url);
        }


        String post = "m_id=" + M.getMB_ID(MainActivity.this);
        postData = EncodingUtils.getBytes(post, "BASE64");

        MyWebView.postUrl(URL, postData);

        MyWebView.setWebChromeClient(CustomWebChromeClient);

    }

    /**
     * @param scheme
     * @return 해당 scheme에 대해 처리를 직접 하는지 여부
     * <p>
     * 결제를 위한 3rd-party 앱이 아직 설치되어있지 않아 ActivityNotFoundException이 발생하는 경우 처리합니다.
     * 여기서 handler되지않은 scheme에 대해서는 intent로부터 Package정보 추출이 가능하다면 다음에서 packageName으로 market이동합니다.
     */
    protected boolean handleNotFoundPaymentScheme(String scheme, Activity activity, WebView target) {
        //PG사에서 호출하는 url에 package정보가 없어 ActivityNotFoundException이 난 후 market 실행이 안되는 경우
        if (PaymentScheme.ISP.equalsIgnoreCase(scheme)) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + PaymentScheme.PACKAGE_ISP)));
            return true;
        } else if (PaymentScheme.BANKPAY.equalsIgnoreCase(scheme)) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + PaymentScheme.PACKAGE_BANKPAY)));
            return true;
        }

        return false;
    }


    private FrameLayout mCustomViewContainer;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;

    WebChromeClient CustomWebChromeClient = new WebChromeClient() {
        @Override
        public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, Message resultMsg) {
            WebView.HitTestResult result = view.getHitTestResult();
            String data = result.getExtra();
            Context context = view.getContext();
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
            context.startActivity(browserIntent);
            return false;
        }

        /*@Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            FrameLayout decor = (FrameLayout) MyActivity.getWindow().getDecorView();
            mCustomViewContainer = new FrameLayout(MyActivity);
            mCustomViewContainer.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            mCustomViewContainer.setBackgroundResource(android.R.color.black);
            mCustomViewContainer.addView(view, ViewGroup.LayoutParams.MATCH_PARENT);
            decor.addView(mCustomViewContainer, ViewGroup.LayoutParams.MATCH_PARENT);
            mCustomViewCallback = callback;

        }*/

        // YouTube Fullscreen Hide
        @Override
        public void onHideCustomView() {
            FrameLayout decor = (FrameLayout) MyActivity.getWindow().getDecorView();
            decor.removeView(mCustomViewContainer);
            mCustomViewContainer = null;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                mCustomViewCallback.onCustomViewHidden();
            }


        }

        // Page Loading Progress
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
            } else {
            }
        }

        @Override
        public boolean onJsAlert(WebView view, String url,
                                 String message, final JsResult result) {

            new AlertDialog.Builder(MyActivity)
                    .setTitle("확인해주세요")
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    result.confirm();
                                }
                            })

                    .create()
                    .show();
            return true;
        }

        public boolean onJsConfirm(WebView view, String url,
                                   String message, final JsResult result) {

            new AlertDialog.Builder(MyActivity)
                    .setTitle("확인해주세요")
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    result.confirm();
                                }
                            })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    result.cancel();
                                }
                            })
                    .create()
                    .show();
            return true;
        }

        //html5 geolocation
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);
        }


        // 자바스크립트 에러 발생 시 로그 출력부
        public boolean onConsoleMessage(ConsoleMessage cm) {
            return true;
        }

        /**
         * 파일 업로드. input tag를 클릭했을 때 호출된다.<br>
         * 카메라와 갤러리 리스트를 함께 보여준다.
         * @param
         */


    };

    public void popup_check() {
        if (M.getMPop_YN(MainActivity.this).equals("1")) {
            PopUpView();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // 애드몹 전면광고 사용시 MainActivity 열림과 동시에 애드몹 호출
        try {
            if (M.getMAd_Main_YN(MainActivity.this).equals("1")) {
                SplashActivity.ad_go();
            }
        } catch (Exception e) {
        }


    }

    public void PopUpView() {


        String URL = M.getMPop_t_url(MainActivity.this);
        Uri uri = Uri.parse(URL);

        dialog = new Dialog(MainActivity.this, R.style.MyThemedDialog);
        dialog.setContentView(R.layout.custom_dialog_image);
        dialog.setCancelable(false);

        Button btnok = (Button) dialog.findViewById(R.id.btnDialogOk);


        btnok.setText("닫기");

        ImageView ivdialog = (ImageView) dialog.findViewById(R.id.imgDialog);

        Glide.with(MainActivity.this)
                .load(M.getMPop_img_src(MainActivity.this))
                .into(ivdialog);


        if (!M.getMPop_browser(MainActivity.this).equals("")) {
            ivdialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (M.getMPop_browser(MainActivity.this).equals("in")) {
                        MyWebView.postUrl(M.getMPop_t_url(MainActivity.this), postData);
                        dialog.dismiss();

                    } else {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(M.getMPop_t_url(MainActivity.this)));
                        startActivity(browserIntent);
                        dialog.dismiss();
                    }


                }
            });
        }

        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dialog.cancel();
            }
        });

        dialog.show();
    }


    public class WebAppInterface {
        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void webviewclose(String toast) {
            finish();
        }


        @JavascriptInterface
        public void webviewreload(String toast) {
            Intent intent = getIntent();
            String URL = intent.getExtras().getString("CALLBACK_URL", "null");

            MyWebView.postUrl(URL, postData);
        }

        /**
         * Show a toast from the web page
         */
        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();

        }


        /**
         * 음성녹음 액티비티 불러오기
         **/
        @JavascriptInterface
        public void recordStart(String uid, String bo_table)
        {
            //recordAudio();

            Intent intent = new Intent(MainActivity.this, SubRecorderActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            //finish();
        }


    }


    private static boolean EXITBack = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {


            if (EXITBack == false) {

                if (MyWebView.canGoBack()) {

                    MyWebView.goBack();
                    return false;

                } else {

                    if (M.getMEnd_type(this).equals("01")) {
                        Toast.makeText(MyActivity, "'뒤로'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
                        EXITBack = true;
                        MyHandler.sendEmptyMessageDelayed(0, 1000 * 2);
                        return true;
                    } else if (M.getMEnd_type(this).equals("02")) {
                        EXITImageView();
                    } else if (M.getMEnd_type(this).equals("03")) {

                        double p = Double.parseDouble(M.getMEnd_percent(MainActivity.this));
                        if (Math.random() < p) {
                            Intent intent_empty = new Intent(MainActivity.this, ExitPopup.class);
                            startActivity(intent_empty);
                            finish();
                            overridePendingTransition(0, 0);
                        } else {
                            Toast.makeText(MyActivity, "'뒤로'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();

                            EXITBack = true;
                            MyHandler.sendEmptyMessageDelayed(0, 1000 * 2);
                            return true;
                        }
                    }
                }
            }


        }


        return super.onKeyDown(keyCode, event);
    }


    public void EXITImageView() {


        dialog = new Dialog(MainActivity.this, R.style.MyThemedDialog);
        dialog.setContentView(R.layout.custom_dialog_image_exit);
        dialog.setCancelable(true);
        //  dialog.setTitle("Location Dutch Province");


        //String count_url = M.parseURL(AppConst.EXIT_COUNT_URL, MainActivity.this) + dbHelper.getResult("app_exit_target");
        //newRequest_count(count_url);


        Button btnclose = (Button) dialog.findViewById(R.id.btnDialogClose);
        Button btnok = (Button) dialog.findViewById(R.id.btnDialogOk);

        btnclose.setText("종료");

        btnok.setText("확인");

        ImageView ivdialog = (ImageView) dialog.findViewById(R.id.imgDialog);


        Glide.with(this).load(M.getMEndImgSrc(this))
                .into(ivdialog);


        btnclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.cancel();
                finish();

            }
        });

        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!M.getMEndImgTurl(MainActivity.this).equals("")) {
                    if (M.getMEndImgBrwser(MainActivity.this).equals("in")) {

                        MyWebView.postUrl(M.getMEndImgTurl(MainActivity.this), postData);
                        dialog.cancel();

                    } else {
                        dialog.cancel();
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(M.getMEndImgTurl(MainActivity.this)));
                        startActivity(browserIntent);
                        finish();
                    }
                } else {

                }

            }
        });

        dialog.show();


    }
}
