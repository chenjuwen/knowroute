package com.heasy.knowroute.service;

import java.util.List;

import com.heasy.knowroute.bean.FriendBean;

public interface FriendService {
	public String checkFriend(int userId, String phone);
	public FriendBean getFriend(int userId, String phone);
	public FriendBean getFriend(int id);
	public List<FriendBean> getFriendList(int userId);
	public boolean insert(int userId, String phone);
	public boolean updateNickname(int id, String newNickname);
	public boolean delete(int id);
}
