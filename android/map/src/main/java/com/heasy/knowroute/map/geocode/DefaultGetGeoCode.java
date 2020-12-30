package com.heasy.knowroute.map.geocode;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;

/**
 * 地址转经纬坐标
 */
public class DefaultGetGeoCode extends AbstractGeoCoder {
    private GeoCodeResultCallback geoCodeResultCallback;

    public void getGeoCode(String city, String address, GeoCodeResultCallback geoCodeResultCallback){
        this.geoCodeResultCallback = geoCodeResultCallback;

        GeoCodeOption geoCodeOption = new GeoCodeOption();
        geoCodeOption.city(city);
        geoCodeOption.address(address);

        getGeoCoder().geocode(geoCodeOption);
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
        if(geoCodeResult != null && geoCodeResult.error == SearchResult.ERRORNO.NO_ERROR){
            LatLng location = geoCodeResult.getLocation();
            if(geoCodeResultCallback != null){
                geoCodeResultCallback.execute(location, null);
            }
        }else{
            if(geoCodeResultCallback != null){
                geoCodeResultCallback.execute(null, geoCodeResult.error.name());
            }
        }
    }

}
