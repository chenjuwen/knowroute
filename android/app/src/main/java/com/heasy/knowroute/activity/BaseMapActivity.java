package com.heasy.knowroute.activity;

import android.app.ProgressDialog;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.heasy.knowroute.core.utils.AndroidUtil;
import com.heasy.knowroute.map.AbstractMapLocationClient;
import com.heasy.knowroute.map.AbstractMapMarkerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2020/11/9.
 */
public abstract class BaseMapActivity extends BaseActivity  implements SensorEventListener {
    private static final Logger logger = LoggerFactory.getLogger(BaseMapActivity.class);

    protected ProgressDialog progressDialog;

    // 传感器相关
    protected SensorManager mSensorManager;
    protected Double lastX = 0.0;
    protected int direction = 0;

    // Map相关
    protected MapView mMapView;
    protected BaiduMap mBaiduMap;

    private AbstractMapLocationClient mapLocationClient;
    private AbstractMapMarkerService markerService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 获取传感器管理服务
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    protected void initBaiduMap(BitmapDescriptor locationIcon, AbstractMapMarkerService markerService) {
        this.markerService = markerService;

        //显示缩放按钮
        mMapView.showZoomControls(false);

        // 地图初始化
        mBaiduMap = mMapView.getMap();

        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        //开启指南针
        mBaiduMap.getUiSettings().setCompassEnabled(false);

        //定位模式为普通LocationMode.NORMAL、默认图标
        MyLocationConfiguration myLocationConfiguration = new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.NORMAL, true, locationIcon);
        mBaiduMap.setMyLocationConfiguration(myLocationConfiguration);

        if(this.markerService != null) {
            this.markerService.setBaiduMap(mBaiduMap);
            mBaiduMap.setOnMarkerClickListener(this.markerService);
        }
    }

    /**
     * 更新地图状态
     */
    protected void updateMapStatus(LatLng targetLatLng){
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(targetLatLng).zoom(AbstractMapLocationClient.DEFAULT_ZOOM); //初始缩放
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build())); //定位到指定位置
    }

    /**
     * 获取屏幕中心的坐标点经纬度
     */
    protected LatLng getScreenCenterLocation(){
        Point point = AndroidUtil.getDisplaySize(this);

        Point centerPoint = new Point();
        centerPoint.x = point.x / 2;
        centerPoint.y = point.y / 2;

        LatLng centerLatLng = mBaiduMap.getProjection().fromScreenLocation(centerPoint);
        return centerLatLng;
    }

    /**
     * 设置定位数据
     * @param accuracy 定位精度
     * @param direction GPS定位时方向角度，顺时针0-360
     * @param longitude 经度坐标
     * @param latitude 纬度坐标
     */
    protected void setLocationData(float accuracy, float direction, double longitude, double latitude){
        MyLocationData locationData = new MyLocationData.Builder()
                .accuracy(accuracy)
                .direction(direction)
                .longitude(longitude)
                .latitude(latitude)
                .build();
        mBaiduMap.setMyLocationData(locationData);
    }

    /**
     * 传感器方向变化，定位方向同步变化
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            direction = (int) x;

            if(mapLocationClient != null) {
                mapLocationClient.setDirection(direction);
                mBaiduMap.setMyLocationData(mapLocationClient.getLocationData());
            }
        }
        lastX = x;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();

        //为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onStop() {
        super.onStop();

        //取消注册传感器监听
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mapLocationClient != null){
            mapLocationClient.destroy();
        }

        if(mBaiduMap != null) {
            if(this.markerService != null) {
                mBaiduMap.removeMarkerClickListener(this.markerService);
            }

            // 关闭定位图层
            mBaiduMap.setMyLocationEnabled(false);
            mBaiduMap.clear();
        }

        if(mMapView != null) {
            mMapView.onDestroy();
            mMapView = null;
        }
    }

    public AbstractMapLocationClient getMapLocationClient() {
        return mapLocationClient;
    }

    public void setMapLocationClient(AbstractMapLocationClient mapLocationClient) {
        this.mapLocationClient = mapLocationClient;
    }
}
