package com.heasy.knowroute.bean;

/**
 * 位置点信息
 */
public class PointBean {
	private double longitude;
	private double latitude;
	private String times;
	
	public PointBean() {
		
	}
	
	public PointBean(double longitude, double latitude, String times) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.times = times;
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

	public String getTimes() {
		return times;
	}

	public void setTimes(String times) {
		this.times = times;
	}
	
}
