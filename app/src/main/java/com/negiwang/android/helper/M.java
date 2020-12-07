package com.negiwang.android.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Cache.Entry;
import com.loopj.android.http.HttpGet;
import com.negiwang.android.App.AppConst;
import com.negiwang.android.App.AppController;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class M {

    private static Cache mCache = AppController.getInstance().getRequestQueue()
            .getCache();
    private static Entry mEntry;
    static ProgressDialog pDialog;
    private static File mFile = null;
    private static FileBody mFileBody = null;
    private static MultipartEntityBuilder builder = null;
    private static SharedPreferences mSharedPreferences;
    private static HttpClient client = new DefaultHttpClient();
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private static final String DEFAULT_ENCODING = "UTF-8";

    public static void showLoadingDialog(Context mContext) {

        pDialog = new ProgressDialog(mContext);
        pDialog.setMessage("Please wait...");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.show();

    }

    public static void hideLoadingDialog() {
        pDialog.dismiss();
    }

    public static void T(Context mContext, String Message) {
        Toast.makeText(mContext, Message, Toast.LENGTH_SHORT).show();
    }

    public static void L(String Message) {
        Log.e(AppConst.TAG, Message);
    }


    public static String simpleGETRequest(String url) throws Exception {
        HttpGet get = new HttpGet(url);
        HttpResponse response = client.execute(get);
        return getContent(response);
    }

    public static String publishStatus(String url, String filePath,
                                       String status, String status0, String privacy) throws Exception {
        builder = MultipartEntityBuilder.create();
        HttpPost post = new HttpPost(url);
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.setCharset(UTF8);
        if (filePath != null) {
            mFile = new File(filePath);
            mFileBody = new FileBody(mFile);
            builder.addPart("image", mFileBody);
        }
        if (status != null) {
            builder.addPart("status",
                    new StringBody(status, "text/plain", UTF8));
        }
        if (status0 != null) {
            builder.addPart("status0",
                    new StringBody(status0, "text/plain", UTF8));
        }
        builder.addTextBody("privacy", privacy);
        return excuteRequest(builder, post);
    }

    @SuppressWarnings("deprecation")
    public static String fileupload(String url, String filePath) throws Exception {
        builder = MultipartEntityBuilder.create();
        HttpPost post = new HttpPost(url);
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.setCharset(UTF8);
        if (filePath != null) {
            mFile = new File(filePath);
            mFileBody = new FileBody(mFile);
            builder.addPart("image", mFileBody);
        }

        return excuteRequest(builder, post);
    }


    public static String excuteRequest(MultipartEntityBuilder builder,
                                       HttpPost http) throws Exception {
        ProgressiveEntity mEntity = new ProgressiveEntity(builder);
        http.setEntity(mEntity);
        HttpResponse response = client.execute(http);
        return getContent(response);
    }

    public static String getContent(HttpResponse response) throws IOException {
        BufferedReader rd = new BufferedReader(new InputStreamReader(response
                .getEntity().getContent()));
        String body = "";
        String content = "";
        while ((body = rd.readLine()) != null) {
            content += body + "\n";
        }
        return content.trim();
    }


    public static String parseURL(String url, Context mContext) {
        return url.replace(":token:", "somansa");
        //return url.replace(":mb_id:", getMB_ID(mContext));
    }

    public static String register(String registerUrl, String filePath,
                                  String mb_id, String mb_password, String mb_name, String mb_nick, String mb_email, String mb_hp, String mb_ip) throws Exception {
        builder = MultipartEntityBuilder
                .create();

        HttpClient httpclient = new DefaultHttpClient();

        HttpPost post = new HttpPost(registerUrl);
        //builder.setCharset(Charset.forName("UTF-8"));
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);


        if (filePath != null) {
            mFile = new File(filePath);
            mFileBody = new FileBody(mFile);
            builder.addPart("image", mFileBody);
        }

        builder.addTextBody("mb_id", mb_id, ContentType.APPLICATION_JSON);
        builder.addTextBody("mb_email", mb_email, ContentType.APPLICATION_JSON);
        builder.addTextBody("mb_password", mb_password, ContentType.APPLICATION_JSON);
        builder.addTextBody("mb_name", mb_name, ContentType.APPLICATION_JSON);
        builder.addTextBody("mb_nick", mb_nick, ContentType.APPLICATION_JSON);
        builder.addTextBody("mb_hp", mb_hp, ContentType.APPLICATION_JSON);
        builder.addTextBody("mb_ip", mb_ip, ContentType.APPLICATION_JSON);

        return excuteRequest(builder, post);
    }

    public static String register_sns(String registerUrl, String mb_name, String mb_email) throws Exception {
        builder = MultipartEntityBuilder
                .create();

        HttpClient httpclient = new DefaultHttpClient();

        HttpPost post = new HttpPost(registerUrl);
        //builder.setCharset(Charset.forName("UTF-8"));
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);


        builder.addTextBody("mb_name", mb_name, ContentType.APPLICATION_JSON);
        builder.addTextBody("mb_email", mb_email, ContentType.APPLICATION_JSON);
        return excuteRequest(builder, post);
    }

    public static String register_sns2(String registerUrl, String mb_name, String mb_nick, String mb_id, String mb_email) throws Exception {
        builder = MultipartEntityBuilder
                .create();

        HttpClient httpclient = new DefaultHttpClient();

        HttpPost post = new HttpPost(registerUrl);
        //builder.setCharset(Charset.forName("UTF-8"));
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);


        builder.addTextBody("mb_name", mb_name, ContentType.APPLICATION_JSON);
        builder.addTextBody("mb_nick", mb_nick, ContentType.APPLICATION_JSON);
        builder.addTextBody("mb_id", mb_id, ContentType.APPLICATION_JSON);
        builder.addTextBody("mb_email", mb_email, ContentType.APPLICATION_JSON);
        return excuteRequest(builder, post);
    }

    public static String update(String url, String image, String mb_id,
                                String mb_name, String mb_nick, String mb_email, String mb_hp,
                                String mb_password) throws Exception {
        builder = MultipartEntityBuilder.create();
        HttpPost post = new HttpPost(url);
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        if (image != null) {
            mFile = new File(image);
            mFileBody = new FileBody(mFile);
            builder.addPart("image", mFileBody);
        }
        builder.addTextBody("mb_id", mb_id, ContentType.APPLICATION_JSON);
        builder.addTextBody("mb_name", mb_name, ContentType.APPLICATION_JSON);
        builder.addTextBody("mb_nick", mb_nick, ContentType.APPLICATION_JSON);
        builder.addTextBody("mb_email", mb_email, ContentType.APPLICATION_JSON);
        builder.addTextBody("mb_hp", mb_hp, ContentType.APPLICATION_JSON);
        builder.addTextBody("mb_password", mb_password, ContentType.APPLICATION_JSON);
        return excuteRequest(builder, post);
    }

    public static boolean isCached(String url) {
        mEntry = mCache.get(url);
        if (mEntry != null) {
            return true;
        } else {
            return false;
        }
    }


    public static String getCache(String url) {
        mEntry = mCache.get(url);
        String data = null;
        if (mEntry != null) {
            try {
                data = new String(mEntry.data, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            return data;
        }
        return data;
    }


    public static JSONArray stringToJSONArray(String data) {
        JSONArray json = null;
        try {
            json = new JSONArray(data);
        } catch (JSONException e) {

            e.printStackTrace();
        }
        return json;
    }

    public static JSONObject stringToJSONObject(String data) {
        JSONObject json = null;
        try {
            json = new JSONObject(data);
        } catch (JSONException e) {

            e.printStackTrace();
        }
        return json;
    }

    public static JSONArray cachedJSONArray(String url) {
        return stringToJSONArray(getCache(url));
    }

    public static JSONObject cachedJSONObject(String url) {
        return stringToJSONObject(getCache(url));

    }


    public static boolean setPermitCheck(String permit_check, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("permit_check", permit_check);
        return editor.commit();
    }

    public static String getPermitCheck(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        return mSharedPreferences.getString("permit_check", "null");
    }


    public static boolean setToken(String token, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("token", token);
        return editor.commit();
    }

    public static String getToken(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        return mSharedPreferences.getString("token", "null");
    }


    public static boolean setMB_ID(String mb_id, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("mb_id", mb_id);
        return editor.commit();
    }

    public static String getMB_ID(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        return mSharedPreferences.getString("mb_id", "null");
    }

    public static boolean setID(String token, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("id", token);
        return editor.commit();
    }

    public static String getID(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        return mSharedPreferences.getString("id", "null");
    }

    public static boolean setNick(String nick, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("nick", nick);
        return editor.commit();
    }

    public static String getNick(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        return mSharedPreferences.getString("nick", "null");
    }

    public static boolean setName(String name, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("name", name);
        return editor.commit();
    }

    public static String getName(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        return mSharedPreferences.getString("name", "null");
    }

    public static boolean setLoginMode(String loginmode, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("loginmode", loginmode);
        return editor.commit();
    }

    public static String getLoginMode(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        return mSharedPreferences.getString("loginmode", "null");
    }

    public static boolean setProfile(String profile, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("profile", profile);
        return editor.commit();
    }

    public static String getProfile(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        return mSharedPreferences.getString("profile", "null");
    }

    public static boolean setMImg_login(String img_login, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("img_login", img_login);
        return editor.commit();
    }

    public static String getMImg_login(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        return mSharedPreferences.getString("img_login", "null");
    }


    public static boolean setMImg_join(String img_join, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("img_join", img_join);
        return editor.commit();
    }

    public static String getMImg_join(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        return mSharedPreferences.getString("img_join", "null");
    }


    public static boolean setMLogin_guest(String login_guest, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("login_guest", login_guest);
        return editor.commit();
    }

    public static String getMLogin_guest(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        return mSharedPreferences.getString("login_guest", "null");
    }


    public static boolean setMLogin_katalk(String login_katalk, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("login_katalk", login_katalk);
        return editor.commit();
    }

    public static String getMLogin_katalk(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        return mSharedPreferences.getString("login_katalk", "null");
    }


    public static boolean setMLogin_facebook(String login_facebook, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("login_facebook", login_facebook);
        return editor.commit();
    }

    public static String getMLogin_facebook(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        return mSharedPreferences.getString("login_facebook", "null");
    }


    public static boolean setMAd_main(String ad_main, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("ad_main", ad_main);
        return editor.commit();
    }

    public static String getMAd_main(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        return mSharedPreferences.getString("ad_main", "null");
    }


    public static boolean setMAd_foot(String ad_foot, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("ad_foot", ad_foot);
        return editor.commit();
    }

    public static String getMAd_foot(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        return mSharedPreferences.getString("ad_foot", "null");
    }


    public static boolean setMAd_Main_YN(String ad_main_yn, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("ad_main_yn", ad_main_yn);
        return editor.commit();
    }

    public static String getMAd_Main_YN(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        return mSharedPreferences.getString("ad_main_yn", "null");
    }


    public static boolean setMAd_foot_YN(String ad_foot_yn, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("ad_foot_yn", ad_foot_yn);
        return editor.commit();
    }

    public static String getMAd_foot_YN(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        return mSharedPreferences.getString("ad_foot_yn", "null");
    }


    public static boolean setMPop_YN(String pop_yn, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("pop_yn", pop_yn);
        return editor.commit();
    }

    public static String getMPop_YN(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        return mSharedPreferences.getString("pop_yn", "null");
    }


    public static boolean setMPop_img_src(String pop_img_src, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("pop_img_src", pop_img_src);
        return editor.commit();
    }

    public static String getMPop_img_src(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        return mSharedPreferences.getString("pop_img_src", "null");
    }


    public static boolean setMPop_t_url(String pop_t_url, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("pop_t_url", pop_t_url);
        return editor.commit();
    }

    public static String getMPop_t_url(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        return mSharedPreferences.getString("pop_t_url", "null");
    }


    public static boolean setMPop_browser(String pop_browser, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("pop_browser", pop_browser);
        return editor.commit();
    }

    public static String getMPop_browser(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        return mSharedPreferences.getString("pop_browser", "null");
    }


    public static boolean setMEnd_type(String end_type, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("end_type", end_type);
        return editor.commit();
    }

    public static String getMEnd_type(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        return mSharedPreferences.getString("end_type", "null");
    }


    public static boolean setMEnd_percent(String end_percent, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("end_percent", end_percent);
        return editor.commit();
    }

    public static String getMEnd_percent(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        return mSharedPreferences.getString("end_percent", "null");
    }


    public static boolean setMEndImgSrc(String endimgsrc, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("endimgsrc", endimgsrc);
        return editor.commit();
    }

    public static String getMEndImgSrc(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        return mSharedPreferences.getString("endimgsrc", "null");
    }


    public static boolean setMEndImgTurl(String endimgTurl, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("endimgTurl", endimgTurl);
        return editor.commit();
    }

    public static String getMEndImgTurl(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        return mSharedPreferences.getString("endimgTurl", "null");
    }


    public static boolean setMEndImgBrwser(String endimgBrwser, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("endimgBrwser", endimgBrwser);
        return editor.commit();
    }

    public static String getMEndImgBrwser(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        return mSharedPreferences.getString("endimgBrwser", "null");
    }


    public static boolean setMKakao_YN(String mkakao_YN, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("mkakao_YN", mkakao_YN);
        return editor.commit();
    }

    public static String getMKakao_YN(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        return mSharedPreferences.getString("mkakao_YN", "null");
    }


    public static boolean setMKakao_txt(String mkakao_txt, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("mkakao_txt", mkakao_txt);
        return editor.commit();
    }

    public static String getMKakao_txt(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        return mSharedPreferences.getString("mkakao_txt", "null");
    }


    public static boolean setShare(String share, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("share", share);
        return editor.commit();
    }

    public static String getShare(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        return mSharedPreferences.getString("share", "null");
    }

    public static boolean setBadge(String badge, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("badge", badge);
        return editor.commit();
    }

    public static String getBadge(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        return mSharedPreferences.getString("badge", "null");
    }


    public static boolean setMKakao_img_src(String mkakao_img_src, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("mkakao_img_src", mkakao_img_src);
        return editor.commit();
    }

    public static String getMKakao_img_src(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("settings", 0);
        return mSharedPreferences.getString("mkakao_img_src", "null");
    }

}