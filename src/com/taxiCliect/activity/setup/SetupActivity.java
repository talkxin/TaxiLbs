package com.taxiCliect.activity.setup;

import java.util.HashMap;

import com.taxiCliect.activity.DriverMainActivity;
import com.taxiCliect.activity.LoginActivity;
import com.taxiCliect.activity.PassengerMainActivity;
import com.taxiCliect.activity.R;
import com.taxiCliect.module.CtrlSoft;
import com.taxiCliect.module.DriverInfo;
import com.taxiCliect.module.TaxiUser;
import com.taxiCliect.util.blueTooth.ObjectUtil;
import com.taxiCliect.util.db.Database2Pojo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

public class SetupActivity extends PreferenceActivity {

	private TaxiUser taxiUser;
	private CtrlSoft ctrlSoft;
	private Database2Pojo db;
	private Preference setModuleChange;
	private Preference setUserChange;
	private Preference safeUserMail;
	private Preference safeUserMobile;
	private Preference setAlipay;
	// 弹框
	private AlertDialog.Builder builder;
	private ProgressDialog progressDialog;
	Intent intent = new Intent();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.setting_preference);
		HashMap<String, Object> map = (HashMap<String, Object>) this
				.getIntent().getSerializableExtra("appointmentDemo");
		taxiUser = (TaxiUser) map.get("userModule");
		db = new Database2Pojo(this);
		ctrlSoft = new CtrlSoft();
		ctrlSoft.setUid(taxiUser.getUid());
		try {
			ctrlSoft = (CtrlSoft) db.query2Id(ctrlSoft);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		builder = new Builder(this);
		setModuleChange = findPreference("setModuleChange");
		setModuleChange
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						// TODO Auto-generated method stub
						switch (ctrlSoft.getUserModule()) {
						case 3:
							builder.setMessage("您是否切换为司机模式");
							break;

						case 4:
							builder.setMessage("您是否切换为乘客模式");
							break;
						}
						builder.setTitle("切换模式");
						builder.setPositiveButton("设置", new UserModuleChange());
						builder.setNegativeButton("取消", null);
						builder.show();
						// SetupActivity.this.finish();
						return false;
					}
				});
		setUserChange = findPreference("setUserChange");
		setUserChange
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						// TODO Auto-generated method stub
						builder.setTitle("切换用户");
						builder.setMessage("您是否切换其他用户登录");
						builder.setPositiveButton("设置", new UserChange());
						builder.setNegativeButton("取消", null);
						builder.show();
						// SetupActivity.this.finish();
						return false;
					}
				});
		safeUserMail = findPreference("safeUserMail");
		safeUserMail
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						// TODO Auto-generated method stub
						return false;
					}
				});
		safeUserMobile = findPreference("safeUserMobile");
		safeUserMobile
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						// TODO Auto-generated method stub
						return false;
					}
				});
		setAlipay = findPreference("setAlipay");
		setAlipay.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				// TODO Auto-generated method stub
				return false;
			}
		});
	}

	/**
	 * 切换登录模式
	 * 
	 * @author talkliu
	 * 
	 */
	private class UserModuleChange implements DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			try {
				intent.putExtra("userModule",
						ObjectUtil.getBytesFromObject(taxiUser));
				switch (ctrlSoft.getUserModule()) {
				case 3:
					// builder.setMessage("您是否切换为司机模式");
					ctrlSoft.setUserModule(4);
					// intent.setClass(SetupActivity.this,
					// DriverMainActivity.class);
					break;

				case 4:
					// builder.setMessage("您是否切换为乘客模式");
					ctrlSoft.setUserModule(3);
					// intent.setClass(SetupActivity.this,
					// PassengerMainActivity.class);
					break;
				}
				db.execForSql(
						"update t_client_ctrlSoft set userModule=? where uid=?",
						new String[] { ctrlSoft.getUserModule().toString(),
								taxiUser.getUid().toString() });
				// db.update2Id(ctrlSoft);
				// SetupActivity.this.startActivity(intent);
				intent.putExtra("setupType", "1");
				setResult(2, intent);
				SetupActivity.this.finish();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * 切换不同用户登录
	 * 
	 * @author talkliu
	 * 
	 */
	private class UserChange implements DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			try {
				db.execForSql("update t_client_taxiUser set loginUser=?",
						new String[] { "0" });
				// intent.setClass(SetupActivity.this, LoginActivity.class);
				// SetupActivity.this.startActivity(intent);
				intent.putExtra("setupType", "2");
				setResult(2, intent);
				SetupActivity.this.finish();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
}
