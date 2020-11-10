package com.heasy.knowroute.map;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.heasy.knowroute.map.bean.LocationBean;

/**
 * Created by Administrator on 2020/11/6.
 */
public class DefaultMapLocationService extends AbstractMapLocationClient {
    public DefaultMapLocationService(BaiduMap baiduMap, Context context){
        super(baiduMap, context);
    }

    @Override
    public void handleRealtimeLocation(BDLocation location, LocationBean locationBean) {

    }
}
