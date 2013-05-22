package com.taxiCliect.activity.driver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import com.taxiCliect.activity.DriverMainActivity;
import com.taxiCliect.activity.R;
import com.taxiCliect.activity.driver.AppointmentListView.OnRefreshListener;
import com.taxiCliect.activity.map.MapMain;
import com.taxiCliect.activity.person.MyGridView;
import com.taxiCliect.activity.person.OftenRoute;
import com.taxiCliect.activity.person.OftenRouteListItem;
import com.taxiCliect.activity.person.OftenRoute.DataToActivity;
import com.taxiCliect.activity.person.OftenRoute.ListViewAdapter;
import com.taxiCliect.activity.person.OftenRoute.TreeNode;
import com.taxiCliect.module.TaxiRoute;
import com.taxiCliect.module.TaxiService;
import com.taxiCliect.module.TaxiUser;
import com.taxiCliect.util.db.Database2Pojo;
import com.taxiCliect.util.json.JsonToObject;
import com.taxiCliect.util.postAction.PostToAction;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class DriverAppointmentActivity extends Activity {
	private Context context;
	public AppointmentListView list;
	public Database2Pojo db = null;
	/**
	 * 当前页
	 */
	private int pageNo = 1;// sqlite分页同mysql需要从0开始

	/**
	 * 页面条数
	 */
	private int pageSize = 15;
	// 读取的view
	private View loadingView;
	// 初始化的text
	TextView listfooter_loding;
	private boolean isEnd = false;
	// 弹出搜索的对话框
	private AlertDialog.Builder builder;
	private ProgressDialog progressDialog;
	public static boolean isOncreate = false;
	// 标题
	TextView often_route_title;
	Handler handler = new Handler() {
		public void handleMessage(Message paramMessage) {
			switch (paramMessage.what) {
			case 1:
				list.removeFooterView(loadingView);
				list.addFooterView(loadingView);
				loadingView.setVisibility(View.VISIBLE);
				break;
			case 2:
				list.removeFooterView(loadingView);
				list.addFooterView(loadingView);
				loadingView.setVisibility(View.GONE);
				break;
			case 3:
				loadingView.setVisibility(View.VISIBLE);
				break;
			case 4:
				loadingView.setVisibility(View.VISIBLE);
				break;
			}
			list.setRefreshable(true);
			resultAdapter.notifyDataSetChanged();
		}
	};
	private List<TreeNode> treeNodes = new ArrayList<TreeNode>();
	ListViewAdapter resultAdapter = null;
	private String city;
	private TaxiUser taxiUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.appointment_main);
		city = this.getIntent().getStringExtra("city");
		taxiUser = (TaxiUser) this.getIntent().getSerializableExtra(
				"userModule");
		context = this;
		if (db == null) {
			db = new Database2Pojo(this);
		}
		// 初始化子选项
		list = (AppointmentListView) findViewById(R.id.resultlist);
		resultAdapter = new ListViewAdapter(this,
				ListViewAdapter.PaddingLeft >> 1);
		loadingView = LayoutInflater.from(this).inflate(R.layout.listfooter,
				null);
		list.addFooterView(loadingView);
		list.setGroupIndicator(null);// 去掉箭头
		list.setAdapter(resultAdapter);
		often_route_title = (TextView) findViewById(R.id.often_route_title);
		often_route_title.setText("查看预定列表");
		list.setonRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				new AsyncTask<Void, Void, Void>() {
					protected Void doInBackground(Void... params) {
						// data.add("刷新后添加的内容");
						// 删除之前的
						treeNodes.removeAll(treeNodes);
						resultAdapter.notifyDataSetChanged();
						loadingView.setVisibility(View.VISIBLE);
						pageNo = 1;
						// 翻页加载数据
						new DataToActivity(list).start();
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						list.onRefreshComplete();
					}

				}.execute(null);
			}
		});
		// 首次加载数据
		new DataToActivity(list).start();
	}

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
				toolbarGrid.setNumColumns(2);// 设置每行列数
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
				new DataToActivity(list).start();
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
			return treeNodes == null ? 0 : treeNodes.size();
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
			TaxiService taxiService = null;
			Intent intent = new Intent();
			if (treeNode != null) {
				taxiService = (TaxiService) treeNode.oldObject;
				switch (position) {
				case 0:
					// 申请预约
					intent.putExtra("service", taxiService);
					intent.setClass(context, DriverMainActivity.class);
					setResult(1, intent);
					DriverAppointmentActivity.this.finish();
					break;

				case 1:
					// 查看地图
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("type", 5);
					map.put("service", treeNode.oldObject);
					map.put("userModule", taxiUser);
					intent.putExtra("appointmentDemo", map);
					intent.setClass(DriverAppointmentActivity.this,
							MapMain.class);
					DriverAppointmentActivity.this.startActivityForResult(
							intent, 1);
					overridePendingTransition(R.anim.main_enter,
							R.anim.main_exit);
					break;
				}
			}

		}

		public int getCount() {
			// TODO Auto-generated method stub
			return 0;
		}
	}

	private Object[] menu_service_appointment = {
			new Object[] { "申请预约", R.drawable.btn_circle },
			new Object[] { "查看地图", R.drawable.btn_circle } };

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

		public DataToActivity(AppointmentListView list) {
			list.setRefreshable(false);
		}

		@Override
		public void run() {
			try {
				// 先禁止刷新
				if (list == null) {
					// 翻页初始化
					list = new ArrayList<DriverAppointmentActivity.TreeNode>();
				}
				TaxiService taxiService = new TaxiService();
				taxiService.setCity(city);
				taxiService.setOther("appointment");
				PostToAction postToAction = new PostToAction();
				String jsonString = postToAction
						.postToServer(
								context.getString(R.string.serverPath)
										+ context
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
							.getArrayJsonList(jsonObject.get("list").toString());
					for (TaxiService tService : servicelist) {
						TreeNode treeNode = new TreeNode();
						// 扩展原始对象
						treeNode.oldObject = tService;
						HashMap<String, Object> name = new HashMap<String, Object>();
						name.put("name", tService.getStartTime());
						name.put("start",
								tService.getAppointmentAdd().split("@@")[1]);
						name.put("end", tService.getAppointmentEnd()
								.split("@@")[1]);
						name.put("km", tService.getKmNumber());
						treeNode.parent = name;
						for (Object obj : menu_service_appointment) {
							Object[] objects = (Object[]) obj;
							HashMap<String, Object> childs = new HashMap<String, Object>();
							childs.put("itemText", objects[0]);
							childs.put("itemImage", objects[1]);
							treeNode.childs.add(childs);
						}
						list.add(treeNode);
					}
					if (pageNo * pageSize >= count) {
						serverDataArrived(list, true);// 不翻页
					} else {
						serverDataArrived(list, false);// 继续翻页
					}
					pageNo++;// 转向第二页
				} else {
					localMessage.what = 4;
					handler.sendMessage(localMessage);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public synchronized void serverDataArrived(List<TreeNode> list,
				boolean isEnd) {
			// TODO Auto-generated method stub
			DriverAppointmentActivity.this.isEnd = isEnd;
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
				localMessage.what = 2;
			}

			handler.sendMessage(localMessage);
		}
	}

	public class TreeNode {
		HashMap<String, Object> parent;
		Object oldObject;
		List<HashMap<String, Object>> childs = new ArrayList<HashMap<String, Object>>();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (resultCode) {
		case 1:
			TaxiService taxiService = (TaxiService) data
					.getSerializableExtra("service");
			Intent intent = new Intent();
			intent.putExtra("service", taxiService);
			intent.setClass(DriverAppointmentActivity.this,
					DriverMainActivity.class);
			setResult(1, intent);
			DriverAppointmentActivity.this.finish();
			break;
		}
	}
}
