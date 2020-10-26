package com.heasy.knowroute.bean;

import java.util.Date;

/**
 * 用户位置点信息
 */
public class PositionBean {
	private String id;
	private int userId;
	private double longitude;
	private double latitude;
	private Date times;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public int getUserId() {
		return userId;
	}
	
	public void setUserId(int userId) {
		this.userId = userId;
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
	
	public Date getTimes() {
		return times;
	}
	
	public void setTimes(Date times) {
		this.times = times;
	}
	
}
