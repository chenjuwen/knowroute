package com.heasy.knowroute.map;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.Poi;
import com.heasy.knowroute.core.utils.FastjsonUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Administrator on 2020/10/25.
 */
public class HeasyLocationClient extends AbstractLocationClient {
    private static final Logger logger = LoggerFactory.getLogger(HeasyLocationClient.class);

    public HeasyLocationClient(Context context){
        super(context);
    }

    @Override
    public void handleReceiveLocation(BDLocation location) {
        //poi list
        List<Poi> list = location.getPoiList();
        if(list != null){
            for(int i=0; i<list.size(); i++){
                Poi poi = list.get(i);
                logger.debug(poi.getId() + ", " + poi.getName() + ", " + poi.getRank());
            }
        }

        logger.debug(FastjsonUtil.object2String(location.getAddress()));
    }
}
