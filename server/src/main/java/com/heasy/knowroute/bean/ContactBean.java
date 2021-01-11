package com.heasy.knowroute.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description="紧急联系人JavaBean类")
public class ContactBean {
	@ApiModelProperty(value="id")
	private int id;
	
	@ApiModelProperty(value="用户ID")
	private int userId;
	
	@ApiModelProperty(value="联系人姓名")
	private String contactName;
	
	@ApiModelProperty(value="联系人手机号")
	private String contactPhone;
	
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
	
	public String getContactName() {
		return contactName;
	}
	
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	
	public String getContactPhone() {
		return contactPhone;
	}
	
	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}
	
}
