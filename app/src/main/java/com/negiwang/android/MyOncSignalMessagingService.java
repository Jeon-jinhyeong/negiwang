package com.negiwang.android;


import android.content.Intent;

import com.onesignal.OSNotification;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;
import com.negiwang.android.Intro.SplashActivity;

import org.json.JSONObject;

import static com.facebook.FacebookSdk.getApplicationContext;


public class MyOncSignalMessagingService implements OneSignal.NotificationOpenedHandler {
        private String TITLE, SUBTITLE, MESSAGE, TICKERTEXT, TARGETURL, SUMMARY, TYPE, PIC, SLIDE_DEL, SOUND;

        @Override
        public void notificationOpened(OSNotificationOpenResult openedResult) {
            OSNotification notification = openedResult.notification;
            JSONObject data = notification.payload.additionalData;




            TITLE = data.optString("title", null);
            SUBTITLE = data.optString("subtitle", null);
            MESSAGE = data.optString("message", null);
            TICKERTEXT = data.optString("tickerText", null);
            TARGETURL = data.optString("targetUrl", null);
            SUMMARY = data.optString("summary", null);
            TYPE = data.optString("type", "text");
            PIC = data.optString("pic", null);
            SLIDE_DEL = data.optString("slide_del", "0");
            SOUND = data.optString("sound", "0");


            Intent MyIntent;
            try {
                if (!TARGETURL.contains("http")) {
                    MyIntent = new Intent(getApplicationContext(), SplashActivity.class);

                } else {

                        MyIntent = new Intent(getApplicationContext(), SplashActivity.class);
                        MyIntent.putExtra("target_url", TARGETURL);

                }
            } catch (Exception e) {
                MyIntent = new Intent(getApplicationContext(), SplashActivity.class);
            }

            MyIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(MyIntent);


        }
    }
