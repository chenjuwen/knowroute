package com.heasy.knowroute.map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.model.LatLng;
import com.heasy.knowroute.R;
import com.heasy.knowroute.activity.FixedPointNavigationActivity;
import com.heasy.knowroute.bean.FixedPointInfoBean;
import com.heasy.knowroute.core.service.ServiceEngineFactory;
import com.heasy.knowroute.core.utils.AndroidUtil;
import com.heasy.knowroute.core.utils.DoubleUtil;
import com.heasy.knowroute.core.utils.StringUtil;
import com.heasy.knowroute.map.bean.LocationBean;
import com.heasy.knowroute.map.geocode.DefaultGetGeoCode;
import com.heasy.knowroute.map.geocode.DefaultGetReverseGeoCode;
import com.heasy.knowroute.map.geocode.ReverseGeoCodeResultCallback;
import com.heasy.knowroute.service.LoginService;
import com.heasy.knowroute.service.LoginServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FixedPointAddWindow {
    private static final Logger logger = LoggerFactory.getLogger(FixedPointAddWindow.class);
    private Activity activity;
    private DefaultMapMarkerService mapMarkerService;
    private int findType; //1按当前位置，2按经纬度，3按地址

    private DefaultGetReverseGeoCode getReverseGeoCode;
    private DefaultGetGeoCode getGeoCode;
    private ProgressDialog progressDialog;

    //add window
    private View view;
    private RadioGroup radioGroup;
    private EditText txtLongitude;
    private EditText txtLatitude;
    private EditText txtAddress;
    private Button btnFind;
    private Button btnClose;

    public FixedPointAddWindow(Activity activity, DefaultMapMarkerService mapMarkerService){
        this.activity = activity;
        this.mapMarkerService = mapMarkerService;
    }

    public void init(){
        getReverseGeoCode = new DefaultGetReverseGeoCode();
        getReverseGeoCode.init();

        getGeoCode = new DefaultGetGeoCode();
        getGeoCode.init();

        initWindowView();
    }

    private void initWindowView(){
        LayoutInflater inflater = LayoutInflater.from(activity);
        view = inflater.inflate(com.heasy.knowroute.R.layout.fixed_point_add, null);

        //Radio
        radioGroup = (RadioGroup) view.findViewById(com.heasy.knowroute.R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getCheckedRadioButtonId()){
                    case com.heasy.knowroute.R.id.radioButton1: //当前位置
                        findType = 1;
                        LocationBean locationBean = HeasyLocationService.getHeasyLocationClient()!=null ? HeasyLocationService.getHeasyLocationClient().getCurrentLocation() : null;
                        txtLongitude.setText(String.valueOf(locationBean!=null ? locationBean.getLongitude() : ""));
                        txtLongitude.setEnabled(false);
                        txtLatitude.setText(String.valueOf(locationBean!=null ? locationBean.getLatitude() : ""));
                        txtLatitude.setEnabled(false);
                        txtAddress.setText(locationBean!=null ? locationBean.getAddress() : "");
                        txtAddress.setEnabled(false);
                        break;
                    case com.heasy.knowroute.R.id.radioButton2: //经纬度
                        findType = 2;
                        txtLongitude.setText("");
                        txtLongitude.setEnabled(true);
                        txtLatitude.setText("");
                        txtLatitude.setEnabled(true);
                        txtAddress.setText("");
                        txtAddress.setEnabled(false);
                        break;
                    case com.heasy.knowroute.R.id.radioButton3: //地址
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
        txtLongitude = (EditText) view.findViewById(com.heasy.knowroute.R.id.txtLongitude);
        txtLatitude = (EditText) view.findViewById(com.heasy.knowroute.R.id.txtLatitude);
        txtAddress = (EditText) view.findViewById(com.heasy.knowroute.R.id.txtAddress);

        //查找
        btnFind = (Button) view.findViewById(com.heasy.knowroute.R.id.btnFind);
        btnFind.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String latitude = "0";
                String longitude = "0";

                switch (findType){
                    case 1: //按当前位置
                        latitude = StringUtil.trimToEmpty(txtLatitude.getText().toString());
                        longitude = StringUtil.trimToEmpty(txtLongitude.getText().toString());
                        if(StringUtil.isEmpty(latitude) || StringUtil.isEmpty(longitude)){
                            AndroidUtil.showToast(activity, "经纬度坐标有误");
                            return;
                        }

                        addMarker(Double.parseDouble(longitude), Double.parseDouble(latitude), txtAddress.getText().toString());
                        break;
                    case 2: //按经纬坐标
                        latitude = StringUtil.trimToEmpty(txtLatitude.getText().toString());
                        longitude = StringUtil.trimToEmpty(txtLongitude.getText().toString());
                        if(StringUtil.isEmpty(latitude) || StringUtil.isEmpty(longitude)){
                            AndroidUtil.showToast(activity, "经纬度坐标有误");
                            return;
                        }

                        LatLng location = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

                        progressDialog = AndroidUtil.showLoadingDialog(activity, "正在定位...");
                        getReverseGeoCode.getReverseGeoCode(location, new ReverseGeoCodeResultCallback() {
                            @Override
                            public void execute(String address, LatLng location) {
                                if(progressDialog != null){
                                    progressDialog.dismiss();
                                    progressDialog = null;
                                }

                                double longitude = DoubleUtil.decimalNum(location.longitude, 6);
                                double latitude = DoubleUtil.decimalNum(location.latitude, 6);

                                addMarker(longitude, latitude, address);
                            }
                        });
                        break;
                    case 3: //按地址

                        break;
                    default:
                        break;
                }
            }
        });

        //关闭
        btnClose = (Button) view.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapMarkerService.getBaiduMap().hideInfoWindow();
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
        Bitmap bitmap = mapMarkerService.getViewBitmap(mapMarkerService.getMapPointView(bean.getLabel()));
        mapMarkerService.addMarkerOverlay(bean, BitmapDescriptorFactory.fromBitmap(bitmap));

        mapMarkerService.updateMapStatus(new LatLng(latitude, longitude));

        mapMarkerService.getBaiduMap().hideInfoWindow();
    }

    private void reset(){
        findType = 0;
        radioGroup.clearCheck();
        txtLongitude.setText("");
        txtLatitude.setText("");
        txtAddress.setText("");
    }

    public void showWindow(){
        reset();
        InfoWindow infoWindow = new InfoWindow(view, mapMarkerService.getScreenCenterLocation(activity), 280);
        mapMarkerService.getBaiduMap().showInfoWindow(infoWindow);
    }

    public void destroy(){
        mapMarkerService = null;
        activity = null;

        if(getReverseGeoCode != null){
            getReverseGeoCode.destroy();
            getReverseGeoCode = null;
        }

        if(getGeoCode != null){
            getGeoCode.destroy();
            getGeoCode = null;
        }
    }
}
