package com.heasy.knowroute.bean;

import java.util.Date;

public class VersionBean {
	private int id;
	private String vname;
	private String vremark;
	private Date vtime;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getVname() {
		return vname;
	}
	
	public void setVname(String vname) {
		this.vname = vname;
	}
	
	public String getVremark() {
		return vremark;
	}
	
	public void setVremark(String vremark) {
		this.vremark = vremark;
	}
	
	public Date getVtime() {
		return vtime;
	}
	
	public void setVtime(Date vtime) {
		this.vtime = vtime;
	}
	
}
