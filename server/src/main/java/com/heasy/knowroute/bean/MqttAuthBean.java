package com.heasy.knowroute.bean;

public class MqttAuthBean {
	private String username;
	private String clientid;
	private String ipaddress;
	private String protocol;
	private String password;
	private String sockport;
	private String commonname;
	private String subject;
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getClientid() {
		return clientid;
	}
	
	public void setClientid(String clientid) {
		this.clientid = clientid;
	}
	
	public String getIpaddress() {
		return ipaddress;
	}
	
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}
	
	public String getProtocol() {
		return protocol;
	}
	
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getSockport() {
		return sockport;
	}
	
	public void setSockport(String sockport) {
		this.sockport = sockport;
	}
	
	public String getCommonname() {
		return commonname;
	}
	
	public void setCommonname(String commonname) {
		this.commonname = commonname;
	}
	
	public String getSubject() {
		return subject;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
}
