package com.heasy.knowroute.map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.heasy.knowroute.R;
import com.heasy.knowroute.activity.FixedPointNavigationActivity;
import com.heasy.knowroute.bean.FixedPointInfoBean;
import com.heasy.knowroute.core.DefaultDaemonThread;
import com.heasy.knowroute.core.service.ServiceEngineFactory;
import com.heasy.knowroute.core.utils.AndroidUtil;
import com.heasy.knowroute.core.utils.DoubleUtil;
import com.heasy.knowroute.core.utils.FastjsonUtil;
import com.heasy.knowroute.core.utils.StringUtil;
import com.heasy.knowroute.event.FixedPointInfoEvent;
import com.heasy.knowroute.map.bean.LocationBean;
import com.heasy.knowroute.map.geocode.DefaultGetReverseGeoCode;
import com.heasy.knowroute.map.geocode.ReverseGeoCodeResultCallback;
import com.heasy.knowroute.service.LoginService;
import com.heasy.knowroute.service.LoginServiceImpl;
import com.heasy.knowroute.service.backend.FixedPointInfoAPI;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class FixedPointMapMarkerService extends AbstractMapMarkerService implements BaiduMap.OnMapLongClickListener {
    private static final Logger logger = LoggerFactory.getLogger(FixedPointMapMarkerService.class);
    public static final int DISTANCE_UPDATE_INTERVAL_SECONDS = 5;

    private InfoWindow infoWindow;
    private View view;
    private EditText txtDistance;
    private EditText txtAddress;
    private EditText txtLabel;
    private EditText txtComments;

    private Activity activity;
    private FixedPointInfoBean bean;
    private DistanceUpdater distanceUpdater;
    private DefaultGetReverseGeoCode getReverseGeoCode;

    public FixedPointMapMarkerService(Activity activity){
        this.activity = activity;
    }

    public void init(){
        ServiceEngineFactory.getServiceEngine().getEventService().register(this);

        getReverseGeoCode = new DefaultGetReverseGeoCode();
        getReverseGeoCode.init();

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

    @Override
    public void onMapLongClick(LatLng location) {
        loadingDialog = AndroidUtil.showLoadingDialog(activity);

        getReverseGeoCode.getReverseGeoCode(location, new ReverseGeoCodeResultCallback() {
            @Override
            public void execute(String address, LatLng location, String error) {
                logger.debug("error: " + error);
                dismissLoadingDialog();
                if(StringUtil.isEmpty(error)) {
                    double longitude = DoubleUtil.decimalNum(location.longitude, 6);
                    double latitude = DoubleUtil.decimalNum(location.latitude, 6);
                    addMarker(longitude, latitude, address);
                }
            }
        });
    }

    private void addMarker(double longitude, double latitude, String address) {
        LoginService loginService = ServiceEngineFactory.getServiceEngine().getService(LoginServiceImpl.class);

        FixedPointInfoBean bean = new FixedPointInfoBean();
        bean.setUserId(loginService.getUserId());
        bean.setCategoryId((Integer) ServiceEngineFactory.getServiceEngine().getDataService().getGlobalMemoryDataCache().get(FixedPointNavigationActivity.FIXED_POINT_CATEGORY_ID));
        bean.setLongitude(longitude);
        bean.setLatitude(latitude);
        bean.setAddress(address);
        bean.setLabel("新增");

        //add marker
        Bitmap bitmap = createViewBitmap(getCustomMapPointView(bean.getLabel(), activity));
        addMarkerOverlay(bean, BitmapDescriptorFactory.fromBitmap(bitmap));

        updateMapStatus(new LatLng(latitude, longitude));
    }

    /**
     * 计算两点之间的直线距离
     */
    private String calculateDistance(){
        LocationBean locationBean = HeasyLocationService.getHeasyLocationClient() != null ? HeasyLocationService.getHeasyLocationClient().getLastedLocation() : null;
        if (locationBean != null && getCurrentMarker() != null) {
            String distanceStr = formatDistance(calculateDistance(locationBean.getLatLng(), getCurrentMarker().getPosition()));
            return distanceStr;
        }else{
            return "0米";
        }
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

                    loadingDialog = AndroidUtil.showLoadingDialog(activity);

                    new DefaultDaemonThread(){
                        @Override
                        public void run() {
                            try {
                                String data = FastjsonUtil.object2String(bean);
                                Integer id = FixedPointInfoAPI.saveOrUpdate(data);
                                if(id != null){
                                    bean.setId(id);

                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("bean", bean);
                                    getCurrentMarker().setExtraInfo(bundle);

                                    FixedPointInfoEvent event = new FixedPointInfoEvent(this, FixedPointInfoEvent.ACTION_NAME.SAVE.name(), "");
                                    ServiceEngineFactory.getServiceEngine().getEventService().postEvent(event);
                                }else{
                                    FixedPointInfoEvent event = new FixedPointInfoEvent(this, FixedPointInfoEvent.ACTION_NAME.SAVE.name(), "保存失败");
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
                    loadingDialog = AndroidUtil.showLoadingDialog(activity);

                    new DefaultDaemonThread(){
                        @Override
                        public void run() {
                            try {
                                String result = FixedPointInfoAPI.deleteById(bean.getUserId(), bean.getId());
                                FixedPointInfoEvent event = new FixedPointInfoEvent(this, FixedPointInfoEvent.ACTION_NAME.DELETE.name(), result);
                                ServiceEngineFactory.getServiceEngine().getEventService().postEvent(event);
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

        //导航
        Button btnNav = (Button)view.findViewById(R.id.btnNav);
        btnNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationBean locationBean = HeasyLocationService.getHeasyLocationClient() != null ? HeasyLocationService.getHeasyLocationClient().getLastedLocation() : null;
                if (locationBean != null && getCurrentMarker() != null) {
                    NaviParaOption option = new NaviParaOption();
                    option.startPoint(locationBean.getLatLng());
                    option.startName("从这里开始");
                    option.endPoint(getCurrentMarker().getPosition());
                    option.endName("到这里结束");

                    try {
                        BaiduMapNavigation.setSupportWebNavi(false);
                        BaiduMapNavigation.openBaiduMapNavi(option, activity);
                    }catch (BaiduMapAppNotSupportNaviException ex){
                        logger.error("", ex);
                        AndroidUtil.showToast(activity, "请确认是否已安装百度地图APP");
                    }catch (Exception ex){
                        logger.error("", ex);
                        AndroidUtil.showToast(activity, "调起百度地图APP失败");
                    }
                }
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
        getBaiduMap().hideInfoWindow(infoWindow);
        infoWindow = null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleEvent(final FixedPointInfoEvent event) {
        if(loadingDialog != null){
            loadingDialog.dismiss();
            loadingDialog = null;
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
        Bitmap bitmap = createViewBitmap(getCustomMapPointView(bean.getLabel(), activity));
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
        getCurrentMarker().setIcon(bitmapDescriptor);
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

    public void destroy(){
        this.activity = null;

        if(getReverseGeoCode != null){
            getReverseGeoCode.destroy();
            getReverseGeoCode = null;
        }

        setBaiduMap(null);
        setMapView(null);

        setCurrentMarker(null);
        ServiceEngineFactory.getServiceEngine().getEventService().unregister(this);

        try {
            if(loadingDialog != null){
                loadingDialog.dismiss();
                loadingDialog = null;
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

    /**
     * 实时计算距离
     */
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
