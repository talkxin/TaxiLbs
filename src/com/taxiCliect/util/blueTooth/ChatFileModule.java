package com.taxiCliect.util.blueTooth;

import java.io.Serializable;

public class ChatFileModule implements Serializable {
	/**
	 * 文件路径
	 */
	private String Route;
	/**
	 * 文件名
	 */
	private String name;
	/**
	 * 新文件名
	 */
	private String newName;
	/**
	 * 文件大小
	 */
	private Long outputSize;
	/**
	 * 已下载文件大小
	 */
	private Long inputSize;
	/**
	 * 是否继续下载
	 */
	private Integer isGo;
	/**
	 * 文件
	 */
	private byte[] fileBytes;
	/**
	 * 存储完成继续传输
	 */
	public boolean save;
	/**
	 * 文件路径
	 */
	public String getRoute() {
		return Route;
	}
	/**
	 * 文件路径
	 */
	public void setRoute(String route) {
		Route = route;
	}
	/**
	 * 文件名
	 */
	public String getName() {
		return name;
	}
	/**
	 * 文件名
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 新文件名
	 */
	public String getNewName() {
		return newName;
	}
	/**
	 * 新文件名
	 */
	public void setNewName(String newName) {
		this.newName = newName;
	}
	/**
	 * 文件大小
	 */
	public Long getOutputSize() {
		return outputSize;
	}
	/**
	 * 文件大小
	 */
	public void setOutputSize(Long outputSize) {
		this.outputSize = outputSize;
	}
	/**
	 * 已下载文件大小
	 */
	public Long getInputSize() {
		return inputSize;
	}
	/**
	 * 已下载文件大小
	 */
	public void setInputSize(Long inputSize) {
		this.inputSize = inputSize;
	}
	/**
	 * 是否继续下载
	 */
	public Integer getIsGo() {
		return isGo;
	}
	/**
	 * 是否继续下载
	 */
	public void setIsGo(Integer isGo) {
		this.isGo = isGo;
	}
	/**
	 * 文件
	 */
	public byte[] getFileBytes() {
		return fileBytes;
	}
	/**
	 * 文件
	 */
	public void setFileBytes(byte[] fileBytes) {
		this.fileBytes = fileBytes;
	}
	/**
	 * 存储完成继续传输
	 */
	public boolean isSave() {
		return save;
	}
	/**
	 * 存储完成继续传输
	 */
	public void setSave(boolean save) {
		this.save = save;
	}

}
