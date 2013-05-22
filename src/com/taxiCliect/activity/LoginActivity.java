package com.taxiCliect.activity;

import java.security.PublicKey;
import java.util.List;

import com.cplatform.privacy.encrypt.MD5Algorithm;
import com.taxiCliect.module.CtrlSoft;
import com.taxiCliect.module.TaxiUser;
import com.taxiCliect.service.Bootservice;
import com.taxiCliect.util.blueTooth.ObjectUtil;
import com.taxiCliect.util.db.Database2Pojo;
import com.taxiCliect.util.json.JsonToObject;
import com.taxiCliect.util.postAction.PostToAction;

import android.R.bool;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 用于登录或自动登录页面
 * 
 * @author talkliu
 * 
 */
public class LoginActivity extends Activity {
	private Database2Pojo db;
	// 初始化得到服务器地址
	private String serverPath;
	// 初始化对server交互类
	private PostToAction action = new PostToAction();
	// 实例一个得到手机信息的对象
	private TelephonyManager telephonyManager;
	// 登录名
	private EditText userName;
	// 密码
	private EditText passWord;
	// 注册
	private Button regButton;
	// 登录
	private Button loginButton;
	// 默认登录
	private CheckBox isLoginBox;
	// 登录对象
	private TaxiUser user;
	// 弹框
	private AlertDialog.Builder builder;
	private ProgressDialog progressDialog;
	// 创建跳转
	private Intent intoEndIntent = new Intent();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginpage);
		telephonyManager = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);

		// 初始化按钮
		serverPath = this.getString(R.string.serverPath);
		regButton = (Button) findViewById(R.id.login_btn_Reg);
		loginButton = (Button) findViewById(R.id.login_btn_login);
		userName = (EditText) findViewById(R.id.login_edit_account);
		passWord = (EditText) findViewById(R.id.login_edit_pwd);
		isLoginBox = (CheckBox) findViewById(R.id.login_cb_savepwd);
		builder = new Builder(LoginActivity.this);
		// 添加按钮的事件
		regButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (userName.getText().toString() != null
						&& !userName.getText().toString().equals("")
						&& passWord.getText().toString() != null
						&& !passWord.getText().toString().equals("")) {
					if (userName.getText().toString().length() != 11) {
						// 不合法的手机号
						builder.setMessage(R.string.loginError5);
						builder.setPositiveButton(R.string.loginErrorDon1, null);
						builder.create().show();
					} else if (passWord.getText().toString().length() < 5) {
						// 密码太短
						builder.setMessage(R.string.loginError3);
						builder.setPositiveButton(R.string.loginErrorDon1, null);
						builder.create().show();
					} else {
						user = new TaxiUser();
						user.setUserName(userName.getText().toString());
						user.setLoginName(userName.getText().toString());
						user.setPassword(MD5Algorithm.digest2Str(passWord
								.getText().toString()));
						user.setPhoneNumber(telephonyManager.getLine1Number());
						progressDialog = ProgressDialog.show(
								LoginActivity.this, "", "正在注册...", true);
						new Thread() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Message msg_listData = new Message();
								msg_listData.what = -1;
								try {
									regTaxiUser();
									if (user != null) {
										// 注册成功
										if (isLoginBox.isChecked()) {
											db.execForSql(
													"update t_client_taxiUser set loginUser=?",
													new Object[] { 0 });
											user.setLoginUser(1);
										}
										// 保存该账户
										db.save(user);
										// 创建初始系统设置
										CtrlSoft cSoft = new CtrlSoft();
										cSoft.setUid(user.getUid());
										db.save(cSoft);
										// 跳转至选择司机或乘客
										msg_listData.what = 2;
									} else {
										// 已存在账户
										msg_listData.what = 5;
									}
								} catch (Exception e) {
									// 无网络连接
									msg_listData.what = 1;
								}
								handler.sendMessage(msg_listData);
							}
						}.start();
					}
				} else {
					// 用户名密码不能为空
					builder.setMessage(R.string.loginError1);
					builder.setPositiveButton(R.string.loginErrorDon1, null);
					builder.create().show();
				}
			}
		});
		loginButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (userName.getText().toString() != null
						&& !userName.getText().toString().equals("")
						&& passWord.getText().toString() != null
						&& !passWord.getText().toString().equals("")) {
					if (userName.getText().toString().length() != 11) {
						// 不合法的手机号
						builder.setMessage(R.string.loginError5);
						builder.setPositiveButton(R.string.loginErrorDon1, null);
						builder.create().show();
					} else {
						user = new TaxiUser();
						user.setUserName(userName.getText().toString());
						user.setLoginName(userName.getText().toString());
						user.setPassword(MD5Algorithm.digest2Str(passWord
								.getText().toString()));
						user.setPhoneNumber(telephonyManager.getLine1Number());
						progressDialog = ProgressDialog.show(
								LoginActivity.this, "", "正在登录...", true);
						new Thread() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Message msg_listData = new Message();
								msg_listData.what = -1;
								try {
									loginTaxiUser();
									if (user != null && user.getUid() != null) {
										// 判断该账户是否存在本地数据库
										// 若存在
										if (db.query2Id(user) != null) {
											if (isLoginBox.isChecked()) {
												db.execForSql(
														"update t_client_taxiUser set loginUser=?",
														new Object[] { 0 });
												user.setLoginUser(1);
												db.update2Id(user);
											}
											// 通过uid取出转向键
											CtrlSoft cSoft = new CtrlSoft();
											cSoft.setUid(user.getUid());
											cSoft = (CtrlSoft) db
													.query2Id(cSoft);
											// 判断应转向司机还是乘客
											msg_listData.what = cSoft
													.getUserModule();
										} else {
											// 若不存在
											if (isLoginBox.isChecked()) {
												db.execForSql(
														"update t_client_taxiUser set loginUser=?",
														new Object[] { 0 });
												user.setLoginUser(1);
											}
											db.save(user);
											// 创建初始系统设置
											CtrlSoft cSoft = new CtrlSoft();
											cSoft.setUid(user.getUid());
											db.save(cSoft);
											// 跳转至选择司机或乘客
											msg_listData.what = 2;
										}
										// Toast.makeText(LoginActivity.this,
										// "登录成功", Toast.LENGTH_SHORT)
										// .show();
									} else {
										msg_listData.what = 6;
									}
								} catch (Exception e1) {

									// TODO Auto-generated catch block
									// builder.setMessage(R.string.loginError4);
									// builder.setPositiveButton(
									// R.string.loginErrorDon2,
									// new OnClickListener() {
									//
									// @Override
									// public void onClick(
									// DialogInterface dialog,
									// int which) {
									// // TODO Auto-generated
									// // method stub
									// Intent intent = new Intent(
									// android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS);
									// ComponentName cName = new ComponentName(
									// "com.android.phone",
									// "com.android.phone.Settings");
									// intent.setComponent(cName);
									// startActivity(intent);
									// }
									// });
									// builder.create().show();
									msg_listData.what = 1;
								}
								handler.sendMessage(msg_listData);
							}
						}.start();
					}

				} else {
					// 用户名密码不能为空
					builder.setMessage(R.string.loginError1);
					builder.setPositiveButton(R.string.loginErrorDon1, null);
					builder.create().show();
				}
			}
		});
		progressDialog = ProgressDialog.show(LoginActivity.this, "", "正在登录...",
				true);
		new Thread() {
			public void run() {
				Message msg_listData = new Message();
				msg_listData.what = -1;
				// 验证网络连接
				if (!isConnectingToInternet(LoginActivity.this)) {
					// 无网络连接
					msg_listData.what = 1;
				} else {
					// 加载时读取数据库，查询默认账户，若有默认账户则登录
					db = new Database2Pojo(LoginActivity.this);
					try {
						List<Object> list = db.query2Where(new TaxiUser(),
								"loginUser=?", new String[] { "1" });
						if (list.size() != 0) {
							user = (TaxiUser) list.get(0);
							// 如果查询不为空，则用该账户进行登录
							loginTaxiUser();
							if (user != null) {
								// 通过uid取出转向键
								CtrlSoft cSoft = new CtrlSoft();
								cSoft.setUid(user.getUid());
								cSoft = (CtrlSoft) db.query2Id(cSoft);
								if (cSoft.getUserModule() == 0) {
									msg_listData.what = 2;
								} else {
									msg_listData.what = cSoft.getUserModule();
								}
							}
						} else {
							progressDialog.dismiss();
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				handler.sendMessage(msg_listData);
			};
		}.start();
	}

	/**
	 * 快速注册
	 * 
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public void regTaxiUser() throws Exception {
		// telephonyManager.getLine1Number(); 手机号
		// 进行注册
		String string = action.postToServer(
				serverPath + this.getString(R.string.regAndLogin),
				new Object[] { user, "userInfo" });

		if (string != null && !string.equals("") && !string.equals("null")) {
			this.user = null;
			this.user = new JsonToObject<TaxiUser>(TaxiUser.class)
					.getJsonObject(string);
		} else {
			this.user = null;
		}
	}

	/**
	 * 登录
	 * 
	 * @param user
	 * @return
	 */
	public void loginTaxiUser() throws Exception {
		String string = action.postToServer(
				serverPath + this.getString(R.string.loginInClient),
				new Object[] { user, "userInfo" });
		if (string != null && !string.equals("") && !string.equals("null")) {
			this.user = null;
			this.user = new JsonToObject<TaxiUser>(TaxiUser.class)
					.getJsonObject(string);
			Bootservice.taxiUser = this.user;
		} else {
			this.user = null;
		}
	}

	/**
	 * 判断网络是否连接
	 * 
	 * @return true/false
	 */
	public boolean isConnectingToInternet(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private Handler handler = new Handler() {

		public void handleMessage(Message message) {
			// 将userModule放入
			if (user != null) {
				try {
					intoEndIntent.putExtra("userModule",
							ObjectUtil.getBytesFromObject(user));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			switch (message.what) {
			case 1:
				progressDialog.dismiss();
				builder.setMessage(R.string.loginError4);
				builder.setPositiveButton(R.string.loginErrorDon2,
						new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated
								// method stub
								Intent intent = new Intent(
										android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS);
								ComponentName cName = new ComponentName(
										"com.android.phone",
										"com.android.phone.Settings");
								intent.setComponent(cName);
								startActivity(intent);
							}
						});
				builder.create().show();
				break;
			case 2:
				progressDialog.dismiss();
				// 弹出提示，提示用户选择某种模式
				builder.setMessage(R.string.ctrlUserModule1);
				builder.setPositiveButton(R.string.ctrlUserModule2,
						new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// 将启动模式修改成司机
								db.execForSql(
										"update t_client_ctrlSoft set userModule=? where uid=?",
										new Object[] { 4, user.getUid() });
								intoEndIntent.setClass(LoginActivity.this,
										DriverMainActivity.class);
								LoginActivity.this.startActivity(intoEndIntent);
								LoginActivity.this.finish();
							}
						});
				builder.setNegativeButton(R.string.ctrlUserModule3,
						new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// 将启动模式修改成乘客
								db.execForSql(
										"update t_client_ctrlSoft set userModule=? where uid=?",
										new Object[] { 3, user.getUid() });
								intoEndIntent.setClass(LoginActivity.this,
										PassengerMainActivity.class);
								LoginActivity.this.startActivity(intoEndIntent);
								LoginActivity.this.finish();
							}
						});
				builder.show();
				break;
			case 3:
				// 乘客
				intoEndIntent.setClass(LoginActivity.this,
						PassengerMainActivity.class);
				LoginActivity.this.startActivity(intoEndIntent);
				LoginActivity.this.finish();
				break;
			case 4:
				// 司机
				intoEndIntent.setClass(LoginActivity.this,
						DriverMainActivity.class);
				LoginActivity.this.startActivity(intoEndIntent);
				LoginActivity.this.finish();
				break;
			case 5:
				progressDialog.dismiss();
				// 已被注册
				builder.setMessage(R.string.loginError2);
				builder.setPositiveButton(R.string.loginErrorDon1, null);
				builder.create().show();
				break;
			case 6:
				progressDialog.dismiss();
				// 密码错误
				builder.setMessage(R.string.loginError2_1);
				builder.setPositiveButton(R.string.loginErrorDon1, null);
				builder.create().show();
				break;
			}
		}
	};
}
