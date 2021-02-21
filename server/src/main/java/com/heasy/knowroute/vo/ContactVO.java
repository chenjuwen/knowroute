package com.heasy.knowroute.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description="紧急联系人VO类")
public class ContactVO {
	@ApiModelProperty(value="求助人用户ID")
	private int userId;
	
	@ApiModelProperty(value="求助人手机号")
	private String helpPhone;
	
	@ApiModelProperty(value="好友手机号")
	private String friendPhone;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getHelpPhone() {
		return helpPhone;
	}

	public void setHelpPhone(String helpPhone) {
		this.helpPhone = helpPhone;
	}

	public String getFriendPhone() {
		return friendPhone;
	}

	public void setFriendPhone(String friendPhone) {
		this.friendPhone = friendPhone;
	}
	
}
