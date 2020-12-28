package com.heasy.knowroute.map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.heasy.knowroute.R;
import com.heasy.knowroute.activity.FixedPointNavigationActivity;
import com.heasy.knowroute.bean.FixedPointInfoBean;
import com.heasy.knowroute.core.service.ServiceEngineFactory;
import com.heasy.knowroute.core.utils.AndroidUtil;
import com.heasy.knowroute.core.utils.FastjsonUtil;
import com.heasy.knowroute.core.utils.StringUtil;
import com.heasy.knowroute.map.bean.LocationBean;
import com.heasy.knowroute.service.LoginService;
import com.heasy.knowroute.service.LoginServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FixedPointAddWindow {
    private static final Logger logger = LoggerFactory.getLogger(FixedPointAddWindow.class);
    private Activity activity;
    private DefaultMapMarkerService mapMarkerService;
    private int findType; //1按当前位置，2按经纬度，3按地址

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
                switch (findType){
                    case 1:
                        String latitude = txtLatitude.getText().toString();
                        String longitude = txtLongitude.getText().toString();
                        if(StringUtil.isEmpty(latitude) || StringUtil.isEmpty(longitude)){
                            AndroidUtil.showToast(activity, "经纬度坐标有误");
                            return;
                        }

                        LoginService loginService = ServiceEngineFactory.getServiceEngine().getService(LoginServiceImpl.class);

                        FixedPointInfoBean bean = new FixedPointInfoBean();
                        bean.setUserId(loginService.getUserId());
                        bean.setCategoryId((Integer) ServiceEngineFactory.getServiceEngine().getDataService().getGlobalMemoryDataCache().get(FixedPointNavigationActivity.FIXED_POINT_CATEGORY_ID));
                        bean.setLongitude(Double.parseDouble(longitude));
                        bean.setLatitude(Double.parseDouble(latitude));
                        bean.setAddress(txtAddress.getText().toString());

                        logger.debug(FastjsonUtil.object2String(bean));

                        //add marker
                        Bitmap bitmap = mapMarkerService.getViewBitmap(mapMarkerService.getMapPointView(""));
                        mapMarkerService.addMarkerOverlay(bean, BitmapDescriptorFactory.fromBitmap(bitmap));

                        mapMarkerService.getBaiduMap().hideInfoWindow();
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

        //关闭
        btnClose = (Button) view.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapMarkerService.getBaiduMap().hideInfoWindow();
            }
        });
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
    }
}
