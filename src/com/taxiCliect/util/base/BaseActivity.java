package com.taxiCliect.util.base;

import com.taxiCliect.util.db.Database2Pojo;

import android.app.Activity;

public class BaseActivity extends Activity {
	public Database2Pojo db = null;

	public BaseActivity() {
		if (db == null) {
			db = new Database2Pojo(BaseActivity.this);
		}
	}
}
