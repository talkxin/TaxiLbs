package com.taxiCliect.module;

import java.io.Serializable;

import com.taxiCliect.util.Annotation.JsonToAction;
import com.taxiCliect.util.Annotation.TableName;
import com.taxiCliect.util.Annotation.TableProperty;

/**
 * 用户登录信息缓存
 * 
 * @author talkliu
 * 
 */
@TableName(name = "t_client_taxiUser", tableKey = "uid", nullable = false)
public class TaxiUser implements Serializable{
	@TableProperty
	@JsonToAction
	private Long uid;
	@TableProperty
	@JsonToAction
	private String userName;
	@TableProperty
	@JsonToAction
	private String loginName;
	@TableProperty
	@JsonToAction
	private String password;
	@TableProperty
	@JsonToAction
	private String phoneNumber;
	@TableProperty
	@JsonToAction
	private String email;
	@TableProperty
	@JsonToAction
	private Integer isLogin;
	@TableProperty
	@JsonToAction(toJson = false)
	private Integer loginUser;

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getIsLogin() {
		return isLogin;
	}

	public void setIsLogin(Integer isLogin) {
		this.isLogin = isLogin;
	}

	public Integer getLoginUser() {
		return loginUser;
	}

	public void setLoginUser(Integer loginUser) {
		this.loginUser = loginUser;
	}

}
