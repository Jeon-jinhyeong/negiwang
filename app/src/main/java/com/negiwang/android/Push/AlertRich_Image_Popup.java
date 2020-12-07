package com.negiwang.android.Push;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.negiwang.android.Intro.SplashActivity;
import com.negiwang.android.R;


public class AlertRich_Image_Popup extends Activity implements View.OnClickListener {



	String mytitle, content, summary, target_url, pic, path;
	ImageButton popup_ok_btn, popup_close_btn;


	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedIntanceState){
		super.onCreate(savedIntanceState);


		requestWindowFeature(Window.FEATURE_NO_TITLE);

		Intent intent = getIntent();



		try {
			mytitle = intent.getExtras().getString("mytitle");
			content = intent.getExtras().getString("content");
			target_url = intent.getExtras().getString("target_url");
			pic = intent.getExtras().getString("pic");
		} catch (Exception e) {
			e.printStackTrace();

			Intent intent_splash = new Intent(AlertRich_Image_Popup.this, SplashActivity.class);
			startActivity(intent_splash);
			finish();
			overridePendingTransition(0, 0);
		}

		setContentView(R.layout.alertrich_image_popup);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

		TextView subject = (TextView) findViewById(R.id.subject);
		subject.setText(mytitle);


		TextView content_text = (TextView) findViewById(R.id.content);

		content_text.setVisibility(View.GONE);

		//content_text.setText(content);


		ImageView push_popup_img = (ImageView) findViewById(R.id.push_popup_img);
		ImageView push_logo = (ImageView) findViewById(R.id.logo);

		Glide.with(AlertRich_Image_Popup.this)
				.load(pic)
				.into(push_popup_img);


		popup_ok_btn = (ImageButton) findViewById(R.id.popup_ok);
		popup_ok_btn.setOnClickListener(this);
		popup_close_btn = (ImageButton) findViewById(R.id.popup_close);
		popup_close_btn.setOnClickListener(this);


	}


	//�׽�Ʈ��
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		Log.d("Activity", "onSaveInstanceState()");
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		Log.d("Activity", "onRestoreInstanceState()");
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d("Activity", "onDestroy()");
	}
	@Override
	protected void onPause() { //�Ͻ� ���� �żҵ�
		super.onPause();
		Log.d("Activity", "onPause()");
	}
	@Override
	protected void onRestart() { //����� �żҵ�
		super.onRestart();
		Log.d("Activity", "onRestart()");
	}
	@Override
	protected void onResume() { //���(��?) �żҵ�
		super.onResume();
		Log.d("Activity", "onResume()");
	}
	@Override
	protected void onStart() { //���� �żҵ�
		super.onStart();
		Log.d("Activity", "onStart()");
	}
	@Override
	protected void onStop() { //���� �żҵ�.
		super.onStop();
		Log.d("Activity", "onStop()");
	}
	@Override
public void onBackPressed(){

	}




	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.popup_ok:

				if(target_url.isEmpty()) {
					Intent intent01 = new Intent(this, SplashActivity.class);
					startActivity(intent01);

				} else {
					Intent intent01 = new Intent(this, SplashActivity.class);
					intent01.putExtra("target_url", target_url);
					startActivity(intent01);
				}
				finish();


				break;
			case R.id.popup_close:
				finish();
				break;
		}

	}


}