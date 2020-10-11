package com.heasy.knowroute.service;

import com.heasy.knowroute.bean.UserBean;
import com.heasy.knowroute.core.service.Service;

/**
 * Created by Administrator on 2020/9/26.
 */
public interface UserService extends Service{
    String changePassword(String account, String oldPassword, String newPassword);
    UserBean getUser(String account);
}
