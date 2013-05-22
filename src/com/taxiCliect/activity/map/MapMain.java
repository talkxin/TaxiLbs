package com.taxiCliect.activity.map;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.provider.Settings;
import android.text.InputType;
import android.text.Layout;
import android.text.style.BulletSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.ItemizedOverlay;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKBusLineResult;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKPlanNode;
import com.baidu.mapapi.MKPoiInfo;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKSuggestionResult;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.MyLocationOverlay;
import com.baidu.mapapi.Overlay;
import com.baidu.mapapi.OverlayItem;
import com.baidu.mapapi.PoiOverlay;
import com.baidu.mapapi.RouteOverlay;
import com.baidu.mapapi.c;
import com.taxiCliect.activity.AppMain;
import com.taxiCliect.activity.DriverMainActivity;
import com.taxiCliect.activity.LoginActivity;
import com.taxiCliect.activity.PassengerMainActivity;
import com.taxiCliect.activity.R;
import com.taxiCliect.activity.driver.DriverAppointmentActivity;
import com.taxiCliect.activity.person.OftenRoute;
import com.taxiCliect.module.TaxiRoute;
import com.taxiCliect.module.TaxiService;
import com.taxiCliect.module.TaxiUser;
import com.taxiCliect.module.TrackService;
import com.taxiCliect.service.Bootservice;
import com.taxiCliect.util.db.Database2Pojo;

public class MapMain extends MapActivity {
	// private ArrayList<GeoPoint> point;// 模拟点
	AppMain app;
	private MapView mapView;
	// private Drawable marker;
	private View popView;
	private MKSearch mkSearch;
	private GeoPoint p;
	private HashMap<String, Object> seachMap = new HashMap<String, Object>();
	private Button startButton;
	private Button endButton;
	// 弹框
	private LongClickOverlay ov;
	// 定位
	private LocationListener mLocationListener;
	// 自己的位置
	private GeoPoint myPt;
	private MKAddrInfo myAddr = null;
	private MyLocationOverlay mLocationOverlay; // 定位图层
	// 起点标志
	MyOverlay startMyOverlay;
	// 终点标志
	MyOverlay endMyOverlay;

	// 定位自己
	ImageButton my_location;
	// 将自己添加至起点
	ImageButton my_start;
	// 将自己添加至终点
	ImageButton my_end;
	// 具体的位置
	String myLocation;
	// 开始地点
	EditText start_text;
	// 结束地点
	EditText end_text;
	// 查询开始
	ImageButton seach_start;
	// 查询结束
	ImageButton seach_end;
	// 保存
	ImageButton save_route;
	// 弹出搜索的对话框
	private AlertDialog.Builder builder;
	private ProgressDialog progressDialog;
	// 存放listView的View
	private View poiView;
	// 存放数据的List
	private List<MKPoiInfo> requestList = null;
	// 存放listView的
	private ListView poiListView;
	// 操作ListView
	private ListAdapter resultAdapter = null;
	// 选择查找的是起点还是终点
	private int isStartTosearch;
	// 用于接收show出来的对话框
	private Dialog dialog;
	// 公里数
	private double mKmRule = 0;

	// 显示路径长度的泡
	View mRuleView = null;
	// 操作路径的Text
	TextView popTextView;
	// 保存路径
	Button popSaveButton;
	// 关闭pop
	Button cancelPopButton;
	// 数据库连接
	public Database2Pojo db = null;
	// 传入的对象
	private HashMap<String, Object> appointmentDemo;

	private RelativeLayout valuation_module;
	private EditText go_time;
	private EditText go_km;
	private EditText go_money;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		app = (AppMain) this.getApplication();
		setContentView(R.layout.map_main);
		// 判断gps是否开启
		LocationManager locationManager = (LocationManager) MapMain.this
				.getSystemService(Context.LOCATION_SERVICE);
		// 检查是否开启了
		if (!locationManager
				.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			gpsToBuilder();
		}
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
		// 添加长按overlay
		ov = new LongClickOverlay(this);
		mapView.getOverlays().add(ov);
		mapView.invalidate();
		// 初始化泡泡弹窗
		popView = getLayoutInflater().inflate(R.layout.popview, null);
		mapView.addView(popView, new MapView.LayoutParams(
				MapView.LayoutParams.WRAP_CONTENT,
				MapView.LayoutParams.WRAP_CONTENT, null,
				MapView.LayoutParams.BOTTOM_CENTER));
		popView.setVisibility(View.GONE);
		// 初始化搜索框
		start_text = (EditText) findViewById(R.id.start_text);
		end_text = (EditText) findViewById(R.id.end_text);

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
					Toast.makeText(MapMain.this, "抱歉，未找到结果", Toast.LENGTH_LONG)
							.show();
					requestList = new ArrayList();
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
						poiNumPages = res.getNumPages();
						requestList = res.getAllPoi();
					}
				}
			}

			@Override
			public void onGetDrivingRouteResult(MKDrivingRouteResult arg0,
					int arg1) {
				// 停止保险线程
				isDriverInsurance = false;
				progressDialog.dismiss();
				// TODO Auto-generated method stub
				// 错误号可参考MKEvent中的定义
				if (arg1 != 0 || arg0 == null) {
					Toast.makeText(MapMain.this, "抱歉，未找到结果", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				RouteOverlay routeOverlay = new RouteOverlay(MapMain.this,
						mapView);
				// 此处仅展示一个方案作为示例
				routeOverlay.setData(arg0.getPlan(0).getRoute(0));
				// 获取公里数保留两位小数
				mKmRule = Double.parseDouble(new DecimalFormat("#.00")
						.format(routeOverlay.mRoute.getDistance() / 1000));

				// System.out.println(routeOverlay.mRoute.getDistance());
				// mapView.getOverlays().clear();
				// 清除起点终点标志
				if (mapView.getOverlays().indexOf(startMyOverlay) != -1) {
					mapView.getOverlays().remove(startMyOverlay);
				} else if (mapView.getOverlays().indexOf(endMyOverlay) != -1) {
					mapView.getOverlays().remove(endMyOverlay);
				}
				// 重置弹出框
				if (mapView.getOverlays().get(mapView.getOverlays().size() - 1) instanceof RouteOverlay) {
					mapView.getOverlays().remove(
							mapView.getOverlays().size() - 1);
				}
				mapView.getOverlays().add(routeOverlay);
				// 弹出距离提示
				showRulePop(((MKPlanNode) seachMap.get("start")).pt);
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
				if (ptEquals(arg0.geoPt, myPt)) {
					myAddr = arg0;
					return;
				}
				TextView textView = (TextView) popView
						.findViewById(R.id.coordinateName);
				myLocation = (arg0.addressComponents.city != null ? arg0.addressComponents.city
						: "")
						+ (arg0.addressComponents.district != null ? arg0.addressComponents.district
								: "")
						+ (arg0.addressComponents.street != null ? arg0.addressComponents.street
								: "")
						+ (arg0.addressComponents.streetNumber != null ? arg0.addressComponents.streetNumber
								: "");
				textView.setText(myLocation);
				MapView.LayoutParams geoLP = (MapView.LayoutParams) popView
						.getLayoutParams();
				geoLP.point = p;
				mapView.updateViewLayout(popView, geoLP);
				popView.setVisibility(View.VISIBLE);
				mapView.invalidate();
			}

			@Override
			public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
				// TODO Auto-generated method stub

			}
		});
		startButton = (Button) popView.findViewById(R.id.startCoordinate);
		endButton = (Button) popView.findViewById(R.id.endCoordinate);
		startButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startLocation(p, true);
			}
		});
		endButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				endLocation(p, true);
			}
		});
		// 定位至自己
		my_location = (ImageButton) findViewById(R.id.my_location);
		my_location.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (myPt != null)
					mapView.getController().animateTo(myPt);
				else {
					if (myPt != null) {
						mkSearch.reverseGeocode(myPt);
					}
					Toast.makeText(MapMain.this, "对不起，正在定位中", Toast.LENGTH_LONG)
							.show();
				}
			}
		});
		// 将自己添加至起点
		my_start = (ImageButton) findViewById(R.id.my_start);
		my_start.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (myPt != null && myAddr != null) {
					startLocation(myPt, false);
				} else {
					if (myPt != null)
						mkSearch.reverseGeocode(myPt);
					Toast.makeText(MapMain.this, "对不起，正在定位中", Toast.LENGTH_LONG)
							.show();
				}
			}
		});
		my_end = (ImageButton) findViewById(R.id.my_end);
		my_end.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (myPt != null && myAddr != null) {
					endLocation(myPt, false);
				} else {
					Toast.makeText(MapMain.this, "对不起，正在定位中", Toast.LENGTH_LONG)
							.show();
				}
			}
		});
		// 定义查找起点的事件
		seach_start = (ImageButton) findViewById(R.id.seach_start_button);
		seach_start.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (myPt != null) {
					requestList = null;
					isStartTosearch = 1;
					progressDialog = ProgressDialog.show(MapMain.this, "",
							"正在查询", true);
					new PoiThread().start();
					mkSearch.setPoiPageCapacity(50);
					mkSearch.poiSearchInCity(myAddr.addressComponents.city,
							start_text.getText().toString());
				} else {
					Toast.makeText(MapMain.this, "对不起，正在定位中", Toast.LENGTH_LONG)
							.show();
				}
			}
		});
		// 定义查找终点的事件
		seach_end = (ImageButton) findViewById(R.id.seach_end_button);
		seach_end.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (myPt != null) {
					requestList = null;
					isStartTosearch = 2;
					progressDialog = ProgressDialog.show(MapMain.this, "",
							"正在查询", true);
					new PoiThread().start();
					mkSearch.setPoiPageCapacity(50);
					mkSearch.poiSearchInCity(myAddr.addressComponents.city,
							end_text.getText().toString());
				} else {
					Toast.makeText(MapMain.this, "对不起，正在定位中", Toast.LENGTH_LONG)
							.show();
				}
			}
		});
		// 初始化距离pop
		// 显示路径长度的泡
		mRuleView = getLayoutInflater().inflate(R.layout.seach_popview, null);
		// 将pop加入
		mapView.addView(mRuleView, new MapView.LayoutParams(
				MapView.LayoutParams.WRAP_CONTENT,
				MapView.LayoutParams.WRAP_CONTENT, null,
				MapView.LayoutParams.BOTTOM_CENTER));
		// 操作路径的Text
		popTextView = (TextView) mRuleView.findViewById(R.id.pop_distance);
		// 保存路径
		popSaveButton = (Button) mRuleView.findViewById(R.id.save_pop);
		popSaveButton.setOnClickListener(saveListener);
		// 关闭pop
		cancelPopButton = (Button) mRuleView.findViewById(R.id.cancel_pop);
		// 注册保存路径按钮事件
		// 注册关闭路径pop按钮事件
		cancelPopButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mRuleView.setVisibility(View.GONE);
			}
		});
		// 注册数据库
		db = new Database2Pojo(MapMain.this);
		// 获取传入的对象
		appointmentDemo = (HashMap<String, Object>) getIntent()
				.getSerializableExtra("appointmentDemo");
		save_route = (ImageButton) findViewById(R.id.save_route);
		save_route.setOnClickListener(saveListener);

		// 获取计价模式
		valuation_module = (RelativeLayout) findViewById(R.id.valuation_module);
		go_time = (EditText) findViewById(R.id.go_time);
		go_km = (EditText) findViewById(R.id.go_km);
		go_money = (EditText) findViewById(R.id.go_money);
		// 初始化地图
		startMapType();
		// 注册定位事件
		mLocationListener = new LocationListener() {

			@Override
			public void onLocationChanged(Location location) {
				if (location != null) {
					myPt = new GeoPoint((int) (location.getLatitude() * 1e6),
							(int) (location.getLongitude() * 1e6));
					mkSearch.reverseGeocode(myPt);
					if (toMypt) {
						mapView.getController().animateTo(myPt);
						toMypt = false;
					}
					switch ((Integer) appointmentDemo.get("type")) {
					case 4:
						MKPlanNode start = new MKPlanNode();
						start.pt = myPt;
						seachMap.put("start", start);
						break;
					}
				}
			}
		};
	}

	// 只定位一次
	private boolean toMypt = true;

	/**
	 * 检测GPS开启情况
	 */
	public void gpsToBuilder() {
		builder = new Builder(MapMain.this);
		builder.setTitle(MapMain.this.getString(R.string.gpsErrorTitle1));
		builder.setMessage(MapMain.this.getString(R.string.gpsErrorMessage1));
		// 设置
		builder.setPositiveButton(
				MapMain.this.getString(R.string.gpsErrorSet1),
				new DialogInterface.OnClickListener() {

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

	// 计价器
	private TrackService trackService;

	/**
	 * 初始化地图，选择是常用路线，还是预约打车，或修改路线
	 */
	public void startMapType() {
		String[] start;
		String[] end;
		MKPlanNode startPt;
		MKPlanNode endPt;
		switch ((Integer) appointmentDemo.get("type")) {
		case 2:
			// 修改常用路线，初始化起点终点
			TaxiRoute taxiRoute = (TaxiRoute) appointmentDemo
					.get("updateRoute");
			start = taxiRoute.getStartAdd().split(",");
			startPt = new MKPlanNode();
			startPt.pt = new GeoPoint(
					(int) (Double.parseDouble(start[0]) * 1e6),
					(int) (Double.parseDouble(start[1]) * 1e6));
			end = taxiRoute.getEndAdd().split(",");
			endPt = new MKPlanNode();
			endPt.pt = new GeoPoint((int) (Double.parseDouble(end[0]) * 1e6),
					(int) (Double.parseDouble(end[1]) * 1e6));
			seachMap.put("start", startPt);
			seachMap.put("startAdr", taxiRoute.getStartStr());
			seachMap.put("end", endPt);
			seachMap.put("endAdr", taxiRoute.getEndStr());
			start_text.setText(taxiRoute.getStartStr());
			end_text.setText(taxiRoute.getEndStr());
			new SearchDriverInsurance(startPt, endPt).start();
			// mkSearch.drivingSearch(null, startPt, null, endPt);
			break;
		case 4:
			// 共乘预约，锁定当前点，使起点不被修改
			start_text.setInputType(InputType.TYPE_NULL);
			break;
		case 5:
			start_text.setInputType(InputType.TYPE_NULL);
			end_text.setInputType(InputType.TYPE_NULL);
			mapView.getOverlays().remove(ov);
			my_start.setVisibility(View.GONE);
			my_end.setVisibility(View.GONE);
			seach_start.setVisibility(View.GONE);
			seach_end.setVisibility(View.GONE);
			taxiService = (TaxiService) appointmentDemo.get("service");
			start = taxiService.getAppointmentAdd().split("@@");
			startPt = new MKPlanNode();
			startPt.pt = new GeoPoint((int) (Double.parseDouble(start[0]
					.split(",")[0]) * 1e6), (int) (Double.parseDouble(start[0]
					.split(",")[1]) * 1e6));
			end = taxiService.getAppointmentEnd().split("@@");
			endPt = new MKPlanNode();
			endPt.pt = new GeoPoint(
					(int) (Double.parseDouble(end[0].split(",")[0]) * 1e6),
					(int) (Double.parseDouble(end[0].split(",")[1]) * 1e6));
			seachMap.put("start", startPt);
			seachMap.put("startAdr", start[1]);
			seachMap.put("end", endPt);
			seachMap.put("endAdr", end[1]);
			start_text.setText(start[1]);
			end_text.setText(end[1]);
			new SearchDriverInsurance(startPt, endPt).start();
			// mkSearch.drivingSearch(null, startPt, null, endPt);
			break;
		case 6:
			// 计价模式
			my_start.setVisibility(View.GONE);
			my_end.setVisibility(View.GONE);
			my_location.setVisibility(View.GONE);
			valuation_module.setVisibility(View.VISIBLE);
			save_route.setVisibility(View.GONE);
			popSaveButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (seachMap.get("start") != null
							&& seachMap.get("end") != null) {
						builderView = getLayoutInflater().inflate(
								R.layout.pop_save_builder, null);
						// 常用路线名称
						group_route = (RelativeLayout) builderView
								.findViewById(R.id.route_name_builder_group);
						// 新增
						builder.setPositiveButton(
								"保存",
								new android.content.DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										progressDialog = ProgressDialog.show(
												MapMain.this, "", "正在保存...",
												true);
										new SaveToMap(route_name_builder
												.getText().toString()).start();
										// saveMapRule(route_name_builder.getText()
										// .toString());
									}

								});
						builder.setNegativeButton("取消", null);
						builder.show();
					}
				}
			});
			new PassengerTrackService().start();
			break;
		}
	}

	/**
	 * 计价器更新线程
	 * 
	 * @author talkliu
	 * 
	 */
	public class PassengerTrackService extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (Bootservice.passengerTrackService != null) {
				trackService = Bootservice.passengerTrackService;
				MapMain.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						String date[] = DriverMainActivity
								.getHourAndMinute(trackService.getGoDate());
						go_time.setText(date[0] + "小时" + date[1] + "分钟");
						go_km.setText(String
								.valueOf(trackService.getGoKm() == null ? "0.0"
										: trackService.getGoKm()));
					}
				});
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private boolean isDriverInsurance;

	public class SearchDriverInsurance extends Thread {
		private MKPlanNode startPt;
		private MKPlanNode endPt;

		public SearchDriverInsurance(MKPlanNode startPt, MKPlanNode endPt) {
			isDriverInsurance = true;
			this.startPt = startPt;
			this.endPt = endPt;
			progressDialog = ProgressDialog
					.show(MapMain.this, "", "正在查询", true);
		}

		@Override
		public void run() {
			while (true) {
				// TODO Auto-generated method stub
				if (isDriverInsurance) {
					mkSearch.drivingSearch(null, startPt, null, endPt);
				} else {
					break;
				}
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	View builderView;
	RelativeLayout group_route;
	RelativeLayout group_service;
	EditText route_name_builder;
	EditText service_date_builder;
	EditText service_time_builder;
	Calendar calendar;
	TaxiService taxiService;
	/**
	 * 用于保存的监听
	 */
	private View.OnClickListener saveListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (seachMap.get("start") != null && seachMap.get("end") != null) {
				builderView = getLayoutInflater().inflate(
						R.layout.pop_save_builder, null);
				// 常用路线名称
				group_route = (RelativeLayout) builderView
						.findViewById(R.id.route_name_builder_group);
				// 预约打车
				group_service = (RelativeLayout) builderView
						.findViewById(R.id.servcie_builder_group);
				// 常用路线名称
				route_name_builder = (EditText) builderView
						.findViewById(R.id.route_name_builder);
				// 预约日期
				service_date_builder = (EditText) builderView
						.findViewById(R.id.service_date_builder);
				// 预约时间
				service_time_builder = (EditText) builderView
						.findViewById(R.id.service_time_builder);
				// 用户对象
				TaxiUser taxiUser = (TaxiUser) appointmentDemo
						.get("userModule");
				// 蓝牙地址
				String blueString = String.valueOf(appointmentDemo
						.get("blueToothAdd"));
				builder = new Builder(MapMain.this);
				builder.setView(builderView);

				switch ((Integer) appointmentDemo.get("type")) {
				case 2:
					// 修改
					TaxiRoute taxiRoute = (TaxiRoute) appointmentDemo
							.get("updateRoute");
					route_name_builder.setText(taxiRoute.getName());
				case 1:
					// 新增
					builder.setPositiveButton(
							"保存",
							new android.content.DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									progressDialog = ProgressDialog.show(
											MapMain.this, "", "正在保存...", true);
									new SaveToMap(route_name_builder.getText()
											.toString()).start();
									// saveMapRule(route_name_builder.getText()
									// .toString());
								}

							});
					builder.setNegativeButton("取消", null);
					builder.show();
					break;
				case 3:
					// 预约
					calendar = Calendar.getInstance();

					service_date_builder.setInputType(InputType.TYPE_NULL);
					service_time_builder.setInputType(InputType.TYPE_NULL);
					group_route.setVisibility(View.GONE);
					group_service.setVisibility(View.VISIBLE);
					// 创建新订单对象
					taxiService = new TaxiService();
					taxiService.setDid(taxiUser.getUid());
					taxiService.setUserName(taxiUser.getUserName());
					taxiService.setUserNambr(taxiUser.getLoginName());
					taxiService.setCity(myAddr.addressComponents.province + ","
							+ myAddr.addressComponents.city);
					taxiService.setServiceType(1);
					taxiService.setServiceEnd(0);
					taxiService.setPassengerBlue(blueString);
					taxiService.setKmNumber(String.valueOf(mKmRule));
					taxiService
							.setAppointmentAdd(getLocationFromGeoPoint(((MKPlanNode) seachMap
									.get("start")).pt)
									+ "@@"
									+ seachMap.get("startAdr").toString());
					taxiService
							.setAppointmentEnd(getLocationFromGeoPoint(((MKPlanNode) seachMap
									.get("end")).pt)
									+ "@@"
									+ seachMap.get("endAdr").toString());
					calendar.setTimeInMillis(System.currentTimeMillis());
					service_date_builder
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									calendar.setTimeInMillis(System
											.currentTimeMillis());
									new DatePickerDialog(
											MapMain.this,
											new OnDateSetListener() {

												@Override
												public void onDateSet(
														DatePicker view,
														int year,
														int monthOfYear,
														int dayOfMonth) {
													// TODO
													// Auto-generated
													// method
													// stub
													calendar.set(Calendar.YEAR,
															year);
													calendar.set(
															Calendar.MONTH,
															monthOfYear);
													calendar.set(
															Calendar.DAY_OF_MONTH,
															dayOfMonth);
													service_date_builder.setText(calendar
															.get(Calendar.YEAR)
															+ "-"
															+ calendar
																	.get(Calendar.MONTH)
															+ "-"
															+ calendar
																	.get(Calendar.DAY_OF_MONTH));
												}
											}, calendar.get(Calendar.YEAR),
											calendar.get(Calendar.MONTH),
											calendar.get(Calendar.DAY_OF_MONTH))
											.show();
								}

							});
					service_time_builder
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									calendar.setTimeInMillis(System
											.currentTimeMillis());
									new TimePickerDialog(
											MapMain.this,
											new OnTimeSetListener() {

												@Override
												public void onTimeSet(
														TimePicker view,
														int hourOfDay,
														int minute) {
													// TODO
													// Auto-generated
													// method
													// stub
													calendar.setTimeInMillis(System
															.currentTimeMillis());
													calendar.set(
															Calendar.HOUR_OF_DAY,
															hourOfDay);
													calendar.set(
															Calendar.MINUTE,
															minute);
													calendar.set(
															Calendar.SECOND, 0);
													calendar.set(
															Calendar.MILLISECOND,
															0);
													if (service_date_builder
															.getText() == null
															|| service_date_builder
																	.getText()
																	.toString()
																	.equals("")) {
														service_date_builder.setText(calendar
																.get(Calendar.YEAR)
																+ "-"
																+ calendar
																		.get(Calendar.MONTH)
																+ "-"
																+ calendar
																		.get(Calendar.DAY_OF_MONTH));
													}
													service_time_builder.setText(calendar
															.get(Calendar.HOUR_OF_DAY)
															+ ":"
															+ calendar
																	.get(Calendar.MINUTE)
															+ ":00");
												}
											}, calendar
													.get(Calendar.HOUR_OF_DAY),
											calendar.get(Calendar.MINUTE), true)
											.show();
								}
							});
					builder.setPositiveButton(
							"保存",
							new android.content.DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									if (!service_time_builder.getText().equals(
											"")
											&& !service_time_builder
													.getText()
													.equals(MapMain.this
															.getString(R.string.pop_save_builder1))) {
										taxiService
												.setStartTime(service_date_builder
														.getText()
														+ " "
														+ service_time_builder
																.getText());
										progressDialog = ProgressDialog.show(
												MapMain.this, "", "正在提交", true);
										try {
											calendar.setTimeInMillis(System
													.currentTimeMillis());
											SimpleDateFormat sdf = new SimpleDateFormat(
													"yyyy-MM-dd HH:mm");
											Date nowDate = sdf.parse(calendar
													.get(Calendar.YEAR)
													+ "-"
													+ calendar
															.get(Calendar.MONTH)
													+ "-"
													+ calendar
															.get(Calendar.DAY_OF_MONTH)
													+ " "
													+ calendar
															.get(Calendar.HOUR_OF_DAY)
													+ ":"
													+ calendar
															.get(Calendar.MINUTE)
													+ ":00");
											Date upDate = sdf.parse(taxiService
													.getStartTime());
											if (upDate.getTime() > nowDate
													.getTime() + 1800000) {
												new SaveToMap("").start();
											} else {
												progressDialog.dismiss();
												Toast.makeText(MapMain.this,
														"对不起，请最少提前半小时预定",
														Toast.LENGTH_LONG)
														.show();
											}
										} catch (ParseException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
								}

							});
					builder.setNegativeButton("取消", null);
					builder.show();
					break;
				case 4:
					// 共乘随机
					taxiService = new TaxiService();
					taxiService.setDid(taxiUser.getUid());
					taxiService.setUserName(taxiUser.getUserName());
					taxiService.setUserNambr(taxiUser.getLoginName());
					taxiService.setCity(myAddr.addressComponents.province + ","
							+ myAddr.addressComponents.city);
					taxiService.setServiceType(0);
					taxiService.setServiceEnd(0);
					taxiService.setKmNumber(String.valueOf(mKmRule));
					taxiService.setStartAddLon(myPt.getLongitudeE6() / 1e6);
					taxiService.setStartAddLat(myPt.getLatitudeE6() / 1e6);
					taxiService.setEndAdd(((MKPlanNode) seachMap.get("end")).pt
							.getLatitudeE6()
							/ 1e6
							+ ","
							+ ((MKPlanNode) seachMap.get("end")).pt
									.getLongitudeE6()
							/ 1e6
							+ "@@"
							+ seachMap.get("endAdr"));
					taxiService.setPassengerBlue(blueString);
					builder = new Builder(MapMain.this);
					builder.setPositiveButton(
							"开始招车",
							new android.content.DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									progressDialog = ProgressDialog.show(
											MapMain.this, "", "正在提交", true);
									new SaveToMap("").start();
								}

							});
					builder.setNegativeButton("取消", null);
					builder.show();
					break;
				case 5:
					// 查看预约订单地图完毕返回
					taxiService = (TaxiService) appointmentDemo.get("service");
					Intent intent = new Intent();
					intent.putExtra("service", taxiService);
					intent.setClass(MapMain.this,
							DriverAppointmentActivity.class);
					setResult(1, intent);
					MapMain.this.finish();
					break;
				}
			} else {
				Toast.makeText(MapMain.this, "您没有选择路径", Toast.LENGTH_LONG)
						.show();
			}
		}
	};

	/**
	 * 用于存储的线程
	 * 
	 * @author talkliu
	 * 
	 */
	public class SaveToMap extends Thread {
		String locationNameString;

		public SaveToMap(String locationNameString) {
			this.locationNameString = locationNameString;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message msg_listData = new Message();
			TaxiRoute taxiRoute;
			// 用户
			TaxiUser taxiUser = (TaxiUser) appointmentDemo.get("userModule");
			// 起点与终点
			MKPlanNode start = (MKPlanNode) seachMap.get("start");
			MKPlanNode end = (MKPlanNode) seachMap.get("end");
			switch ((Integer) appointmentDemo.get("type")) {
			case 1:
				// 新增路线
				taxiRoute = new TaxiRoute();
				taxiRoute.setUid(taxiUser.getUid());
				taxiRoute.setCity(myAddr.addressComponents.city);
				taxiRoute.setStartAdd(getLocationFromGeoPoint(start.pt));
				taxiRoute.setStartStr(seachMap.get("startAdr").toString());
				taxiRoute.setEndAdd(getLocationFromGeoPoint(end.pt));
				taxiRoute.setEndStr(seachMap.get("endAdr").toString());
				taxiRoute.setRouteKm(String.valueOf(mKmRule));
				if (locationNameString == null || locationNameString.equals("")) {
					taxiRoute.setName("新增路线");
				} else {
					taxiRoute.setName(locationNameString);
				}
				try {
					db.save(taxiRoute);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 2:
				// 修改路线
				// 修改常用路线，初始化起点终点
				taxiRoute = (TaxiRoute) appointmentDemo.get("updateRoute");
				taxiRoute.setCity(myAddr.addressComponents.province + ","
						+ myAddr.addressComponents.city);
				taxiRoute.setStartAdd(getLocationFromGeoPoint(start.pt));
				taxiRoute.setStartStr(seachMap.get("startAdr").toString());
				taxiRoute.setEndAdd(getLocationFromGeoPoint(end.pt));
				taxiRoute.setEndStr(seachMap.get("endAdr").toString());
				taxiRoute.setRouteKm(String.valueOf(mKmRule));
				if (locationNameString == null || locationNameString.equals("")) {
					taxiRoute.setName("新增路线");
				} else {
					taxiRoute.setName(locationNameString);
				}
				try {
					db.update2Id(taxiRoute);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 3:
				// 新增预约
				try {
					Parcel data = Parcel.obtain();
					Parcel reply = Parcel.obtain();
					HashMap<String, Object> serviceMap = new HashMap<String, Object>();
					serviceMap.put("servcie", taxiService);
					data.writeMap(serviceMap);
					PassengerMainActivity.serviceBinder.transact(1, data,
							reply, 0);
				} catch (Exception e) {
					// TODO: handle exception
				}
				break;
			case 4:
				// 随机预约
				try {
					Parcel data = Parcel.obtain();
					Parcel reply = Parcel.obtain();
					HashMap<String, Object> serviceMap = new HashMap<String, Object>();
					serviceMap.put("servcie", taxiService);
					data.writeMap(serviceMap);
					PassengerMainActivity.serviceBinder.transact(1, data,
							reply, 0);
				} catch (Exception e) {
					// TODO: handle exception
				}
				break;
			}
			msg_listData.what = 3;
			handler.handleMessage(msg_listData);
		}
	}

	/**
	 * 设置起点
	 * 
	 * @param pt
	 * @param unShow
	 */
	private void startLocation(GeoPoint pt, boolean unShow) {
		if ((Integer) appointmentDemo.get("type") != 4) {
			MKPlanNode start = new MKPlanNode();
			start.pt = pt;
			if (seachMap.get("end") != null) {
				// mapView.getOverlays().remove(ov);
				popView.setVisibility(View.VISIBLE);
				new SearchDriverInsurance(start,
						(MKPlanNode) seachMap.get("end")).start();
				// mkSearch.drivingSearch(null, start, null,
				// (MKPlanNode) seachMap.get("end"));
			} else {
				if (startMyOverlay != null) {
					mapView.getOverlays().remove(startMyOverlay);
				}
				Drawable startDrawable = getResources().getDrawable(
						R.drawable.icon_nav_start_h);
				startMyOverlay = new MyOverlay(start.pt, startDrawable);
				mapView.getOverlays().add(startMyOverlay);
				mapView.getController().animateTo(start.pt);
			}
			if (unShow) {
				popView.setVisibility(View.GONE);
				start_text.setText(myLocation);
				seachMap.put("startAdr", myLocation);
				// start.name = myLocation;
			} else {
				start_text.setText("我的位置");
				seachMap.put("startAdr", myAddr.strAddr);
				// start.name = String.valueOf(myAddr.strAddr);
			}
			seachMap.put("start", start);
		}
	}

	/**
	 * 设置终点
	 * 
	 * @param pt
	 * @param unShow
	 */
	private void endLocation(GeoPoint pt, boolean unShow) {
		MKPlanNode end = new MKPlanNode();
		end.pt = pt;
		if (seachMap.get("start") != null) {
			new SearchDriverInsurance((MKPlanNode) seachMap.get("start"), end)
					.start();
			// mkSearch.drivingSearch(null, (MKPlanNode) seachMap.get("start"),
			// null, end);
		} else {
			if (endMyOverlay != null) {
				mapView.getOverlays().remove(endMyOverlay);
			}
			Drawable endDrawable = getResources().getDrawable(
					R.drawable.icon_nav_end_h);
			endMyOverlay = new MyOverlay(end.pt, endDrawable);
			mapView.getOverlays().add(endMyOverlay);
			mapView.getController().animateTo(end.pt);
		}
		if (unShow) {
			popView.setVisibility(View.GONE);
			end_text.setText(myLocation);
			seachMap.put("endAdr", myLocation);
			// end.name = myLocation;
		} else {
			end_text.setText("我的位置");
			seachMap.put("endAdr", myAddr.strAddr);
			// end.name = String.valueOf(myAddr.strAddr);
		}
		seachMap.put("end", end);
	}

	/**
	 * 显示路径长度的pop
	 * 
	 * @param pt
	 */
	private void showRulePop(GeoPoint pt) {
		mRuleView.setVisibility(View.VISIBLE);
		mapView.getController().animateTo(pt);
		if (mKmRule <= 0) {
			popTextView.setText("小于1公里");
		} else {
			popTextView.setText(mKmRule + "公里");
		}
		mapView.updateViewLayout(mRuleView, new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, pt,
				MapView.LayoutParams.BOTTOM_CENTER));
	}

	@Override
	protected void onPause() {
		app.mBMapMan.getLocationManager().removeUpdates(mLocationListener);
		mLocationOverlay.disableMyLocation();
		// mLocationOverlay.disableCompass(); // 关闭指南针
		// app.mBMapMan.stop();
		super.onPause();
	}

	@Override
	protected void onResume() {
		// 注册定位事件，定位后将地图移动到定位点
		app.mBMapMan.getLocationManager().requestLocationUpdates(
				mLocationListener);
		mLocationOverlay.enableMyLocation();
		// mLocationOverlay.enableCompass(); // 打开指南针
		// app.mBMapMan.start();
		super.onResume();
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * 显示起泡
	 * 
	 * @param x
	 * @param y
	 */
	public void showPopupWindow(int x, int y) {
		p = mapView.getProjection().fromPixels(x, y);
		mkSearch.reverseGeocode(p);
	}

	/**
	 * 隐藏起泡
	 */
	public void unShowPopupWindow() {
		popView.setVisibility(View.GONE);
	}

	/**
	 * 重新加载已有的元素
	 */
	public void showAllOld() {
		mapView.getOverlays().add(mLocationOverlay);
		mapView.getOverlays().add(ov);
	}

	/**
	 * 查询起点或终点的Item
	 * 
	 * @author talkliu
	 * 
	 */
	public class MyOverlay extends ItemizedOverlay<OverlayItem> {
		protected List<OverlayItem> mGeoList = new ArrayList<OverlayItem>();

		public MyOverlay(GeoPoint geoPoint, Drawable arg0) {
			super(boundCenter(arg0));
			mGeoList.add(new OverlayItem(geoPoint, "", ""));
			populate();
		}

		@Override
		protected OverlayItem createItem(int arg0) {
			// TODO Auto-generated method stub
			return mGeoList.get(0);
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return mGeoList.size();
		}

	}

	public final class ViewHolder {
		public TextView title;
	}

	/**
	 * 操作Listview的baseAdapter
	 * 
	 * @author talkliu
	 * 
	 */
	public class PoiResultAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public PoiResultAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (requestList != null)
				return requestList.size();
			else
				return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (requestList != null) {
				convertView = mInflater.inflate(R.layout.poi_item, null);
				PoiItem poiItem = (PoiItem) convertView;
				poiItem.setItemName(requestList.get(position).name);
			}
			// ViewHolder holder = null;
			// if (convertView == null) {
			// holder = new ViewHolder();
			// convertView = mInflater.inflate(R.layout.poi_item, null);
			// convertView.setMinimumHeight(100);
			// holder.title = (TextView) convertView
			// .findViewById(R.id.addrItem);
			// convertView.setTag(holder);
			// } else {
			// holder = (ViewHolder) convertView.getTag();
			// }
			// holder.title.setText(requestList.get(position).name);

			return convertView;
		}
	}

	/**
	 * 点击事件
	 */
	private AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			MKPoiInfo searchInfo = requestList.get(position);
			myLocation = searchInfo.name;
			switch (isStartTosearch) {
			case 1:
				startLocation(searchInfo.pt, true);
				break;

			case 2:
				endLocation(searchInfo.pt, true);
				break;
			}
			dialog.dismiss();
		}
	};
	// 总页数
	private int poiNumPages = 0;
	// 当前页
	private int poiNowPage = 0;
	/**
	 * 处理查询多线程
	 */
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				builder = new Builder(MapMain.this);
				// 添加数据
				// 初始化弹出层
				poiView = getLayoutInflater().inflate(R.layout.poi_listview,
						null);
				poiListView = (ListView) poiView
						.findViewById(R.id.pof_listView);
				resultAdapter = new PoiResultAdapter(MapMain.this);
				poiListView.setAdapter(resultAdapter);
				poiListView.setOnItemClickListener(mOnClickListener);
				progressDialog.dismiss();

				builder.setView(poiView);
				// if (requestList.size() >= 10) {
				// builder.setPositiveButton("上一页", new PageUp());
				// builder.setNegativeButton("下一页", new NextPage());
				// }
				dialog = builder.show();
				break;
			case 2:
				progressDialog.dismiss();
				break;
			case 3:
				progressDialog.dismiss();
				OftenRoute.isOncreate = true;
				MapMain.this.finish();
				break;
			}
		};
	};

	/**
	 * 上一页
	 * 
	 * @author talkliu
	 * 
	 */
	public class PageUp implements
			android.content.DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			if (poiNumPages != 0 && poiNowPage != 0) {
				poiNowPage--;
				progressDialog = ProgressDialog.show(MapMain.this, "", "正在查询",
						true);
				mkSearch.goToPoiPage(poiNowPage);
				new PoiThread().start();
			} else {
				Toast.makeText(MapMain.this, "第一页", Toast.LENGTH_LONG).show();
				new PoiThread().start();
			}
		}

	}

	/**
	 * 下一页
	 * 
	 * @author talkliu
	 * 
	 */
	public class NextPage implements
			android.content.DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			if (poiNumPages != 0 && poiNowPage != poiNumPages) {
				poiNowPage++;
				progressDialog = ProgressDialog.show(MapMain.this, "", "正在查询",
						true);
				mkSearch.goToPoiPage(poiNowPage);
				new PoiThread().start();
			} else {
				Toast.makeText(MapMain.this, "最后一页", Toast.LENGTH_LONG).show();
				new PoiThread().start();
			}
		}

	}

	/**
	 * 进行加载并弹出的进程
	 * 
	 * @author talkliu
	 * 
	 */
	public class PoiThread extends Thread {
		private Context context;

		public PoiThread() {
			context = MapMain.this;
		}

		@Override
		public void run() {
			while (true) {
				Message msg_listData = new Message();
				if (requestList != null && requestList.size() == 0) {
					msg_listData.what = 2;
					handler.sendMessage(msg_listData);
					break;
				} else if (requestList != null && requestList.size() > 0) {
					msg_listData.what = 1;
					handler.sendMessage(msg_listData);
					break;
				}
			}
		}
	}

	/**
	 * 检验路径是否相等取前7位
	 * 
	 * @param pt1
	 * @param pt2
	 * @return
	 */
	private boolean ptEquals(GeoPoint pt1, GeoPoint pt2) {
		if (pt1 != null && pt2 != null) {
			String pt1Lat = String.valueOf(pt1.getLatitudeE6()).substring(0, 6);
			String pt1Lon = String.valueOf(pt1.getLongitudeE6())
					.substring(0, 6);
			String pt2Lat = String.valueOf(pt2.getLatitudeE6()).substring(0, 6);
			String pt2Lon = String.valueOf(pt2.getLongitudeE6())
					.substring(0, 6);
			if (pt1Lat.equals(pt2Lat) && pt1Lon.equals(pt2Lon)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 通过GeoPoint获取路径
	 * 
	 * @param pt
	 * @return
	 */
	private String getLocationFromGeoPoint(GeoPoint pt) {
		return (pt.getLatitudeE6() / 1e6) + "," + (pt.getLongitudeE6() / 1e6);
	}

	/**
	 * 后退时释放掉
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		MapMain.this.finish();
	}
}