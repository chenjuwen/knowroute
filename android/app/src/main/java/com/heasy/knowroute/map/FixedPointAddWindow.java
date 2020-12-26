package com.heasy.knowroute.map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.model.LatLng;
import com.heasy.knowroute.R;
import com.heasy.knowroute.bean.FixedPointInfoBean;
import com.heasy.knowroute.core.utils.AndroidUtil;
import com.heasy.knowroute.core.utils.StringUtil;
import com.heasy.knowroute.map.bean.LocationBean;

public class FixedPointAddWindow {
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

                        FixedPointInfoBean bean = new FixedPointInfoBean();
                        bean.setLongitude(Double.parseDouble(longitude));
                        bean.setLatitude(Double.parseDouble(latitude));
                        bean.setAddress(txtAddress.getText().toString());

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

    /**
     * 获取屏幕中心的坐标点经纬度
     */
    private LatLng getScreenCenterLocation(){
        Point point = AndroidUtil.getDisplaySize(activity);

        Point centerPoint = new Point();
        centerPoint.x = point.x / 2;
        centerPoint.y = point.y / 2;

        LatLng centerLatLng = mapMarkerService.getBaiduMap().getProjection().fromScreenLocation(centerPoint);
        return centerLatLng;
    }

    public void showWindow(){
        initWindowView();
        InfoWindow infoWindow = new InfoWindow(view, getScreenCenterLocation(), 280);
        mapMarkerService.getBaiduMap().showInfoWindow(infoWindow);
    }
}
