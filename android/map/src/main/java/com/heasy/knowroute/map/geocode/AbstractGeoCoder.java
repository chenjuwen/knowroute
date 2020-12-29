package com.heasy.knowroute.map.geocode;

import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

public abstract class AbstractGeoCoder implements OnGetGeoCoderResultListener {
    private GeoCoder geoCoder = null;

    public void init(){
        geoCoder = GeoCoder.newInstance();
        geoCoder.setOnGetGeoCodeResultListener(this);
    }

    public void destroy(){
        if(geoCoder != null){
            geoCoder.destroy();
            geoCoder = null;
        }
    }

    public GeoCoder getGeoCoder() {
        return geoCoder;
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

    }
}
