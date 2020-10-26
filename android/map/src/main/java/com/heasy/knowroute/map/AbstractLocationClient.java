package com.heasy.knowroute.map;

import android.content.Context;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

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

    //定位相关属性
    private double longitude;
    private double latitude;
    private String address;
    private String time;

    private int scanSpanMillSeconds = 10000; //获取定位信息的间隔毫秒
    private boolean needLocationPoiList = false; //是否需要返回POI列表

    public AbstractLocationClient(Context context){
        this.context = context;
    }

    public void init(){
        logger.info("init LocationClient...");
        mLocationClient = new LocationClient(this.context);
        mLocationClient.registerLocationListener(this);

        mLocationClient.setLocOption(getLocationClientOption());
        mLocationClient.start();
    }

    private LocationClientOption getLocationClientOption(){
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型：百度经纬度坐标
        option.setScanSpan(scanSpanMillSeconds); //发起定位请求的间隔，单位毫秒ms，默认0，即仅定位一次
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy); //定位模式：高精度
        option.setIsNeedAddress(true); //设置是否需要地址信息
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，结果类似于“在北京天安门附近”
        option.setNeedDeviceDirect(true); //设置是否需要设备方向结果
        //option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationPoiList(needLocationPoiList);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        //option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setIgnoreKillProcess(false); ///可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        return option;
    }

    public void destroy(){
        if(mLocationClient != null) {
            logger.info("stop LocationClient...");
            mLocationClient.stop();
        }
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        if (location == null) {
            return;
        }

        longitude = location.getLongitude(); //经度
        latitude = location.getLatitude(); //纬度

        //地址
        address = location.getAddrStr();
        if(address != null){
            address = address.replaceFirst("中国", "");

            String locationDescribe = location.getLocationDescribe();
            if(locationDescribe != null){
                locationDescribe = locationDescribe.replaceFirst("在", "");
                address += locationDescribe;
            }
        }

        time = location.getTime();

        logger.info(longitude + ", " + latitude);
        logger.info(address);
        logger.info(time);

        handleReceiveLocation(location);
    }

    public abstract void handleReceiveLocation(BDLocation location);

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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
