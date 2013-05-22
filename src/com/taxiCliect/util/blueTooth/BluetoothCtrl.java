package com.taxiCliect.util.blueTooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

@SuppressLint({ "NewApi", "NewApi", "NewApi", "NewApi", "NewApi", "NewApi",
		"NewApi", "NewApi", "NewApi", "NewApi", "NewApi", "NewApi", "NewApi",
		"NewApi", "NewApi", "NewApi", "NewApi", "NewApi" })
public class BluetoothCtrl {
	// 蓝牙驱动
	private static BluetoothDevice remoteDevice;
	// 蓝牙通讯类
	private BluetoothChat bluetoothChat;
	// 连接handler
	private Handler handler = new Handler();
	// 传入的context
	private Context context;

	private BluetoothAdapter bluetoothAdapter;
	/**
	 * 连接的蓝牙地址
	 */
	private String addr;
	/**
	 * 连接的pin码
	 */
	private String pin;

	/**
	 * 构造方法传入Context
	 * 
	 * @param context
	 */
	public BluetoothCtrl(Context context) {
		this.context = context;
		// 获取蓝牙操作
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		// 如果开启则初始化
		bluetoothChat = new BluetoothChat(context);
	}

	// Runnable startRunnable = new Runnable() {
	// boolean on = true;
	//
	// @Override
	// public void run() {
	// bluetoothAdapter.cancelDiscovery();
	// if (!bluetoothAdapter.isEnabled()) {
	// // 未开启蓝牙
	// if (on) {
	// bluetoothAdapter.enable();
	// on = false;
	// }
	// handler.postDelayed(startRunnable, 1000);
	// } else {
	// // 继续进行配对
	// pair(addr, pin);
	// // 移除检查蓝牙进程
	// handler.removeCallbacks(startRunnable);
	// }
	// }
	// };

	public class startRunnable extends Thread {
		boolean on = true;

		@Override
		public void run() {
			while (true) {
				bluetoothAdapter.cancelDiscovery();
				if (!bluetoothAdapter.isEnabled()) {
					// 未开启蓝牙
					if (on) {
						bluetoothAdapter.enable();
						on = false;
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					// 继续进行配对
					pair(addr, pin);
					break;
					// 移除检查蓝牙进程
					// handler.removeCallbacks(startRunnable);
				}
			}
		}
	}

	/**
	 * 配对线程
	 */
	// Runnable runnable = new Runnable() {
	//
	// @Override
	// public void run() {
	// // TODO Auto-generated method stub
	// runpair(addr, pin);
	// }
	// };
	public static int LONG_TIME_CONNECTED = 360;

	public class runnable extends Thread {
		int count = 0;

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Looper.prepare();
			bluetoothChat = new BluetoothChat(context);
			if (!BluetoothAdapter.checkBluetoothAddress(addr)) { // 检查蓝牙地址是否有效
				return;
			}
			BluetoothDevice device = bluetoothAdapter.getRemoteDevice(addr);
			while (true) {
				if (count == LONG_TIME_CONNECTED) {
					Looper.loop();
				}
				if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
					// 如果未匹配，则继续执行
					// handler.postDelayed(runnable, 5000);
					try {
						ClsUtils.setPin(device.getClass(), device, pin); // 手机和蓝牙采集器配对
						ClsUtils.createBond(device.getClass(), device);
						remoteDevice = device; // 配对完毕就把这个设备对象传给全局的remoteDevice
						Thread.sleep(5000);
						count++;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					// 赋值至全局
					remoteDevice = device;
					// 移除线程后立即连接
					try {
						bluetoothChat.goConnect(device);
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					Looper.loop();
				}
			}
		}
	}

	/**
	 * 移除配对进程
	 */
	Runnable runnable2 = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			endPir(addr);
		}
	};

	/**
	 * 配对进程方法
	 * 
	 * @param strAddr
	 * @param strPsw
	 */
	// public void runpair(String strAddr, String strPsw) {
	// // boolean result = false;
	// // BluetoothAdapter bluetoothAdapter = BluetoothAdapter
	// // .getDefaultAdapter();
	// // bluetoothAdapter.cancelDiscovery();
	// BluetoothDevice device = bluetoothAdapter.getRemoteDevice(strAddr);
	// if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
	// // 如果未匹配，则继续执行
	// pair(strAddr, strPsw);
	// // handler.postDelayed(runnable, 5000);
	// } else {
	// // 赋值至全局
	// this.remoteDevice = device;
	// // 已匹配，移除线程
	// handler.removeCallbacks(runnable);
	// // 移除线程后立即连接
	// bluetoothChat.goConnect(device);
	// }
	//
	// }

	// 进行配对
	public boolean pair(String strAddr, String strPsw) {
		// 让全局的地址获得值
		this.addr = strAddr;
		this.pin = strPsw;
		boolean result = false;
		// BluetoothAdapter bluetoothAdapter = BluetoothAdapter
		// .getDefaultAdapter();
		// // 蓝牙地址--> bluetoothAdapter.getAddress()
		// bluetoothAdapter.cancelDiscovery();
		if (!bluetoothAdapter.isEnabled()) {
			// 未开启蓝牙
			// bluetoothAdapter.enable();
			// handler.post(startRunnable);
			new startRunnable().start();
			return false;

		} else {
			new runnable().start();
			// // 如果开启则初始化
			// bluetoothChat = new BluetoothChat(context);
			// if (!BluetoothAdapter.checkBluetoothAddress(strAddr)) { //
			// 检查蓝牙地址是否有效
			// return false;
			// }
			// // 远程设备地址--> strAddr
			// BluetoothDevice device =
			// bluetoothAdapter.getRemoteDevice(strAddr);
			//
			// if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
			// // 手机未匹配
			// try {
			// Log.d("mylog", "NOT BOND_BONDED");
			// ClsUtils.setPin(device.getClass(), device, strPsw); // 手机和蓝牙采集器配对
			// ClsUtils.createBond(device.getClass(), device);
			// remoteDevice = device; // 配对完毕就把这个设备对象传给全局的remoteDevice
			// result = true;
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// Log.d("mylog", "setPiN failed!");
			// e.printStackTrace();
			// } //
			// handler.postDelayed(runnable, 5000);
			// } else {
			// // 手机已匹配则连接
			// bluetoothChat.goConnect(device);
			// }
		}
		return result;
	}

	/**
	 * 移除配对进程
	 * 
	 * @param strAddr
	 */
	public void endPir(String addr) {
		this.addr = addr;
		// 若连接则先停止
		if (bluetoothChat.isBluetooth()) {
			bluetoothChat.getmChatService().stop();
			bluetoothChat.getmChatService().start();
			bluetoothChat.setBluetooth(false);
			// 让线程暂停2秒
			handler.postDelayed(runnable2, 2000);
		}
		// BluetoothAdapter bluetoothAdapter = BluetoothAdapter
		// .getDefaultAdapter();
		// bluetoothAdapter.cancelDiscovery();
		if (addr == null || addr.equals(""))
			return;
		BluetoothDevice device = bluetoothAdapter.getRemoteDevice(this.addr);
		try {
			if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
				// 该匹配正在移除
				ClsUtils.removeBond(device.getClass(), device);
				handler.postDelayed(runnable2, 5000);
			} else {
				// 已经移除
				handler.removeCallbacks(runnable2);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * 获取操作对象
	 * 
	 * @return
	 */
	public BluetoothChat getBluetoothChat() {
		if (!bluetoothAdapter.isEnabled()) {
			return null;
		}
		return bluetoothChat;
	}

	/**
	 * 获取蓝牙驱动
	 * 
	 * @return
	 */
	public static BluetoothDevice getRemoteDevice() {
		return remoteDevice;
	}

}
