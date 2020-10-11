package com.heasy.knowroute.core.service;

import com.heasy.knowroute.core.HeasyContext;

/**
 * Created by Administrator on 2017/12/28.
 */
public interface Service {
    void init();
    boolean isInit();
    void unInit();
    HeasyContext getHeasyContext();
    void setHeasyContext(HeasyContext heasyContext);
}
