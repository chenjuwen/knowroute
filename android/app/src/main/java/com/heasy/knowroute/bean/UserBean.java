package com.heasy.knowroute.bean;

import java.util.Date;

/**
 * Created by Administrator on 2020/9/27.
 */
public class UserBean {
	private int id;
    private String phone;
    private String nickname;
    private Float longitude;
    private Float latitude;
    private String address;
    private String invite_code;
    private Date create_date;
    private Date last_login_date;
    
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
	
	public String getInvite_code() {
		return invite_code;
	}
	
	public void setInvite_code(String invite_code) {
		this.invite_code = invite_code;
	}
	
	public Date getCreate_date() {
		return create_date;
	}
	
	public void setCreate_date(Date create_date) {
		this.create_date = create_date;
	}
	
	public Date getLast_login_date() {
		return last_login_date;
	}
	
	public void setLast_login_date(Date last_login_date) {
		this.last_login_date = last_login_date;
	}
    
}
