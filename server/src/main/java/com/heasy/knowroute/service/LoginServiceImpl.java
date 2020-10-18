package com.heasy.knowroute.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.heasy.knowroute.bean.UserBean;
import com.heasy.knowroute.utils.StringUtil;

@Service
public class LoginServiceImpl implements LoginService {
    private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

    @Autowired
    private UserService userService;
    
    /**
     * 登录处理：
     * 	手机号对应的用户记录不存在，则添加新用户
     * 	返回用户记录的id值
     */
    @Override
    public int login(String phone) {
        try {
        	UserBean userBean = userService.getUser(phone);
        	if(userBean == null) {
        		String inviteCode = StringUtil.getUUIDString();
        		int id = userService.insert(phone, inviteCode);
        		return id;
        	}
            
        	return userBean.getId();
        	
        }catch(Exception ex){
            logger.error("", ex);
            return 0;
        }
    }
}
