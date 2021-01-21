package com.heasy.knowroute.service;

import com.heasy.knowroute.core.service.Service;

/**
 * Created by Administrator on 2020/9/26.
 */
public interface LoginService extends Service{
    boolean getCaptcha(String phone);
    String doLogin(String phone, String captche);
    int getUserId();
    String getPhone();
    String getNickname();
    void setNickname(String nickname);
    String getToken();
    boolean isLogin();
    boolean cleanCache();
}
