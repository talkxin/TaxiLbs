package com.taxiCliect.module;

import com.taxiCliect.util.Annotation.TableName;
import com.taxiCliect.util.Annotation.TableProperty;

@TableName(name = "t_client_FileInteractive", tableKey = "id", nullable = true)
public class FileInteractive {
	@TableProperty
	private Integer id;
	@TableProperty
	private String route;
	@TableProperty
	private String name;
	@TableProperty
	private String newName;
	@TableProperty
	private String outputSize;
	@TableProperty
	private String inputSize;
	@TableProperty
	private int isGo;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNewName() {
		return newName;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}

	public String getOutputSize() {
		return outputSize;
	}

	public void setOutputSize(String outputSize) {
		this.outputSize = outputSize;
	}

	public String getInputSize() {
		return inputSize;
	}

	public void setInputSize(String inputSize) {
		this.inputSize = inputSize;
	}

	public Integer getIsGo() {
		return isGo;
	}

	public void setIsGo(Integer isGo) {
		this.isGo = isGo;
	}

}
