package com.heasy.knowroute.vo;

public class InviteUserVO {
	private String userId;
	private String friendUserId;
	private String phone;
	
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getFriendUserId() {
		return friendUserId;
	}

	public void setFriendUserId(String friendUserId) {
		this.friendUserId = friendUserId;
	}

	public String getPhone() {
		return phone;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
}
