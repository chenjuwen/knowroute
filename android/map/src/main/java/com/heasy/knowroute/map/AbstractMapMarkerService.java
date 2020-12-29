package com.heasy.knowroute.map;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;

public abstract class AbstractMapMarkerService implements BaiduMap.OnMarkerClickListener {
    private MapView mapView;
    private BaiduMap baiduMap;
    private Marker currentMarker;

    public void updateMapStatus(LatLng latLng){
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(latLng).zoom(AbstractMapLocationClient.DEFAULT_ZOOM);
        getBaiduMap().animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    public MapView getMapView() {
        return mapView;
    }

    public void setMapView(MapView mapView) {
        this.mapView = mapView;
    }

    public BaiduMap getBaiduMap() {
        return baiduMap;
    }

    public void setBaiduMap(BaiduMap baiduMap) {
        this.baiduMap = baiduMap;
    }

    public Marker getCurrentMarker() {
        return currentMarker;
    }

    public void setCurrentMarker(Marker currentMarker) {
        this.currentMarker = currentMarker;
    }
}
