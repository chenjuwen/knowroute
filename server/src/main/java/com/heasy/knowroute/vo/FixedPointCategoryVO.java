package com.heasy.knowroute.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description="定点类别DTO类")
public class FixedPointCategoryVO {
	@ApiModelProperty(value="id")
	private int id;
	
	@ApiModelProperty(value="用户ID，表示类别的归属")
	private int userId;
	
	@ApiModelProperty(value="类别名")
	private String name;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getUserId() {
		return userId;
	}
	
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
}
