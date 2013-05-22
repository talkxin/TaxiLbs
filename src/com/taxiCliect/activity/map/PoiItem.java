package com.taxiCliect.activity.map;

import com.taxiCliect.activity.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PoiItem extends LinearLayout {
	TextView itemName;

	public PoiItem(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public PoiItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setItemName(String name) {
		itemName.setText(name);
	}

	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		this.itemName = (TextView) findViewById(R.id.addrItem);
	}
}
