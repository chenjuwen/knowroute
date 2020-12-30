package com.heasy.knowroute.map.geocode;

import com.baidu.mapapi.model.LatLng;

public interface ReverseGeoCodeResultCallback {
    void execute(String address, LatLng location, String error);
}
