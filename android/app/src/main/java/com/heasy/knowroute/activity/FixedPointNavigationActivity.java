package com.heasy.knowroute.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.heasy.knowroute.R;
import com.heasy.knowroute.ServiceEngineFactory;
import com.heasy.knowroute.action.ResponseBean;
import com.heasy.knowroute.action.ResponseCode;
import com.heasy.knowroute.action.common.FixedPointNavigationEvent;
import com.heasy.knowroute.bean.FixedPointCategoryBean;
import com.heasy.knowroute.bean.UserBean;
import com.heasy.knowroute.core.DefaultDaemonThread;
import com.heasy.knowroute.core.event.ToastEvent;
import com.heasy.knowroute.core.utils.AndroidUtil;
import com.heasy.knowroute.core.utils.FastjsonUtil;
import com.heasy.knowroute.service.HttpService;
import com.heasy.knowroute.service.LoginService;
import com.heasy.knowroute.service.LoginServiceImpl;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class FixedPointNavigationActivity extends BaseMapActivity implements View.OnClickListener{
    private static final Logger logger = LoggerFactory.getLogger(FixedPointNavigationActivity.class);

    private DrawerLayout drawer_layout;
    private View navigation_toolbar;
    private Button btnBack;
    private TextView selectedCategory;
    private ImageButton btnShowMenu;
    private ListView list_view;

    private UserBean userBean;
    private List<FixedPointCategoryBean> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixed_point_navigation);

        hideActionBar();

        ServiceEngineFactory.getServiceEngine().getEventService().register(this);

        loadData();
        initViews();
        initMapView();
    }

    private void loadData(){
        new DefaultDaemonThread(){
            @Override
            public void run() {
                try{
                    LoginService loginService = ServiceEngineFactory.getServiceEngine().getService(LoginServiceImpl.class);
                    String url = "fixedPointCategory/list/" + loginService.getUserId();
                    ResponseBean responseBean = HttpService.get(ServiceEngineFactory.getServiceEngine().getHeasyContext(), url);
                    if (responseBean.getCode() == ResponseCode.SUCCESS.code()) {
                        dataList = FastjsonUtil.arrayString2List((String) responseBean.getData(), FixedPointCategoryBean.class);
                        ServiceEngineFactory.getServiceEngine().getEventService().postEvent(new FixedPointNavigationEvent(this, "数据加载成功"));
                    }
                }catch (Exception ex){
                    logger.error("", ex);
                }
            }
        }.start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleEvent(final FixedPointNavigationEvent event){
        if(event != null){
            FixedPointNavigationAdapter adapter = new FixedPointNavigationAdapter(FixedPointNavigationActivity.this, dataList);
            list_view.setAdapter(adapter);
            list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    selectedCategory.setText(dataList.get(position).getName());
                    drawer_layout.closeDrawer(Gravity.END);
                }
            });
        }
    }

    private void initViews(){
        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigation_toolbar = findViewById(R.id.navigation_toolbar);
        btnBack = (Button)navigation_toolbar.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);
        selectedCategory = (TextView)navigation_toolbar.findViewById(R.id.selectedCategory);
        btnShowMenu = (ImageButton)navigation_toolbar.findViewById(R.id.btnShowMenu);
        btnShowMenu.setOnClickListener(this);
        list_view = (ListView) findViewById(R.id.list_view);

        //设置侧滑菜单只能通过编程来打开
        lockDrawerLayout();

        drawer_layout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {

            }

            @Override
            public void onDrawerOpened(@NonNull View view) {

            }

            @Override
            public void onDrawerClosed(@NonNull View view) {
                lockDrawerLayout();
            }

            @Override
            public void onDrawerStateChanged(int i) {

            }
        });
    }

    private void initMapView(){
        mMapView = ((MapView) findViewById(R.id.mapView));
        initBaiduMap(null);
        initPosition();
    }

    private void initPosition() {
        new DefaultDaemonThread(){
            @Override
            public void run() {
                LoginService loginService = ServiceEngineFactory.getServiceEngine().getService(LoginServiceImpl.class);
                String requestUrl = "user/getById?id=" + loginService.getUserId();
                ResponseBean responseBean = HttpService.get(ServiceEngineFactory.getServiceEngine().getHeasyContext(), requestUrl);
                if(responseBean.getCode() == ResponseCode.SUCCESS.code()) {
                    userBean = FastjsonUtil.string2JavaBean((String) responseBean.getData(), UserBean.class);
                    doLocate();
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
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnBack){
            finish();
        }else if(v.getId() == R.id.btnShowMenu){
            drawer_layout.openDrawer(Gravity.END); //左侧菜单
            unlockDrawerLayout();
        }
    }

    private void lockDrawerLayout(){
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.END);
    }

    private void unlockDrawerLayout(){
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.END);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ServiceEngineFactory.getServiceEngine().getEventService().unregister(this);
    }
}
