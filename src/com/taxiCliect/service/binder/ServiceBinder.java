package com.taxiCliect.service.binder;

import java.util.HashMap;

import com.taxiCliect.service.Bootservice;

import android.os.Binder;
import android.os.Parcel;
import android.os.RemoteException;

public class ServiceBinder extends Binder {
	private Bootservice service;

	/**
	 * 构造方法取得service
	 * 
	 * @param service
	 */
	public ServiceBinder(Bootservice service) {
		this.service = service;
	}

	public Bootservice getService() {
		return service;
	}

	@Override
	protected boolean onTransact(int code, Parcel data, Parcel reply, int flags)
			throws RemoteException {
		// 读取map将之放入rootService中处理分发
		HashMap<String, Object> serviceMap = data.readHashMap(HashMap.class
				.getClassLoader());
		service.rootService(serviceMap,code);
		return super.onTransact(code, data, reply, flags);
	}
}
