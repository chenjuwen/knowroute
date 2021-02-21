package com.heasy.knowroute.service;

import java.util.List;

import com.heasy.knowroute.bean.FriendBean;

public interface FriendService {
	public String checkFriend(int userId, String phone);
	public FriendBean getFriend(int userId, String phone);
	public FriendBean getFriend(int id);
	public List<FriendBean> getFriendList(int userId);
	
	/**
	 * 确认是否要添加为好友
	 * @param messageId 消息ID
	 * @param pass 是否同意添加为好友：yes or no
	 */
	public boolean confirmAdd(int messageId, String pass);
	
	public void addFriendRelationship(int aUserId, String bPhone, int bUserId, String aPhone);
	public void insert(int userId, String phone);
	public void updateNickname(int id, String newNickname, int userId);
	public boolean delete(int id, int userId);
	public void forbidLookTrace(int id, int traceFlag);
	public boolean checkForbid(int userId, int viewTrackUserId);
	public boolean isFriendRelationship(int aUserId, int bUserId);
	public boolean isFriendRelationship(String aPhone, String bPhone);
}
