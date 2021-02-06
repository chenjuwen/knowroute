package com.heasy.knowroute.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.heasy.knowroute.R;
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
import com.heasy.knowroute.map.AbstractMapLocationClient;
import com.heasy.knowroute.map.DefaultMapLocationClient;
import com.heasy.knowroute.map.FixedPointAddWindow;
import com.heasy.knowroute.map.FixedPointMapMarkerService;
import com.heasy.knowroute.service.backend.BaseAPI;
import com.heasy.knowroute.service.backend.FixedPointCategoryAPI;
import com.heasy.knowroute.service.backend.FixedPointInfoAPI;
import com.heasy.knowroute.service.backend.UserAPI;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class FixedPointNavigationActivity extends BaseMapActivity implements View.OnClickListener{
    private static final Logger logger = LoggerFactory.getLogger(FixedPointNavigationActivity.class);

    private DrawerLayout drawer_layout;
    private TextView btnBack;
    private TextView selectedCategory;
    private ImageButton btnDrawPoint;
    private ImageButton btnShowMenu;
    private ListView list_view;

    private UserBean userBean;
    //类别集合
    private List<FixedPointCategoryBean> categoryList = new ArrayList<>();
    //某个类别的点集合
    private List<FixedPointInfoBean> pointList;

    private FixedPointMapMarkerService mapMarkerService;
    private FixedPointAddWindow fixedPointAddWindow;
    private AbstractMapLocationClient mapLocationClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixed_point_navigation);

        hideActionBar();

        ServiceEngineFactory.getServiceEngine().getEventService().register(this);

        this.mapMarkerService = new FixedPointMapMarkerService(FixedPointNavigationActivity.this);
        this.mapMarkerService.init();

        this.fixedPointAddWindow = new FixedPointAddWindow(FixedPointNavigationActivity.this, this.mapMarkerService);
        this.fixedPointAddWindow.init();

        initViews();

        mMapView = ((MapView) findViewById(R.id.mapView));
        initBaiduMap(MyLocationConfiguration.LocationMode.COMPASS, null, this.mapMarkerService); //COMPASS罗盘仪

        initPosition();

        this.mapLocationClient = new DefaultMapLocationClient(mBaiduMap, FixedPointNavigationActivity.this);
        this.mapLocationClient.init();
        setMapLocationClient(this.mapLocationClient);
    }

    private void initViews(){
        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        selectedCategory = (TextView)findViewById(R.id.selectedCategory);
        list_view = (ListView) findViewById(R.id.list_view);

        btnBack = (TextView)findViewById(R.id.btnBack);
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
                userBean = UserAPI.getById(BaseAPI.getLoginService().getUserId());
                if(userBean != null){
                    //以指定点坐标为中心显示地图
                    LatLng latLng = new LatLng(userBean.getLatitude(), userBean.getLongitude());
                    getMapLocationClient().updateMapStatus(latLng);

                    //定位数据，显示默认的定位Marker
                    MyLocationData locationData = new MyLocationData.Builder()
                            .accuracy(1.0f)
                            .direction(getMapLocationClient().getDirection())  // 方向信息，顺时针0-360
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
                    String result = FixedPointCategoryAPI.list();
                    categoryList = FastjsonUtil.arrayString2List(result, FixedPointCategoryBean.class);
                    ServiceEngineFactory.getServiceEngine().getEventService()
                            .postEvent(new FixedPointNavigationEvent(this, "success"));
                }catch (Exception ex){
                    logger.error("", ex);
                    ServiceEngineFactory.getServiceEngine().getEventService()
                            .postEvent(new FixedPointNavigationEvent(this, "error"));
                }
            }
        }.start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleNavigationEvent(final FixedPointNavigationEvent event){
        if(event != null){
            dismissLoadingDialog();
            if(categoryList != null && categoryList.size() > 0) {
                FixedPointNavigationAdapter adapter = new FixedPointNavigationAdapter(FixedPointNavigationActivity.this, categoryList);
                list_view.setAdapter(adapter);
                list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        btnDrawPoint.setVisibility(View.VISIBLE);
                        drawer_layout.closeDrawer(Gravity.END);

                        Integer newCategoryId = categoryList.get(position).getId();

                        //点击同一个类别，不再加载数据
                       //Integer oldCategoryId = (Integer) ServiceEngineFactory.getServiceEngine().getDataService().getGlobalMemoryDataCache().get(FIXED_POINT_CATEGORY_ID);
                        //if(newCategoryId == oldCategoryId){
                        //    return;
                        //}

                        //categoryId set to cache
                        mapMarkerService.setCategoryIdToCache(newCategoryId);

                        selectedCategory.setText(categoryList.get(position).getName());

                        //refresh map
                        mapMarkerService.getBaiduMap().clear();

                        loadingDialog = AndroidUtil.showLoadingDialog(FixedPointNavigationActivity.this);

                        new DefaultDaemonThread(){
                            @Override
                            public void run() {
                                try{
                                    Integer categoryId = mapMarkerService.getCategoryIdFromCache();
                                    pointList = FixedPointInfoAPI.list(categoryId);
                                    ServiceEngineFactory.getServiceEngine().getEventService().postEvent(new FixedPointCategoryChangeEvent(this, ""));
                                }catch (Exception ex){
                                    logger.error("", ex);
                                    ServiceEngineFactory.getServiceEngine().getEventService().postEvent(new FixedPointCategoryChangeEvent(this, "数据加载失败"));
                                }
                            }
                        }.start();
                    }
                });

                openDrawer();
            }else{
                AndroidUtil.showToast(FixedPointNavigationActivity.this, "可到【我的>定点位置类别设置】添加类别");
            }
        }
    }

    /**
     * 显示某个类别的所有Marker
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleCategoryChangeEvent(final FixedPointCategoryChangeEvent event) {
        if (event != null) {
            dismissLoadingDialog();

            if(StringUtil.isEmpty(event.getMessage())){//success
                if(pointList != null && pointList.size() > 0){
                    List<LatLng> list = new ArrayList<>();

                    for(int i=0; i<pointList.size(); i++){
                        FixedPointInfoBean bean = pointList.get(i);
                        Bitmap bitmap = mapMarkerService.createViewBitmap(mapMarkerService.getCustomMapPointView(bean.getLabel(), FixedPointNavigationActivity.this));
                        mapMarkerService.addMarkerOverlay(bean, BitmapDescriptorFactory.fromBitmap(bitmap));
                        list.add(new LatLng(bean.getLatitude(), bean.getLongitude()));
                    }

                    //在屏幕显示所有点
                    mapMarkerService.showAllMarkersInScreen(list);
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
                loadingDialog = AndroidUtil.showLoadingDialog(FixedPointNavigationActivity.this);
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
        mapMarkerService.deleteCategoryIdFromCache();
        this.fixedPointAddWindow.destroy();
        this.mapMarkerService.destroy();
        this.mapLocationClient.destroy();
        ServiceEngineFactory.getServiceEngine().getEventService().unregister(this);
    }

}
