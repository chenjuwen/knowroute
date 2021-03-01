package com.heasy.knowroute.bean;

public class MqttAclBean {
	/**
	 * 1 = sub, 2 = pub
	 */
	private String access;
	private String username;
	private String clientid;
	private String ipaddress;
	private String protocol;
	private String mountpoint;
	private String topic;
	
	public String getAccess() {
		return access;
	}

	public void setAccess(String access) {
		this.access = access;
	}

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

	public String getMountpoint() {
		return mountpoint;
	}

	public void setMountpoint(String mountpoint) {
		this.mountpoint = mountpoint;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}
}
