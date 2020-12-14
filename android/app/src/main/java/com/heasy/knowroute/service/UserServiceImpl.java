package com.heasy.knowroute.service;

import com.heasy.knowroute.action.ResponseBean;
import com.heasy.knowroute.action.ResponseCode;
import com.heasy.knowroute.core.Constants;
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
        successInit = true;
    }

    @Override
    public String updateNickname(int userId, String newNickname) {
        String requestURL = "user/updateNickname";
        String data = FastjsonUtil.toJSONString("userId", String.valueOf(userId), "newNickname", newNickname);

        ResponseBean responseBean = HttpService.postJson(getHeasyContext(), requestURL, data);
        if(responseBean.getCode() == ResponseCode.SUCCESS.code()){
            return Constants.SUCCESS;
        }else{
            return HttpService.getFailureMessage(responseBean);
        }
    }
}
