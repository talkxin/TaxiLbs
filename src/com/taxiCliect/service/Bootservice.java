package com.taxiCliect.service;

import java.util.Date;
import java.util.HashMap;

import org.json.JSONObject;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKAddrInfo;
import com.taxiCliect.activity.AppMain;
import com.taxiCliect.activity.DriverMainActivity;
import com.taxiCliect.activity.PassengerMainActivity;
import com.taxiCliect.activity.R;
import com.taxiCliect.map.GPSLocation;
import com.taxiCliect.module.MLocation;
import com.taxiCliect.module.TaxiService;
import com.taxiCliect.module.TaxiUser;
import com.taxiCliect.module.TrackService;
import com.taxiCliect.service.binder.ServiceBinder;
import com.taxiCliect.util.blueTooth.BluetoothCtrl;
import com.taxiCliect.util.blueTooth.ObjectUtil;
import com.taxiCliect.util.db.Database2Pojo;
import com.taxiCliect.util.json.JsonToObject;
import com.taxiCliect.util.postAction.PostToAction;

import android.R.integer;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.widget.TextView;

/**
 * 服务类
 * 
 * @author talkliu
 * 
 */
public class Bootservice extends Service {
	// 创建binder对象
	private ServiceBinder binder;
	// // 百度MapAPI的管理类
	// private BMapManager mBMapMan = null;
	// // 用于定位的监听器
	// LocationListener mLocationListener = null;
	// // 地图控制类
	// public AppMain appMain;// = (AppMain) this.getApplication();
	/**
	 * 蓝牙连接控制
	 */
	public static BluetoothCtrl bluetoothCtrl;

	// /**
	// * 经度
	// */
	// public Double longitude = null;
	// /**
	// * 维度
	// */
	// public Double latitude = null;
	/**
	 * 用户对象
	 */
	public static TaxiUser taxiUser = null;

	public static TrackService passengerTrackService = null;
	/**
	 * 数据库操作对象
	 */
	private Database2Pojo db;
	// 随机打车防止重复提交
	private boolean suijiOpen = true;

	@Override
	public void onCreate() {
		super.onCreate();
		binder = new ServiceBinder(this);
		// 初始化地图控制类
		// appMain = (AppMain) this.getApplication();
		// 初始化数据库
		db = new Database2Pojo(this);
		bluetoothCtrl = new BluetoothCtrl(this);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	/**
	 * 处理各种事务的总入口
	 * 
	 * @param service
	 */
	public void rootService(HashMap<String, Object> service, int serviceId) {
		// 获取用户对象
		// taxiUser = (TaxiUser) service.get("userModule");
		// 通过指令进行处理分发到不同的线程中
		TaxiService ts = null;
		// 生成新订单地址
		String pathString = Bootservice.this.getString(R.string.serverPath)
				+ Bootservice.this.getString(R.string.regService);
		// 标题
		String title = "";
		try {
			switch (serviceId) {
			case 0:
				// if (suijiOpen) {
				// 关闭提交
				// suijiOpen = false;
				// // 开启定位
				// startMapMaster();
				// // 注册定位事件
				// mLocationListener = new LocationListener() {
				//
				// @Override
				// public void onLocationChanged(Location location) {
				// if (location != null) {
				// // String strLog = String
				// // .format("您当前的位置:\r\n" + "纬度:%f\r\n" + "经度:%f",
				// // location.getLongitude(),
				// // location.getLatitude());
				// // System.out.println(strLog);
				// longitude = location.getLongitude();
				// latitude = location.getLatitude();
				// // 搜索到以后移除
				// removeLocationListener();
				// }
				// }
				// };
				// 注册监听开始定位
				// appMain.mBMapMan.getLocationManager().requestLocationUpdates(
				// mLocationListener);
				// 将随机打车指令，分发制service1
				// new TaxiService1(service).start();
				GeoPoint myPt = (GeoPoint) service.get("myPt");
				MKAddrInfo myAddr = (MKAddrInfo) service.get("myAddr");
				// MLocation moudle = new GPSLocation(latitude, longitude)
				// .transResponse();
				ts = (TaxiService) service.get("service");
				ts.setDid(taxiUser.getUid());// 用户ID
				ts.setUserNambr(taxiUser.getLoginName());
				ts.setServiceType(0);// 订单类型为普通
				ts.setPassengerBlue(service.get("blueToothAdd").toString());
				ts.setServiceEnd(0);
				ts.setServiceType(0);// 此为乘客

				// 传入服务器获取添加成功信息
				ts = new JsonToObject<TaxiService>(TaxiService.class)
						.getJsonObject(new PostToAction().postToServer(
								pathString, new Object[] { ts, "serviceObj" }));
				ts.setServiceUserType(0);
				// 将开关复位
				suijiOpen = true;
				title = "您的打车申请已成功提交,请查看";
				// }
				break;
			case 1:
				// 预约打车
				ts = (TaxiService) service.get("servcie");
				ts.setServiceEnd(0);
				ts = new JsonToObject<TaxiService>(TaxiService.class)
						.getJsonObject(new PostToAction().postToServer(
								pathString, new Object[] { ts, "serviceObj" }));
				ts.setServiceUserType(0);
				break;
			}
			// 将返回的对象存入数据库
			db.save(ts);
			// 通过成功信息，创建一条通知
			Intent intent = new Intent(Bootservice.this,
					PassengerMainActivity.class);
			intent.putExtra("taxiService", ObjectUtil.getBytesFromObject(ts));
			toNotice(Bootservice.this, R.drawable.icon, "您有条新消息", "我要打车",
					title, intent, 0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// /**
	// * 用于处理随机打车订单
	// *
	// * @author talkliu
	// *
	// */
	// public class TaxiService1 extends Thread {
	// // 计数器，当计数器超长，则停止进程，同时报位置索引错误
	// // int i = 0;
	// // 服务map
	// private HashMap<String, Object> map;
	//
	// public TaxiService1(HashMap<String, Object> map) {
	// this.map = map;
	// }
	//
	// @Override
	// public void run() {
	// // 循环监听
	// while (true) {
	// // 判断获取经纬度，获取后发送到服务器产生新订单
	// if (longitude != null && latitude != null) {
	// try {
	// MLocation moudle = new GPSLocation(latitude, longitude)
	// .transResponse();
	// String city = moudle.Region + "," + moudle.City;
	// TaxiService ts = new TaxiService();
	// ts.setDid(taxiUser.getUid());// 用户ID
	// ts.setUserNambr(taxiUser.getLoginName());
	// ts.setStartAddLon(longitude);
	// ts.setStartAddLat(latitude);
	// ts.setServiceType(0);// 订单类型为普通
	// ts.setCity(city);// 所属城市
	// ts.setPassengerBlue(map.get("blueToothAdd").toString());
	// ts.setServiceEnd(0);
	// ts.setServiceType(0);// 此为乘客
	// String pathString = Bootservice.this
	// .getString(R.string.serverPath)
	// + Bootservice.this
	// .getString(R.string.regService);
	// // 传入服务器获取添加成功信息
	// ts = new JsonToObject<TaxiService>(TaxiService.class)
	// .getJsonObject(new PostToAction().postToServer(
	// pathString, new Object[] { ts,
	// "serviceObj" }));
	// // 将返回的对象存入数据库
	// db.save(ts);
	// // 通过成功信息，创建一条通知
	// Intent intent = new Intent(Bootservice.this,
	// PassengerMainActivity.class);
	// intent.putExtra("taxiService",
	// ObjectUtil.getBytesFromObject(ts));
	// toNotice(Bootservice.this, R.drawable.icon, "您有条新消息",
	// "我要打车", "您的打车申请已成功提交,请查看", intent);
	// // 将开关复位
	// suijiOpen = true;
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// break;
	// }
	// }
	// }
	// }

	/**
	 * 用于处理蓝牙连接
	 * 
	 * @author talkliu
	 * 
	 */
	public class TaxiBlueTooth extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
		}
	}

	// /**
	// * 用于移除定位监听
	// */
	// private void removeLocationListener() {
	// if (mLocationListener != null) {
	// appMain.mBMapMan.getLocationManager().removeUpdates(
	// mLocationListener);
	// appMain.mBMapMan.stop();
	// }
	// }

	// /**
	// * 开启地图控制器
	// */
	// private void startMapMaster() {
	// // 开启地图控制器
	// if (appMain.mBMapMan == null) {
	// appMain.mBMapMan = new BMapManager(getApplication());
	// appMain.mBMapMan.init(
	// this.getApplication().getString(R.string.baiduMapKey),
	// new AppMain.MyGeneralListener());
	// }
	// appMain.mBMapMan.start();
	// }

	/**
	 * 创建一个通知
	 * 
	 * @param context
	 * @param Image
	 * @param tickerText
	 * @param title
	 * @param message
	 * @param activity
	 */
	public static void toNotice(Context context, int Image, String tickerText,
			String title, String message, Intent activity, int type) {
		// 初始化控制类
		NotificationManager nm = (NotificationManager) context
				.getSystemService(context.NOTIFICATION_SERVICE);
		// 发送的标题及时间
		Notification notification = new Notification(Image, title,
				System.currentTimeMillis());
		PendingIntent contentIntent = null;
		switch (type) {
		case 0:
			contentIntent = PendingIntent.getActivity(context, 0, activity,
					PendingIntent.FLAG_ONE_SHOT);
			notification.setLatestEventInfo(context, title, message,
					contentIntent);
			nm.notify(0, notification);
			break;
		case 1:
			contentIntent = PendingIntent.getActivity(context, 1, activity,
					PendingIntent.FLAG_UPDATE_CURRENT);
			notification.setLatestEventInfo(context, title, message,
					contentIntent);
			nm.notify(1, notification);
			break;
		case 2:
			toStartIntent = activity;
			contentIntent = PendingIntent.getActivity(context, 2, activity,
					PendingIntent.FLAG_UPDATE_CURRENT);
			notification.setLatestEventInfo(context, title, message,
					contentIntent);
			notification.flags |= Notification.FLAG_ONGOING_EVENT; // 将此通知放到通知栏的"Ongoing"即"正在运行"组中
			notification.flags |= Notification.FLAG_NO_CLEAR; // 表明在点击了通知栏中的"清除通知"后，此通知不清除，经常与FLAG_ONGOING_EVENT一起使用
			nm.notify(2, notification);
			break;
		}
	}

	public static Intent toStartIntent = null;

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		if (this.passengerTrackService != null) {
			this.bluetoothCtrl.endPir(this.passengerTrackService
					.getTaxiService().getDriverBlue());
		} else if (DriverMainActivity.trackServic != null) {
			this.bluetoothCtrl.endPir(DriverMainActivity.trackServic
					.getTaxiService().getPassengerBlue());
		}
		return super.onUnbind(intent);
	}

	// /**
	// * 服务启动时调用
	// */
	// public void onStart(Intent intent, int startId) {
	// // TODO Auto-generated method stub
	// super.onStart(intent, startId);
	// NotificationManager nm = (NotificationManager)
	// getSystemService(NOTIFICATION_SERVICE);
	// Notification notification = new Notification(
	// R.drawable.ic_action_search, "Service started",
	// System.currentTimeMillis());
	// PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
	// new Intent(this, MainActivity.class), 0);
	// // must set this for content view, or will throw a exception
	// notification.setLatestEventInfo(this, "触发的广播是", intent.getAction(),
	// contentIntent);
	// nm.notify(R.string.hello_world, notification);
	// System.out.println(intent.getExtras().getString("gpsCeshi"));
	// }
}
