package com.taxiCliect.activity.person;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.taxiCliect.activity.PassengerMainActivity;
import com.taxiCliect.activity.R;
import com.taxiCliect.activity.map.MapMain;
import com.taxiCliect.activity.map.MapMain.SaveToMap;
import com.taxiCliect.module.TaxiRoute;
import com.taxiCliect.module.TaxiService;
import com.taxiCliect.module.TaxiUser;
import com.taxiCliect.util.base.BaseActivity;
import com.taxiCliect.util.blueTooth.ObjectUtil;
import com.taxiCliect.util.db.Database2Pojo;
import com.taxiCliect.util.json.JsonToObject;
import com.taxiCliect.util.postAction.PostToAction;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnFocusChangeListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint({ "NewApi", "NewApi" })
public class OftenRoute extends Activity {
	// private List<Map<String, Object>> filterData;
	public Database2Pojo db = null;

	/**
	 * 当前页
	 */
	private int pageNo = 0;// sqlite分页同mysql需要从0开始

	/**
	 * 页面条数
	 */
	private int pageSize = 15;
	// 读取的view
	private View loadingView;
	private ExpandableListView list;
	private boolean isEnd = false;
	// private boolean isLoadingRemoved = false;
	// 弹出搜索的对话框
	private AlertDialog.Builder builder;
	private ProgressDialog progressDialog;
	// 用于处理的view
	View builderView;
	RelativeLayout group_route;
	RelativeLayout group_service;
	EditText service_date_builder;
	EditText service_time_builder;
	EditText route_name_builder;
	Calendar calendar;

	ListViewAdapter resultAdapter = null;
	// 初始化的text
	TextView listfooter_loding;
	// 初始化的按钮
	Button lodingDataNull;
	// 加载
	ProgressBar often_route_progressBar1;
	// 用户及蓝牙map
	HashMap<String, Object> map;
	// 添加按钮
	Button often_route_save;
	// 标题
	TextView often_route_title;
	Handler handler = new Handler() {
		public void handleMessage(Message paramMessage) {
			switch (paramMessage.what) {
			case 1:
				// loadingView.setVisibility(View.GONE);
				list.removeFooterView(loadingView);
				list.addFooterView(loadingView);
				listfooter_loding.setVisibility(View.GONE);
				often_route_progressBar1.setVisibility(View.VISIBLE);
				// lodingDataNull.setVisibility(View.VISIBLE);
				break;
			case 2:
				// list.removeFooterView(loadingView);
				listfooter_loding.setVisibility(View.GONE);
				often_route_progressBar1.setVisibility(View.GONE);
				lodingDataNull.setVisibility(View.VISIBLE);
				// isLoadingRemoved = true;
				break;
			case 3:
				list.removeFooterView(loadingView);
				list.addFooterView(loadingView);
				loadingView.setVisibility(View.VISIBLE);
				often_route_progressBar1.setVisibility(View.GONE);
				listfooter_loding.setVisibility(View.GONE);
				lodingDataNull.setVisibility(View.VISIBLE);
				// isLoadingRemoved = false;
				break;
			case 4:
				// loadingView.setVisibility(View.VISIBLE);
				listfooter_loding.setVisibility(View.GONE);
				often_route_progressBar1.setVisibility(View.GONE);
				lodingDataNull.setVisibility(View.VISIBLE);
				break;

			}
		}
	};
	// 用户对象
	private TaxiUser taxiUser;

	private int activityType = -1;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (db == null) {
			db = new Database2Pojo(OftenRoute.this);
		}
		treeNodes = new ArrayList<TreeNode>();
		map = (HashMap<String, Object>) OftenRoute.this.getIntent()
				.getSerializableExtra("appointmentDemo");
		setContentView(R.layout.often_route);
		// 初始化子选项
		list = (ExpandableListView) findViewById(R.id.resultlist);
		// list.setOnItemClickListener(mOnClickListener);
		resultAdapter = new ListViewAdapter(this,
				ListViewAdapter.PaddingLeft >> 1);
		loadingView = LayoutInflater.from(this).inflate(R.layout.listfooter,
				null);
		list.addFooterView(loadingView);
		// 初始化按钮
		listfooter_loding = (TextView) findViewById(R.id.listfooter_loding);
		often_route_progressBar1 = (ProgressBar) findViewById(R.id.often_route_progressBar1);
		lodingDataNull = (Button) findViewById(R.id.lodingDataNull);
		lodingDataNull.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(gotoAddMap());
				overridePendingTransition(R.anim.main_enter, R.anim.main_exit);
			}
		});
		list.setGroupIndicator(null);// 去掉箭头
		list.setAdapter(resultAdapter);
		map = (HashMap<String, Object>) OftenRoute.this.getIntent()
				.getSerializableExtra("appointmentDemo");
		taxiUser = (TaxiUser) map.get("userModule");
		activityType = this.getIntent().getIntExtra("type", -1);
		often_route_save = (Button) findViewById(R.id.often_route_save);
		often_route_title = (TextView) findViewById(R.id.often_route_title);
		switch (activityType) {
		case 3:
			// 常用路线
			often_route_save.setText(R.string.often_route4);
			often_route_title.setText(R.string.often_route3);
			break;

		case 4:
			// 订单查询
			often_route_save.setText(R.string.ofenRouteError2);
			often_route_title.setText(R.string.often_route2);
			lodingDataNull.setText(R.string.ofenRouteError2);
			break;
		}
		// 首次加载数据
		new DataToActivity().start();
	}

	public static boolean isOncreate = false;

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if (isOncreate) {
			isResume();
			isOncreate = false;
		}
		super.onResume();
	}

	/**
	 * 从新加载数据
	 */
	public void isResume() {
		// if (treeNodes != null && treeNodes.size() != 0) {
		// 清除数据
		pageNo = 0;
		treeNodes.removeAll(treeNodes);
		resultAdapter.notifyDataSetChanged();
		listfooter_loding.setVisibility(View.VISIBLE);
		often_route_progressBar1.setVisibility(View.VISIBLE);
		lodingDataNull.setVisibility(View.GONE);
		new DataToActivity().start();
		// }
	}

	/**
	 * 转向添加点
	 * 
	 * @return
	 */
	private Intent gotoAddMap() {
		Intent intent = new Intent();
		switch (activityType) {
		case 3:
			map.put("type", 1);
			intent.putExtra("appointmentDemo", map);
			intent.setClass(OftenRoute.this, MapMain.class);
			break;

		case 4:
			map.put("type", 3);
			intent.putExtra("appointmentDemo", map);
			intent.setClass(OftenRoute.this, MapMain.class);
			break;
		}
		return intent;
	}

	// 子选项
	private Object[] menu_toolbar_name_array = {
			new Object[] { "发起预约", R.drawable.btn_circle },
			new Object[] { "预约返程", R.drawable.btn_circle },
			new Object[] { "修改路线", R.drawable.btn_circle },
			new Object[] { "删除路线", R.drawable.btn_circle } };
	private Object[] menu_toolbar_service_name = {
			new Object[] { "加入路线", R.drawable.btn_circle },
			new Object[] { "再次预约", R.drawable.btn_circle },
			new Object[] { "取消订单", R.drawable.btn_circle },
			new Object[] { "查看详情", R.drawable.btn_circle }, };

	private List<TreeNode> treeNodes = null;

	public class ListViewAdapter extends BaseExpandableListAdapter implements
			OnItemClickListener {
		private LayoutInflater mInflater;

		public static final int ItemHeight = 48;// 每项的高度
		public static final int PaddingLeft = 36;// 每项的高度
		private int myPaddingLeft = 0;

		private MyGridView toolbarGrid;

		private Context parentContext;

		private LayoutInflater layoutInflater;

		public ListViewAdapter(Context view, int myPaddingLeft) {
			this.mInflater = LayoutInflater.from(view);
			parentContext = view;
			this.myPaddingLeft = myPaddingLeft;
		}

		public List<TreeNode> GetTreeNode() {
			return treeNodes;
		}

		public void UpdateTreeNode(List<TreeNode> nodes) {
			treeNodes = nodes;
		}

		public void RemoveAll() {
			treeNodes.clear();
		}

		public Object getChild(int groupPosition, int childPosition) {
			return treeNodes.get(groupPosition).childs.get(childPosition);
		}

		public int getChildrenCount(int groupPosition) {
			// return treeNodes.get(groupPosition).childs.size();
			return 1;
		}

		public TextView getTextView(Context context) {
			AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, ItemHeight);

			TextView textView = new TextView(context);
			textView.setLayoutParams(lp);
			textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			return textView;
		}

		// 点中选项获取对象
		public TreeNode treeNode = null;

		/**
		 * 可自定义ExpandableListView
		 */
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			if (convertView == null) {
				// 获取选择的组
				treeNode = treeNodes.get(groupPosition);
				layoutInflater = (LayoutInflater) parentContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = layoutInflater.inflate(R.layout.groupview, null);

				toolbarGrid = (MyGridView) convertView
						.findViewById(R.id.GridView_toolbar);
				toolbarGrid.setNumColumns(5);// 设置每行列数
				toolbarGrid.setGravity(Gravity.CENTER);// 位置居中
				toolbarGrid.setHorizontalSpacing(10);// 水平间隔
				toolbarGrid.setAdapter(new SimpleAdapter(parentContext,
						treeNodes.get(groupPosition).childs,
						R.layout.item_menu, new String[] { "itemImage",
								"itemText" }, new int[] { R.id.item_image,
								R.id.item_text }));// 设置菜单Adapter
				toolbarGrid.setOnItemClickListener(this);

			}

			return convertView;
		}

		/**
		 * 可自定义list
		 */
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			// if (convertView == null) {
			// Log.v("is NULL", "DF2" + groupPosition);
			// }
			//
			// Log.v("ListViewLog", "DF" + groupPosition);
			convertView = mInflater.inflate(R.layout.resultitem, null);

			OftenRouteListItem item = (OftenRouteListItem) convertView;

			HashMap<String, Object> name = treeNodes.get(groupPosition).parent;
			String nameString = name.get("name").toString();
			String startString = "起点:" + name.get("start").toString();
			String endString = "终点:" + name.get("end").toString();
			String kmString = name.get("km").toString() + "公里";
			if (kmString.equals("-1公里")) {
				kmString = "订单已取消";
			}
			item.setPoiData(nameString, startString, endString, kmString);

			if (groupPosition == treeNodes.size() - 1 && !isEnd) {
				loadingView.setVisibility(View.VISIBLE);
				// 翻页加载数据
				new DataToActivity().start();
			}

			return convertView;
		}

		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		public Object getGroup(int groupPosition) {
			return treeNodes.get(groupPosition).parent;
		}

		public int getGroupCount() {
			return treeNodes.size();
		}

		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		public boolean hasStableIds() {
			return true;
		}

		// /**
		// * 构造菜单Adapter
		// *
		// * @param menuNameArray
		// * 名称
		// * @param imageResourceArray
		// * 图片
		// * @return SimpleAdapter
		// */
		// private SimpleAdapter getMenuAdapter(String[] menuNameArray,
		// int[] imageResourceArray) {
		// ArrayList<HashMap<String, Object>> data = new
		// ArrayList<HashMap<String, Object>>();
		// for (int i = 0; i < menuNameArray.length; i++) {
		// HashMap<String, Object> map = new HashMap<String, Object>();
		// map.put("itemImage", imageResourceArray[i]);
		// map.put("itemText", menuNameArray[i]);
		// data.add(map);
		// }
		// SimpleAdapter simperAdapter = new SimpleAdapter(parentContext,
		// data, R.layout.item_menu, new String[] { "itemImage",
		// "itemText" }, new int[] { R.id.item_image,
		// R.id.item_text });
		// return simperAdapter;
		// // return null;
		// }
		// 创建一个订单
		TaxiService taxiService;
		// 创建一个路线
		TaxiRoute taxiRoute;

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// Toast.makeText(parentContext, "当前选中的是:" + id,
			// Toast.LENGTH_SHORT).show();
			if (treeNode != null) {
				calendar = Calendar.getInstance();
				builderView = OftenRoute.this.getLayoutInflater().inflate(
						R.layout.pop_save_builder, null);
				// 常用路线名称
				group_route = (RelativeLayout) builderView
						.findViewById(R.id.route_name_builder_group);
				// 预约打车
				group_service = (RelativeLayout) builderView
						.findViewById(R.id.servcie_builder_group);
				// 预约日期
				service_date_builder = (EditText) builderView
						.findViewById(R.id.service_date_builder);
				// 预约时间
				service_time_builder = (EditText) builderView
						.findViewById(R.id.service_time_builder);
				service_date_builder.setInputType(InputType.TYPE_NULL);
				service_time_builder.setInputType(InputType.TYPE_NULL);
				builder = new Builder(OftenRoute.this);
				builder.setView(builderView);
				// 用户对象
				TaxiUser taxiUser = (TaxiUser) map.get("userModule");
				// 蓝牙地址
				String blueString = map.get("blueToothAdd").toString();
				if (treeNode.oldObject instanceof TaxiRoute) {
					group_route.setVisibility(View.GONE);
					group_service.setVisibility(View.VISIBLE);
					// 获取路线对象
					TaxiRoute tRoute = (TaxiRoute) treeNode.oldObject;
					// 创建新订单对象
					taxiService = new TaxiService();
					taxiService.setDid(taxiUser.getUid());
					taxiService.setUserName(taxiUser.getUserName());
					taxiService.setUserNambr(taxiUser.getLoginName());
					taxiService.setCity(tRoute.getCity());
					taxiService.setServiceType(1);
					taxiService.setPassengerBlue(blueString);
					taxiService.setKmNumber(tRoute.getRouteKm());
					taxiService.setServiceEnd(0);
					switch (position) {
					case 1:
						// 返程
						taxiService.setAppointmentAdd(tRoute.getEndAdd() + "@@"
								+ tRoute.getEndStr());
						taxiService.setAppointmentEnd(tRoute.getStartAdd()
								+ "@@" + tRoute.getStartStr());
					case 0:
						if (taxiService.getAppointmentAdd() == null
								|| taxiService.getAppointmentAdd().equals("")) {
							taxiService.setAppointmentAdd(tRoute.getStartAdd()
									+ "@@" + tRoute.getStartStr());
							taxiService.setAppointmentEnd(tRoute.getEndAdd()
									+ "@@" + tRoute.getEndStr());
						}
						calendar.setTimeInMillis(System.currentTimeMillis());
						service_date_builder
								.setOnClickListener(new DateListener());
						service_time_builder
								.setOnClickListener(new TimeListener());
						builder.setPositiveButton("保存", new Save_builder(
								taxiService));
						builder.setNegativeButton("取消", null);
						builder.show();
						break;
					case 2:
						Intent intent = new Intent();
						map.put("updateRoute", treeNode.oldObject);
						map.put("type", 2);
						intent.putExtra("appointmentDemo", map);
						intent.setClass(OftenRoute.this, MapMain.class);
						OftenRoute.this.startActivity(intent);
						break;
					case 3:
						builder = new Builder(OftenRoute.this);
						builder.setMessage(OftenRoute.this
								.getString(R.string.often_route1));
						builder.setPositiveButton(
								"删除",
								new android.content.DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										try {
											db.delete2Id(treeNode.oldObject);
											isOncreate = true;
											isResume();
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}

								});
						builder.setNegativeButton("取消", null);
						builder.show();
						break;
					}
				} else if (treeNode.oldObject instanceof TaxiService) {
					TaxiService oldTaxiService = (TaxiService) treeNode.oldObject;
					switch (position) {
					case 0:
						if ((oldTaxiService.getServiceType() == 0
								&& oldTaxiService.getEndAdd() != null && !oldTaxiService
								.getEndAdd().equals("null"))
								|| (oldTaxiService.getServiceType() == 1
										&& oldTaxiService.getAppointmentEnd() != null && !oldTaxiService
										.getAppointmentEnd().equals("null"))) {
							route_name_builder = (EditText) builderView
									.findViewById(R.id.route_name_builder);
							group_route.setVisibility(View.VISIBLE);
							group_service.setVisibility(View.GONE);
							taxiRoute = new TaxiRoute();
							taxiRoute.setUid(taxiUser.getUid());
							taxiRoute.setCity(oldTaxiService.getCity());
							if (oldTaxiService.getServiceType() == 0) {
								taxiRoute.setStartAdd(oldTaxiService
										.getStartAddLat()
										+ ","
										+ oldTaxiService.getStartAddLon());
								taxiRoute.setStartStr("随机开始地点");
								taxiRoute.setEndAdd(oldTaxiService.getEndAdd());
								taxiRoute.setEndStr("随机结束地点");
							} else {
								taxiRoute.setStartAdd(oldTaxiService
										.getAppointmentAdd().split("@@")[0]);
								taxiRoute.setStartStr(oldTaxiService
										.getAppointmentAdd().split("@@")[1]);
								taxiRoute.setEndAdd(oldTaxiService
										.getAppointmentEnd().split("@@")[0]);
								taxiRoute.setEndStr(oldTaxiService
										.getAppointmentEnd().split("@@")[1]);
							}
							taxiRoute.setRouteKm(String.valueOf(oldTaxiService
									.getKmNumber()));
							builder.setPositiveButton(
									"保存",
									new android.content.DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											if (route_name_builder == null
													|| route_name_builder
															.getText()
															.toString() == null
													|| route_name_builder
															.getText()
															.toString()
															.equals("")) {
												taxiRoute.setName("新增路线");
											} else {
												taxiRoute
														.setName(route_name_builder
																.getText()
																.toString());
											}
											try {
												db.save(taxiRoute);
												Toast.makeText(OftenRoute.this,
														"保存成功",
														Toast.LENGTH_LONG)
														.show();
											} catch (Exception e) {
												// TODO Auto-generated catch
												// block
												e.printStackTrace();
											}
										}

									});
							builder.setNegativeButton("取消", null);
							builder.show();
						} else {
							Toast.makeText(OftenRoute.this, "该订单被取消，无法加入常用路线",
									Toast.LENGTH_SHORT).show();
						}
						break;

					case 1:
						group_route.setVisibility(View.GONE);
						group_service.setVisibility(View.VISIBLE);
						// 创建新订单对象
						taxiService = new TaxiService();
						taxiService.setDid(taxiUser.getUid());
						taxiService.setUserName(taxiUser.getUserName());
						taxiService.setUserNambr(taxiUser.getLoginName());
						taxiService.setCity(oldTaxiService.getCity());
						taxiService.setServiceType(1);
						taxiService.setServiceEnd(0);
						taxiService.setPassengerBlue(blueString);
						taxiService.setAppointmentAdd(oldTaxiService
								.getAppointmentAdd());
						taxiService.setAppointmentEnd(oldTaxiService
								.getAppointmentEnd());
						taxiService.setKmNumber(oldTaxiService.getKmNumber());
						service_date_builder
								.setOnClickListener(new DateListener());
						service_time_builder
								.setOnClickListener(new TimeListener());
						builder.setPositiveButton("保存", new Save_builder(
								taxiService));
						builder.setNegativeButton("取消", null);
						builder.show();
						break;
					case 2:
						// 取消订单
						progressDialog = ProgressDialog.show(OftenRoute.this,
								"", "正在提交", true);
						PostToAction postToAction = new PostToAction();
						try {
							postToAction
									.postToServer(
											OftenRoute.this
													.getString(R.string.serverPath)
													+ OftenRoute.this
															.getString(R.string.removeService),
											new Object[] { oldTaxiService,
													"serviceObj" });
							progressDialog.dismiss();
							isOncreate = true;
							isResume();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					case 3:

						break;
					}
				}
				// instanceof
			}

		}

		public int getCount() {
			// TODO Auto-generated method stub
			return 0;
		}
	}

	public class TreeNode {
		HashMap<String, Object> parent;
		Object oldObject;
		List<HashMap<String, Object>> childs = new ArrayList<HashMap<String, Object>>();
	}

	/**
	 * 数据加载类
	 * 
	 * @author talkliu
	 * 
	 */
	public class DataToActivity extends Thread {
		private List<TreeNode> list = null;
		private int count = 0;
		Message localMessage = new Message();

		@Override
		public void run() {
			// try {
			// Thread.sleep(500);
			// } catch (InterruptedException e1) {
			// // TODO Auto-generated catch block
			// e1.printStackTrace();
			// }
			try {
				if (list == null) {
					// 翻页初始化
					list = new ArrayList<OftenRoute.TreeNode>();
				}
				if (activityType == 3) {
					// Thread.sleep(1000);
					// if (count == 0) {
					count = db.queryCount(new TaxiRoute());
					// }
					// 开始分页
					List<Object> dataList = db.queryAllObjectForLimit(
							new TaxiRoute(), pageNo * pageSize, pageSize,
							"and uid=" + taxiUser.getUid());
					// 若表内无数据则
					if (dataList.size() == 0) {
						localMessage.what = 4;
						handler.sendMessage(localMessage);
					} else {
						// 若有数据，则开始显示
						for (Object o : dataList) {
							TaxiRoute oRoute = (TaxiRoute) o;
							TreeNode treeNode = new TreeNode();
							// 扩展原始对象
							treeNode.oldObject = o;
							HashMap<String, Object> name = new HashMap<String, Object>();
							name.put("name", oRoute.getName());
							name.put("start", oRoute.getStartStr());
							name.put("end", oRoute.getEndStr());
							name.put("km", "总距离：" + oRoute.getRouteKm() == null
									|| oRoute.getRouteKm().equals("null") ? 0
									: oRoute.getRouteKm());
							// 显示组件
							treeNode.parent = name;
							// 子显示组件
							for (Object obj : menu_toolbar_name_array) {
								Object[] objects = (Object[]) obj;
								HashMap<String, Object> childs = new HashMap<String, Object>();
								childs.put("itemText", objects[0]);
								childs.put("itemImage", objects[1]);
								treeNode.childs.add(childs);
							}
							list.add(treeNode);
						}
						pageNo++;// 转向第二页
						if (pageNo * pageSize >= count) {
							serverDataArrived(list, true);// 不翻页
						} else {
							serverDataArrived(list, false);// 继续翻页
						}
						return;
					}
				} else if (activityType == 4) {
					if (pageNo == 0) {
						pageNo = 1;
					}
					TaxiService taxiService = new TaxiService();
					taxiService.setDid(taxiUser.getUid());
					PostToAction postToAction = new PostToAction();
					String jsonString = postToAction
							.postToServer(
									OftenRoute.this
											.getString(R.string.serverPath)
											+ OftenRoute.this
													.getString(R.string.getServiceListForUser),
									new Object[] { taxiService, "serviceObj" },
									new Object[] { "pageNo", pageNo },
									new Object[] { "pageSize", pageSize });
					if (jsonString != null && !jsonString.equals("")) {
						JSONObject jsonObject = new JSONObject(jsonString);
						count = jsonObject.getInt("count");
						JsonToObject<TaxiService> jsonToObject = new JsonToObject<TaxiService>(
								TaxiService.class);
						List<TaxiService> servicelist = (List<TaxiService>) jsonToObject
								.getArrayJsonList(jsonObject.get("list")
										.toString());
						for (TaxiService tService : servicelist) {
							TreeNode treeNode = new TreeNode();
							// 扩展原始对象
							treeNode.oldObject = tService;
							HashMap<String, Object> name = new HashMap<String, Object>();
							switch (tService.getServiceType()) {
							case 0:
								name.put("name", tService.getOrderAddTime());
								name.put("start", "随机打车");
								name.put("end", "");
								if (tService.getServiceEnd() == 1) {
									name.put(
											"km",
											"总距离：" + tService.getKmNumber() == null
													|| tService.getKmNumber()
															.equals("null") ? 0
													: tService.getKmNumber()
															+ "公里");
								} else if (tService.getServiceEnd() == 0) {
									name.put("km", "该订单未开始");
								} else {
									name.put("km", "该订单被取消");
								}
								break;

							case 1:
								name.put("name", tService.getStartTime());
								name.put("start", tService.getAppointmentAdd()
										.split("@@")[1]);
								name.put("end", tService.getAppointmentEnd()
										.split("@@")[1]);
								if (tService.getServiceEnd() == 0) {
									name.put("km",
											tService.getKmNumber() == null
													|| tService.getKmNumber()
															.equals("null") ? 0
													: tService.getKmNumber());
								} else if (tService.getServiceEnd() == 1) {
									name.put("km",
											tService.getKmNumber() == null
													|| tService.getKmNumber()
															.equals("null") ? 0
													: tService.getKmNumber());
								} else if (tService.getServiceEnd() == 3) {
									name.put("km", -1);
								} else {
									name.put("km", -2);
								}
								break;
							}
							treeNode.parent = name;
							for (int i = 0; i < menu_toolbar_service_name.length; i++) {
								if (i == 2) {
									if (tService.getServiceEnd() == 0) {
										Object[] objects = (Object[]) menu_toolbar_service_name[i];
										HashMap<String, Object> childs = new HashMap<String, Object>();
										childs.put("itemText", objects[0]);
										childs.put("itemImage", objects[1]);
										treeNode.childs.add(childs);
									}
								} else {
									Object[] objects = (Object[]) menu_toolbar_service_name[i];
									HashMap<String, Object> childs = new HashMap<String, Object>();
									childs.put("itemText", objects[0]);
									childs.put("itemImage", objects[1]);
									treeNode.childs.add(childs);
								}
							}
							list.add(treeNode);
						}
						if (pageNo * pageSize > count) {
							serverDataArrived(list, true);// 不翻页
						} else {
							serverDataArrived(list, false);// 继续翻页
						}
						pageNo++;// 转向第二页
					} else {
						localMessage.what = 4;
						handler.sendMessage(localMessage);
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public synchronized void serverDataArrived(List<TreeNode> list,
				boolean isEnd) {
			// TODO Auto-generated method stub
			OftenRoute.this.isEnd = isEnd;
			List<TreeNode> treeNode = resultAdapter.GetTreeNode();
			for (TreeNode tNode : list) {
				// mData.add((Map<String, Object>) iter.next());
				// TreeNode node = new TreeNode();
				// node.parent = (HashMap<String, Object>) iter.next();
				// // 注入子选项
				// for (Object[] objects : menu_toolbar_name_array) {
				// HashMap<String, Object> map = new HashMap<String, Object>();
				// map.put("itemText", objects[0]);
				// map.put("itemImage", objects[1]);
				// node.childs.add(map);
				// }
				treeNode.add(tNode);
			}
			resultAdapter.UpdateTreeNode(treeNode);
			if (!isEnd) {
				localMessage.what = 1;
			} else {
				localMessage.what = 3;
			}

			handler.sendMessage(localMessage);
		}
	}

	/**
	 * 日期监听器
	 * 
	 * @author talkliu
	 * 
	 */
	private class DateListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			calendar.setTimeInMillis(System.currentTimeMillis());
			new DatePickerDialog(OftenRoute.this, new OnDateSetListener() {

				@Override
				public void onDateSet(DatePicker view, int year,
						int monthOfYear, int dayOfMonth) {
					// TODO
					// Auto-generated
					// method
					// stub
					calendar.set(Calendar.YEAR, year);
					calendar.set(Calendar.MONTH, monthOfYear);
					calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
					service_date_builder.setText(calendar.get(Calendar.YEAR)
							+ "-" + calendar.get(Calendar.MONTH) + "-"
							+ calendar.get(Calendar.DAY_OF_MONTH));
				}
			}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH)).show();
		}

	}

	/**
	 * 时间监听器
	 * 
	 * @author talkliu
	 * 
	 */
	private class TimeListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			calendar.setTimeInMillis(System.currentTimeMillis());
			new TimePickerDialog(OftenRoute.this, new OnTimeSetListener() {

				@Override
				public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
					// TODO
					// Auto-generated
					// method
					// stub
					calendar.setTimeInMillis(System.currentTimeMillis());
					calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
					calendar.set(Calendar.MINUTE, minute);
					calendar.set(Calendar.SECOND, 0);
					calendar.set(Calendar.MILLISECOND, 0);
					if (service_date_builder.getText() == null
							|| service_date_builder.getText().toString()
									.equals("")) {
						service_date_builder.setText(calendar
								.get(Calendar.YEAR)
								+ "-"
								+ calendar.get(Calendar.MONTH)
								+ "-"
								+ calendar.get(Calendar.DAY_OF_MONTH));
					}
					service_time_builder.setText(calendar
							.get(Calendar.HOUR_OF_DAY)
							+ ":"
							+ calendar.get(Calendar.MINUTE) + ":00");
				}
			}, calendar.get(Calendar.HOUR_OF_DAY), calendar
					.get(Calendar.MINUTE), true).show();
		}
	}

	private class Save_builder implements
			android.content.DialogInterface.OnClickListener {
		TaxiService taxiService;

		public Save_builder(TaxiService taxiService) {
			this.taxiService = taxiService;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			if (!service_time_builder.getText().equals("")
					&& !service_time_builder.getText().equals(
							OftenRoute.this
									.getString(R.string.pop_save_builder1))) {
				taxiService.setStartTime(service_date_builder.getText() + " "
						+ service_time_builder.getText());
				try {
					calendar.setTimeInMillis(System.currentTimeMillis());
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm");
					Date nowDate = sdf.parse(calendar.get(Calendar.YEAR) + "-"
							+ calendar.get(Calendar.MONTH) + "-"
							+ calendar.get(Calendar.DAY_OF_MONTH) + " "
							+ calendar.get(Calendar.HOUR_OF_DAY) + ":"
							+ calendar.get(Calendar.MINUTE) + ":00");
					Date upDate = sdf.parse(taxiService.getStartTime());
					if (upDate.getTime() > nowDate.getTime() + 1800000) {
						Parcel data = Parcel.obtain();
						Parcel reply = Parcel.obtain();
						HashMap<String, Object> serviceMap = new HashMap<String, Object>();
						serviceMap.put("servcie", taxiService);
						data.writeMap(serviceMap);
						PassengerMainActivity.serviceBinder.transact(1, data,
								reply, 0);
					} else {
						Toast.makeText(OftenRoute.this, "对不起，请最少提前半小时预定",
								Toast.LENGTH_LONG).show();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch
					// block
					e.printStackTrace();
				}
			} else {
				Toast.makeText(OftenRoute.this, "请选择预约时间", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		OftenRoute.this.finish();
		// super.onBackPressed();
	}
}