package com.heasy.knowroute.bean;

import java.util.Date;

public class FixedPointCategoryBean {
	private int id;
	private int userId;
	private String name;
	private int topping;
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
