package com.heasy.knowroute.activity;

import android.app.ProgressDialog;
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
import com.baidu.mapapi.model.LatLng;
import com.heasy.knowroute.map.AbstractMapLocationClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2020/11/9.
 */
public class BaseMapActivity extends BaseActivity  implements SensorEventListener {
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 获取传感器管理服务
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    protected void initBaiduMap(BitmapDescriptor locationIcon) {
        mMapView.showZoomControls(false); //缩放按钮

        // 地图初始化
        mBaiduMap = mMapView.getMap();

        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        //定位模式为普通LocationMode.NORMAL、默认图标
        MyLocationConfiguration myLocationConfiguration = new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.NORMAL, true, locationIcon);
        mBaiduMap.setMyLocationConfiguration(myLocationConfiguration);
    }

    protected void updateMapStatus(LatLng targetLatLng){
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(targetLatLng).zoom(AbstractMapLocationClient.DEFAULT_ZOOM); //初始缩放
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build())); //定位到指定位置
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
