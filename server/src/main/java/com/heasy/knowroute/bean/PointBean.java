package com.heasy.knowroute.bean;

/**
 * 位置点信息
 */
public class PointBean {
	private double longitude;
	private double latitude;
	
	public PointBean() {
		
	}
	
	public PointBean(double longitude, double latitude) {
		this.longitude = longitude;
		this.latitude = latitude;
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
	
}
