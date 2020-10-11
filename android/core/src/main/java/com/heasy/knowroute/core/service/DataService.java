package com.heasy.knowroute.core.service;

import com.heasy.knowroute.core.datastorage.DataCache;

/**
 * Created by Administrator on 2017/12/29.
 */
public interface DataService extends Service {
    DataCache getGlobalMemoryDataCache();
}
