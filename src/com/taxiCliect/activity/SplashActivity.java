package com.taxiCliect.activity;

import com.taxiCliect.service.Bootservice;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.view.Menu;

public class SplashActivity extends Activity {
	private final int TIME_UP = 1;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == TIME_UP) {
				Intent intent = new Intent();
				intent.setClass(SplashActivity.this, LoginActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.splash_screen_fade,
						R.anim.splash_screen_hold);
				SplashActivity.this.finish();
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen_view);
		// 屏幕暂停两秒后转向下个activity
		if (Bootservice.toStartIntent != null) {
			this.startActivity(Bootservice.toStartIntent);
			this.finish();
		} else {
			new Thread() {
				public void run() {
					try {
						Thread.sleep(2000);
					} catch (Exception e) {

					}
					Message msg = new Message();
					msg.what = TIME_UP;
					handler.sendMessage(msg);
				}
			}.start();
		}
	}

}
