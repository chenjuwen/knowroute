package com.heasy.knowroute.map.geocode;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.heasy.knowroute.core.utils.StringUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 经纬坐标转地址
 */
public class DefaultGetReverseGeoCode extends AbstractGeoCoder {
    private static final Logger logger = LoggerFactory.getLogger(DefaultGetReverseGeoCode.class);
    private ReverseGeoCodeResultCallback reverseGeoCodeResultCallback;

    public void getReverseGeoCode(LatLng location, ReverseGeoCodeResultCallback reverseGeoCodeResultCallback){
        this.reverseGeoCodeResultCallback = reverseGeoCodeResultCallback;

        ReverseGeoCodeOption reverseGeoCodeOption = new ReverseGeoCodeOption();
        reverseGeoCodeOption.location(location);
        reverseGeoCodeOption.newVersion(1); //是否返回新数据
        reverseGeoCodeOption.radius(500); //POI召回半径，允许设置区间为0-1000米

        getGeoCoder().reverseGeoCode(reverseGeoCodeOption);
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        logger.debug(reverseGeoCodeResult.error.name());
        if(reverseGeoCodeResult != null && reverseGeoCodeResult.error == SearchResult.ERRORNO.NO_ERROR){
            String address = reverseGeoCodeResult.getAddress();
            if(StringUtil.isNotEmpty(reverseGeoCodeResult.getSematicDescription())){
                address += reverseGeoCodeResult.getSematicDescription();
            }
            logger.debug(address);

            LatLng location = reverseGeoCodeResult.getLocation();
            logger.debug(location.longitude + "," + location.latitude);

            if(reverseGeoCodeResultCallback != null){
                reverseGeoCodeResultCallback.execute(address, location, null);
            }
        }else{
            if(reverseGeoCodeResultCallback != null){
                reverseGeoCodeResultCallback.execute(null, null, reverseGeoCodeResult.error.name());
            }
        }
    }

}
