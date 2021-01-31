package com.heasy.knowroute.activity;

import android.app.Dialog;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.heasy.knowroute.map.AbstractMapLocationClient;
import com.heasy.knowroute.map.AbstractMapMarkerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2020/11/9.
 */
public abstract class BaseMapActivity extends BaseActivity  implements SensorEventListener {
    private static final Logger logger = LoggerFactory.getLogger(BaseMapActivity.class);

    protected Dialog loadingDialog;

    // 传感器相关
    protected SensorManager mSensorManager;
    protected Double lastX = 0.0;

    // Map相关
    protected MapView mMapView;
    protected BaiduMap mBaiduMap;

    private AbstractMapLocationClient mapLocationClient;
    private AbstractMapMarkerService markerService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 获取传感器管理服务
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    protected void initBaiduMap(MyLocationConfiguration.LocationMode locationMode, BitmapDescriptor locationIcon, AbstractMapMarkerService markerService) {
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
        MyLocationConfiguration myLocationConfiguration = new MyLocationConfiguration(locationMode, true, locationIcon);
        mBaiduMap.setMyLocationConfiguration(myLocationConfiguration);

        if(this.markerService != null) {
            this.markerService.setMapView(mMapView);
            this.markerService.setBaiduMap(mBaiduMap);
            mBaiduMap.setOnMarkerClickListener(this.markerService);
            mBaiduMap.setOnMapLongClickListener(this.markerService);
        }
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
            int direction = (int) x;
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
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();

        //为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onStop() {
        //取消注册传感器监听
        mSensorManager.unregisterListener(this);

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if(mapLocationClient != null){
            mapLocationClient.destroy();
        }

        if(mBaiduMap != null) {
            if(this.markerService != null) {
                mBaiduMap.removeMarkerClickListener(this.markerService);
                mBaiduMap.setOnMapLongClickListener(null);
            }

            // 关闭定位图层
            mBaiduMap.setMyLocationEnabled(false);
            mBaiduMap.clear();
        }

        if(mMapView != null) {
            mMapView.onDestroy();
            mMapView = null;
        }

        super.onDestroy();
    }

    public AbstractMapLocationClient getMapLocationClient() {
        return mapLocationClient;
    }

    public void setMapLocationClient(AbstractMapLocationClient mapLocationClient) {
        this.mapLocationClient = mapLocationClient;
    }

    protected void dismissLoadingDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }
}
