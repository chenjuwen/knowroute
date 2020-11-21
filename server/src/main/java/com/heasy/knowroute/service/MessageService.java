package com.heasy.knowroute.service;

import java.util.List;

import com.heasy.knowroute.bean.MessageBean;

public interface MessageService {
	public List<MessageBean> getMessageList(int owner);
	public MessageBean getMessage(int id);
	public MessageBean getMessage(String sender, String receiver, String category);
	public List<MessageBean> getInviteAgreeMessage(String phone);
	public int insert(MessageBean messageBean);
	public boolean confirmMessage(int id, String result);
}
