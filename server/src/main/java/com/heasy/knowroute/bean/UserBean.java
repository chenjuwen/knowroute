package com.heasy.knowroute.bean;

import java.util.Date;

/**
 * 用户
 * Created by Administrator on 2020/9/27.
 */
public class UserBean {
	private int id;
    private String phone;
    private String nickname;
    private Float longitude;
    private Float latitude;
    private String address;
    private String inviteCode;
    private Date createDate;
    private Date lastLoginDate;
    
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
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
	
	public Float getLongitude() {
		return longitude;
	}
	
	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}
	
	public Float getLatitude() {
		return latitude;
	}
	
	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}

	public String getInviteCode() {
		return inviteCode;
	}

	public void setInviteCode(String inviteCode) {
		this.inviteCode = inviteCode;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}
	
}
