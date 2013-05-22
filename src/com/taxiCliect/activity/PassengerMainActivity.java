package com.taxiCliect.activity;

import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKBusLineResult;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKSuggestionResult;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.baidu.mapapi.MapActivity;
import com.taxiCliect.activity.map.MapMain;
import com.taxiCliect.activity.person.OftenRoute;
import com.taxiCliect.activity.setup.SetupActivity;
import com.taxiCliect.module.TaxiService;
import com.taxiCliect.module.TaxiUser;
import com.taxiCliect.service.Bootservice;
import com.taxiCliect.service.binder.ServiceBinder;
import com.taxiCliect.util.blueTooth.ObjectUtil;
import com.taxiCliect.util.db.Database2Pojo;
import com.taxiCliect.util.postAction.PostToAction;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ViewDebug.IntToString;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class PassengerMainActivity extends MapActivity {
	private GridView grid;
	private DisplayMetrics localDisplayMetrics;
	private View view;
	private int itemId;
	// 弹框
	private AlertDialog.Builder builder;
	private ProgressDialog progressDialog;
	// 用户对象
	TaxiUser taxiUser = null;
	// service
	private Bootservice bootservice;
	// 交互对象
	public static ServiceBinder serviceBinder;
	/**
	 * 数据库操作对象
	 */
	private Database2Pojo db = null;
	/**
	 * 订单全局
	 */
	private TaxiService taxiService;
	// 蓝牙地址
	private String buleTooth;
	/**
	 * 初始化服务交互对象
	 */
	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			bootservice = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder iBinder) {
			// TODO Auto-generated method stub
			PassengerMainActivity.this.serviceBinder = (ServiceBinder) iBinder;
			bootservice = ((ServiceBinder) iBinder).getService();
		}
	};
	// 自己的位置
	private GeoPoint myPt;
	private MKAddrInfo myAddr = null;
	private MKSearch mkSearch;
	AppMain app;
	// 定位
	private LocationListener mLocationListener;

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		// 停止服务
		PassengerMainActivity.this.unbindService(conn);
		super.onDestroy();
	}

	@SuppressLint({ "NewApi", "NewApi", "NewApi" })
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 初始化定位
		app = (AppMain) this.getApplication();
		setContentView(R.layout.map_main);
		if (app.mBMapMan == null) {
			app.mBMapMan = new BMapManager(this);
			app.mBMapMan.init(getString(R.string.baiduMapKey),
					new AppMain.MyGeneralListener());
		}
		app.mBMapMan.start();
		super.initMapActivity(app.mBMapMan);
		// 初始化搜索
		mkSearch = new MKSearch();
		mkSearch.init(app.mBMapMan, new MKSearchListener() {

			@Override
			public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
				// TODO Auto-generated method stub
				if (arg1 != 0) {
					String str = String.format("错误号：%d", arg1);
					return;
				}
				myAddr = arg0;
			}

			@Override
			public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetDrivingRouteResult(MKDrivingRouteResult arg0,
					int arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetTransitRouteResult(MKTransitRouteResult arg0,
					int arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetWalkingRouteResult(MKWalkingRouteResult arg0,
					int arg1) {
				// TODO Auto-generated method stub

			}

		});
		// 判断gps是否开启
		LocationManager locationManager = (LocationManager) PassengerMainActivity.this
				.getSystemService(Context.LOCATION_SERVICE);
		// 检查是否开启了
		if (!locationManager
				.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			gpsToBuilder();
		}
		// 注册定位事件
		mLocationListener = new LocationListener() {

			@Override
			public void onLocationChanged(Location location) {
				if (location != null) {
					myPt = new GeoPoint((int) (location.getLatitude() * 1e6),
							(int) (location.getLongitude() * 1e6));
					mkSearch.reverseGeocode(myPt);
				}
			}
		};
		// 初始化服务
		Intent intent = new Intent(this, Bootservice.class);
		this.bindService(intent, conn, BIND_AUTO_CREATE);

		view = this.getLayoutInflater().inflate(R.layout.passenger_main, null);
		setContentView(view);

		localDisplayMetrics = getResources().getDisplayMetrics();

		grid = (GridView) view.findViewById(R.id.my_grid);
		ListAdapter adapter = new GridAdapter(this);
		grid.setAdapter(adapter);
		grid.setOnItemClickListener(mOnClickListener);
		// 初始化按钮
		builder = new Builder(PassengerMainActivity.this);
		// 获取登录的用户
		Intent tonumberIntent = getIntent();
		// 初始化数据库
		if (db == null) {
			db = new Database2Pojo(PassengerMainActivity.this);
		}
		try {
			taxiUser = Bootservice.taxiUser;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		buleTooth = BluetoothAdapter.getDefaultAdapter().getAddress();
		Intent intent1 = new Intent();
		intent1.setClass(PassengerMainActivity.this,
				PassengerMainActivity.class);
		Bootservice.toNotice(PassengerMainActivity.this, R.drawable.icon,
				"打开我要打车", "我要打车", "", intent1, 2);
	}

	@Override
	protected void onPause() {
		app.mBMapMan.getLocationManager().removeUpdates(mLocationListener);
		// mLocationOverlay.disableCompass(); // 关闭指南针
		app.mBMapMan.stop();
		super.onPause();
	}

	@Override
	protected void onResume() {
		// 注册定位事件，定位后将地图移动到定位点
		app.mBMapMan.getLocationManager().requestLocationUpdates(
				mLocationListener);
		// mLocationOverlay.enableCompass(); // 打开指南针
		app.mBMapMan.start();
		super.onResume();
	}

	// 初始化Parcel
	Parcel data;
	Parcel reply;
	HashMap<String, Object> map;
	/**
	 * 设置点击事件
	 */
	@SuppressLint({ "NewApi", "NewApi", "NewApi", "NewApi" })
	private AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			// 初始化Parcel
			data = Parcel.obtain();
			reply = Parcel.obtain();
			// 放入不同的业务对象，初始放入登录用户对象以及蓝牙地址
			map = new HashMap<String, Object>();
			map.put("userModule", taxiUser);
			map.put("blueToothAdd", buleTooth);
			Intent intent = new Intent();
			// 获取用户点击的功能
			switch (itemId) {
			case 0:
				// 判断gps是否开启
				LocationManager locationManager = (LocationManager) PassengerMainActivity.this
						.getSystemService(Context.LOCATION_SERVICE);
				// 检查是否开启了
				if (!locationManager
						.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
					gpsToBuilder();
				} else {
					builder = new Builder(PassengerMainActivity.this);
					builder.setMessage("请选择随机打车的模式");
					builder.setNegativeButton("共乘模式", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							Intent intent = new Intent();
							map.put("type", 4);
							intent.putExtra("appointmentDemo", map);
							intent.setClass(PassengerMainActivity.this,
									MapMain.class);
							PassengerMainActivity.this.startActivity(intent);
							overridePendingTransition(R.anim.main_enter,
									R.anim.main_exit);
						}
					});
					builder.setPositiveButton("直接招车", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							new ToLocation().start();
						}
					});
					builder.show();
				}
				break;

			case 1:
				// 预约打车
				map.put("type", 3);
				intent.putExtra("appointmentDemo", map);
				intent.setClass(PassengerMainActivity.this, MapMain.class);
				startActivity(intent);
				overridePendingTransition(R.anim.main_enter, R.anim.main_exit);
				break;
			case 2:
				// 预约代驾
				break;
			case 3:
				// 常用路线
				// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				// intent.putExtra("newMSG", "");
				// intent.putExtra("DelMSG", "");
				intent.putExtra("appointmentDemo", map);
				intent.putExtra("type", 3);
				intent.setClass(PassengerMainActivity.this, OftenRoute.class);
				startActivity(intent);
				overridePendingTransition(R.anim.main_enter, R.anim.main_exit);
				break;
			case 4:
				// 订单查询
				// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				// intent.putExtra("newMSG", "");
				// intent.putExtra("DelMSG", "");
				intent.putExtra("appointmentDemo", map);
				intent.putExtra("type", 4);
				intent.setClass(PassengerMainActivity.this, OftenRoute.class);
				startActivity(intent);
				overridePendingTransition(R.anim.main_enter, R.anim.main_exit);
				break;
			case 5:
				// 软件设置
				intent.putExtra("appointmentDemo", map);
				intent.setClass(PassengerMainActivity.this, SetupActivity.class);
				startActivityForResult(intent, 2);
				overridePendingTransition(R.anim.main_enter, R.anim.main_exit);
				break;
			}
		}
		// ResultActivity.this.finish();
	};

	/**
	 * 检测GPS开启情况
	 */
	public void gpsToBuilder() {
		builder = new Builder(PassengerMainActivity.this);
		builder.setTitle(PassengerMainActivity.this
				.getString(R.string.gpsErrorTitle1));
		builder.setMessage(PassengerMainActivity.this
				.getString(R.string.gpsErrorMessage1));
		// 设置
		builder.setPositiveButton(
				PassengerMainActivity.this.getString(R.string.gpsErrorSet1),
				new OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
					}
				});
		// // 转向地图，手动设置
		// builder.setNegativeButton(
		// PassengerMainActivity.this.getString(R.string.gpsErrorSet2),
		// new OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// // TODO Auto-generated method stub
		// Intent intent = new Intent();
		// // 放入不同的业务对象，初始放入登录用户对象以及蓝牙地址
		// map = new HashMap<String, Object>();
		// map.put("userModule", taxiUser);
		// map.put("blueToothAdd", buleTooth);
		// map.put("type", 4);
		// intent.putExtra("appointmentDemo", map);
		// PassengerMainActivity.this.startActivity(intent);
		// }
		// });
		builder.show();
	}

	/**
	 * 处理随机打车
	 * 
	 * @author talkliu
	 * 
	 */
	public class ToLocation extends Thread {

		@Override
		public void run() {
			Message message = new Message();
			while (true) {
				if (myPt != null && myAddr != null) {
					message.what = 1;
					handler.handleMessage(message);
					break;
				} else if (myPt != null && myAddr == null) {
					mkSearch.reverseGeocode(myPt);
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					System.out.println("");
				}
			}
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				// 随机打车
				try {
					// 判断是否已经发出了申请
					// List list = db.query2Where(new TaxiService(),
					// "serviceType=0 and serviceEnd=0", null);
					// 发现有未接单的随机打车，则发出提示，提示用户是否取消
					// if (list.size() > 0) {
					// taxiService = (TaxiService) list.get(0);
					// builder = new Builder(PassengerMainActivity.this);
					// builder.setMessage(PassengerMainActivity.this
					// .getString(R.string.suijiError1));
					// builder.setPositiveButton(PassengerMainActivity.this
					// .getString(R.string.suijiError2),
					// new OnClickListener() {
					//
					// @Override
					// public void onClick(DialogInterface dialog,
					// int which) {
					// // TODO Auto-generated method stub
					// try {
					// taxiService.setServiceEnd(3);
					// db.update2Id(taxiService);
					// new ToLocation().start();
					// } catch (Exception e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }
					// }
					// });
					// builder.setNegativeButton(PassengerMainActivity.this
					// .getString(R.string.loginErrorDon1), null);
					// builder.show();
					// } else {
					TaxiService ts = new TaxiService();
					ts.setStartAddLon(myPt.getLongitudeE6() / 1e6);
					ts.setStartAddLat(myPt.getLatitudeE6() / 1e6);
					ts.setServiceEnd(0);
					ts.setCity(myAddr.addressComponents.province + ","
							+ myAddr.addressComponents.city);
					map.put("service", ts);
					// 向service发出搜索gps并生成随机打车订单的请求
					data.writeMap(map);
					serviceBinder.transact(0, data, reply, 0);
					// Toast.makeText(PassengerMainActivity.this, "已发送",
					// Toast.LENGTH_SHORT).show();
					// }
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		};
	};

	/**
	 * 已经提交
	 */
	public void suijiServiceDon(TaxiService t) {
		taxiService = t;
		builder = new Builder(PassengerMainActivity.this);
		builder.setMessage(PassengerMainActivity.this
				.getString(R.string.suijiError1));
		builder.setPositiveButton(
				PassengerMainActivity.this.getString(R.string.suijiError2),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						try {
							taxiService.setServiceEnd(3);
							db.update2Id(taxiService);
							new ToLocation().start();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
		builder.setNegativeButton(
				PassengerMainActivity.this.getString(R.string.loginErrorDon1),
				null);
		builder.show();

	}

	public class GridAdapter extends BaseAdapter {
		private LayoutInflater inflater;

		public GridAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		public final int getCount() {
			return 6;
		}

		public final Object getItem(int paramInt) {
			return null;
		}

		public final long getItemId(int paramInt) {
			itemId = paramInt;
			return paramInt;
		}

		public View getView(int paramInt, View paramView,
				ViewGroup paramViewGroup) {
			paramView = inflater.inflate(R.layout.passenger_label_item, null);
			TextView text = (TextView) paramView
					.findViewById(R.id.activity_name);

			switch (paramInt) {
			case 0: {
				text.setText(PassengerMainActivity.this
						.getString(R.string.passengItem0));
				Drawable draw = getResources().getDrawable(
						R.drawable.home_button_local);
				draw.setBounds(0, 0, draw.getIntrinsicWidth(),
						draw.getIntrinsicHeight());
				text.setCompoundDrawables(null, draw, null, null);
				break;
			}

			case 1: {
				text.setText(PassengerMainActivity.this
						.getString(R.string.passengItem1));
				Drawable draw = getResources().getDrawable(
						R.drawable.home_button_search);
				draw.setBounds(0, 0, draw.getIntrinsicWidth(),
						draw.getIntrinsicHeight());
				text.setCompoundDrawables(null, draw, null, null);
				break;
			}

			case 2: {
				text.setText(PassengerMainActivity.this
						.getString(R.string.passengItem2));
				Drawable draw = getResources().getDrawable(
						R.drawable.home_button_checkin);
				draw.setBounds(0, 0, draw.getIntrinsicWidth(),
						draw.getIntrinsicHeight());
				text.setCompoundDrawables(null, draw, null, null);
				break;
			}

			case 3: {
				text.setText(PassengerMainActivity.this
						.getString(R.string.passengItem3));
				Drawable draw = getResources().getDrawable(
						R.drawable.home_button_promo);
				draw.setBounds(0, 0, draw.getIntrinsicWidth(),
						draw.getIntrinsicHeight());
				text.setCompoundDrawables(null, draw, null, null);
				break;
			}

			case 4: {
				text.setText(PassengerMainActivity.this
						.getString(R.string.passengItem4));
				Drawable draw = getResources().getDrawable(
						R.drawable.home_button_tuan);
				draw.setBounds(0, 0, draw.getIntrinsicWidth(),
						draw.getIntrinsicHeight());
				text.setCompoundDrawables(null, draw, null, null);
				break;
			}

			case 5: {
				text.setText(PassengerMainActivity.this
						.getString(R.string.passengItem5));
				Drawable draw = getResources().getDrawable(
						R.drawable.home_button_rank);
				draw.setBounds(0, 0, draw.getIntrinsicWidth(),
						draw.getIntrinsicHeight());
				text.setCompoundDrawables(null, draw, null, null);
				break;
			}

			// case 6: {
			// text.setText("最近浏览");
			// Drawable draw = getResources().getDrawable(
			// R.drawable.home_button_history);
			// draw.setBounds(0, 0, draw.getIntrinsicWidth(),
			// draw.getIntrinsicHeight());
			// text.setCompoundDrawables(null, draw, null, null);
			// break;
			// }
			//
			// case 7: {
			// text.setText("个人中心");
			// Drawable draw = getResources().getDrawable(
			// R.drawable.home_button_myzone);
			// draw.setBounds(0, 0, draw.getIntrinsicWidth(),
			// draw.getIntrinsicHeight());
			// text.setCompoundDrawables(null, draw, null, null);
			// break;
			// }
			// case 8: {
			// text.setText("更多");
			// Drawable draw = getResources().getDrawable(
			// R.drawable.home_button_more);
			// draw.setBounds(0, 0, draw.getIntrinsicWidth(),
			// draw.getIntrinsicHeight());
			// text.setCompoundDrawables(null, draw, null, null);
			// break;
			// }
			}

			paramView
					.setMinimumHeight((int) (96.0F * localDisplayMetrics.density));
			paramView
					.setMinimumWidth(((-12 + localDisplayMetrics.widthPixels) / 3));

			return paramView;
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * 让用户选择是否退出程序
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		builder = new Builder(PassengerMainActivity.this);
		builder.setTitle("您是否退出程序");
		builder.setPositiveButton("退出", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				// 初始化控制类
				NotificationManager nm = (NotificationManager) PassengerMainActivity.this
						.getSystemService(PassengerMainActivity.this.NOTIFICATION_SERVICE);
				nm.cancel(2);
				Bootservice.toStartIntent = null;
				PassengerMainActivity.this.finish();
			}
		});
		builder.setNeutralButton("最小化", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				onBack();
			}
		});
		builder.setNegativeButton("取消", null);
		builder.show();
	}

	// public boolean onKeyUp(int keyCode, KeyEvent event) {
	// if (keyCode == KeyEvent.KEYCODE_BACK) {
	// this.finish();
	// }
	// return super.onKeyUp(keyCode, event);
	// }
	public void onBack() {
		super.onBackPressed();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		// super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case 2:
			// 设置
			System.out.println(Integer.parseInt(data
					.getStringExtra("setupType")));
			switch (Integer.parseInt(data.getStringExtra("setupType"))) {
			case 1:
				data.setClass(PassengerMainActivity.this,
						DriverMainActivity.class);
				startActivity(data);
				PassengerMainActivity.this.finish();
				break;
			case 2:
				data.setClass(PassengerMainActivity.this, LoginActivity.class);
				startActivity(data);
				PassengerMainActivity.this.finish();
				break;
			}
			break;
		}
	}
}