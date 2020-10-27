package com.heasy.knowroute.service;

import com.heasy.knowroute.bean.UserBean;

/**
 * Created by Administrator on 2020/9/26.
 */
public interface UserService{
	int login(String phone);
    UserBean getUser(int id);
    UserBean getUser(String phone);
    int insert(String phone, String invite_code);
    boolean updateNickname(int id, String newNickname);
    boolean updateLastLoginDate(int id);
    boolean updatePositionInfo(int id, double longitude, double latitude, String address);
}
