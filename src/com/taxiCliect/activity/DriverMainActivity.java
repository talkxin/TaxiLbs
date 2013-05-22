package com.taxiCliect.activity;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.nio.channels.Selector;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.ItemizedOverlay;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKBusLineResult;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKPlanNode;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKSuggestionResult;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.MyLocationOverlay;
import com.baidu.mapapi.OverlayItem;
import com.baidu.mapapi.Projection;
import com.baidu.mapapi.RouteOverlay;
import com.taxiCliect.activity.DriverMainActivity.OverItemT.NewOverlayItem;
import com.taxiCliect.activity.driver.DriverAppointmentActivity;
import com.taxiCliect.activity.driver.OverService;
import com.taxiCliect.activity.map.MapMain;
import com.taxiCliect.activity.setup.SetupActivity;
import com.taxiCliect.module.DriverInfo;
import com.taxiCliect.module.TaxiService;
import com.taxiCliect.module.TaxiUser;
import com.taxiCliect.module.TrackService;
import com.taxiCliect.service.Bootservice;
import com.taxiCliect.service.binder.ServiceBinder;
import com.taxiCliect.util.Annotation.JsonToAction;
import com.taxiCliect.util.blueTooth.BluetoothChat;
import com.taxiCliect.util.blueTooth.ChatModule;
import com.taxiCliect.util.blueTooth.ObjectUtil;
import com.taxiCliect.util.db.Database2Pojo;
import com.taxiCliect.util.json.JsonToObject;
import com.taxiCliect.util.postAction.PostToAction;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.provider.Settings;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint({ "NewApi", "NewApi" })
public class DriverMainActivity extends MapActivity {
	private GridView grid;
	private DisplayMetrics localDisplayMetrics;
	private View view;
	private int itemId;
	AppMain app;
	private MapView mapView;
	private MKSearch mkSearch;
	// 弹出搜索的对话框
	private AlertDialog.Builder builder;
	private ProgressDialog progressDialog;
	// 定位
	private LocationListener mLocationListener;
	// 自己的位置
	private GeoPoint myPt;
	private MKAddrInfo myAddr = null;
	private MyLocationOverlay mLocationOverlay; // 定位图层
	// 各种对象
	private TaxiUser taxiUser;
	private DriverInfo driverInfo;
	// 数据库对象
	private Database2Pojo db = null;
	// 随即打车开启按钮
	private Button suijiButton;
	// 公里数
	private double mKmRule = 0;
	// service
	private Bootservice bootservice;
	// 交互对象
	public static ServiceBinder serviceBinder;
	// 蓝牙地址
	private String buleTooth;
	private ImageButton automatic_route;
	private ImageButton automatic_route1;
	// 弹出的对话框View
	private View mpopView;
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
			DriverMainActivity.this.serviceBinder = (ServiceBinder) iBinder;
			bootservice = ((ServiceBinder) iBinder).getService();
		}
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		// 停止服务
		DriverMainActivity.this.unbindService(conn);
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		app = (AppMain) this.getApplication();
		buleTooth = BluetoothAdapter.getDefaultAdapter().getAddress();
		setContentView(R.layout.driver_main);

		// 初始化停止搜索按钮
		automatic_route = (ImageButton) findViewById(R.id.automatic_route);
		automatic_route.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
		// 判断gps是否开启
		LocationManager locationManager = (LocationManager) DriverMainActivity.this
				.getSystemService(Context.LOCATION_SERVICE);
		// 检查是否开启了
		if (!locationManager
				.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			gpsToBuilder();
		}
		// 初始化服务
		Intent intent = new Intent(this, Bootservice.class);
		this.bindService(intent, conn, BIND_AUTO_CREATE);
		if (app.mBMapMan == null) {
			app.mBMapMan = new BMapManager(this);
			app.mBMapMan.init(getString(R.string.baiduMapKey),
					new AppMain.MyGeneralListener());
		}
		app.mBMapMan.start();
		super.initMapActivity(app.mBMapMan);
		mapView = (MapView) findViewById(R.id.bmapView);
		mapView.setBuiltInZoomControls(true);
		mapView.setDrawOverlayWhenZooming(true);
		// 添加定位图层
		mLocationOverlay = new MyLocationOverlay(this, mapView);
		mapView.getOverlays().add(mLocationOverlay);
		mapView.invalidate();
		// 初始化菜单
		localDisplayMetrics = getResources().getDisplayMetrics();
		grid = (GridView) this.findViewById(R.id.my_grid);
		ListAdapter adapter = new GridAdapter(this);
		grid.setAdapter(adapter);
		grid.setOnItemClickListener(mOnClickListener);
		// 初始化搜索监听
		mkSearch = new MKSearch();
		mkSearch.init(app.mBMapMan, new MKSearchListener() {

			@Override
			public void onGetWalkingRouteResult(MKWalkingRouteResult arg0,
					int arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetTransitRouteResult(MKTransitRouteResult arg0,
					int arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetPoiResult(MKPoiResult res, int type, int error) {
				// TODO Auto-generated method stub
				// 错误号可参考MKEvent中的定义
				if (error != 0 || res == null) {
					Toast.makeText(DriverMainActivity.this, "抱歉，未找到结果",
							Toast.LENGTH_LONG).show();
				} else {
					// 将地图移动到第一个POI中心点
					if (res.getCurrentNumPois() > 0) {
						// 将poi结果显示到地图上
						// for (int i = 0; i < res.getAllPoi().size(); i++) {
						// System.out.println(res.getAllPoi().get(0).pt);
						// }
						// System.out.println("总结果数为：" + res.getNumPois());
						// System.out.println("当前页结果数" +
						// res.getCurrentNumPois());
						// System.out.println("总页数" + res.getNumPages());
						// System.out.println("当前页索引" + res.getPageIndex());
						// System.out.println("当前城市结果数" + res.getCityListNum());
					}
				}
			}

			@Override
			public void onGetDrivingRouteResult(MKDrivingRouteResult arg0,
					int arg1) {
				// TODO Auto-generated method stub
				// 错误号可参考MKEvent中的定义
				if (arg1 != 0 || arg0 == null) {
					Toast.makeText(DriverMainActivity.this, "抱歉，未找到结果",
							Toast.LENGTH_SHORT).show();
					return;
				}
				RouteOverlay routeOverlay = new RouteOverlay(
						DriverMainActivity.this, mapView);
				// 此处仅展示一个方案作为示例
				routeOverlay.setData(arg0.getPlan(0).getRoute(0));
				// 获取公里数保留两位小数
				mKmRule = Double.parseDouble(new DecimalFormat("#.00")
						.format(routeOverlay.mRoute.getDistance() / 1000));

				// System.out.println(routeOverlay.mRoute.getDistance());
				// mapView.getOverlays().clear();
				mapView.getOverlays().add(routeOverlay);
				// 弹出距离提示
				mapView.invalidate();
				mapView.getController().animateTo(arg0.getStart().pt);
				//
				// showAllOld();
			}

			@Override
			public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
				if (arg1 != 0) {
					String str = String.format("错误号：%d", arg1);
					return;
				}
				// String strInfo = String.format("纬度：%f 经度：%f 地址：%s\r\n",
				// arg0.geoPt.getLatitudeE6() / 1e6,
				// arg0.geoPt.getLongitudeE6() / 1e6,
				// arg0.addressComponents.city
				// + arg0.addressComponents.district
				// + arg0.addressComponents.street);
				myAddr = arg0;
			}

			@Override
			public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
				// TODO Auto-generated method stub

			}
		});
		// 注册定位事件
		mLocationListener = new LocationListener() {

			@Override
			public void onLocationChanged(Location location) {
				if (location != null) {
					myPt = new GeoPoint((int) (location.getLatitude() * 1e6),
							(int) (location.getLongitude() * 1e6));
					if (myAddr == null)
						mkSearch.reverseGeocode(myPt);
					mapView.getController().animateTo(myPt);
				}
			}
		};
		// 初始化数据库连接
		db = new Database2Pojo(this);
		// 初始化更新
		updateDriverCoordinate = new UpdateDriverCoordinate();
		// 获取用户对象并搜索数据库
		try {
			taxiUser = Bootservice.taxiUser;
			driverInfo = new DriverInfo();
			driverInfo.setUid(taxiUser.getUid());
			List<Object> driverObject = db.query2Where(driverInfo, "uid=?",
					new String[] { taxiUser.getUid().toString() });
			if (driverObject != null && driverObject.size() != 0) {
				driverInfo = (DriverInfo) driverObject.get(0);
				updateDriverCoordinate.start();
			} else {
				progressDialog = ProgressDialog.show(DriverMainActivity.this,
						"", "正在获取信息", true);
				new GetServerToDriver().start();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mapView.setDrawOverlayWhenZooming(true);
		// 获得弹框实例
		mpopView = DriverMainActivity.this.getLayoutInflater().inflate(
				R.layout.popview, null);
		mapView.addView(mpopView, new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, null,
				MapView.LayoutParams.TOP_LEFT));
		mpopView.setVisibility(View.GONE);
		iZoom = mapView.getZoomLevel();

		automatic_route = (ImageButton) findViewById(R.id.automatic_route);
		automatic_route.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				randomTaxi = false;
				automatic_route.setVisibility(View.GONE);
				automatic_route1.setVisibility(View.VISIBLE);
				Toast.makeText(DriverMainActivity.this, "已关闭自动接单",
						Toast.LENGTH_SHORT).show();
			}
		});
		automatic_route1 = (ImageButton) findViewById(R.id.automatic_route1);
		automatic_route1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				randomTaxi = true;
				automatic_route.setVisibility(View.VISIBLE);
				automatic_route1.setVisibility(View.GONE);
				Toast.makeText(DriverMainActivity.this, "已开启自动接单",
						Toast.LENGTH_SHORT).show();
			}
		});
		valuation_module = (RelativeLayout) findViewById(R.id.valuation_module);
		go_time = (EditText) findViewById(R.id.go_time);
		go_km = (EditText) findViewById(R.id.go_km);
		go_money = (EditText) findViewById(R.id.go_money);
		go_save = (Button) findViewById(R.id.go_save);
		go_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 弹出支付
				builder = new Builder(DriverMainActivity.this);
				builder.setMessage("支付方式");
				builder.setPositiveButton("现金支付", new PayTaxi());
				builder.setNegativeButton("支付宝", new PayTaxi());
				builder.show();
			}
		});
		Intent intent1 = new Intent();
		intent1.setClass(DriverMainActivity.this, DriverMainActivity.class);
		Bootservice.toNotice(DriverMainActivity.this, R.drawable.icon,
				"打开我要打车", "我要打车", "", intent1, 2);

	}

	private RelativeLayout valuation_module;
	private EditText go_time;
	private EditText go_km;
	private EditText go_money;
	private Button go_save;

	// 搜索乘客开关
	private boolean randomTaxi = true;
	private UpdateDriverCoordinate updateDriverCoordinate;
	private List<TaxiService> serviceList;
	// private Drawable marker;
	int iZoom = 0;
	int removeService = 0;

	/**
	 * 更新司机坐标并搜索乘客
	 * 
	 * @author talkliu
	 * 
	 */
	public class UpdateDriverCoordinate extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (true) {
				try {
					// 关闭弹出框
					mpopView.setVisibility(View.GONE);
					// 每5分钟上传一次
					driverInfo
							.setLat(String.valueOf(myPt.getLatitudeE6() / 1e6));
					driverInfo
							.setLon(String.valueOf(myPt.getLongitudeE6() / 1e6));
					postToAction
							.postToServer(
									DriverMainActivity.this
											.getString(R.string.serverPath)
											+ DriverMainActivity.this
													.getString(R.string.updateDriverCoordinate),
									new Object[] { driverInfo, "drivreInfo" });
					String jsonArray = postToAction
							.postToServer(
									DriverMainActivity.this
											.getString(R.string.serverPath)
											+ DriverMainActivity.this
													.getString(R.string.getRandomServiceList),
									new Object[] { "lon",
											myPt.getLongitudeE6() / 1e6 },
									new Object[] { "lat",
											myPt.getLatitudeE6() / 1e6 });
					if (jsonArray != null && !jsonArray.equals("")) {
						serviceList = new JsonToObject<TaxiService>(
								TaxiService.class).getArrayJsonList(jsonArray);
						Drawable marker = getResources().getDrawable(
								R.drawable.greenmanpic1); // 得到需要标在地图上的资源
						marker.setBounds(0, 0, marker.getIntrinsicWidth(),
								marker.getIntrinsicHeight()); // 为maker定义位置和边界
						overitem = new OverItemT(marker,
								DriverMainActivity.this);
						mapView.getOverlays().add(overitem); // 添加ItemizedOverlay实例到mMapView
						if (randomTaxi) {
							TaxiService mService = serviceList.get(0);
							Double shortDouble = getDistance(
									myPt.getLatitudeE6() / 1e6,
									myPt.getLongitudeE6() / 1e6, serviceList
											.get(0).getStartAddLat(),
									serviceList.get(0).getStartAddLon());
							for (int i = 0; i < serviceList.size(); i++) {
								Double double1 = getDistance(
										myPt.getLatitudeE6() / 1e6,
										myPt.getLongitudeE6() / 1e6,
										serviceList.get(i).getStartAddLat(),
										serviceList.get(i).getStartAddLon());
								if (double1 < shortDouble) {
									shortDouble = double1;
									mService = serviceList.get(i);
									removeService = i;
								}
							}
							// 进行申请
							acceptService(mService);
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					Thread.sleep(Long.parseLong(DriverMainActivity.this
							.getString(R.string.driverSleep)));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 通过Long值换算时间
	 * 
	 * @param time
	 * @return
	 */
	public static String[] getHourAndMinute(Long time) {
		Long miao = time / 1000;
		Long shi = miao / 3600;
		Long fen = miao / 60 >= 60 ? (miao / 60) - (60 * shi) : miao / 60;
		return new String[] { String.valueOf(shi), String.valueOf(fen) };
	}

	/**
	 * 两点的直线距离
	 * 
	 * @param lat1
	 * @param lon1
	 * @param lat2
	 * @param lon2
	 * @return
	 */
	public static double getDistance(double lat1, double lon1, double lat2,
			double lon2) {
		float[] results = new float[1];
		Location.distanceBetween(lat1, lon1, lat2, lon2, results);
		return results[0];
	}

	// 蓝牙共享对象
	public static TrackService trackServic = null;

	/**
	 * 接受一个订单
	 * 
	 * @param taxiService
	 * @throws Exception
	 */
	public synchronized void acceptService(TaxiService taxiService)
			throws Exception {
		if (trackServic == null) {
			if (!taxiService.getPassengerBlue().equals(buleTooth)) {
				taxiService.setServiceEnd(1);
				taxiService.setUid(taxiUser.getUid());
				taxiService.setDriverBlue(buleTooth);
				String pathString = DriverMainActivity.this
						.getString(R.string.serverPath)
						+ DriverMainActivity.this
								.getString(R.string.chexInService);
				String json = new PostToAction().postToServer(pathString,
						new Object[] { taxiService, "serviceObj" });
				if (json != null && !json.equals("") && !json.equals("null")) {
					// JSONObject jsonObject = new JSONObject(json);
					if (Integer.parseInt(json) > 0) {
						randomTaxi = false;
						trackServic = new TrackService();
						trackServic.setGoDate(Long.parseLong("0"));
						trackServic.setTaxiService(taxiService);
						trackServic.setMoney(0.0);
						trackServic.setUp(false);
						Bootservice.bluetoothCtrl.pair(trackServic
								.getTaxiService().getPassengerBlue(), "0000");
						new TrackThread().start();
					} else {

					}
				}
			} else {
				Toast.makeText(DriverMainActivity.this, "不能接受自己的订单",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * 接客线程
	 * 
	 * @author talkliu
	 * 
	 */
	public class TrackThread extends Thread {
		Double lon;
		Double lat;
		Double km;

		public TrackThread() {
			DriverMainActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					valuation_module.setVisibility(View.VISIBLE);
					automatic_route.setVisibility(View.GONE);
					automatic_route1.setVisibility(View.GONE);
				}
			});
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (true) {
				if (trackServic != null) {
					if (trackServic.isUp()) {
						// 记录上车时间及所在位置
						// 判断半分钟
						if (trackServic.getInstruction() == TrackService.PASSENGER_UP_VERIFICATION) {
							try {
								trackServic.setGoDate(new Long(5000));
								trackServic.setLat(myPt.getLatitudeE6() / 1e6);
								trackServic.setLon(myPt.getLongitudeE6() / 1e6);
								Thread.sleep(5000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							trackServic
									.setInstruction(TrackService.PASSENGER_UP_THROUGH);
							continue;
						} else if (trackServic.getInstruction() == TrackService.PASSENGER_UP_THROUGH) {
							// 乘客方请求请求对象
							ChatModule chatModule = new ChatModule();
							chatModule
									.setChatState(BluetoothChat.MESSAGE_TOOBJECT);
							chatModule.setChatObject(trackServic);
							// ChatModule chatModule = new ChatModule();
							// chatModule
							// .setChatState(BluetoothChat.MESSAGE_TOSTRING);
							// chatModule.setChatString("测试文字");
							try {
								Bootservice.bluetoothCtrl
										.getBluetoothChat()
										.sendMessage(
												ObjectUtil
														.getBytesFromObject(chatModule));
								// Bootservice.bluetoothCtrl
								// .getBluetoothChat()
								// .sendMessage(
								// ObjectUtil
								// .getBytesFromObject(chatModule));
							} catch (Exception e) {
								// TODO: handle exception
							}
							trackServic
									.setInstruction(TrackService.PASSENGER_UP);
						} else if (trackServic.getInstruction() == TrackService.PASSENGER_UP) {
							// 乘客方请求请求对象
							ChatModule chatModule = new ChatModule();
							chatModule
									.setChatState(BluetoothChat.MESSAGE_TOOBJECT);
							chatModule.setChatObject(trackServic);
							try {
								Bootservice.bluetoothCtrl
										.getBluetoothChat()
										.sendMessage(
												ObjectUtil
														.getBytesFromObject(chatModule));
							} catch (Exception e) {
								// TODO: handle exception
							}
						} else if (trackServic.getInstruction() == TrackService.PASSENGER_LOSE) {
							DriverMainActivity.this
									.runOnUiThread(new Runnable() {

										@Override
										public void run() {
											// TODO Auto-generated method stub
											builder = new Builder(
													DriverMainActivity.this);
											builder.setMessage("支付方式");
											builder.setPositiveButton("现金支付",
													new PayTaxi());
											builder.setNegativeButton("支付宝",
													new PayTaxi());
											builder.show();
										}
									});
						}
						// 开始更新
						lon = myPt.getLongitudeE6() / 1e6;
						lat = myPt.getLatitudeE6() / 1e6;
						km = getDistance(trackServic.getLat(),
								trackServic.getLon(), lat, lon);
						trackServic.setLat(lat);
						trackServic.setLon(lon);
						trackServic.setGoKm(km);
						trackServic.setGoDate(trackServic.getGoDate() + 20000);
						trackServic.getTaxiService().setGoTime(
								trackServic.getGoDate());
						DriverMainActivity.this.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								try {
									String date[] = getHourAndMinute(trackServic
											.getGoDate());
									go_time.setText(date[0] + "小时" + date[1]
											+ "分钟");
									go_km.setText(String.valueOf(km));
								} catch (Exception e) {
									// TODO: handle exception
								}
							}
						});
						try {
							Thread.sleep(20000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} else {
					break;
				}
			}
		}
	}

	// 提交用对象
	private PostToAction postToAction = new PostToAction();

	/**
	 * 获取司机用线程
	 * 
	 * @author talkliu
	 * 
	 */
	public class GetServerToDriver extends Thread {
		@Override
		public void run() {
			Message message = new Message();
			message.what = -1;
			while (true) {
				if (myAddr != null) {
					try {
						Context context = DriverMainActivity.this;
						String url = context.getString(R.string.serverPath)
								+ context.getString(R.string.getDriverInfo);
						String json = postToAction.postToServer(url,
								new Object[] { "uid", taxiUser.getUid() });
						if (json != null && !json.equals("")
								&& !json.equals("null")) {
							driverInfo = new JsonToObject<DriverInfo>(
									DriverInfo.class).getJsonObject(json);
							updateDriverCoordinate.start();
						} else {
							driverInfo = null;
							message.what = 1;
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					driverHandler.dispatchMessage(message);
					break;
				} else {
					if (myPt != null) {
						mkSearch.reverseGeocode(myPt);
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	// 司机注册用的Edit
	EditText reg1;
	EditText reg2;
	EditText reg3;
	EditText reg4;
	Button regdriver_save;
	Dialog dialog;
	private Handler driverHandler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			progressDialog.dismiss();
			switch (msg.what) {
			case 1:

				try {
					Looper.prepare();
					builder = new Builder(DriverMainActivity.this);
					// 需要注册
					View regvView = DriverMainActivity.this.getLayoutInflater()
							.inflate(R.layout.reg_driver, null);
					reg1 = (EditText) regvView.findViewById(R.id.driverName);
					reg2 = (EditText) regvView.findViewById(R.id.driverCompany);
					reg3 = (EditText) regvView.findViewById(R.id.plateNumber);
					reg4 = (EditText) regvView.findViewById(R.id.companyNo);
					regdriver_save = (Button) regvView
							.findViewById(R.id.regdriver_save);
					regdriver_save
							.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									if (reg1.getText().toString() == null
											|| reg1.getText().toString()
													.equals("")
											|| reg1.getText().toString()
													.equals("必填项")) {
										Toast.makeText(DriverMainActivity.this,
												"您没有填写姓名", Toast.LENGTH_LONG)
												.show();
									} else if (reg2.getText().toString() == null
											|| reg2.getText().toString()
													.equals("")
											|| reg2.getText().toString()
													.equals("必填项")) {
										Toast.makeText(DriverMainActivity.this,
												"您没有填写公司", Toast.LENGTH_LONG)
												.show();
									} else if (reg3.getText().toString() == null
											|| reg3.getText().toString()
													.equals("")
											|| reg3.getText().toString()
													.equals("必填项")) {
										Toast.makeText(DriverMainActivity.this,
												"您没有填写车牌号", Toast.LENGTH_LONG)
												.show();
									}
									// else if (reg4.getText().toString() ==
									// null
									// || reg4.getText().toString()
									// .equals("")
									// || reg4.getText().toString()
									// .equals("必填项")) {
									// Toast.makeText(DriverMainActivity.this,
									// "您没有填写工号", Toast.LENGTH_LONG)
									// .show();
									// }
									else {
										try {
											driverInfo = new DriverInfo();
											driverInfo.setUid(taxiUser.getUid());
											driverInfo
													.setDriverCity(myAddr.addressComponents.city);
											driverInfo.setDriverName(reg1
													.getText().toString());
											driverInfo.setDriverCompany(reg2
													.getText().toString());
											driverInfo.setPlateNumber(reg3
													.getText().toString());
											driverInfo.setCompanyNo(reg3
													.getText().toString());
											postToAction.postToServer(
													DriverMainActivity.this
															.getString(R.string.serverPath)
															+ DriverMainActivity.this
																	.getString(R.string.regDriver),
													new Object[] { driverInfo,
															"drivreInfo" });
											// 开始搜索和更新地理位置
											updateDriverCoordinate.start();
											dialog.dismiss();
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
								}
							});
					builder.setView(regvView);
					dialog = builder.show();
					Looper.loop();
				} catch (Exception e) {
					// TODO: handle exception
				}
				break;
			case 2:
				valuation_module.setVisibility(View.VISIBLE);
				if (randomTaxi) {
					automatic_route.setVisibility(View.GONE);
				} else {
					automatic_route1.setVisibility(View.GONE);
				}
				break;
			}

		}
	};

	@Override
	protected void onPause() {
		app.mBMapMan.getLocationManager().removeUpdates(mLocationListener);
		mLocationOverlay.disableMyLocation();
		mLocationOverlay.disableCompass(); // 关闭指南针
		// app.mBMapMan.stop();
		super.onPause();
	}

	@Override
	protected void onResume() {
		// 注册定位事件，定位后将地图移动到定位点
		app.mBMapMan.getLocationManager().requestLocationUpdates(
				mLocationListener);
		// app.mBMapMan.getLocationManager().enableProvider(
		// app.mBMapMan.getLocationManager().MK_GPS_PROVIDER);
		mLocationOverlay.enableMyLocation();
		mLocationOverlay.enableCompass(); // 打开指南针
		// app.mBMapMan.start();
		super.onResume();
	}

	/**
	 * 检测GPS开启情况
	 */
	public void gpsToBuilder() {
		builder = new Builder(DriverMainActivity.this);
		builder.setTitle(DriverMainActivity.this
				.getString(R.string.gpsErrorTitle1));
		builder.setMessage(DriverMainActivity.this
				.getString(R.string.gpsErrorMessage1));
		// 设置
		builder.setPositiveButton(
				DriverMainActivity.this.getString(R.string.gpsErrorSet1),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
					}
				});
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
				text.setText(DriverMainActivity.this
						.getString(R.string.passengItem0));
				Drawable draw = getResources().getDrawable(
						R.drawable.home_button_local);
				draw.setBounds(0, 0, draw.getIntrinsicWidth(),
						draw.getIntrinsicHeight());
				text.setCompoundDrawables(null, draw, null, null);
				break;
			}

			case 1: {
				text.setText(DriverMainActivity.this
						.getString(R.string.driver_item1));
				Drawable draw = getResources().getDrawable(
						R.drawable.home_button_search);
				draw.setBounds(0, 0, draw.getIntrinsicWidth(),
						draw.getIntrinsicHeight());
				text.setCompoundDrawables(null, draw, null, null);
				break;
			}

			case 2: {
				text.setText(DriverMainActivity.this
						.getString(R.string.driver_item2));
				Drawable draw = getResources().getDrawable(
						R.drawable.home_button_checkin);
				draw.setBounds(0, 0, draw.getIntrinsicWidth(),
						draw.getIntrinsicHeight());
				text.setCompoundDrawables(null, draw, null, null);
				break;
			}

			case 3: {
				text.setText(DriverMainActivity.this
						.getString(R.string.driver_item3));
				Drawable draw = getResources().getDrawable(
						R.drawable.home_button_promo);
				draw.setBounds(0, 0, draw.getIntrinsicWidth(),
						draw.getIntrinsicHeight());
				text.setCompoundDrawables(null, draw, null, null);
				break;
			}

			case 4: {
				text.setText(DriverMainActivity.this
						.getString(R.string.driver_item4));
				Drawable draw = getResources().getDrawable(
						R.drawable.home_button_rank);
				draw.setBounds(0, 0, draw.getIntrinsicWidth(),
						draw.getIntrinsicHeight());
				text.setCompoundDrawables(null, draw, null, null);
				break;
			}

			}

			paramView
					.setMinimumHeight((int) (96.0F * localDisplayMetrics.density));
			paramView
					.setMinimumWidth(((-12 + localDisplayMetrics.widthPixels) / 3));

			return paramView;
		}
	}

	HashMap<String, Object> map;
	Parcel data;
	Parcel reply;
	/**
	 * 设置点击事件
	 */
	private AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			// 初始化Parcel
			data = Parcel.obtain();
			reply = Parcel.obtain();
			// 放入不同的业务对象，初始放入登录用户对象以及蓝牙地址
			map = new HashMap<String, Object>();
			map.put("userModule", taxiUser);
			map.put("blueToothAdd", BluetoothAdapter.getDefaultAdapter()
					.getAddress());
			Intent intent = new Intent();
			switch (itemId) {
			case 0:
				break;
			case 1:
				// 预约列表
				if (myAddr != null) {
					intent.putExtra("userModule", taxiUser);
					intent.putExtra("city", myAddr.addressComponents.province
							+ "," + myAddr.addressComponents.city);
					intent.setClass(DriverMainActivity.this,
							DriverAppointmentActivity.class);
					startActivityForResult(intent, 1);
					overridePendingTransition(R.anim.main_enter,
							R.anim.main_exit);
				} else {
					Toast.makeText(DriverMainActivity.this, "对不起，正在定位中",
							Toast.LENGTH_LONG).show();
				}
				break;
			case 2:
				break;
			case 3:
				break;
			case 4:
				// 软件设置
				intent.putExtra("appointmentDemo", map);
				intent.setClass(DriverMainActivity.this, SetupActivity.class);
				startActivityForResult(intent, 2);
				overridePendingTransition(R.anim.main_enter, R.anim.main_exit);
				break;
			}
		}
	};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case 1:
			TaxiService taxiService = (TaxiService) data
					.getSerializableExtra("service");
			try {
				acceptService(taxiService);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 2:
			// 设置
			switch (Integer.parseInt(data.getStringExtra("setupType"))) {
			case 1:
				data.setClass(DriverMainActivity.this,
						PassengerMainActivity.class);
				startActivity(data);
				DriverMainActivity.this.finish();
				break;
			case 2:
				data.setClass(DriverMainActivity.this, LoginActivity.class);
				startActivity(data);
				DriverMainActivity.this.finish();
				break;
			}
			break;
		}
	};

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	OverItemT overitem = null;
	Button goButton;
	Button cancelButton;

	/**
	 * 构建地图上的点
	 * 
	 * @author talkliu
	 * 
	 */
	class OverItemT extends ItemizedOverlay<OverlayItem> {

		public List<NewOverlayItem> mGeoList = new ArrayList<NewOverlayItem>();
		private Drawable marker;
		private Context mContext;
		TaxiService taxiService;

		public OverItemT(Drawable marker, Context context) {
			super(boundCenterBottom(marker));

			this.marker = marker;
			this.mContext = context;

			// 用给定的经纬度构造GeoPoint，单位是微度 (度 * 1E6)
			if (serviceList != null && serviceList.size() != 0) {
				for (TaxiService service : serviceList) {
					GeoPoint geoPoint = new GeoPoint(
							(int) (service.getStartAddLat() * 1e6),
							(int) (service.getStartAddLon() * 1e6));
					NewOverlayItem newo = new NewOverlayItem();
					newo.item = new OverlayItem(geoPoint, "", "");
					newo.oldObejct = service;
					mGeoList.add(newo);
				}
			}

			populate(); // createItem(int)方法构造item。一旦有了数据，在调用其它方法前，首先调用这个方法
		}

		public void updateOverlay() {
			populate();
		}

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {

			// Projection接口用于屏幕像素坐标和经纬度坐标之间的变换
			Projection projection = mapView.getProjection();
			for (int index = size() - 1; index >= 0; index--) { // 遍历mGeoList
				OverlayItem overLayItem = getItem(index); // 得到给定索引的item

				String title = overLayItem.getTitle();
				// 把经纬度变换到相对于MapView左上角的屏幕像素坐标
				Point point = projection.toPixels(overLayItem.getPoint(), null);

				// 可在此处添加您的绘制代码
				Paint paintText = new Paint();
				paintText.setColor(Color.BLUE);
				paintText.setTextSize(15);
				canvas.drawText(title, point.x - 30, point.y, paintText); // 绘制文本
			}

			super.draw(canvas, mapView, shadow);
			// 调整一个drawable边界，使得（0，0）是这个drawable底部最后一行中心的一个像素
			boundCenterBottom(marker);
		}

		@Override
		protected OverlayItem createItem(int i) {
			// TODO Auto-generated method stub
			return mGeoList.size() > 0 ? mGeoList.get(i).item : null;
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return mGeoList.size();
		}

		@Override
		// 处理当点击事件
		protected boolean onTap(int i) {
			// 设置点击事件
			goButton = (Button) mpopView.findViewById(R.id.startCoordinate);
			cancelButton = (Button) mpopView.findViewById(R.id.endCoordinate);
			goButton.setText("预约");
			cancelButton.setText("取消");
			taxiService = (TaxiService) mGeoList.get(i).oldObejct;
			goButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					try {
						acceptService(taxiService);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mpopView.setVisibility(View.GONE);
				}
			});
			cancelButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mpopView.setVisibility(View.GONE);
				}
			});
			setFocus(mGeoList.get(i).item);
			// 更新气泡位置,并使之显示
			GeoPoint pt = mGeoList.get(i).item.getPoint();
			mapView.updateViewLayout(mpopView, new MapView.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, pt,
					MapView.LayoutParams.BOTTOM_CENTER));
			mpopView.setVisibility(View.VISIBLE);
			// ItemizedOverlayDemo.mPopView.setVisibility(View.GONE);
			return true;
		}

		@Override
		public boolean onTap(GeoPoint arg0, MapView arg1) {
			// TODO Auto-generated method stub
			// 消去弹出的气泡
			// ItemizedOverlayDemo.mPopView.setVisibility(View.GONE);
			return super.onTap(arg0, arg1);
		}

		/**
		 * 用来存储点与订单
		 * 
		 * @author talkliu
		 * 
		 */
		public class NewOverlayItem {
			OverlayItem item;
			Object oldObejct;
		}

	}

	/**
	 * 后退为退出程序
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// super.onBackPressed();
		builder = new Builder(DriverMainActivity.this);
		builder.setTitle("您是否退出程序");
		builder.setPositiveButton("退出",
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						NotificationManager nm = (NotificationManager) DriverMainActivity.this
								.getSystemService(DriverMainActivity.this.NOTIFICATION_SERVICE);
						nm.cancel(2);
						Bootservice.toStartIntent = null;
						DriverMainActivity.this.finish();
					}

				});
		builder.setNeutralButton("最小化",
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						onBack();
					}
				});
		builder.setNegativeButton("取消", null);
		builder.show();
	}

	public void onBack() {
		super.onBackPressed();
	}

	/**
	 * 现金支付
	 * 
	 * @author talkliu
	 * 
	 */
	public class PayTaxi implements
			android.content.DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			// TODO Auto-generated method stub
			// 获取切断的蓝牙地址
			String addr = trackServic.getTaxiService().getPassengerBlue();
			// 恢复自动筛选
			randomTaxi = true;
			valuation_module.setVisibility(View.GONE);
			automatic_route.setVisibility(View.VISIBLE);
			automatic_route1.setVisibility(View.GONE);
			// 进行申请完成
			try {
				trackServic.getTaxiService().setEndAdd(
						myPt.getLatitudeE6() / 1e6 + ","
								+ myPt.getLongitudeE6() / 1e6);
				new OverService(trackServic.getTaxiService(),
						DriverMainActivity.this).start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 清空缓存切断连接
			trackServic = null;
			// 切断连接
			Bootservice.bluetoothCtrl.getBluetoothChat().stopService();
			// 取消配对
			Bootservice.bluetoothCtrl.endPir(addr);
			// 删除地图上的点
			serviceList.remove(removeService);
			mapView.getOverlays().remove(overitem);
			Drawable marker = getResources().getDrawable(
					R.drawable.greenmanpic1); // 得到需要标在地图上的资源
			marker.setBounds(0, 0, marker.getIntrinsicWidth(),
					marker.getIntrinsicHeight()); // 为maker定义位置和边界
			overitem = new OverItemT(marker, DriverMainActivity.this);
			mapView.getOverlays().add(overitem); // 添加ItemizedOverlay实例到mMapView
		}
	}
}
