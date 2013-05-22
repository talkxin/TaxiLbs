package com.taxiCliect.module;

import com.taxiCliect.util.Annotation.JsonToAction;
import com.taxiCliect.util.Annotation.TableName;
import com.taxiCliect.util.Annotation.TableProperty;

/**
 * 司机信息缓存
 * 
 * @author talkliu
 * 
 */
@TableName(name = "t_client_DriverInfo", tableKey = "id", nullable = true)
public class DriverInfo {
	@TableProperty
	@JsonToAction
	private Integer id;
	@TableProperty
	@JsonToAction
	private Long uid;
	@TableProperty
	@JsonToAction
	private String driverName;
	@TableProperty
	@JsonToAction
	private String driverCompany;
	@TableProperty
	@JsonToAction
	private String companyPhone;
	@TableProperty
	@JsonToAction
	private String driverCity;
	@TableProperty
	@JsonToAction
	private String plateNumber;
	@TableProperty
	@JsonToAction
	private String companyNo;
	@TableProperty
	@JsonToAction
	private String taobaoLoginName;
	@TableProperty
	@JsonToAction
	private Integer isTrue;
	@JsonToAction
	@TableProperty(toObject = false)
	private String lon;
	@JsonToAction
	@TableProperty(toObject = false)
	private String lat;
	@JsonToAction
	@TableProperty(toObject = false)
	private String updateTime;

	public String getLon() {
		return lon;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getDriverCompany() {
		return driverCompany;
	}

	public void setDriverCompany(String driverCompany) {
		this.driverCompany = driverCompany;
	}

	public String getCompanyPhone() {
		return companyPhone;
	}

	public void setCompanyPhone(String companyPhone) {
		this.companyPhone = companyPhone;
	}

	public String getDriverCity() {
		return driverCity;
	}

	public void setDriverCity(String driverCity) {
		this.driverCity = driverCity;
	}

	public String getPlateNumber() {
		return plateNumber;
	}

	public void setPlateNumber(String plateNumber) {
		this.plateNumber = plateNumber;
	}

	public String getCompanyNo() {
		return companyNo;
	}

	public void setCompanyNo(String companyNo) {
		this.companyNo = companyNo;
	}

	public String getTaobaoLoginName() {
		return taobaoLoginName;
	}

	public void setTaobaoLoginName(String taobaoLoginName) {
		this.taobaoLoginName = taobaoLoginName;
	}

	public Integer getIsTrue() {
		return isTrue;
	}

	public void setIsTrue(Integer isTrue) {
		this.isTrue = isTrue;
	}
}
