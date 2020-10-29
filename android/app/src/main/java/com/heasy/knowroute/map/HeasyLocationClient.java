package com.heasy.knowroute.map;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.heasy.knowroute.ServiceEngineFactory;
import com.heasy.knowroute.action.ResponseBean;
import com.heasy.knowroute.action.ResponseCode;
import com.heasy.knowroute.core.utils.FastjsonUtil;
import com.heasy.knowroute.core.utils.StringUtil;
import com.heasy.knowroute.map.bean.LocationBean;
import com.heasy.knowroute.service.HttpService;
import com.heasy.knowroute.service.LoginService;
import com.heasy.knowroute.service.LoginServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;

/**
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
    }

    @Override
    public void handleReceiveLocation(BDLocation dbLocation, LocationBean locationBean) {
        try {
            logger.debug(FastjsonUtil.object2String(locationBean));
            queue.put(locationBean);
        } catch (InterruptedException ex) {
            logger.error("", ex);
        }
    }

    class Sender extends Thread{
        @Override
        public void run() {
            while (true) {
                try{
                    LocationBean locationBean = queue.take();

                    LoginService loginService = ServiceEngineFactory.getServiceEngine().getService(LoginServiceImpl.class);

                    //位置信息实时传输到后台
                    String url = "position/insert";
                    String jsonData = FastjsonUtil.toJSONString(
                            "id", StringUtil.getUUIDString(),
                            "userId", String.valueOf(loginService.getUserId()),
                            "longitude", String.valueOf(locationBean.getLongitude()),
                            "latitude", String.valueOf(locationBean.getLatitude()),
                            "address", locationBean.getAddress(),
                            "times", locationBean.getTime());

                    ResponseBean responseBean = HttpService.httpPost(ServiceEngineFactory.getServiceEngine().getHeasyContext(), url, jsonData);
                    if(responseBean.getCode() == ResponseCode.SUCCESS.code()){
                        logger.debug("位置信息已上传：" + FastjsonUtil.object2String(locationBean));
                    }else{
                        logger.error(HttpService.getFailureMessage(responseBean));
                    }
                }catch (Exception ex){
                    logger.error("", ex);
                }
            }
        }
    }

}
