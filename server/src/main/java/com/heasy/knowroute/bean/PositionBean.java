package com.heasy.knowroute.bean;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description="用户轨迹点JavaBean类")
public class PositionBean {
	@ApiModelProperty(value="id")
	private String id;
	
	@ApiModelProperty(value="用户ID")
	private int userId;
	
	@ApiModelProperty(value="经度")
	private double longitude;
	
	@ApiModelProperty(value="纬度")
	private double latitude;
	
	@ApiModelProperty(value="地址")
	private String address;
	
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", locale="zh", timezone="GMT+8")
	@ApiModelProperty(value="时间")
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
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getTimes() {
		return times;
	}
	
	public void setTimes(Date times) {
		this.times = times;
	}
	
}
