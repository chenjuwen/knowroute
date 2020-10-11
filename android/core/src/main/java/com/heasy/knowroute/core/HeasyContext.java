package com.heasy.knowroute.core;

import com.heasy.knowroute.core.service.ServiceEngine;
import com.heasy.knowroute.core.webview.JSInterface;

/**
 * Created by Administrator on 2017/12/29.
 */
public class HeasyContext {
    private JSInterface jsInterface;
    private ServiceEngine serviceEngine;

    public JSInterface getJsInterface() {
        return jsInterface;
    }

    public void setJsInterface(JSInterface jsInterface) {
        this.jsInterface = jsInterface;
    }

    public ServiceEngine getServiceEngine() {
        return serviceEngine;
    }

    public void setServiceEngine(ServiceEngine serviceEngine) {
        this.serviceEngine = serviceEngine;
    }

}
