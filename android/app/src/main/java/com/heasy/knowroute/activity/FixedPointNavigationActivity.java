package com.heasy.knowroute.activity;

import android.graphics.Bitmap;
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

import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.heasy.knowroute.R;
import com.heasy.knowroute.action.ResponseBean;
import com.heasy.knowroute.action.ResponseCode;
import com.heasy.knowroute.bean.FixedPointCategoryBean;
import com.heasy.knowroute.bean.FixedPointInfoBean;
import com.heasy.knowroute.bean.UserBean;
import com.heasy.knowroute.core.DefaultDaemonThread;
import com.heasy.knowroute.core.service.ServiceEngineFactory;
import com.heasy.knowroute.core.utils.AndroidUtil;
import com.heasy.knowroute.core.utils.FastjsonUtil;
import com.heasy.knowroute.core.utils.StringUtil;
import com.heasy.knowroute.event.FixedPointCategoryChangeEvent;
import com.heasy.knowroute.event.FixedPointNavigationEvent;
import com.heasy.knowroute.map.DefaultMapMarkerService;
import com.heasy.knowroute.map.FixedPointAddWindow;
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
    public static final String FIXED_POINT_CATEGORY_ID = "fixedPointCategoryId";

    private DrawerLayout drawer_layout;
    private Button btnBack;
    private TextView selectedCategory;
    private ImageButton btnDrawPoint;
    private ImageButton btnShowMenu;
    private ListView list_view;

    private UserBean userBean;
    private List<FixedPointCategoryBean> categoryList = new ArrayList<>();
    private List<FixedPointInfoBean> pointList;

    private DefaultMapMarkerService mapMarkerService;
    private FixedPointAddWindow fixedPointAddWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixed_point_navigation);

        hideActionBar();

        ServiceEngineFactory.getServiceEngine().getEventService().register(this);

        this.mapMarkerService = new DefaultMapMarkerService(FixedPointNavigationActivity.this);
        this.mapMarkerService.init();

        this.fixedPointAddWindow = new FixedPointAddWindow(FixedPointNavigationActivity.this, this.mapMarkerService);
        this.fixedPointAddWindow.init();

        initViews();
        initBaiduMap(null, this.mapMarkerService);
        initPosition();
    }

    private void initViews(){
        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mMapView = ((MapView) findViewById(R.id.mapView));
        selectedCategory = (TextView)findViewById(R.id.selectedCategory);
        list_view = (ListView) findViewById(R.id.list_view);

        btnBack = (Button)findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);
        btnDrawPoint = (ImageButton)findViewById(R.id.btnDrawPoint);
        btnDrawPoint.setOnClickListener(this);
        btnShowMenu = (ImageButton)findViewById(R.id.btnShowMenu);
        btnShowMenu.setOnClickListener(this);

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

    private void initPosition() {
        new DefaultDaemonThread(){
            @Override
            public void run() {
                LoginService loginService = ServiceEngineFactory.getServiceEngine().getService(LoginServiceImpl.class);
                String requestUrl = "user/getById?id=" + loginService.getUserId();
                ResponseBean responseBean = HttpService.get(ServiceEngineFactory.getServiceEngine().getHeasyContext(), requestUrl);
                if(responseBean.getCode() == ResponseCode.SUCCESS.code()) {
                    userBean = FastjsonUtil.string2JavaBean((String) responseBean.getData(), UserBean.class);

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
            }
        }.start();
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
                        categoryList = FastjsonUtil.arrayString2List((String) responseBean.getData(), FixedPointCategoryBean.class);
                        ServiceEngineFactory.getServiceEngine().getEventService().postEvent(new FixedPointNavigationEvent(this, "success"));
                    }
                }catch (Exception ex){
                    logger.error("", ex);
                    ServiceEngineFactory.getServiceEngine().getEventService().postEvent(new FixedPointNavigationEvent(this, "error"));
                }
            }
        }.start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleNavigationEvent(final FixedPointNavigationEvent event){
        if(event != null){
            if(progressDialog != null){
                progressDialog.dismiss();
                progressDialog = null;
            }

            if(categoryList != null && categoryList.size() > 0) {
                FixedPointNavigationAdapter adapter = new FixedPointNavigationAdapter(FixedPointNavigationActivity.this, categoryList);
                list_view.setAdapter(adapter);
                list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        btnDrawPoint.setVisibility(View.VISIBLE);
                        drawer_layout.closeDrawer(Gravity.END);

                        Integer newCategoryId = categoryList.get(position).getId();
                        Integer oldCategoryId = (Integer) ServiceEngineFactory.getServiceEngine().getDataService().getGlobalMemoryDataCache().get(FIXED_POINT_CATEGORY_ID);
                        if(newCategoryId == oldCategoryId){
                            return;
                        }

                        //categoryId set to cache
                        ServiceEngineFactory.getServiceEngine().getDataService().getGlobalMemoryDataCache().set(FIXED_POINT_CATEGORY_ID, newCategoryId);

                        selectedCategory.setText(categoryList.get(position).getName());

                        //refresh map
                        mapMarkerService.getBaiduMap().clear();

                        progressDialog = AndroidUtil.showLoadingDialog(FixedPointNavigationActivity.this, "数据加载中...");

                        new DefaultDaemonThread(){
                            @Override
                            public void run() {
                                try{
                                    Integer categoryId = (Integer)ServiceEngineFactory.getServiceEngine().getDataService().getGlobalMemoryDataCache().get(FIXED_POINT_CATEGORY_ID);
                                    LoginService loginService = ServiceEngineFactory.getServiceEngine().getService(LoginServiceImpl.class);
                                    String url = "fixedPointInfo/list/" + loginService.getUserId() + "/" + categoryId;
                                    ResponseBean responseBean = HttpService.get(ServiceEngineFactory.getServiceEngine().getHeasyContext(), url);
                                    if (responseBean.getCode() == ResponseCode.SUCCESS.code()) {
                                        pointList = FastjsonUtil.arrayString2List((String) responseBean.getData(), FixedPointInfoBean.class);
                                        ServiceEngineFactory.getServiceEngine().getEventService().postEvent(new FixedPointCategoryChangeEvent(this, ""));
                                    }
                                }catch (Exception ex){
                                    logger.error("", ex);
                                    ServiceEngineFactory.getServiceEngine().getEventService().postEvent(new FixedPointCategoryChangeEvent(this, "数据加载失败"));
                                }
                            }
                        }.start();
                    }
                });

                openDrawer();
            }
        }
    }

    /**
     * 显示某个类别的所有Marker
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleCategoryChangeEvent(final FixedPointCategoryChangeEvent event) {
        if (event != null) {
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }

            if(StringUtil.isEmpty(event.getMessage())){//success
                if(pointList != null && pointList.size() > 0){
                    for(int i=0; i<pointList.size(); i++){
                        FixedPointInfoBean bean = pointList.get(i);
                        Bitmap bitmap = mapMarkerService.getViewBitmap(mapMarkerService.getMapPointView(bean.getLabel()));
                        mapMarkerService.addMarkerOverlay(bean, BitmapDescriptorFactory.fromBitmap(bitmap));
                    }
                }
            }else{
                AndroidUtil.showToast(FixedPointNavigationActivity.this, event.getMessage());
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnBack){
            finish();
        }else if(v.getId() == R.id.btnDrawPoint){
            fixedPointAddWindow.showWindow();
        }else if(v.getId() == R.id.btnShowMenu){
            if(categoryList == null || categoryList.size() == 0){
                progressDialog = AndroidUtil.showLoadingDialog(FixedPointNavigationActivity.this, "数据加载中...");
                loadData();
            }else{
                openDrawer();
            }
        }
    }

    private void openDrawer(){
        drawer_layout.openDrawer(Gravity.END); //左侧菜单
        unlockDrawerLayout();
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
        ServiceEngineFactory.getServiceEngine().getDataService().getGlobalMemoryDataCache().delete(FIXED_POINT_CATEGORY_ID);
        this.fixedPointAddWindow.destroy();
        this.mapMarkerService.destroy();
        ServiceEngineFactory.getServiceEngine().getEventService().unregister(this);
    }
}
