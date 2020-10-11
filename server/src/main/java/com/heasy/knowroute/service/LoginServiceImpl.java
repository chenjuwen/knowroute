package com.heasy.knowroute.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.heasy.knowroute.bean.UserBean;
import com.heasy.knowroute.common.Constants;

@Service
public class LoginServiceImpl implements LoginService {
    private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

    @Autowired
    private UserService userService;
    
    @Override
    public String checkLogin(String account, String password) {
        try {
        	UserBean userBean = userService.getUser(account);
        	if(userBean == null) {
        		return "账号或密码有误！";
        	}
        	
        	if(!password.equals(userBean.getPassword())) {
        		return "账号或密码有误！";
        	}

            return Constants.SUCCESS;

        }catch(Exception ex){
            logger.error("", ex);
            return "登录出错！";
        }
    }
}
