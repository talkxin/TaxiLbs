package com.taxiCliect.util.blueTooth;

import java.io.Serializable;

public class ChatModule implements Serializable {
	/**
	 * 对话状态
	 */
	private int chatState;
	/**
	 * 用户存储交流的对象
	 */
	private Object chatObject;
	/**
	 * 用户存储交流的字符串
	 */
	private String chatString;
	/**
	 * 用户存储交流的文件对象
	 */
	private ChatFileModule chatFileModule;

	/**
	 * 构造方法
	 * 
	 * @param chatState
	 * @param chatObject
	 * @param chatString
	 * @param chatFileModule
	 */
	public ChatModule(int chatState, Object chatObject, String chatString,
			ChatFileModule chatFileModule) {
		this.chatState = chatState;
		this.chatObject = chatObject;
		this.chatString = chatString;
		this.chatFileModule = chatFileModule;
	}

	/**
	 * 空构造
	 */
	public ChatModule() {

	}

	public int getChatState() {
		return chatState;
	}

	public void setChatState(int chatState) {
		this.chatState = chatState;
	}

	public Object getChatObject() {
		return chatObject;
	}

	public void setChatObject(Object chatObject) {
		this.chatObject = chatObject;
	}

	public String getChatString() {
		return chatString;
	}

	public void setChatString(String chatString) {
		this.chatString = chatString;
	}

	public ChatFileModule getChatFileModule() {
		return chatFileModule;
	}

	public void setChatFileModule(ChatFileModule chatFileModule) {
		this.chatFileModule = chatFileModule;
	}
}
