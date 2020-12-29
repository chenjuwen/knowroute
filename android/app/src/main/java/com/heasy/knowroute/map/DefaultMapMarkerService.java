package com.heasy.knowroute.map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.utils.DistanceUtil;
import com.heasy.knowroute.R;
import com.heasy.knowroute.action.ResponseBean;
import com.heasy.knowroute.action.ResponseCode;
import com.heasy.knowroute.bean.FixedPointInfoBean;
import com.heasy.knowroute.core.DefaultDaemonThread;
import com.heasy.knowroute.core.service.ServiceEngineFactory;
import com.heasy.knowroute.core.utils.AndroidUtil;
import com.heasy.knowroute.core.utils.DoubleUtil;
import com.heasy.knowroute.core.utils.FastjsonUtil;
import com.heasy.knowroute.core.utils.StringUtil;
import com.heasy.knowroute.event.FixedPointInfoEvent;
import com.heasy.knowroute.map.bean.LocationBean;
import com.heasy.knowroute.service.HttpService;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 百度地图覆盖物服务
 */
public class DefaultMapMarkerService extends AbstractMapMarkerService {
    private static final Logger logger = LoggerFactory.getLogger(DefaultMapMarkerService.class);
    public static final int DISTANCE_UPDATE_INTERVAL_SECONDS = 5;

    private InfoWindow infoWindow;
    private View view;
    private EditText txtDistance;
    private EditText txtAddress;
    private EditText txtLabel;
    private EditText txtComments;

    private Activity activity;
    private FixedPointInfoBean bean;
    private ProgressDialog progressDialog;
    private DistanceUpdater distanceUpdater;

    public DefaultMapMarkerService(Activity activity){
        this.activity = activity;
    }

    public void init(){
        ServiceEngineFactory.getServiceEngine().getEventService().register(this);

        distanceUpdater = new DistanceUpdater();
        distanceUpdater.start();
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        setCurrentMarker(marker);

        //更新地图中心点
        updateMapStatus(marker.getPosition());

        initWindowView();

        infoWindow = new InfoWindow(view, marker.getPosition(), 350);
        getBaiduMap().showInfoWindow(infoWindow);

        return true;
    }

    /**
     * 计算两点之间的直线距离
     */
    private String calculateDistance(){
        String distanceStr = "";
        try {
            long distance = 0;
            LocationBean locationBean = HeasyLocationService.getHeasyLocationClient() != null ? HeasyLocationService.getHeasyLocationClient().getLastedLocation() : null;
            if (locationBean != null && getCurrentMarker() != null) {
                distance = new Double(DistanceUtil.getDistance(locationBean.getLatLng(), getCurrentMarker().getPosition())).longValue();
            }

            if(distance > 1000){
                distanceStr = DoubleUtil.decimalNum(distance / 1000.0, 3) + "公里";
            }else{
                distanceStr = distance + "米";
            }
        }catch (Exception ex){
            logger.error("", ex);
        }
        return distanceStr;
    }

    private void initWindowView(){
        bean = (FixedPointInfoBean)getCurrentMarker().getExtraInfo().getSerializable("bean");

        LayoutInflater inflater = LayoutInflater.from(activity);
        view = inflater.inflate(com.heasy.knowroute.R.layout.fixed_point_edit, null);

        updateMarkerLabelDisplayStatus();

        final EditText txtLongitude = (EditText)view.findViewById(R.id.txtLongitude);
        txtLongitude.setText(String.valueOf(bean.getLongitude()));

        final EditText txtLatitude = (EditText)view.findViewById(R.id.txtLatitude);
        txtLatitude.setText(String.valueOf(bean.getLatitude()));

        txtAddress = (EditText)view.findViewById(R.id.txtAddress);
        txtAddress.setText(bean.getAddress());

        //距离
        txtDistance = (EditText)view.findViewById(R.id.txtDistance);
        txtDistance.setText(calculateDistance());

        txtLabel = (EditText)view.findViewById(R.id.txtLabel);
        txtLabel.setText(bean.getLabel());

        txtComments = (EditText)view.findViewById(R.id.txtComments);
        txtComments.setText(bean.getComments());

        //保存
        Button btnSave = (Button) view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(bean != null){
                    bean.setAddress(StringUtil.trimToEmpty(txtAddress.getText().toString()));
                    bean.setLabel(StringUtil.trimToEmpty(txtLabel.getText().toString()));
                    bean.setComments(StringUtil.trimToEmpty(txtComments.getText().toString()));

                    progressDialog = AndroidUtil.showLoadingDialog(activity, "正在处理...");

                    new DefaultDaemonThread(){
                        @Override
                        public void run() {
                            try {
                                String url = "fixedPointInfo/saveOrUpdate";
                                String data = FastjsonUtil.object2String(bean);
                                ResponseBean responseBean = HttpService.postJson(ServiceEngineFactory.getServiceEngine().getHeasyContext(), url, data);
                                if (responseBean.getCode() == ResponseCode.SUCCESS.code()) {
                                    String result = (String) responseBean.getData();
                                    int id = FastjsonUtil.string2JSONObject(result).getIntValue("id");
                                    bean.setId(id);

                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("bean", bean);
                                    getCurrentMarker().setExtraInfo(bundle);

                                    FixedPointInfoEvent event = new FixedPointInfoEvent(this, FixedPointInfoEvent.ACTION_NAME.SAVE.name(), "");
                                    ServiceEngineFactory.getServiceEngine().getEventService().postEvent(event);
                                } else {
                                    FixedPointInfoEvent event = new FixedPointInfoEvent(this, FixedPointInfoEvent.ACTION_NAME.SAVE.name(), HttpService.getFailureMessage(responseBean));
                                    ServiceEngineFactory.getServiceEngine().getEventService().postEvent(event);
                                }
                            }catch (Exception ex){
                                logger.error("", ex);
                                FixedPointInfoEvent event = new FixedPointInfoEvent(this, FixedPointInfoEvent.ACTION_NAME.SAVE.name(), "保存失败");
                                ServiceEngineFactory.getServiceEngine().getEventService().postEvent(event);
                            }
                        }
                    }.start();
                }
            }
        });

        //删除
        Button btnDelete = (Button) view.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bean.getId() > 0){
                    progressDialog = AndroidUtil.showLoadingDialog(activity, "正在处理...");
                    new DefaultDaemonThread(){
                        @Override
                        public void run() {
                            try {
                                String url = "fixedPointInfo/deleteById/" + bean.getUserId() + "/" + bean.getId();
                                ResponseBean responseBean = HttpService.postJson(ServiceEngineFactory.getServiceEngine().getHeasyContext(), url, "");
                                if (responseBean.getCode() == ResponseCode.SUCCESS.code()) {
                                    FixedPointInfoEvent event = new FixedPointInfoEvent(this, FixedPointInfoEvent.ACTION_NAME.DELETE.name(), "");
                                    ServiceEngineFactory.getServiceEngine().getEventService().postEvent(event);
                                } else {
                                    FixedPointInfoEvent event = new FixedPointInfoEvent(this, FixedPointInfoEvent.ACTION_NAME.DELETE.name(), HttpService.getFailureMessage(responseBean));
                                    ServiceEngineFactory.getServiceEngine().getEventService().postEvent(event);
                                }
                            }catch (Exception ex){
                                logger.error("", ex);
                                FixedPointInfoEvent event = new FixedPointInfoEvent(this, FixedPointInfoEvent.ACTION_NAME.DELETE.name(), "删除失败");
                                ServiceEngineFactory.getServiceEngine().getEventService().postEvent(event);
                            }
                        }
                    }.start();
                }else{
                    getCurrentMarker().remove();
                    closeInfoWindow();
                    AndroidUtil.showToast(activity, "删除成功");
                }
            }
        });

        //路径规划
        Button btnRoute = (Button)view.findViewById(R.id.btnRoute);
        btnRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeInfoWindow();
            }
        });

        //关闭
        Button btnClose = (Button) view.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeInfoWindow();
            }
        });
    }

    private void closeInfoWindow(){
        getBaiduMap().hideInfoWindow();
        infoWindow = null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleEvent(final FixedPointInfoEvent event) {
        if(progressDialog != null){
            progressDialog.dismiss();
            progressDialog = null;
        }

        if (event != null) {
            if(event.getActionName().equalsIgnoreCase(FixedPointInfoEvent.ACTION_NAME.UPDATE_DISTANCE.name())){
                if(txtDistance != null) {
                    txtDistance.setText(event.getMessage());
                }
            }else{
                if(StringUtil.isEmpty(event.getMessage())){ //success
                    if(event.getActionName().equalsIgnoreCase(FixedPointInfoEvent.ACTION_NAME.SAVE.name())){//save
                        updateMarkerLabelDisplayStatus();
                        updateMarkerIcon();
                        AndroidUtil.showToast(activity, "保存成功");
                    }else if(event.getActionName().equalsIgnoreCase(FixedPointInfoEvent.ACTION_NAME.DELETE.name())){//delete
                        getCurrentMarker().remove();
                        closeInfoWindow();
                        AndroidUtil.showToast(activity, "删除成功");
                    }
                }else{
                    AndroidUtil.showToast(activity, event.getMessage());
                }
            }
        }
    }

    private void updateMarkerLabelDisplayStatus() {
        //用于表示新建的标签
        final TextView tvNewLabel = (TextView)view.findViewById(R.id.tvNewLabel);
        if(bean.getId() > 0){
            tvNewLabel.setVisibility(View.GONE);
        }else{
            tvNewLabel.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 更新Marker的Icon图标
     */
    public void updateMarkerIcon(){
        Bitmap bitmap = getViewBitmap(getMapPointView(bean.getLabel()));
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
        getCurrentMarker().setIcon(bitmapDescriptor);
    }

    public void addMarkerOverlay(FixedPointInfoBean bean){
        addMarkerOverlay(bean, BitmapDescriptorFactory.fromResource(R.drawable.icon_location_2));
    }

    /**
     * 添加Marker覆盖物
     * @param bean bean对象
     * @param icon 图标
     */
    public void addMarkerOverlay(FixedPointInfoBean bean, BitmapDescriptor icon){
        LatLng position = new LatLng(bean.getLatitude(), bean.getLongitude());

        Bundle bundle = new Bundle();
        bundle.putSerializable("bean", bean);

        MarkerOptions markerOptions = new MarkerOptions()
                .position(position)
                .icon(icon)
                .extraInfo(bundle);

        getBaiduMap().addOverlay(markerOptions);
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
    public Bitmap getViewBitmap(View view){
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
     * 获取自定义的定位图标
     * @param label 文本标签
     */
    public View getMapPointView(String label){
        LayoutInflater inflater = LayoutInflater.from(activity);
        View pointView = inflater.inflate(com.heasy.knowroute.R.layout.fixed_point_navigation_icon, null);

        TextView textView = pointView.findViewById(R.id.tv_name);
        textView.setText(label);

        if(StringUtil.isEmpty(StringUtil.trimToEmpty(label))){
            textView.setVisibility(View.GONE);
        }else{
            textView.setVisibility(View.VISIBLE);
        }

        return pointView;
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

    public void destroy(){
        this.activity = null;
        setBaiduMap(null);
        setMapView(null);
        setCurrentMarker(null);
        ServiceEngineFactory.getServiceEngine().getEventService().unregister(this);

        try {
            if(progressDialog != null){
                progressDialog.dismiss();
                progressDialog = null;
            }
        }catch (Exception ex){

        }

        try {
            if (distanceUpdater != null) {
                distanceUpdater.interrupt();
            }
        }catch (Exception ex){

        }
    }

    class DistanceUpdater extends DefaultDaemonThread{
        @Override
        public void run() {
            while (true){
                try{
                    TimeUnit.SECONDS.sleep(DISTANCE_UPDATE_INTERVAL_SECONDS);

                    if(infoWindow == null){
                        continue;
                    }

                    String distance = calculateDistance();
                    logger.debug("当前距离目标点距离为 " + distance);

                    FixedPointInfoEvent event = new FixedPointInfoEvent(this, FixedPointInfoEvent.ACTION_NAME.UPDATE_DISTANCE.name(), distance);
                    ServiceEngineFactory.getServiceEngine().getEventService().postEvent(event);
                }catch (Exception ex){
                    logger.error("", ex);
                }
            }
        }
    }

}
