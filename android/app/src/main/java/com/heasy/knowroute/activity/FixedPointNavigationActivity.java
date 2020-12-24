package com.heasy.knowroute.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.baidu.mapapi.map.InfoWindow;
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
import com.heasy.knowroute.core.utils.FastjsonUtil;
import com.heasy.knowroute.map.HeasyLocationService;
import com.heasy.knowroute.map.bean.LocationBean;
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
    private Button btnBack;
    private TextView selectedCategory;
    private ImageButton btnDrawPoint;
    private ImageButton btnShowMenu;
    private ListView list_view;

    private UserBean userBean;
    private List<FixedPointCategoryBean> dataList = new ArrayList<>();
    private int selectedIndex = -1;

    //add window
    private View addView;
    private RadioGroup radioGroup;
    private EditText txtLongitude;
    private EditText txtLatitude;
    private EditText txtAddress;
    private Button btnFind;
    private Button btnSave;
    private Button btnClose;
    private int findType; //1按当前位置，2按经纬度，3按地址

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixed_point_navigation);

        hideActionBar();

        ServiceEngineFactory.getServiceEngine().getEventService().register(this);

        initViews();
        initBaiduMap(null);
        initPosition();
        initAddWindow();
        loadData();
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

    private void initAddWindow(){
        LayoutInflater inflater = LayoutInflater.from(FixedPointNavigationActivity.this);
        addView = inflater.inflate(R.layout.fixed_point_add, null);

        //Radio
        radioGroup = (RadioGroup)addView.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getCheckedRadioButtonId()){
                    case R.id.radioButton1: //当前位置
                        findType = 1;
                        LocationBean locationBean = HeasyLocationService.getHeasyLocationClient()!=null ? HeasyLocationService.getHeasyLocationClient().getCurrentLocation() : null;
                        txtLongitude.setText(String.valueOf(locationBean!=null ? locationBean.getLongitude() : ""));
                        txtLongitude.setEnabled(false);
                        txtLatitude.setText(String.valueOf(locationBean!=null ? locationBean.getLatitude() : ""));
                        txtLatitude.setEnabled(false);
                        txtAddress.setText(locationBean!=null ? locationBean.getAddress() : "");
                        txtAddress.setEnabled(false);
                        break;
                    case R.id.radioButton2: //经纬度
                        findType = 2;
                        txtLongitude.setText("");
                        txtLongitude.setEnabled(true);
                        txtLatitude.setText("");
                        txtLatitude.setEnabled(true);
                        txtAddress.setText("");
                        txtAddress.setEnabled(false);
                        break;
                    case R.id.radioButton3: //地址
                        findType = 3;
                        txtLongitude.setText("");
                        txtLongitude.setEnabled(false);
                        txtLatitude.setText("");
                        txtLatitude.setEnabled(false);
                        txtAddress.setText("");
                        txtAddress.setEnabled(true);
                        break;
                    default:
                        break;
                }
            }
        });

        //input
        txtLongitude = (EditText)addView.findViewById(R.id.txtLongitude);
        txtLatitude = (EditText)addView.findViewById(R.id.txtLatitude);
        txtAddress = (EditText)addView.findViewById(R.id.txtAddress);

        //查找
        btnFind = (Button) addView.findViewById(R.id.btnFind);
        btnFind.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                switch (findType){
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    default:
                        break;
                }
            }
        });

        //保存
        btnSave = (Button) addView.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

            }
        });

        //关闭
        btnClose = (Button) addView.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBaiduMap.hideInfoWindow();
            }
        });
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
                    selectedIndex = position;
                    selectedCategory.setText(dataList.get(position).getName());
                    btnDrawPoint.setVisibility(View.VISIBLE);
                    drawer_layout.closeDrawer(Gravity.END);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnBack){
            finish();
        }else if(v.getId() == R.id.btnDrawPoint){
            openAddWindow();
        }else if(v.getId() == R.id.btnShowMenu){
            drawer_layout.openDrawer(Gravity.END); //左侧菜单
            unlockDrawerLayout();
        }
    }

    private void openAddWindow(){
        InfoWindow addWindow = new InfoWindow(addView, getScreenCenterLocation(), 250);
        mBaiduMap.showInfoWindow(addWindow);
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
