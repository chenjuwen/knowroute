package com.heasy.knowroute;

import com.heasy.knowroute.core.service.ServiceEngine;

/**
 * Created by Administrator on 2018/11/11.
 */
public class ServiceEngineFactory {
    private static ServiceEngine serviceEngine = null;

    public static ServiceEngine getServiceEngine() {
        return serviceEngine;
    }

    public static void setServiceEngine(ServiceEngine serviceEngine) {
        ServiceEngineFactory.serviceEngine = serviceEngine;
    }

}
