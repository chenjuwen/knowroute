package com.heasy.knowroute.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.heasy.knowroute.bean.UserBean;
import com.heasy.knowroute.core.DefaultDaemonThread;
import com.heasy.knowroute.core.utils.FastjsonUtil;
import com.heasy.knowroute.service.HttpService;
import com.heasy.knowroute.service.LoginService;
import com.heasy.knowroute.service.LoginServiceImpl;

public class FixedPointNavigationActivity extends BaseMapActivity implements View.OnClickListener{
    private DrawerLayout drawer_layout;
    private View navigation_toolbar;
    private Button btnBack;
    private TextView selectedCategory;
    private ImageButton btnShowMenu;
    private ListView list_view;

    private UserBean userBean;
    private String[] dataArray = { "第一类别", "第二类别", "第三类别", "第四类别",
            "第五类别", "第六类别", "第七类别", "第八类别", "第九类别", "第十类别",
            "第十一类别", "第十二类别", "第十三类别", "第十四类别", "第十五类别", "第十六类别" };
    private int selectedIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixed_point_navigation);

        hideActionBar();

        initViews();
        initMapView();
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(FixedPointNavigationActivity.this, R.layout.fixed_point_navigation_listitem, dataArray);
        list_view.setAdapter(adapter);
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedIndex = position;
                selectedCategory.setText(dataArray[position]);
                drawer_layout.closeDrawer(Gravity.END);
            }
        });

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
}
