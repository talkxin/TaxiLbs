package com.taxiCliect.util.blueTooth;

import java.util.HashMap;

import com.taxiCliect.activity.DriverMainActivity;
import com.taxiCliect.activity.R;
import com.taxiCliect.activity.map.MapMain;
import com.taxiCliect.module.TrackService;
import com.taxiCliect.service.Bootservice;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

/**
 * 接受蓝牙处理的封装类
 * 
 * @author talkliu
 * 
 */
@SuppressLint("NewApi")
public class BluetoothChat {
	// 检测handler的状态
	public static final int MESSAGE_STATE_CHANGE = 1;
	// 读取状态
	public static final int MESSAGE_READ = 2;
	// 发送状态
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;
	// 传送对象
	public static final int MESSAGE_TOOBJECT = 6;
	// 传送字符串
	public static final int MESSAGE_TOSTRING = 7;
	// 传送文件
	public static final int MESSAGE_TOFILE = 8;

	// handler回传的信息key
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";
	/**
	 * 是否连接成功
	 */
	private boolean isBluetooth = false;

	// 配对后的连接
	private BluetoothDevice device;
	// 获取的Context权限
	private Context context;
	// 蓝牙连接管理类对象
	private BluetoothChatService mChatService;
	// 对连接情况进行监视的线程
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				switch (msg.arg1) {
				case BluetoothChatService.STATE_CONNECTED:
					// 连接成功
					Toast.makeText(context, "连接成功", Toast.LENGTH_SHORT).show();
					break;
				case BluetoothChatService.STATE_CONNECTING:
					// 正在连接状态
					break;
				case BluetoothChatService.STATE_LISTEN:
				case BluetoothChatService.STATE_NONE:
					// 此为无连接情况下进行的操作
					break;
				}
				break;
			case MESSAGE_WRITE:
				// 自己发送的信息
				break;
			case MESSAGE_READ:
				// 接收的信息
				ChatModule module = (ChatModule) msg.obj;
				switch (msg.arg2) {
				case MESSAGE_TOOBJECT:
					// 取出Object
					useObject(module.getChatObject());
					break;
				case MESSAGE_TOSTRING:
					// 取出传递字符串
					useString(module.getChatString());
					break;
				case MESSAGE_TOFILE:
					// 取出传递的文件
					useFile(module.getChatFileModule());
					break;
				}
				break;
			case MESSAGE_DEVICE_NAME:
				// save the connected device's name
				// 连接成功后的操作
				// msg.getData().getString(DEVICE_NAME); 获取连接的名称
				// Toast.makeText(context, "连接成功", Toast.LENGTH_SHORT).show();
				// 连接成功后，将状态改为成功
				isBluetooth = true;
				// 司机确认上车
				if (DriverMainActivity.trackServic != null) {
					DriverMainActivity.trackServic
							.setInstruction(TrackService.PASSENGER_UP_VERIFICATION);
					DriverMainActivity.trackServic.setUp(isBluetooth);
				} else {
					// 乘客方请求请求对象
					ChatModule chatModule = new ChatModule();
					chatModule.setChatState(BluetoothChat.MESSAGE_TOSTRING);
					chatModule.setChatString(String
							.valueOf(TrackService.PASSENGER_UP));
					try {
						Bootservice.bluetoothCtrl
								.getBluetoothChat()
								.sendMessage(
										ObjectUtil
												.getBytesFromObject(chatModule));
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
				break;
			case MESSAGE_TOAST:
				// 通知失去连接
				if (DriverMainActivity.trackServic != null) {
					DriverMainActivity.trackServic.setUp(false);
				}
				if (Bootservice.passengerTrackService != null) {
					Bootservice.bluetoothCtrl
							.endPir(Bootservice.passengerTrackService
									.getTaxiService().getDriverBlue());
					Bootservice.passengerTrackService = null;
				}
				// 失去连接时的操作
				switch (msg.getData().getInt(TOAST)) {
				case BluetoothChatService.STATE_UNABLE:
					if (mChatService != null) {
						mChatService.stop();
						mChatService.start();
						isBluetooth = false;
					}
					break;
				case BluetoothChatService.STATE_LOST:
					// 失去连接的时候重新初始化所有
					if (mChatService != null) {
						mChatService.stop();
						mChatService.start();
						isBluetooth = false;
					}
					break;
				}
				break;
			}
		}
	};

	/**
	 * 构造方法，初始化时及构造蓝牙的连接，即配对后进行连接！
	 * 
	 * @param context
	 * @param device
	 */
	public BluetoothChat(Context context) {
		this.context = context;
		mChatService = new BluetoothChatService(context, mHandler);
		if (mChatService != null) {
			// 初始化连接，并开启
			if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
				mChatService.start();
			}
		}
	}

	/**
	 * 关闭服务
	 */
	public void stopService() {
		mChatService.stop();
	}

	/**
	 * 发送信息用方法
	 * 
	 * @param message
	 */
	public void sendMessage(byte[] message) {
		// 判断连接状态
		if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
			// 如果是空连接则不发送
			return;
		}

		if (null != message && message.length > 0) {
			// 发送信息
			try {
				mChatService.write(message);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	/**
	 * 创建连接
	 * 
	 * @param device
	 */
	public void goConnect(BluetoothDevice device) {
		mChatService.connect(device);
	}

	/**
	 * 处理对象
	 * 
	 * @param obj
	 */
	private void useObject(Object obj) {
		TrackService trackService = (TrackService) obj;
		Bootservice.passengerTrackService = trackService;
		// switch (trackService.getInstruction()) {
		// case -2:
		Intent intent = new Intent(context, MapMain.class);
		HashMap<String, Object> appointmentDemo = new HashMap<String, Object>();
		appointmentDemo.put("type", 6);
		intent.putExtra("appointmentDemo", appointmentDemo);
		Bootservice.toNotice(context, R.drawable.icon, "您有条新消息", "我要打车",
				"点击进入计价模式", intent, 1);
		// break;
		// }
	}

	/**
	 * 处理字符串
	 * 
	 * @param string
	 */
	private void useString(String string) {
		Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 处理接受文件
	 * 
	 * @param fileMap
	 */
	// private void useFile(HashMap<String, Object> fileMap) {
	// // 进行存储文件
	// try {
	// if ((Boolean) fileMap.get(ObjectUtil.file_save)) {
	// fileMap = ObjectUtil.aheadFileMap(fileMap, context);
	// } else {
	// fileMap = ObjectUtil.saveFile(fileMap, this.context);
	// }
	// if (null != fileMap) {
	// gtoFile(fileMap);
	// }
	// if (MainActivity.firstBar.getMax() != Integer.parseInt(fileMap.get(
	// "file_outputSize").toString())) {
	// MainActivity.firstBar.setMax(Integer.parseInt(fileMap.get(
	// "file_outputSize").toString()));
	// MainActivity.firstBar.setVisibility(View.VISIBLE);
	// MainActivity.firstBar.setProgress(Integer.parseInt(fileMap.get(
	// "file_inputSize").toString()));
	// } else {
	// MainActivity.firstBar.setProgress(Integer.parseInt(fileMap.get(
	// "file_inputSize").toString()));
	// }
	//
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// Toast.makeText(context, "传送完成", Toast.LENGTH_SHORT).show();
	// e.printStackTrace();
	// }
	// }

	private void useFile(ChatFileModule chatFileModule) {
		// 进行存储文件
		try {
			if (chatFileModule.isSave()) {
				chatFileModule = ObjectUtil.aheadFileMap(chatFileModule,
						context);
			} else {
				chatFileModule = ObjectUtil.saveFile(chatFileModule,
						this.context);
			}
			if (null != chatFileModule) {
				gtoFile(chatFileModule);
			}
			if (null != chatFileModule) {
				int zongliang = Integer.parseInt(String.valueOf(chatFileModule
						.getOutputSize()));
				int zengliang = Integer.parseInt(String.valueOf(chatFileModule
						.getInputSize()));
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			Toast.makeText(context, "传送完成", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	/**
	 * 处理传递文件
	 * 
	 * @param path
	 */
	public void gtoFile(ChatFileModule chatFileModule) {
		// 判断连接状态
		if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
			// 如果是空连接则不发送
			return;
		}
		try {
			// HashMap<String, Object> gotoMap = new HashMap<String, Object>();
			// gotoMap.put(BluetoothChatService.MESSAGE_NAME,
			// BluetoothChat.MESSAGE_TOFILE);
			ChatModule chatModule = new ChatModule();
			chatModule.setChatState(BluetoothChat.MESSAGE_TOFILE);
			// 封装成传送文件模式并发送
			// gotoMap.put(BluetoothChatService.MESSAGE_OBJECT, map);
			chatModule.setChatFileModule(chatFileModule);
			mChatService.write(ObjectUtil.getBytesFromObject(chatModule));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 返回对象，用于停止连接
	 * 
	 * @return
	 */
	public BluetoothChatService getmChatService() {
		return mChatService;
	}

	/**
	 * 返回是否连接成功
	 * 
	 * @return
	 */
	public boolean isBluetooth() {
		return isBluetooth;
	}

	/**
	 * 初始化
	 * 
	 * @param isBluetooth
	 */
	public void setBluetooth(boolean isBluetooth) {
		this.isBluetooth = isBluetooth;
	}

}
