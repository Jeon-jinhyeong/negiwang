package com.negiwang.android.App;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.facebook.FacebookSdk;
import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;
import com.negiwang.android.MyOncSignalMessagingService;
import com.onesignal.OneSignal;

//import com.facebook.FacebookSdk;
//import com.kakao.Session;

//import android.support.multidex.MultiDex;

public class AppController extends Application {
	private static volatile AppController instance = null;
	private RequestQueue mRequestQueue;
	public static final String TAG = AppController.class.getSimpleName();

	private static class KakaoSDKAdapter extends KakaoAdapter {

		@Override
		public ISessionConfig getSessionConfig() {
			return new ISessionConfig() {
				@Override
				public AuthType[] getAuthTypes() {
					return new AuthType[] {AuthType.KAKAO_LOGIN_ALL};
				}

				@Override
				public boolean isUsingWebviewTimer() {
					return false;
				}

				@Override
				public boolean isSecureMode() {
					return false;
				}

				@Override
				public ApprovalType getApprovalType() {
					return ApprovalType.INDIVIDUAL;
				}

				@Override
				public boolean isSaveFormData() {
					return true;
				}
			};
		}

		@Override
		public IApplicationConfig getApplicationConfig() {
			return new IApplicationConfig() {
				@Override
				public Context getApplicationContext() {
					return AppController.getGlobalApplicationContext();
				}
			};
		}
	}

	public static AppController getGlobalApplicationContext() {
		if(instance == null)
			throw new IllegalStateException("this application does not inherit com.kakao.GlobalApplication");
		return instance;
	}


	@Override
	public void onCreate() {
		super.onCreate();

		instance = this;
		KakaoSDK.init(new KakaoSDKAdapter());
		FacebookSdk.sdkInitialize(this.getApplicationContext());


		OneSignal.startInit(this)
				.inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
				.setNotificationOpenedHandler(new MyOncSignalMessagingService())  //Custom PUSH메세지 사용
				.init();


	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}

	public static synchronized AppController getInstance() {
		return instance;
	}

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}

		return mRequestQueue;
	}




	public <T> void addToRequestQueue(Request<T> req, String tag) {
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		getRequestQueue().add(req);
	}

	public <T> void addToRequestQueue(Request<T> req) {
		req.setTag(TAG);
		getRequestQueue().add(req);
	}

	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}

}
