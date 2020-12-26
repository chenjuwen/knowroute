package com.heasy.knowroute.map;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.heasy.knowroute.R;
import com.heasy.knowroute.bean.FixedPointInfoBean;
import com.heasy.knowroute.core.service.ServiceEngineFactory;
import com.heasy.knowroute.core.utils.StringUtil;
import com.heasy.knowroute.map.bean.LocationBean;

/**
 * 百度地图覆盖物服务
 */
public class DefaultMapMarkerService extends AbstractMapMarkerService {
    private View view;
    private EditText txtDistance;
    private EditText txtAddress;
    private EditText txtLabel;
    private EditText txtComments;
    private FixedPointInfoBean bean;

    private void initWindowView(){
        bean = (FixedPointInfoBean)getCurrentMarker().getExtraInfo().getSerializable("bean");

        LayoutInflater inflater = LayoutInflater.from(ServiceEngineFactory.getServiceEngine().getAndroidContext());
        view = inflater.inflate(com.heasy.knowroute.R.layout.map_marker_popmenu, null);

        //用于表示新建Marker的标记
        final TextView tvNewLabel = (TextView)view.findViewById(R.id.tvNewLabel);
        if(bean.getId() > 0){
            tvNewLabel.setVisibility(View.GONE);
        }else{
            tvNewLabel.setVisibility(View.VISIBLE);
        }

        final EditText txtLongitude = (EditText)view.findViewById(R.id.txtLongitude);
        txtLongitude.setText(String.valueOf(bean.getLongitude()));

        final EditText txtLatitude = (EditText)view.findViewById(R.id.txtLatitude);
        txtLatitude.setText(String.valueOf(bean.getLatitude()));

        txtAddress = (EditText)view.findViewById(R.id.txtAddress);
        txtAddress.setText(bean.getAddress());

        //距离
        long distance = 0;
        LocationBean currentLocationBean = HeasyLocationService.getHeasyLocationClient()!=null ? HeasyLocationService.getHeasyLocationClient().getCurrentLocation() : null;
        if(currentLocationBean != null) {
            distance = new Double(DistanceUtil.getDistance(currentLocationBean.getLatLng(), getCurrentMarker().getPosition())).longValue();
        }
        txtDistance = (EditText)view.findViewById(R.id.txtDistance);
        txtDistance.setText(distance + " 米");

        txtLabel = (EditText)view.findViewById(R.id.txtLabel);
        txtLabel.setText(bean.getLabel());

        txtComments = (EditText)view.findViewById(R.id.txtComments);
        txtComments.setText(bean.getComments());

        /*
        serviceEngine.getSearchService().reverseGeoCode(marker.getPosition().latitude, marker.getPosition().longitude, new ReverseGeoCodeResultCallback() {
            @Override
            public void execute(String address, LatLng latLng) {
                //距离，单位为米
                long distance = new Double(DistanceUtil.getDistance(serviceEngine.getLocationService().getPosition(), latLng)).longValue();

                double lat = new BigDecimal(latLng.latitude).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
                double lon = new BigDecimal(latLng.longitude).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
                txtLatitude.setText(String.valueOf(lat));
                txtLongitude.setText(String.valueOf(lon));

                txtDistance.setText(String.valueOf(distance) + " 米");
                txtAddress.setText(address);

                if(StringUtil.isNotEmpty(id)){
                    ConfigBean bean = ConfigService.getConfigMap().get(id);
                    if(bean != null){
                        txtComments.setText(bean.getComments());
                    }
                }
            }
        });
        */

        //保存
        Button btnSave = (Button) view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(bean != null){
                    bean.setAddress(StringUtil.trimToEmpty(txtAddress.getText().toString()));
                    bean.setLabel(StringUtil.trimToEmpty(txtLabel.getText().toString()));
                    bean.setComments(StringUtil.trimToEmpty(txtComments.getText().toString()));
                }

                updateMarkerIcon();
                getBaiduMap().hideInfoWindow();
            }
        });

        //删除
        Button btnDelete = (Button) view.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentMarker().remove();
                getBaiduMap().hideInfoWindow();
            }
        });

        //路径规划
        Button btnRoute = (Button)view.findViewById(R.id.btnRoute);
        btnRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                serviceEngine.getLocationService().setRealtimeLocation(false);

                LatLng currentPosition = HeasyLocationService.getHeasyLocationClient().getCurrentLocation().getLatLng();
                LatLng destPosition = marker.getPosition();

                String routePlanMode = PreferenceUtil.getStringValue(serviceEngine.getContext(), "routePlanMode");
                Log.i(TAG, "routePlanMode=" + routePlanMode);

                switch (routePlanMode){
                    case "步行":
                        serviceEngine.getRoutePlanService().walkingSearch(currentPosition, destPosition);
                        break;
                    case "骑行":
                        serviceEngine.getRoutePlanService().bikingSearch(currentPosition, destPosition);
                        break;
                    case "驾车":
                        serviceEngine.getRoutePlanService().drivingSearch(currentPosition, destPosition);
                        break;
                    case "公交":
                        serviceEngine.getRoutePlanService().transitSearch(serviceEngine.getLocationService().city, currentPosition, destPosition);
                        break;
                    default:
                        serviceEngine.getRoutePlanService().walkingSearch(currentPosition, destPosition);
                }
                */
                getBaiduMap().hideInfoWindow();
            }
        });

        //关闭
        Button btnClose = (Button) view.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBaiduMap().hideInfoWindow();
            }
        });
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        setCurrentMarker(marker);

        //更新地图中心点
        updateMapStatus(marker.getPosition());

        initWindowView();

        InfoWindow infoWindow = new InfoWindow(view, marker.getPosition(), 350);
        getBaiduMap().showInfoWindow(infoWindow);

        return true;
    }

    public void addMarkerOverlay(FixedPointInfoBean bean){
        addMarkerOverlay(bean, BitmapDescriptorFactory.fromResource(R.drawable.icon_location_2));
    }

    /**
     * 添加Marker覆盖物
     * @param bean bean对象
     * @param icon 图标
     */
    public void addMarkerOverlay(FixedPointInfoBean bean, BitmapDescriptor icon){
        LatLng position = new LatLng(bean.getLatitude(), bean.getLongitude());

        Bundle bundle = new Bundle();
        bundle.putSerializable("bean", bean);

        MarkerOptions markerOptions = new MarkerOptions()
                .position(position)
                .icon(icon)
                .extraInfo(bundle);

        getBaiduMap().addOverlay(markerOptions);
    }

    /**
     * 更新Marker的Icon图标
     */
    public void updateMarkerIcon(){
        Bitmap bitmap = getViewBitmap(getMapPointView(bean.getLabel()));
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
        getCurrentMarker().setIcon(bitmapDescriptor);
    }

    /**
     * 添加文本覆盖物
     * @param text 文本内容
     * @param position 经纬坐标
     */
    public void addTextOverlay(String text, LatLng position){
        OverlayOptions textOptions = new TextOptions()
                .text(text)
                .bgColor(Color.YELLOW)
                .fontSize(27)
                .fontColor(Color.RED)
                .position(position);
        getBaiduMap().addOverlay(textOptions);
    }

    /**
     * 将view转换成Bitmap
     */
    public Bitmap getViewBitmap(View view){
        view.setDrawingCacheEnabled(true);
        //view宽和高的测量
        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        //view上下左右的位置
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();

        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        return bitmap;
    }

    /**
     * 获取自定义的定位图标
     * @param label 文本标签
     */
    public View getMapPointView(String label){
        LayoutInflater inflater = LayoutInflater.from(ServiceEngineFactory.getServiceEngine().getAndroidContext());
        View pointView = inflater.inflate(com.heasy.knowroute.R.layout.view_map_point, null);

        TextView textView = pointView.findViewById(R.id.tv_name);
        textView.setText(label);

        return pointView;
    }

}
