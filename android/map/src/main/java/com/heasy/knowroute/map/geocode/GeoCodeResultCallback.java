package com.heasy.knowroute.map.geocode;

import com.baidu.mapapi.model.LatLng;

public interface GeoCodeResultCallback {
    void execute(LatLng location, String error);
}
