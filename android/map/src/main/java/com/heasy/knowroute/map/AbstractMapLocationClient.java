package com.heasy.knowroute.map;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.heasy.knowroute.map.bean.LocationBean;

/**
 * 绑定百度地图的定位组件
 * Created by Administrator on 2020/11/6.
 */
public abstract class AbstractMapLocationClient extends AbstractLocationClient{
    /**
     * 默认缩放层级
     */
    public static final float DEFAULT_ZOOM =  17.0f;

    private BaiduMap baiduMap;
    private int direction = 0;

    private boolean isFirstLocation = true; // 是否首次定位
    private boolean realtimeLocation = false; //是否实时定位

    public AbstractMapLocationClient(BaiduMap baiduMap, Context context){
        super(context);
        this.baiduMap = baiduMap;
    }

    public MyLocationData getLocationData(){
        if(getCurrentLocation() != null) {
            MyLocationData locationData = new MyLocationData.Builder()
                    .accuracy(getCurrentLocation().getRadius())
                    .direction(getDirection())  // 方向信息，顺时针0-360
                    .longitude(getCurrentLocation().getLongitude())
                    .latitude(getCurrentLocation().getLatitude())
                    .build();
            return locationData;
        }else{
            return null;
        }
    }

    @Override
    public void handleReceiveLocation(BDLocation dbLocation, LocationBean locationBean) {
        baiduMap.setMyLocationData(getLocationData());

        //首次定位
        if (isFirstLocation) {
            isFirstLocation = false;
            updateMapStatus(new LatLng(locationBean.getLatitude(), locationBean.getLongitude()));
        }

        if(realtimeLocation){ //实时处理定位
            handleRealtimeLocation(dbLocation, locationBean);
        }
    }

    public void updateMapStatus(LatLng targetLatLng){
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(targetLatLng).zoom(DEFAULT_ZOOM); //初始缩放
        baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build())); //定位到指定位置
    }

    public abstract void handleRealtimeLocation(BDLocation location, LocationBean locationBean);

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public void setRealtimeLocation(boolean realtimeLocation) {
        this.realtimeLocation = realtimeLocation;
    }
}
