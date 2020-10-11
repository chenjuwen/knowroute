package com.heasy.knowroute.core.service;

import com.heasy.knowroute.core.datastorage.DataCache;
import com.heasy.knowroute.core.datastorage.MemoryDataCache;

/**
 * Created by Administrator on 2018/2/7.
 */
public class DefaultDataService extends AbstractService implements DataService {
    private DataCache globalMemoryDataCache;

    @Override
    public void init() {
        globalMemoryDataCache = new MemoryDataCache();
        successInit = true;
    }

    @Override
    public void unInit() {
        super.unInit();

        if(globalMemoryDataCache != null) {
            globalMemoryDataCache.clear();
            globalMemoryDataCache = null;
        }
    }

    @Override
    public DataCache getGlobalMemoryDataCache() {
        return globalMemoryDataCache;
    }

}
