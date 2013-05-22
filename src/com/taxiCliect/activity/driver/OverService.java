package com.taxiCliect.activity.driver;

import android.content.Context;

import com.taxiCliect.activity.R;
import com.taxiCliect.module.TaxiService;
import com.taxiCliect.util.postAction.PostToAction;

/**
 * 完成订单
 * 
 * @author talkliu
 * 
 */
public class OverService extends Thread {
	private TaxiService taxiService;
	private Context context;

	public OverService(TaxiService taxiService, Context context)
			throws Exception {
		// TODO Auto-generated constructor stub
		taxiService.setServiceEnd(2);
		String path = context.getString(R.string.serverPath)
				+ context.getString(R.string.overService);
		PostToAction postToAction = new PostToAction();
		postToAction.postToServer(path, new Object[] { taxiService,
				"serviceObj" });
	}
}
