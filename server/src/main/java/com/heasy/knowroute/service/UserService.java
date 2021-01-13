package com.heasy.knowroute.service;

import java.util.Date;

import com.heasy.knowroute.bean.UserBean;

/**
 * Created by Administrator on 2020/9/26.
 */
public interface UserService{
	int login(String phone);
    UserBean getUserById(int id);
    UserBean getUserByPhone(String phone);
    int insert(String phone, String invite_code);
    void updateNickname(int id, String newNickname);
    boolean updateLastLoginDate(int id);
    boolean updatePositionInfo(int id, double longitude, double latitude, String address, Date positionTimes);
    void cancelAccount(int id, String phone);
}
