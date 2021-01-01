package com.heasy.knowroute.map;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.heasy.knowroute.core.utils.FastjsonUtil;
import com.heasy.knowroute.core.utils.StringUtil;
import com.heasy.knowroute.map.bean.LocationBean;
import com.heasy.knowroute.service.backend.PositionAPI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * 获取定位信息并发送到系统后台存储
 * Created by Administrator on 2020/10/25.
 */
public class HeasyLocationClient extends AbstractLocationClient {
    private static final Logger logger = LoggerFactory.getLogger(HeasyLocationClient.class);
    private ArrayBlockingQueue<LocationBean> queue;
    private Sender sender;

    public HeasyLocationClient(Context context){
        super(context);
    }

    @Override
    public void init() {
        super.init();

        queue = new ArrayBlockingQueue<LocationBean>(20);

        sender = new Sender();
        sender.setDaemon(true);
        sender.start();

        logger.info("HeasyLocationClient inited!");
    }

    @Override
    public void destroy() {
        super.destroy();

        if(queue != null){
            queue.clear();
            queue = null;
        }

        try {
            if(sender != null){
                sender.interrupt();
            }
        } catch (Exception ex) {
            logger.error("", ex);
        }

        logger.info("HeasyLocationClient destroy!");
    }

    @Override
    public void handleReceiveLocation(BDLocation dbLocation, LocationBean locationBean) {
        try {
            logger.debug(FastjsonUtil.object2String(locationBean));
            queue.put(locationBean);
        } catch (Exception ex) {
            logger.error("", ex);
        }
    }

    /**
     * 定位信息上传
     */
    class Sender extends Thread{
        @Override
        public void run() {
            while (true) {
                try{
                    LocationBean locationBean = queue.take();
                    String result = PositionAPI.insert(locationBean);
                    if(StringUtil.isEmpty(result)){
                        logger.debug("位置信息已上传：" + FastjsonUtil.object2String(locationBean));
                    }else{
                        logger.error("位置信息无法上传：" + result);
                    }
                }catch (Exception ex){
                    logger.error("", ex);
                }
            }
        }
    }

}
