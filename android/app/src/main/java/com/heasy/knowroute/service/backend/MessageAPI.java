package com.heasy.knowroute.service.backend;

import com.heasy.knowroute.bean.ResponseBean;
import com.heasy.knowroute.bean.ResponseCode;
import com.heasy.knowroute.service.common.HttpService;

public class MessageAPI extends BaseAPI {
    public static String list(){
        String url = "message/list/" + getLoginService().getUserId();
        ResponseBean responseBean = HttpService.get(getHeasyContext(), url);
        if (responseBean.getCode() == ResponseCode.SUCCESS.code()) {
            return (String) responseBean.getData();
        }else{
            return "[]";
        }
    }

}
