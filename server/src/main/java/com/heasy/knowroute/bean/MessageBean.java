package com.heasy.knowroute.bean;

import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description="系统消息JavaBean类")
public class MessageBean {
	@ApiModelProperty(value="id")
	private int id;

	@ApiModelProperty(value="标题")
	private String title;

	@ApiModelProperty(value="内容")
	private String content;

	@ApiModelProperty(value="消息类别")
	private String category;

	@ApiModelProperty(value="消息处理结果")
	private String result;

	@ApiModelProperty(value="消息发送者")
	private String sender;

	@ApiModelProperty(value="发送者昵称")
	private String senderNickname;

	@ApiModelProperty(value="发送者手机号")
	private String senderPhone;

	@ApiModelProperty(value="消息接收者")
	private String receiver;

	@ApiModelProperty(value="消息归属")
	private Integer owner;

	@ApiModelProperty(value="创建时间")
	private Date createDate;
	
	@ApiModelProperty(value="消息状态，0待处理，1已处理")
	private int status;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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
	
	public String getSenderNickname() {
		return senderNickname;
	}

	public void setSenderNickname(String senderNickname) {
		this.senderNickname = senderNickname;
	}

	public String getSenderPhone() {
		return senderPhone;
	}

	public void setSenderPhone(String senderPhone) {
		this.senderPhone = senderPhone;
	}

	public String getReceiver() {
		return receiver;
	}
	
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public Integer getOwner() {
		return owner;
	}

	public void setOwner(Integer owner) {
		this.owner = owner;
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
	
}
