package com.heasy.knowroute.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.model.LatLng;
import com.heasy.knowroute.R;
import com.heasy.knowroute.bean.UserBean;
import com.heasy.knowroute.core.DefaultDaemonThread;
import com.heasy.knowroute.core.event.ToastEvent;
import com.heasy.knowroute.core.service.ServiceEngineFactory;
import com.heasy.knowroute.map.AbstractMapLocationClient;
import com.heasy.knowroute.map.DefaultMapLocationClient;
import com.heasy.knowroute.service.backend.UserAPI;

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

    private AbstractMapLocationClient mapLocationClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_map);

        hideActionBar();

        //获取传递过来的参数
        Bundle bundle = getIntent().getExtras();
        userId = bundle.getString("userId");

        ServiceEngineFactory.getServiceEngine().getEventService().register(this);

        initViewComponents();

        mMapView = ((MapView) findViewById(R.id.mapView));
        initBaiduMap(MyLocationConfiguration.LocationMode.NORMAL, null, null);
        initPosition();

        this.mapLocationClient = new DefaultMapLocationClient(mBaiduMap, HelpMapActivity.this);
        this.mapLocationClient.init();
        setMapLocationClient(this.mapLocationClient);
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
                userBean = UserAPI.getById(Integer.parseInt(userId));
                if(userBean != null){
                    doLocate();
                    ServiceEngineFactory.getServiceEngine().getEventService()
                            .postEvent(new ToastEvent(this, "显示位置信息"));
                }
            }
        }.start();
    }

    private void doLocate() {
        //以指定点坐标为中心显示地图
        LatLng latLng = new LatLng(userBean.getLatitude(), userBean.getLongitude());
        getMapLocationClient().updateMapStatus(latLng);

        //定位数据
        setLocationData(1.0f, 0, userBean.getLongitude(), userBean.getLatitude());
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

        if(mapLocationClient != null){
            mapLocationClient.destroy();
        }

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
