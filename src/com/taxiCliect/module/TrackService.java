package com.taxiCliect.module;

import java.io.Serializable;

/**
 * 用来追踪的对象
 * 
 * @author talkliu
 * 
 */
public class TrackService implements Serializable {
	// 预约订单
	private TaxiService taxiService;
	private Long goDate;
	private Double money;
	private Double goKm;
	private Boolean isUp;
	private Integer instruction;
	private Double lon;
	private Double lat;
	public static Integer PASSENGER_UP_VERIFICATION = -1;// Verification
	public static Integer PASSENGER_UP_THROUGH = -2;// Through
	public static Integer PASSENGER_UP = 1;// PassengerMainActivity.java
	public static Integer PASSNEGER_UP_UPDATE = 2;
	public static Integer PASSENGER_LOSE = -3;
	public static Integer IAM_DRIVER = 3;
	public static Integer IM_PASSENGER = 4;
	public static Integer TO_ALIPAY = 5;
	public static Integer TO_PAYFORCARD = 6;

	public Double getLon() {
		return lon;
	}

	public Double getGoKm() {
		return goKm;
	}

	public void setGoKm(Double goKm) {
		this.goKm = goKm;
	}

	public void setLon(Double lon) {
		this.lon = lon;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Boolean isUp() {
		return isUp;
	}

	public Integer getInstruction() {
		return instruction;
	}

	public void setInstruction(Integer instruction) {
		this.instruction = instruction;
	}

	public void setUp(Boolean isUp) {
		this.isUp = isUp;
	}

	public TaxiService getTaxiService() {
		return taxiService;
	}

	public void setTaxiService(TaxiService taxiService) {
		this.taxiService = taxiService;
	}

	public Long getGoDate() {
		return goDate;
	}

	public void setGoDate(Long goDate) {
		this.goDate = goDate;
	}

	public Double getMoney() {
		return money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

}
