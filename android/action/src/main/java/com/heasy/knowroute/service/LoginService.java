package com.heasy.knowroute.service;

import com.heasy.knowroute.core.service.Service;

/**
 * Created by Administrator on 2020/9/26.
 */
public interface LoginService extends Service{
    String checkLogin(String account, String password);
    boolean isAdministrator();
    boolean cleanCache();
}
