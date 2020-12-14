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
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.heasy.knowroute.R;
import com.heasy.knowroute.ServiceEngineFactory;
import com.heasy.knowroute.action.ResponseBean;
import com.heasy.knowroute.action.ResponseCode;
import com.heasy.knowroute.bean.UserBean;
import com.heasy.knowroute.core.DefaultDaemonThread;
import com.heasy.knowroute.core.event.ToastEvent;
import com.heasy.knowroute.core.utils.FastjsonUtil;
import com.heasy.knowroute.service.HttpService;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 紧急求助地图定位
 */
public class HelpMapActivity extends BaseMapActivity implements View.OnClickListener{
    private static final Logger logger = LoggerFactory.getLogger(HelpMapActivity.class);

    private Button btnBack;
    private TextView tvPhone;
    private TextView tvAddress;
    private TextView tvLongitude;
    private TextView tvLatitude;

    private String userId;
    private UserBean userBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_map);

        hideActionBar();

        //获取传递过来的参数
        Bundle bundle = getIntent().getExtras();
        userId = bundle.getString("userId");

        ServiceEngineFactory.getServiceEngine().getEventService().register(this);

        initViewComponents();

        mMapView = ((MapView) findViewById(R.id.mapView));
        initBaiduMap(null);
        initPosition();
    }

    private void initViewComponents() {
        btnBack = (Button)findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);

        tvPhone = (TextView) findViewById(R.id.tvPhone);
        tvAddress = (TextView)findViewById(R.id.tvAddress);
        tvLongitude = (TextView)findViewById(R.id.tvLongitude);
        tvLatitude = (TextView)findViewById(R.id.tvLatitude);
    }

    private void initPosition() {
        new DefaultDaemonThread(){
            @Override
            public void run() {
                String requestUrl = "user/getById?id=" + userId;
                ResponseBean responseBean = HttpService.get(ServiceEngineFactory.getServiceEngine().getHeasyContext(), requestUrl);
                if(responseBean.getCode() == ResponseCode.SUCCESS.code()) {
                    userBean = FastjsonUtil.string2JavaBean((String) responseBean.getData(), UserBean.class);
                    doLocate();
                    ServiceEngineFactory.getServiceEngine().getEventService().postEvent(new ToastEvent(this, "显示位置信息"));
                }
            }
        }.start();
    }

    private void doLocate() {
        //以指定点坐标为中心显示地图
        LatLng latLng = new LatLng(userBean.getLatitude(), userBean.getLongitude());
        updateMapStatus(latLng);

        //定位数据，显示默认的定位Marker
        MyLocationData locationData = new MyLocationData.Builder()
                .accuracy(1.0f)
                .direction(direction)  // 方向信息，顺时针0-360
                .longitude(userBean.getLongitude())
                .latitude(userBean.getLatitude())
                .build();
        mBaiduMap.setMyLocationData(locationData);

        //文字
        /*
        OverlayOptions mTextOptions = new TextOptions()
                .text("我在这里")
                .bgColor(0xffffff00) //黄色
                .fontSize(30)
                .fontColor(0xffff0000) //红色
                .position(latLng);
        mBaiduMap.addOverlay(mTextOptions);
         */
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnBack:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ServiceEngineFactory.getServiceEngine().getEventService().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleToast(final ToastEvent event){
        if(userBean != null){
            tvPhone.setText(userBean.getPhone());
            tvAddress.setText(userBean.getAddress());
            tvLongitude.setText("经度：" + userBean.getLongitude());
            tvLatitude.setText("纬度：" + userBean.getLatitude());
        }
    }

}
