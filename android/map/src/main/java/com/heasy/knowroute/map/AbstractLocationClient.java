package com.heasy.knowroute.map;

import android.app.Notification;
import android.content.Context;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.utils.DistanceUtil;
import com.heasy.knowroute.core.utils.StringUtil;
import com.heasy.knowroute.map.bean.LocationBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 定位组件
 * Created by Administrator on 2020/10/25.
 */
public abstract class AbstractLocationClient extends BDAbstractLocationListener {
    private static final Logger logger = LoggerFactory.getLogger(AbstractLocationClient.class);
    private LocationClient mLocationClient = null;
    private Context context;

    private LocationBean currentLocation; //当前已被采纳的位置点
    private LocationBean lastedLocation; //最新的位置点，不一定被采纳
    private String city;

    /**
     * 获取定位信息的间隔时间，单位为毫秒
     */
    private int scanSpanMillSeconds = 2 * 1000;

    /**
     * 是否需要返回POI列表
     */
    private boolean needLocationPoiList = false;

    /**
     * 接纳为轨迹点的最少间隔距离，单位为米
     */
    private long acceptMinDistance = 10;

    public AbstractLocationClient(Context context){
        this.context = context;
    }

    public void init(){
        mLocationClient = new LocationClient(this.context);
        mLocationClient.registerLocationListener(this);

        mLocationClient.setLocOption(getLocationClientOption());
        mLocationClient.start();
    }

    private LocationClientOption getLocationClientOption(){
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType(CoordType.BD09LL.name()); // 坐标类型：百度经纬度坐标
        option.setScanSpan(scanSpanMillSeconds); //发起定位请求的间隔，单位毫秒ms，默认0，即仅定位一次
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy); //定位模式：高精度
        option.setIsNeedAddress(true); //设置是否需要地址信息
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，结果类似于“在北京天安门附近”
        option.setNeedDeviceDirect(true); //设置是否需要设备方向结果
        //option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationPoiList(needLocationPoiList);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        //option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setIgnoreKillProcess(false); ///可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setNeedNewVersionRgc(true); //是否需要最新版本的地址信息
        return option;
    }

    public void enableLocInForeground(Notification notification){
        if(mLocationClient != null) {
            mLocationClient.enableLocInForeground(2001, notification);
        }
    }

    public void disableLocInForeground(){
        if(mLocationClient != null) {
            mLocationClient.disableLocInForeground(true);
        }
    }

    public void restart(){
        if(mLocationClient != null) {
            mLocationClient.restart();
        }
    }

    public void destroy(){
        if(mLocationClient != null) {
            mLocationClient.unRegisterLocationListener(this);
            mLocationClient.stop();
            mLocationClient = null;
        }
    }

    @Override
    public void onReceiveLocation(BDLocation dbLocation) {
        if (dbLocation == null) {
            return;
        }
        //logger.debug(FastjsonUtil.object2String(dbLocation));

        //地址
        String address = dbLocation.getAddrStr();
        if(address != null){
            address = address.replaceFirst("中国", "");

            String locationDescribe = dbLocation.getLocationDescribe();
            if(locationDescribe != null){
                locationDescribe = locationDescribe.replaceFirst("在", "");
                address += locationDescribe;
            }
        }

        if(StringUtil.isEmpty(address)){
            return;
        }

        double longitude = dbLocation.getLongitude(); //经度
        double latitude = dbLocation.getLatitude(); //纬度
        float radius = dbLocation.getRadius(); //定位精度
        String time = dbLocation.getTime(); //时间

        city = dbLocation.getCity();

        //POI
        //List<Poi> poiList = dbLocation.getPoiList();

        LocationBean locationBean = new LocationBean();
        locationBean.setLongitude(longitude);
        locationBean.setLatitude(latitude);
        locationBean.setRadius(radius);
        locationBean.setAddress(address);
        locationBean.setTimes(time);

        setLastedLocation(locationBean);

        if(getCurrentLocation() == null) { //首次定位
            setCurrentLocation(locationBean);
            handleReceiveLocation(dbLocation, locationBean);
        }else{
            //两点间隔一定距离才处理
            //计算两点之间的距离，单位为 米
            long distance = new Double(DistanceUtil.getDistance(getCurrentLocation().getLatLng(), locationBean.getLatLng())).longValue();
            logger.debug("distance=" + distance);

            if(distance >= acceptMinDistance){
                setCurrentLocation(locationBean);
                handleReceiveLocation(dbLocation, locationBean);
            }
        }
    }

    public abstract void handleReceiveLocation(BDLocation dbLocation, LocationBean bean);

    public LocationBean getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(LocationBean currentLocation) {
        this.currentLocation = currentLocation;
    }

    public LocationBean getLastedLocation() {
        return lastedLocation;
    }

    public void setLastedLocation(LocationBean lastedLocation) {
        this.lastedLocation = lastedLocation;
    }

    public String getCity() {
        return city;
    }

    public int getScanSpanMillSeconds() {
        return scanSpanMillSeconds;
    }

    public void setScanSpanMillSeconds(int scanSpanMillSeconds) {
        this.scanSpanMillSeconds = scanSpanMillSeconds;
    }

    public boolean isNeedLocationPoiList() {
        return needLocationPoiList;
    }

    public void setNeedLocationPoiList(boolean needLocationPoiList) {
        this.needLocationPoiList = needLocationPoiList;
    }
}
