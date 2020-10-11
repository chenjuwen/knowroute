package com.heasy.knowroute.core.service;

import android.content.Context;

import com.heasy.knowroute.core.HeasyContext;

/**
 * Created by Administrator on 2017/12/29.
 */
public interface ServiceEngine {
    Context getAndroidContext();
    void setAndroidContext(Context context);

    HeasyContext getHeasyContext();
    void setHeasyContext(HeasyContext heasyContext);

    <T> T getService(Class<T> clazz);

    ConfigurationService getConfigurationService();
    EventService getEventService();
    DataService getDataService();

    void open();
    void close();
}
