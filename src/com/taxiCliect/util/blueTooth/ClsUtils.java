package com.taxiCliect.util.blueTooth;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

public class ClsUtils {
	/**
	 * 
	 * 与设备配对 参考源码：platform/packages/apps/Settings.git
	 * 
	 * /Settings/src/com/android/settings/bluetooth/CachedBluetoothDevice.java
	 */

	static public boolean createBond(Class btClass, BluetoothDevice btDevice)

	throws Exception

	{
		try {
			System.out.println("进行配对");
			Method createBondMethod = btClass.getMethod("createBond");
			createBondMethod.invoke(btDevice);
			// ClsUtils.cancelPairingUserInput(btDevice.getClass(), btDevice);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}

	}

	/**
	 * 
	 * 与设备解除配对 参考源码：platform/packages/apps/Settings.git
	 * 
	 * /Settings/src/com/android/settings/bluetooth/CachedBluetoothDevice.java
	 */

	static public boolean removeBond(Class btClass, BluetoothDevice btDevice)

	throws Exception

	{
		try {
			Method removeBondMethod = btClass.getMethod("removeBond");
			removeBondMethod.invoke(btDevice);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}

	}

	// 设置pin码
	static public boolean setPin(Class btClass, BluetoothDevice btDevice,
			String str) throws Exception {
		try {
			Method removeBondMethod = btClass.getDeclaredMethod("setPin",
					byte[].class);
			removeBondMethod.setAccessible(true);
			removeBondMethod.invoke(btDevice, str.getBytes("UTF-8"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	// 取消用户输入

	static public boolean cancelPairingUserInput(Class btClass,
			BluetoothDevice device) throws Exception {
		// System.out.println("取消该广播");
		try {
			Method createBondMethod = btClass
					.getMethod("cancelPairingUserInput");
			createBondMethod.invoke(device);
			// cancelBondProcess()
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}

	// 取消配对

	static public boolean cancelBondProcess(Class btClass,
			BluetoothDevice device) throws Exception {
		Method createBondMethod = btClass.getMethod("cancelBondProcess");
		Boolean returnValue = (Boolean) createBondMethod.invoke(device);
		return returnValue.booleanValue();
	}

	/**
	 * 
	 * 显示类
	 * 
	 * @param clsShow
	 */

	static public void printAllInform(Class clsShow) {
		try {
			// 取得所有方法
			Method[] hideMethod = clsShow.getMethods();
			int i = 0;
			for (; i < hideMethod.length; i++) {
				// System.out.println("方法名--》" + hideMethod[i].getName()
				// + ";and the i is:"
				//
				// + i);
			}
			// 取得所有常量
			Field[] allFields = clsShow.getFields();
			for (i = 0; i < allFields.length; i++) {
				if (allFields[i].getName().equals("mAddress"))
					System.out.println("常量--》" + allFields[i].getName());
			}
		}

		catch (SecurityException e) {
			// throw new RuntimeException(e.getMessage());
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// throw new RuntimeException(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
