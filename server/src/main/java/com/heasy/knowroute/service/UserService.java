package com.heasy.knowroute.service;

import com.heasy.knowroute.bean.UserBean;

/**
 * Created by Administrator on 2020/9/26.
 */
public interface UserService{
    UserBean getUser(String account);
    String changePassword(String account, String oldPassword, String newPassword);
}
