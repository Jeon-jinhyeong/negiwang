package com.negiwang.android;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.negiwang.android.Push.AlertRich_Image_Popup;
import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationDisplayedResult;
import com.onesignal.OSNotificationReceivedResult;
import com.onesignal.OneSignal;
import com.negiwang.android.helper.M;

import org.json.JSONObject;

import java.util.List;


public class MyOneSignalMessagingService extends NotificationExtenderService {
    private static final String TAG = "MyFirebaseMsgService";

    private String TYPE, PIC, TITLE, SUBTITLE, MESSAGE, TICKERTEXT, TARGETURL, SUMMARY, SLIDE_DEL, SOUND, ID;

    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult receivedResult) {
        OneSignal.enableSound(false);
        OneSignal.enableVibrate(false);


        String Badge_count = M.getBadge(this);

        if (Badge_count.equals("null")) {
            Badge_count = "0";
        }

        updateIconBadgeCount(this, Integer.parseInt(Badge_count) + 1);

        int newcount = Integer.parseInt(Badge_count) + 1;

        M.setBadge(String.valueOf(newcount), this);


        JSONObject data = receivedResult.payload.additionalData;

        TYPE = data.optString("type", null);
        TITLE = data.optString("title", null);
        SUBTITLE = data.optString("subtitle", null);
        MESSAGE = data.optString("message", null);
        TICKERTEXT = data.optString("tickerText", null);
        TARGETURL = data.optString("targetUrl", null);
        SUMMARY = data.optString("summary", null);
        ID = data.optString("id", null);
        PIC = data.optString("pic", null);
        SLIDE_DEL = data.optString("slide_del", "0");
        SOUND = data.optString("sound", "0");




        if (TYPE.equals("rich")) {

            Intent pupInt = new Intent(this, AlertRich_Image_Popup.class);
            pupInt.putExtra("mytitle", TITLE);
            pupInt.putExtra("content", MESSAGE);
            pupInt.putExtra("target_url", TARGETURL);
            pupInt.putExtra("pic", PIC);
            pupInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            this.getApplicationContext().startActivity(pupInt);

        }


        OverrideSettings overrideSettings = new OverrideSettings();
        overrideSettings.extender = new NotificationCompat.Extender() {
            @Override
            public NotificationCompat.Builder extend(NotificationCompat.Builder builder) {

                if (SLIDE_DEL.equals("1")) {
                    builder.setOngoing(true);
                } else {
                    builder.setOngoing(false);
                }

                if (SOUND.equals("1")) {
                } else {
                    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    builder.setSound(defaultSoundUri);
                }
                return null;

            }
        };


        OSNotificationDisplayedResult displayedResult = displayNotification(overrideSettings);
        Log.d("One", "Notification displayed with id: " + displayedResult.androidNotificationId);


        return true;
    }




    public void updateIconBadgeCount(Context context, int count) {

        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");

        // Component를 정의
        intent.putExtra("badge_count_package_name", context.getPackageName());
        intent.putExtra("badge_count_class_name", getLauncherClassName(context));

        // 카운트를 넣어준다.
        intent.putExtra("badge_count", count);

        // Version이 3.1이상일 경우에는 Flags를 설정하여 준다.
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            intent.setFlags(0x00000020);
        }

        // send
        context.sendBroadcast(intent);
    }

    private String getLauncherClassName(Context context) {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setPackage(context.getPackageName());

        List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(intent, 0);
        if (resolveInfoList != null && resolveInfoList.size() > 0) {
            return resolveInfoList.get(0).activityInfo.name;
        }
        return "";
    }


}

