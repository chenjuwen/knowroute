package com.heasy.knowroute.bean;

import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description="定点类别JavaBean类")
public class FixedPointCategoryBean {
	@ApiModelProperty(value="id")
	private int id;
	
	@ApiModelProperty(value="用户ID，表示类别的归属")
	private int userId;
	
	@ApiModelProperty(value="类别名")
	private String name;
	
	@ApiModelProperty(value="是否置顶，1置顶，0不置顶")
	private int topping;
	
	@ApiModelProperty(value="创建日期")
	private Date createDate;
	
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
	
	public int getTopping() {
		return topping;
	}
	
	public void setTopping(int topping) {
		this.topping = topping;
	}
	
	public Date getCreateDate() {
		return createDate;
	}
	
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
}
