package com.heasy.knowroute.map;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.utils.DistanceUtil;
import com.heasy.knowroute.core.utils.AndroidUtil;
import com.heasy.knowroute.core.utils.DoubleUtil;
import com.heasy.knowroute.core.utils.StringUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 百度地图覆盖物服务
 */
public abstract class AbstractMapMarkerService implements BaiduMap.OnMarkerClickListener, BaiduMap.OnMapLongClickListener {
    private static final Logger logger = LoggerFactory.getLogger(AbstractMapMarkerService.class);
    private MapView mapView;
    private BaiduMap baiduMap;
    private Marker currentMarker;
    protected Dialog loadingDialog;

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

    public void updateMapStatus(LatLng latLng){
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(latLng).zoom(baiduMap.getMapStatus().zoom); //zoom不变
        getBaiduMap().animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    /**
     * 计算两点之间的直线距离，单位为米
     */
    public long calculateDistance(LatLng startPosition, LatLng endPosition){
        long distance = 0;
        try {
            if (startPosition != null && endPosition != null) {
                distance = new Double(DistanceUtil.getDistance(startPosition, endPosition)).longValue();
            }
        }catch (Exception ex){
            logger.error("", ex);
        }
        return distance;
    }

    public String formatDistance(long distance){
        String distanceStr = "";
        if(distance > 1000){
            distanceStr = DoubleUtil.decimalNum(distance / 1000.0, 3) + "公里";
        }else{
            distanceStr = distance + "米";
        }
        return distanceStr;
    }

    /**
     * 添加文本覆盖物
     * @param text 文本内容
     * @param position 经纬坐标
     */
    public void addTextOverlay(String text, LatLng position){
        OverlayOptions textOptions = new TextOptions()
                .text(text)
                .bgColor(Color.YELLOW)
                .fontSize(27)
                .fontColor(Color.RED)
                .position(position);
        getBaiduMap().addOverlay(textOptions);
    }

    /**
     * 将view转换成Bitmap
     */
    public Bitmap createViewBitmap(View view){
        view.setDrawingCacheEnabled(true);
        //view宽和高的测量
        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        //view上下左右的位置
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();

        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        return bitmap;
    }

    /**
     * 获取屏幕中心的坐标点经纬度
     */
    public LatLng getScreenCenterLocation(Activity activity){
        Point point = AndroidUtil.getDisplaySize(activity);

        Point centerPoint = new Point();
        centerPoint.x = point.x / 2;
        centerPoint.y = point.y / 2;

        LatLng centerLatLng = getBaiduMap().getProjection().fromScreenLocation(centerPoint);
        return centerLatLng;
    }

    /**
     * 屏幕内显示所有marker
     */
    public void showAllMarkersInScreen(List<LatLng> pointList){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : pointList) {
            builder = builder.include(latLng);
        }
        LatLngBounds latlngBounds = builder.build();
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngBounds(latlngBounds, getMapView().getWidth()-50, getMapView().getHeight()-50);
        getBaiduMap().animateMapStatus(mapStatusUpdate);
    }

    /**
     * 获取自定义的定位图标
     * @param label 文本标签
     */
    public View getCustomMapPointView(String label, Activity activity){
        LayoutInflater inflater = LayoutInflater.from(activity);
        View pointView = inflater.inflate(R.layout.custom_map_point_icon, null);

        TextView textView = pointView.findViewById(R.id.mapPointIconLabel);
        textView.setText(label);

        if(StringUtil.isEmpty(StringUtil.trimToEmpty(label))){
            textView.setVisibility(View.GONE);
        }else{
            textView.setVisibility(View.VISIBLE);
        }

        return pointView;
    }

    public void dismissLoadingDialog() {
        if(loadingDialog != null){
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }
}
