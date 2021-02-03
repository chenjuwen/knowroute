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
	private double longitude;
	private double latitude;
	private String address;
	private String positionTimes;
	
	private int forbidLookTrace; //禁止好友查看轨迹
	
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

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPositionTimes() {
		return positionTimes;
	}

	public void setPositionTimes(String positionTimes) {
		this.positionTimes = positionTimes;
	}

	public int getForbidLookTrace() {
		return forbidLookTrace;
	}

	public void setForbidLookTrace(int forbidLookTrace) {
		this.forbidLookTrace = forbidLookTrace;
	}
	
}
