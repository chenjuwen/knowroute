package com.heasy.knowroute.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.heasy.knowroute.R;
import com.heasy.knowroute.RouteTrackEvent;
import com.heasy.knowroute.ServiceEngineFactory;
import com.heasy.knowroute.action.ResponseBean;
import com.heasy.knowroute.action.ResponseCode;
import com.heasy.knowroute.bean.UserBean;
import com.heasy.knowroute.core.DefaultDaemonThread;
import com.heasy.knowroute.core.event.ToastEvent;
import com.heasy.knowroute.core.utils.AndroidUtil;
import com.heasy.knowroute.core.utils.DatetimeUtil;
import com.heasy.knowroute.core.utils.FastjsonUtil;
import com.heasy.knowroute.core.utils.ParameterUtil;
import com.heasy.knowroute.service.HttpService;

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
    public static final int PLAYBACK_INTERVAL_MILLISECONDS = 100;

    private Button btnBack;
    private TextView tvTitle;
    private TextView txtStartDate;
    private TextView txtEndDate;
    private Button btnViewTrack;

    private Date minDate = DatetimeUtil.addDate(Calendar.MONTH, -12);
    private Date maxDate = DatetimeUtil.addDate(Calendar.MONTH, 12);
    private DefaultDaemonThread trackRunThread;

    private String userId;
    private String nickName;
    private UserBean userBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_track);

        hideActionBar();

        //获取传递过来的参数
        Bundle bundle = getIntent().getExtras();
        userId = bundle.getString("userId");
        nickName = bundle.getString("nickName");
        logger.debug("userId=" + userId + ", nickName=" + nickName);

        ServiceEngineFactory.getServiceEngine().getEventService().register(this);

        initViewComponents();

        mMapView = ((MapView) findViewById(R.id.mapView));
        initBaiduMap();
        initPosition();
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
    }

    private void initPosition() {
        new DefaultDaemonThread(){
            @Override
            public void run() {
                String requestUrl = "user/getById?id=" + userId;
                ResponseBean responseBean = HttpService.httpGet(ServiceEngineFactory.getServiceEngine().getHeasyContext(), requestUrl);
                logger.debug(FastjsonUtil.object2String(responseBean));
                if(responseBean.getCode() == ResponseCode.SUCCESS.code()) {
                    userBean = FastjsonUtil.string2JavaBean((String) responseBean.getData(), UserBean.class);
                    doLocate();
                }
            }
        }.start();
    }

    private void doLocate() {
        LatLng latLng = new LatLng(userBean.getLatitude(), userBean.getLongitude());
        updateMapStatus(latLng);

        BitmapDescriptor personBitmap = BitmapDescriptorFactory.fromResource(R.drawable.person);
        OverlayOptions personMarkerOptions = new MarkerOptions()
                .position(latLng)
                .icon(personBitmap);
        mBaiduMap.addOverlay(personMarkerOptions);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnBack:
                goback();
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

    private void goback(){
        finish();
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

        mBaiduMap.clear();

        if(trackRunThread != null){
            try{
                trackRunThread.interrupt();
                trackRunThread = null;
            }catch (Exception ex){

            }
        }

        progressDialog = AndroidUtil.showLoadingDialog(this, "正在加载...");
        logger.debug("current zoom: " + mBaiduMap.getMapStatus().zoom);

        new DefaultDaemonThread(){
            @Override
            public void run() {
                try {
                    getPointData(startDate, endDate);
                }catch (Exception ex){
                    logger.error("", ex);
                }
                progressDialog.dismiss();
            }
        }.start();
    }

    private void getPointData(Date startDate, Date endDate) {
        String requestUrl = "position/getPoints?userId=" + userId
                + "&startDate=" + ParameterUtil.encodeParamValue(DatetimeUtil.formatDate(startDate, DatetimeUtil.DEFAULT_PATTERN_DT2))
                + "&endDate=" + ParameterUtil.encodeParamValue(DatetimeUtil.formatDate(endDate, DatetimeUtil.DEFAULT_PATTERN_DT2));

        ResponseBean responseBean = HttpService.httpGet(ServiceEngineFactory.getServiceEngine().getHeasyContext(), requestUrl);
        if(responseBean.getCode() == ResponseCode.SUCCESS.code()){
            JSONArray jsonArray = FastjsonUtil.string2JSONArray((String)responseBean.getData());

            if(jsonArray == null || jsonArray.size() < 2){
                doLocate();
                ServiceEngineFactory.getServiceEngine().getEventService().postEvent(new ToastEvent(this, "暂无轨迹数据"));
                return;
            }

            List<LatLng> points = new ArrayList<LatLng>();
            for(int i=0; i<jsonArray.size(); i++){
                JSONObject jsonObject = (JSONObject)jsonArray.get(i);
                LatLng p = new LatLng(jsonObject.getDouble("latitude"), jsonObject.getDouble("longitude"));
                points.add(p);
            }

            ServiceEngineFactory.getServiceEngine().getEventService().postEvent(new RouteTrackEvent(this, points));

        }else{
            logger.error(HttpService.getFailureMessage(responseBean));
            ServiceEngineFactory.getServiceEngine().getEventService().postEvent(new ToastEvent(this, "操作失败"));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ServiceEngineFactory.getServiceEngine().getEventService().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleToast(final ToastEvent event){
        if(event != null){
            AndroidUtil.showToast(RouteTrackActivity.this, event.getMessage());
        }
    }

    /**
     * 回放轨迹路线
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleRouteTrack(final RouteTrackEvent event){
        if(event != null){
            //折线
            OverlayOptions polylineOptions = new PolylineOptions()
                    .width(10)
                    .color(0xAAFF0000)
                    .points(event.getPoints());

            mBaiduMap.addOverlay(polylineOptions);

            /*
            //开始点
            BitmapDescriptor startBitmap = BitmapDescriptorFactory.fromResource(R.drawable.start);
            OverlayOptions startMarkerOptions = new MarkerOptions()
                    .position(event.getPoints().get(0))
                    .icon(startBitmap);
            mBaiduMap.addOverlay(startMarkerOptions);

            //结束点
            BitmapDescriptor endBitmap = BitmapDescriptorFactory.fromResource(R.drawable.end);
            OverlayOptions endMarkerOptions = new MarkerOptions()
                    .position(event.getPoints().get(event.getPoints().size()-1))
                    .icon(endBitmap);
            mBaiduMap.addOverlay(endMarkerOptions);
            */

            //屏幕内显示所有marker
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng latLng : event.getPoints()) {
                builder = builder.include(latLng);
            }
            LatLngBounds latlngBounds = builder.build();
            MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngBounds(latlngBounds, mMapView.getWidth(), mMapView.getHeight());
            mBaiduMap.animateMapStatus(mapStatusUpdate);

            //轨迹回放
            trackRunThread = new DefaultDaemonThread(){
                @Override
                public void run() {
                    try {
                        //运动小人
                        BitmapDescriptor personBitmap = BitmapDescriptorFactory.fromResource(R.drawable.person);
                        OverlayOptions personMarkerOptions = new MarkerOptions()
                                .position(event.getPoints().get(0))
                                .icon(personBitmap);
                        Marker marker = (Marker)mBaiduMap.addOverlay(personMarkerOptions);

                        for(int i=1; i<event.getPoints().size(); i++){
                            marker.setPosition(event.getPoints().get(i));
                            TimeUnit.MILLISECONDS.sleep(PLAYBACK_INTERVAL_MILLISECONDS);
                        }

                        //marker.remove();

                    }catch (Exception ex){

                    }
                }
            };
            trackRunThread.start();
        }
    }

}
