package com.negiwang.android.App;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;


import com.negiwang.android.MainActivity;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class GlobalFunctions {

    /** OneSignal Tag 그룹 추가 **/
    public static void OneSignal_Group_add(String Tag, String Value) {
        try {
            JSONObject tags = new JSONObject();
            tags.put(Tag, Value);
            OneSignal.sendTags(tags);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /** Toast메세지 basic **/
    public static void ShowToast(String Message, Boolean TypeLong) {
        if (TypeLong == true) {
            Toast toast = Toast.makeText(MainActivity.MyActivity, Message, Toast.LENGTH_LONG);
            toast.show();
        } else if (TypeLong == false) {
            Toast toast = Toast.makeText(MainActivity.MyActivity, Message, Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    public static String onesignalID(Context context)
    {

        /** OneSignal 에서 Player_ID값 추출 **/
        OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
        status.getPermissionStatus().getEnabled();
        status.getSubscriptionStatus().getSubscribed();
        status.getSubscriptionStatus().getUserSubscriptionSetting();
        String id = "";
        try {
            id = status.getSubscriptionStatus().getUserId();
        } catch (Exception e){
            id = "null";
        }
        return id;
    }

    /** URL에서 이미지 bitmap으로 변환 **/
    @SuppressLint("InlinedApi")
    public static Bitmap getBitmapFromURL(String src, Context context) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Resources res = context.getResources();
            int height = (int) res.getDimension(android.R.dimen.notification_large_icon_height);
            int width = (int) res.getDimension(android.R.dimen.notification_large_icon_width);
            myBitmap = Bitmap.createScaledBitmap(myBitmap, 96, 96, false);

            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    /** 웹에서 알림 아이콘 비트맵 만들기 **/
    @SuppressLint("InlinedApi")
    public static Bitmap getBitmapFromURL2(String src, Context context) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Resources res = context.getResources();
            int height = (int) res.getDimension(android.R.dimen.notification_large_icon_height);
            int width = (int) res.getDimension(android.R.dimen.notification_large_icon_width);
            myBitmap = Bitmap.createScaledBitmap(myBitmap, 720, 385, false);

            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }






}