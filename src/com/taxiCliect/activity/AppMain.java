package com.taxiCliect.activity;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKEvent;
import com.baidu.mapapi.MKGeneralListener;
import com.taxiCliect.activity.R;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

/**
 * 应用开启时注册
 * 
 * @author talkliu
 * 
 */
public class AppMain extends Application {
	static AppMain appMain;
	// 百度MapAPI的管理类
	public BMapManager mBMapMan = null;
	boolean m_bKeyRight = true; // 授权Key正确，验证通过

	public static class MyGeneralListener implements MKGeneralListener {
		@Override
		public void onGetNetworkState(int iError) {
			Log.d("MyGeneralListener", "onGetNetworkState error is " + iError);
			Toast.makeText(AppMain.appMain.getApplicationContext(), "您的网络出错啦！",
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onGetPermissionState(int iError) {
			 Log.d("MyGeneralListener", "onGetPermissionState error is "
			 + iError);
			 if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
			 // 授权Key错误：
			 Toast.makeText(AppMain.appMain.getApplicationContext(),
			 "请在BMapApiDemoApp.java文件输入正确的授权Key！", Toast.LENGTH_LONG)
			 .show();
			 AppMain.appMain.m_bKeyRight = false;
			 }
		}
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		appMain = this;
		mBMapMan = new BMapManager(this);
		mBMapMan.init(this.getString(R.string.baiduMapKey),
				new MyGeneralListener());
		mBMapMan.getLocationManager().setNotifyInternal(10, 5);
		super.onCreate();
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		if (mBMapMan != null) {
			mBMapMan.destroy();
			mBMapMan = null;
		}
		super.onTerminate();
	}
}
