package com.heasy.knowroute.bean;

/**
 * 好友
 */
public class FriendBean {
	private int id;
	private int userId;
	private int relatedUserId;
	private String phone;
	private String nickname;
	
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
	
	public int getRelatedUserId() {
		return relatedUserId;
	}
	
	public void setRelatedUserId(int relatedUserId) {
		this.relatedUserId = relatedUserId;
	}
	
	public String getPhone() {
		return phone;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
}
