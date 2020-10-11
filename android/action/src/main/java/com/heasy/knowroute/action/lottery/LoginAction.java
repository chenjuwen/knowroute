package com.heasy.knowroute.action.lottery;

import com.alibaba.fastjson.JSONObject;
import com.heasy.knowroute.core.Constants;
import com.heasy.knowroute.core.HeasyContext;
import com.heasy.knowroute.core.annotation.JSActionAnnotation;
import com.heasy.knowroute.core.utils.FastjsonUtil;
import com.heasy.knowroute.core.webview.Action;
import com.heasy.knowroute.service.LoginService;
import com.heasy.knowroute.service.LoginServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JSActionAnnotation(name = "LoginAction")
public class LoginAction implements Action {
    private static final Logger logger = LoggerFactory.getLogger(LoginAction.class);

    @Override
    public String execute(final HeasyContext heasyContext, String jsonData, String extend) {
        JSONObject jsonObject = FastjsonUtil.string2JSONObject(jsonData);

        if("login".equalsIgnoreCase(extend)) {
            String account = FastjsonUtil.getString(jsonObject, "account");
            String password = FastjsonUtil.getString(jsonObject, "password");
            
			LoginService loginService = heasyContext.getServiceEngine().getService(LoginServiceImpl.class);
            String result = loginService.checkLogin(account, password);
            return result;

        }else if("checkAdmin".equalsIgnoreCase(extend)){
            LoginService loginService = heasyContext.getServiceEngine().getService(LoginServiceImpl.class);
            return Boolean.toString(loginService.isAdministrator());
        }else if("cleanCache".equalsIgnoreCase(extend)){
            LoginService loginService = heasyContext.getServiceEngine().getService(LoginServiceImpl.class);
            loginService.cleanCache();
        }

        return Constants.SUCCESS;
    }
}
