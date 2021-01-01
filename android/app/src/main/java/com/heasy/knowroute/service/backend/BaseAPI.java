package com.heasy.knowroute.service.backend;

import com.heasy.knowroute.core.HeasyContext;
import com.heasy.knowroute.core.service.ServiceEngineFactory;
import com.heasy.knowroute.service.LoginService;
import com.heasy.knowroute.service.LoginServiceImpl;

public class BaseAPI {
    public static LoginService getLoginService() {
        return ServiceEngineFactory.getServiceEngine().getService(LoginServiceImpl.class);
    }

    public static HeasyContext getHeasyContext(){
        return ServiceEngineFactory.getServiceEngine().getHeasyContext();
    }
}
