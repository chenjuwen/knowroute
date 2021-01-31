package com.heasy.knowroute.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.heasy.knowroute.R;
import com.heasy.knowroute.bean.PointBean;
import com.heasy.knowroute.bean.UserBean;
import com.heasy.knowroute.core.DefaultDaemonThread;
import com.heasy.knowroute.core.event.ToastEvent;
import com.heasy.knowroute.core.service.ServiceEngineFactory;
import com.heasy.knowroute.core.utils.AndroidUtil;
import com.heasy.knowroute.core.utils.DatetimeUtil;
import com.heasy.knowroute.event.RouteTrackEvent;
import com.heasy.knowroute.map.AbstractMapLocationClient;
import com.heasy.knowroute.map.DefaultMapLocationClient;
import com.heasy.knowroute.map.RouteTrackMapMarkerService;
import com.heasy.knowroute.service.backend.PositionAPI;
import com.heasy.knowroute.service.backend.UserAPI;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import jsc.kit.datetimepicker.widget.DateTimePicker;

/**
 * 轨迹回放
 */
public class RouteTrackActivity extends BaseMapActivity implements View.OnClickListener {
    private static final Logger logger = LoggerFactory.getLogger(RouteTrackActivity.class);

    /**
     * 回放间隔毫秒数
     */
    public static final int PLAYBACK_INTERVAL_MILLISECONDS = 250;
    /**
     * 轨迹线宽
     */
    public static final int LINE_WIDTH = 7;
    /**
     * 轨迹线颜色
     */
    public static final int LINE_COLOR = 0xAAFF0000; //红色

    private Button btnBack;
    private TextView tvTitle;
    private TextView txtStartDate;
    private TextView txtEndDate;
    private Button btnViewTrack;
    private TextView statInfo;

    private Date minDate = DatetimeUtil.addDate(Calendar.MONTH, -12);
    private Date maxDate = DatetimeUtil.addDate(Calendar.MONTH, 12);
    private DefaultDaemonThread trackRunThread;

    private String userId;
    private String nickName;
    private UserBean userBean;

    private AbstractMapLocationClient mapLocationClient;
    private RouteTrackMapMarkerService mapMarkerService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_track);

        hideActionBar();

        //获取传递过来的参数
        Bundle bundle = getIntent().getExtras();
        userId = bundle.getString("userId");
        nickName = bundle.getString("nickName");
        logger.debug("userId=" + userId + ", nickName=" + nickName);

        ServiceEngineFactory.getServiceEngine().getEventService().register(this);

        this.mapMarkerService = new RouteTrackMapMarkerService();

        initViewComponents();

        mMapView = ((MapView) findViewById(R.id.mapView));
        initBaiduMap(MyLocationConfiguration.LocationMode.NORMAL, null, this.mapMarkerService);
        initPosition();

        this.mapLocationClient = new DefaultMapLocationClient(mBaiduMap, RouteTrackActivity.this);
        this.mapLocationClient.init();
        setMapLocationClient(this.mapLocationClient);
    }

    private void initViewComponents() {
        btnBack = (Button)findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);

        tvTitle = (TextView)findViewById(R.id.tvTitle);
        tvTitle.setText(nickName + "的轨迹");

        txtStartDate = (TextView)findViewById(R.id.txtStartDate);
        txtStartDate.setText(DatetimeUtil.formatDate(DatetimeUtil.addDate(Calendar.HOUR, -3), DatetimeUtil.DEFAULT_PATTERN_DT2));
        txtStartDate.setOnClickListener(this);

        txtEndDate = (TextView)findViewById(R.id.txtEndDate);
        txtEndDate.setText(DatetimeUtil.formatDate(DatetimeUtil.nowDate(), DatetimeUtil.DEFAULT_PATTERN_DT2));
        txtEndDate.setOnClickListener(this);

        btnViewTrack = (Button)findViewById(R.id.btnViewTrack);
        btnViewTrack.setOnClickListener(this);

        statInfo = (TextView)findViewById(R.id.statInfo);
    }

    private void initPosition() {
        new DefaultDaemonThread(){
            @Override
            public void run() {
                userBean = UserAPI.getById(Integer.parseInt(userId));
                if(userBean != null){
                    //以指定点坐标为中心显示地图
                    LatLng latLng = new LatLng(userBean.getLatitude(), userBean.getLongitude());
                    getMapLocationClient().updateMapStatus(latLng);

                    //定位数据
                    setLocationData(1.0f, 0, userBean.getLongitude(), userBean.getLatitude());
                }
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnBack:
                finish();
                break;
            case R.id.txtStartDate:
                selectStartDate();
                break;
            case R.id.txtEndDate:
                selectEndDate();
                break;
            case R.id.btnViewTrack:
                doViewTrack();
                break;
        }
    }

    private void selectStartDate(){
        DateTimePicker dateTimePicker = new DateTimePicker(RouteTrackActivity.this, new DateTimePicker.ResultHandler() {
            @Override
            public void handle(Date date) {
                txtStartDate.setText(DatetimeUtil.formatDate(date, DatetimeUtil.DEFAULT_PATTERN_DT2));
            }
        }, minDate, maxDate);

        dateTimePicker.show(DatetimeUtil.toDate(txtStartDate.getText().toString(), DatetimeUtil.DEFAULT_PATTERN_DT2));
    }

    private void selectEndDate(){
        DateTimePicker dateTimePicker = new DateTimePicker(RouteTrackActivity.this, new DateTimePicker.ResultHandler() {
            @Override
            public void handle(Date date) {
                txtEndDate.setText(DatetimeUtil.formatDate(date, DatetimeUtil.DEFAULT_PATTERN_DT2));
            }
        }, minDate, maxDate);

        dateTimePicker.show(DatetimeUtil.toDate(txtEndDate.getText().toString(), DatetimeUtil.DEFAULT_PATTERN_DT2));
    }

    private void doViewTrack(){
        final Date startDate = DatetimeUtil.toDate(txtStartDate.getText().toString(), DatetimeUtil.DEFAULT_PATTERN_DT2);
        final Date endDate = DatetimeUtil.toDate(txtEndDate.getText().toString(), DatetimeUtil.DEFAULT_PATTERN_DT2);

        if(startDate.after(endDate)){
            AndroidUtil.showToast(getApplicationContext(), "开始时间不能大于结束时间");
            return;
        }

        if(DatetimeUtil.differHours(startDate, endDate) > 24){
            AndroidUtil.showToast(getApplicationContext(), "轨迹查询时间段必须在24小时内");
            return;
        }

        statInfo.setText("");
        statInfo.setVisibility(View.GONE);

        mBaiduMap.clear();

        if(trackRunThread != null){
            try{
                trackRunThread.interrupt();
                trackRunThread = null;
            }catch (Exception ex){

            }
        }

        loadingDialog = AndroidUtil.showLoadingDialog(this);
        logger.debug("current zoom: " + mBaiduMap.getMapStatus().zoom);

        new DefaultDaemonThread(){
            @Override
            public void run() {
                try {
                    List<PointBean> list = PositionAPI.getPoints(userId, startDate, endDate);
                    if(list == null || list.size() < 2){
                        ServiceEngineFactory.getServiceEngine().getEventService()
                                .postEvent(new ToastEvent(this, "暂无轨迹数据"));
                        return;
                    }

                    ServiceEngineFactory.getServiceEngine().getEventService()
                            .postEvent(new RouteTrackEvent(this, list));
                }catch (Exception ex){
                    logger.error("", ex);
                }finally {
                    dismissLoadingDialog();
                }
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mapLocationClient != null){
            mapLocationClient.destroy();
        }

        this.mapMarkerService.destroy();

        ServiceEngineFactory.getServiceEngine().getEventService().unregister(this);
    }

    /**
     * Toast消息显示
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleToast(final ToastEvent event){
        if(event != null){
            AndroidUtil.showToast(RouteTrackActivity.this, event.getMessage());
        }
    }

    /**
     * 轨迹路线回放
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleRouteTrack(final RouteTrackEvent event){
        if(event != null){
            Date startTime = null;
            Date endTime = null;

            long totalDistance = 0; //总路程
            LatLng provPoint = null;
            List<LatLng> points = new ArrayList<LatLng>();
            for(int i=0; i<event.getList().size(); i++){
                PointBean pointBean = event.getList().get(i);
                LatLng point = new LatLng(pointBean.getLatitude(), pointBean.getLongitude());

                if(provPoint == null){
                    provPoint = point;
                }else{
                    totalDistance += mapMarkerService.calculateDistance(provPoint, point);
                    provPoint = point;
                }

                points.add(point);

                if(i == 0){
                    startTime = DatetimeUtil.toDate(pointBean.getTimes());
                }

                if(i == event.getList().size() - 1){
                    endTime = DatetimeUtil.toDate(pointBean.getTimes());
                }
            }

            //总耗时
            logger.debug(DatetimeUtil.formatDate(startTime) + " - " + DatetimeUtil.formatDate(endTime));
            String statInfoStr = "行程 " + totalDistance + " 米, 耗时 " + DatetimeUtil.differMinutes(startTime, endTime) + " 分钟";
            logger.debug(statInfoStr);
            statInfo.setText(statInfoStr);
            statInfo.setVisibility(View.VISIBLE);

            //折线
            OverlayOptions polylineOptions = new PolylineOptions()
                    .width(LINE_WIDTH)
                    .color(LINE_COLOR)
                    .points(points);

            mBaiduMap.addOverlay(polylineOptions);

            /*
            //开始点
            Bitmap startBitmap = mapMarkerService.createViewBitmap(mapMarkerService.getCustomMapPointView(startTime, RouteTrackActivity.this));
            mapMarkerService.addMarkerOverlay(startPoint, BitmapDescriptorFactory.fromBitmap(startBitmap));

            //结束点
            Bitmap endBitmap = mapMarkerService.createViewBitmap(mapMarkerService.getCustomMapPointView(endTime, RouteTrackActivity.this));
            mapMarkerService.addMarkerOverlay(endPoint, BitmapDescriptorFactory.fromBitmap(endBitmap));
            */

            //在屏幕显示所有点
            mapMarkerService.showAllMarkersInScreen(points);

            //轨迹回放
            trackRunThread = new DefaultDaemonThread(){
                @Override
                public void run() {
                    try {
                        //运动小人
                        BitmapDescriptor personBitmap = BitmapDescriptorFactory.fromResource(R.drawable.person);
                        OverlayOptions personMarkerOptions = new MarkerOptions()
                                .position(points.get(0))
                                .icon(personBitmap);
                        Marker marker = (Marker)mBaiduMap.addOverlay(personMarkerOptions);

                        for(int i=0; i<points.size(); i++){
                            marker.setPosition(points.get(i));
                            TimeUnit.MILLISECONDS.sleep(PLAYBACK_INTERVAL_MILLISECONDS);
                        }
                    }catch (Exception ex){

                    }
                }
            };
            trackRunThread.start();
        }
    }

}
