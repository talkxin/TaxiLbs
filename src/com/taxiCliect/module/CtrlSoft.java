package com.taxiCliect.module;

import android.R.integer;

import com.taxiCliect.util.Annotation.TableName;
import com.taxiCliect.util.Annotation.TableProperty;

/**
 * 系统设置
 * 
 * @author talkliu
 * 
 */
@TableName(name = "t_client_ctrlSoft", tableKey = "uid", nullable = false)
public class CtrlSoft {
	@TableProperty
	private Long uid;
	@TableProperty
	private Integer userModule;
	@TableProperty
	private Integer music;
	@TableProperty
	private Integer vibration;
	@TableProperty
	private Integer speech;
	@TableProperty
	private Integer offLine;
	@TableProperty
	private Integer layoutModule;
	@TableProperty
	private Integer help_passenger_main;
	@TableProperty
	private Integer help_passenger_dache;
	@TableProperty
	private Integer help_passenger_yuyue;
	@TableProperty
	private Integer help_passenger_daijia;
	@TableProperty
	private Integer help_passenger_changyongluxian;
	@TableProperty
	private Integer help_passenger_dingdanchaxun;
	@TableProperty
	private Integer help_passenger_jijiamoshi;
	@TableProperty
	private Integer help_driver_main;
	@TableProperty
	private Integer help_driver_yuyue;
	@TableProperty
	private Integer help_driver_dingdanchaxun;
	@TableProperty
	private Integer help_driver_jijiamoshi;

	/**
	 * 主键
	 * 
	 * @return
	 */
	public Long getUid() {
		return uid;
	}

	/**
	 * 主键
	 * 
	 * @return
	 */
	public void setUid(Long uid) {
		this.uid = uid;
	}

	/**
	 * 所在城市
	 * 
	 * @param uid
	 */
	public Integer getUserModule() {
		return userModule;
	}

	/**
	 * 所在城市
	 * 
	 * @param uid
	 */
	public void setUserModule(Integer userModule) {
		this.userModule = userModule;
	}

	/**
	 * 是否音乐提醒
	 * 
	 * @return
	 */
	public Integer getMusic() {
		return music;
	}

	/**
	 * 是否音乐提醒
	 * 
	 * @return
	 */
	public void setMusic(Integer music) {
		this.music = music;
	}

	/**
	 * 是否震动提示
	 * 
	 * @return
	 */
	public Integer getVibration() {
		return vibration;
	}

	/**
	 * 是否震动提示
	 * 
	 * @return
	 */
	public void setVibration(Integer vibration) {
		this.vibration = vibration;
	}

	/**
	 * 是否语音提示
	 * 
	 * @return
	 */
	public Integer getSpeech() {
		return speech;
	}

	/**
	 * 是否语音提示
	 * 
	 * @return
	 */
	public void setSpeech(Integer speech) {
		this.speech = speech;
	}

	/**
	 * 离线地图
	 * 
	 * @return
	 */
	public Integer getOffLine() {
		return offLine;
	}

	/**
	 * 离线地图
	 * 
	 * @return
	 */
	public void setOffLine(Integer offLine) {
		this.offLine = offLine;
	}

	/**
	 * 是否开启横屏
	 * 
	 * @return
	 */
	public Integer getLayoutModule() {
		return layoutModule;
	}

	/**
	 * 是否开启横屏
	 * 
	 * @return
	 */
	public void setLayoutModule(Integer layoutModule) {
		this.layoutModule = layoutModule;
	}

	public Integer getHelp_passenger_main() {
		return help_passenger_main;
	}

	public void setHelp_passenger_main(Integer help_passenger_main) {
		this.help_passenger_main = help_passenger_main;
	}

	public Integer getHelp_passenger_dache() {
		return help_passenger_dache;
	}

	public void setHelp_passenger_dache(Integer help_passenger_dache) {
		this.help_passenger_dache = help_passenger_dache;
	}

	public Integer getHelp_passenger_yuyue() {
		return help_passenger_yuyue;
	}

	public void setHelp_passenger_yuyue(Integer help_passenger_yuyue) {
		this.help_passenger_yuyue = help_passenger_yuyue;
	}

	public Integer getHelp_passenger_daijia() {
		return help_passenger_daijia;
	}

	public void setHelp_passenger_daijia(Integer help_passenger_daijia) {
		this.help_passenger_daijia = help_passenger_daijia;
	}

	public Integer getHelp_passenger_changyongluxian() {
		return help_passenger_changyongluxian;
	}

	public void setHelp_passenger_changyongluxian(
			Integer help_passenger_changyongluxian) {
		this.help_passenger_changyongluxian = help_passenger_changyongluxian;
	}

	public Integer getHelp_passenger_dingdanchaxun() {
		return help_passenger_dingdanchaxun;
	}

	public void setHelp_passenger_dingdanchaxun(
			Integer help_passenger_dingdanchaxun) {
		this.help_passenger_dingdanchaxun = help_passenger_dingdanchaxun;
	}

	public Integer getHelp_passenger_jijiamoshi() {
		return help_passenger_jijiamoshi;
	}

	public void setHelp_passenger_jijiamoshi(Integer help_passenger_jijiamoshi) {
		this.help_passenger_jijiamoshi = help_passenger_jijiamoshi;
	}

	public Integer getHelp_driver_main() {
		return help_driver_main;
	}

	public void setHelp_driver_main(Integer help_driver_main) {
		this.help_driver_main = help_driver_main;
	}

	public Integer getHelp_driver_yuyue() {
		return help_driver_yuyue;
	}

	public void setHelp_driver_yuyue(Integer help_driver_yuyue) {
		this.help_driver_yuyue = help_driver_yuyue;
	}

	public Integer getHelp_driver_dingdanchaxun() {
		return help_driver_dingdanchaxun;
	}

	public void setHelp_driver_dingdanchaxun(Integer help_driver_dingdanchaxun) {
		this.help_driver_dingdanchaxun = help_driver_dingdanchaxun;
	}

	public Integer getHelp_driver_jijiamoshi() {
		return help_driver_jijiamoshi;
	}

	public void setHelp_driver_jijiamoshi(Integer help_driver_jijiamoshi) {
		this.help_driver_jijiamoshi = help_driver_jijiamoshi;
	}

}
