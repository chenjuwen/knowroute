package com.heasy.knowroute.map.bean;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by Administrator on 2020/10/26.
 */
public class LocationBean {
    private double longitude;
    private double latitude;
    private String address;
    private String time;

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public LatLng getLatLng(){
        LatLng latLng = new LatLng(this.latitude, this.longitude);
        return latLng;
    }
}
