package com.taxiCliect.module;

import java.io.Serializable;

import com.taxiCliect.util.Annotation.TableName;
import com.taxiCliect.util.Annotation.TableProperty;

/**
 * 路线module
 * 
 * @author talkliu
 * 
 */
@TableName(name = "t_client_taxiRoute", tableKey = "id", nullable = true)
public class TaxiRoute implements Serializable {
	@TableProperty
	private Integer id;
	@TableProperty
	private Long uid;
	@TableProperty
	private String name;
	@TableProperty
	private String city;
	@TableProperty
	private String startAdd;
	@TableProperty
	private String startStr;
	@TableProperty
	private String endAdd;
	@TableProperty
	private String endStr;
	@TableProperty
	private String routeKm;
	@TableProperty
	private String remarks;
	@TableProperty
	private Integer uses;

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCity() {
		return city;
	}

	public Integer getUses() {
		return uses;
	}

	public void setUses(Integer uses) {
		this.uses = uses;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getStartAdd() {
		return startAdd;
	}

	public void setStartAdd(String startAdd) {
		this.startAdd = startAdd;
	}

	public String getEndAdd() {
		return endAdd;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStartStr() {
		return startStr;
	}

	public void setStartStr(String startStr) {
		this.startStr = startStr;
	}

	public String getEndStr() {
		return endStr;
	}

	public void setEndStr(String endStr) {
		this.endStr = endStr;
	}

	public void setEndAdd(String endAdd) {
		this.endAdd = endAdd;
	}

	public String getRouteKm() {
		return routeKm;
	}

	public void setRouteKm(String routeKm) {
		this.routeKm = routeKm;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}
