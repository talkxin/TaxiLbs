package com.taxiCliect.module;

import java.io.Serializable;

import com.taxiCliect.util.Annotation.JsonToAction;
import com.taxiCliect.util.Annotation.TableName;
import com.taxiCliect.util.Annotation.TableProperty;

/**
 * 订单缓存
 * 
 * @author talkliu
 * 
 */
@TableName(name = "t_client_taxiService", tableKey = "serviceId", nullable = false)
public class TaxiService implements Serializable {
	@TableProperty
	@JsonToAction
	private Long serviceId;
	@TableProperty
	@JsonToAction
	private Long did;
	@TableProperty
	@JsonToAction
	private String userName;
	@TableProperty
	@JsonToAction
	private String userNambr;
	@TableProperty
	@JsonToAction
	private Long uid;
	@TableProperty
	@JsonToAction
	private String city;
	@TableProperty
	@JsonToAction
	private Double startAddLon;
	@TableProperty
	@JsonToAction
	private Double startAddLat;
	@TableProperty
	@JsonToAction
	private String endAdd;
	@TableProperty
	@JsonToAction
	private String startTime;
	@TableProperty
	@JsonToAction
	private String upTime;
	@TableProperty
	@JsonToAction
	private String endTime;
	@TableProperty
	@JsonToAction
	private String appointmentTime;
	@TableProperty
	@JsonToAction
	private String appointmentAdd;
	@TableProperty
	@JsonToAction
	private String appointmentEnd;
	@TableProperty
	@JsonToAction
	private String appointmentEndTime;
	@TableProperty
	@JsonToAction
	private String newEndAdd;
	@TableProperty
	@JsonToAction
	private String newEndTime;
	@TableProperty
	@JsonToAction
	private String kmNumber;
	@TableProperty
	@JsonToAction
	private Double payNumber;
	@TableProperty
	@JsonToAction
	private Integer serviceType;
	@TableProperty
	@JsonToAction
	private Integer serviceEnd;
	@TableProperty
	@JsonToAction(toJson = false)
	private Integer serviceUserType;
	@TableProperty
	@JsonToAction(toJson = false)
	private String bluetoothMac;
	@TableProperty
	@JsonToAction
	private String orderAddTime;
	@TableProperty(toObject = false)
	@JsonToAction
	private String passengerBlue;
	@TableProperty(toObject = false)
	@JsonToAction
	private String driverBlue;
	@TableProperty(toObject = false)
	@JsonToAction
	private String other;
	@TableProperty(toObject = false)
	@JsonToAction
	private Long goTime;

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public Long getGoTime() {
		return goTime;
	}

	public void setGoTime(Long goTime) {
		this.goTime = goTime;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}

	public Long getDid() {
		return did;
	}

	public void setDid(Long did) {
		this.did = did;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserNambr() {
		return userNambr;
	}

	public void setUserNambr(String userNambr) {
		this.userNambr = userNambr;
	}

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Double getStartAddLon() {
		return startAddLon;
	}

	public void setStartAddLon(Double startAddLon) {
		this.startAddLon = startAddLon;
	}

	public Double getStartAddLat() {
		return startAddLat;
	}

	public void setStartAddLat(Double startAddLat) {
		this.startAddLat = startAddLat;
	}

	public String getEndAdd() {
		return endAdd;
	}

	public void setEndAdd(String endAdd) {
		this.endAdd = endAdd;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getUpTime() {
		return upTime;
	}

	public void setUpTime(String upTime) {
		this.upTime = upTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getAppointmentTime() {
		return appointmentTime;
	}

	public void setAppointmentTime(String appointmentTime) {
		this.appointmentTime = appointmentTime;
	}

	public String getAppointmentAdd() {
		return appointmentAdd;
	}

	public void setAppointmentAdd(String appointmentAdd) {
		this.appointmentAdd = appointmentAdd;
	}

	public String getAppointmentEnd() {
		return appointmentEnd;
	}

	public void setAppointmentEnd(String appointmentEnd) {
		this.appointmentEnd = appointmentEnd;
	}

	public String getAppointmentEndTime() {
		return appointmentEndTime;
	}

	public void setAppointmentEndTime(String appointmentEndTime) {
		this.appointmentEndTime = appointmentEndTime;
	}

	public String getNewEndAdd() {
		return newEndAdd;
	}

	public void setNewEndAdd(String newEndAdd) {
		this.newEndAdd = newEndAdd;
	}

	public String getNewEndTime() {
		return newEndTime;
	}

	public void setNewEndTime(String newEndTime) {
		this.newEndTime = newEndTime;
	}

	public String getKmNumber() {
		return kmNumber;
	}

	public void setKmNumber(String kmNumber) {
		this.kmNumber = kmNumber;
	}

	public Double getPayNumber() {
		return payNumber;
	}

	public void setPayNumber(Double payNumber) {
		this.payNumber = payNumber;
	}

	public Integer getServiceType() {
		return serviceType;
	}

	public void setServiceType(Integer serviceType) {
		this.serviceType = serviceType;
	}

	public Integer getServiceEnd() {
		return serviceEnd;
	}

	public void setServiceEnd(Integer serviceEnd) {
		this.serviceEnd = serviceEnd;
	}

	public Integer getServiceUserType() {
		return serviceUserType;
	}

	public void setServiceUserType(Integer serviceUserType) {
		this.serviceUserType = serviceUserType;
	}

	public String getBluetoothMac() {
		return bluetoothMac;
	}

	public void setBluetoothMac(String bluetoothMac) {
		this.bluetoothMac = bluetoothMac;
	}

	public String getOrderAddTime() {
		return orderAddTime;
	}

	public void setOrderAddTime(String orderAddTime) {
		this.orderAddTime = orderAddTime;
	}

	public String getPassengerBlue() {
		return passengerBlue;
	}

	public void setPassengerBlue(String passengerBlue) {
		this.passengerBlue = passengerBlue;
	}

	public String getDriverBlue() {
		return driverBlue;
	}

	public void setDriverBlue(String driverBlue) {
		this.driverBlue = driverBlue;
	}

}
