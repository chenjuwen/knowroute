package com.heasy.knowroute.map;

import android.app.Dialog;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RouteTrackMapMarkerService extends AbstractMapMarkerService {
    private static final Logger logger = LoggerFactory.getLogger(RouteTrackMapMarkerService.class);
    private Dialog loadingDialog;

    @Override
    public boolean onMarkerClick(final Marker marker) {
        return true;
    }

    /**
     * 添加Marker覆盖物
     * @param position 位置对象
     * @param icon 图标
     */
    public void addMarkerOverlay(LatLng position, BitmapDescriptor icon){
        MarkerOptions markerOptions = new MarkerOptions()
                .position(position)
                .icon(icon);
        getBaiduMap().addOverlay(markerOptions);
    }

    public void destroy(){
        setBaiduMap(null);
        setMapView(null);

        try {
            if(loadingDialog != null){
                loadingDialog.dismiss();
                loadingDialog = null;
            }
        }catch (Exception ex){

        }
    }

}
