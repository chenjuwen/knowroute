package com.heasy.knowroute.bean;

import java.util.Date;

/**
 * 消息
 */
public class MessageBean {
	private int id;
	private String content;
	private String category;
	private String result;
	private String sender;
	private String receiver;
	private Date createDate;
	/**
	 * 状态：0待处理，1已处理
	 */
	private int status;
	private Integer owner;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getResult() {
		return result;
	}
	
	public void setResult(String result) {
		this.result = result;
	}
	
	public String getSender() {
		return sender;
	}
	
	public void setSender(String sender) {
		this.sender = sender;
	}
	
	public String getReceiver() {
		return receiver;
	}
	
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	
	public Date getCreateDate() {
		return createDate;
	}
	
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Integer getOwner() {
		return owner;
	}

	public void setOwner(Integer owner) {
		this.owner = owner;
	}
	
}
