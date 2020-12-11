package com.heasy.knowroute.action.business;

import com.alibaba.fastjson.JSONObject;
import com.heasy.knowroute.action.ResponseBean;
import com.heasy.knowroute.action.ResponseCode;
import com.heasy.knowroute.core.Constants;
import com.heasy.knowroute.core.HeasyContext;
import com.heasy.knowroute.core.annotation.JSActionAnnotation;
import com.heasy.knowroute.core.utils.FastjsonUtil;
import com.heasy.knowroute.core.webview.Action;
import com.heasy.knowroute.service.HttpService;
import com.heasy.knowroute.service.LoginService;
import com.heasy.knowroute.service.LoginServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JSActionAnnotation(name = "MessageAction")
public class MessageAction implements Action {
    private static final Logger logger = LoggerFactory.getLogger(MessageAction.class);

    @Override
    public String execute(HeasyContext heasyContext, String jsonData, String extend) {
        JSONObject jsonObject = FastjsonUtil.string2JSONObject(jsonData);
        LoginService loginService = heasyContext.getServiceEngine().getService(LoginServiceImpl.class);

        if ("list".equalsIgnoreCase(extend)){
            try {
                String url = "message/list/" + loginService.getUserId();
                ResponseBean responseBean = HttpService.get(heasyContext, url);
                if (responseBean.getCode() == ResponseCode.SUCCESS.code()) {
                    return (String) responseBean.getData();
                }
            }catch (Exception ex){
                logger.error("", ex);
            }
            return "[]";
        }

        return Constants.SUCCESS;
    }
}
