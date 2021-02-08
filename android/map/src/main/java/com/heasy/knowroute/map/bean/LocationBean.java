package com.heasy.knowroute.map.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.baidu.mapapi.model.LatLng;

import java.io.Serializable;

/**
 * Created by Administrator on 2020/10/26.
 */
public class LocationBean implements Serializable {
    private String id;
    private int userId;
    private double longitude;
    private double latitude;
    private String address;
    private float radius = 0.0f;
    private String times;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

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

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    /**
     * 该字段不进行序列化
     */
    @JSONField(serialize = false)
    public LatLng getLatLng(){
        LatLng latLng = new LatLng(this.latitude, this.longitude);
        return latLng;
    }
}
