package com.heasy.knowroute.service;

import com.heasy.knowroute.action.ResponseBean;
import com.heasy.knowroute.action.ResponseCode;
import com.heasy.knowroute.bean.UserBean;
import com.heasy.knowroute.core.service.AbstractService;
import com.heasy.knowroute.core.utils.FastjsonUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2020/9/26.
 */
public class UserServiceImpl extends AbstractService implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public void init() {

    }

    @Override
    public UserBean getUser(String account) {
        try {
            String requestUrl = "/user/getUser?account=" + account;
            ResponseBean responseBean = HttpService.httpGet(getHeasyContext(), requestUrl);

            if(responseBean.getCode() == ResponseCode.SUCCESS.code()){
                UserBean userBean = FastjsonUtil.string2JavaBean((String)responseBean.getData(), UserBean.class);
                return userBean;
            }else{
                return null;
            }
        }catch (Exception ex){
            logger.error("", ex);
            return null;
        }
    }
}
