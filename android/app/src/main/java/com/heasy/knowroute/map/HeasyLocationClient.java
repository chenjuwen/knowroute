package com.heasy.knowroute.map;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.heasy.knowroute.core.DefaultDaemonThread;
import com.heasy.knowroute.core.service.ServiceEngineFactory;
import com.heasy.knowroute.core.utils.FastjsonUtil;
import com.heasy.knowroute.core.utils.StringUtil;
import com.heasy.knowroute.map.bean.LocationBean;
import com.heasy.knowroute.service.LoginService;
import com.heasy.knowroute.service.LoginServiceImpl;
import com.heasy.knowroute.service.backend.PositionAPI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 获取定位信息并发送到系统后台存储
 * Created by Administrator on 2020/10/25.
 */
public class HeasyLocationClient extends AbstractLocationClient {
    private static final Logger logger = LoggerFactory.getLogger(HeasyLocationClient.class);
    private Lock queueLock = new ReentrantLock();
    private LinkedList<LocationBean> queue;
    private Sender sender;
    private LoginService loginService;

    public HeasyLocationClient(Context context){
        super(context);
    }

    @Override
    public void init() {
        super.init();

        queue = new LinkedList<LocationBean>();

        sender = new Sender();
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
        queueLock.lock();
        try {
            locationBean.setId(StringUtil.getUUIDString());
            locationBean.setUserId(getLoginService().getUserId());
            logger.debug(FastjsonUtil.object2String(locationBean));

            queue.addLast(locationBean);
            logger.debug("addLast 队列长度：" + queue.size());
        } catch (Exception ex) {
            logger.error("", ex);
        } finally {
            queueLock.unlock();
        }
    }

    private LoginService getLoginService() {
        if(loginService == null){
            loginService = ServiceEngineFactory.getServiceEngine().getService(LoginServiceImpl.class);
        }
        return loginService;
    }

    /**
     * 定位信息上传
     */
    class Sender extends DefaultDaemonThread {
        @Override
        public void run() {
            while (true) {
                int count = 0;
                List<LocationBean> dataList = null;

                queueLock.lock();
                try{
                    if (queue.size() >=5) {
                        count = 5;
                    } else {
                        count = queue.size();
                    }
                    logger.debug("count=" + count);

                    dataList = queue.subList(0, count);
                    logger.debug("subList 长度：" + dataList.size());
                } catch (Exception ex){
                    logger.error("", ex);
                } finally {
                    queueLock.unlock();
                }

                if(dataList != null && dataList.size() > 0) {
                    try{
                        String result = PositionAPI.insert(dataList);
                        if (StringUtil.isEmpty(result)) {
                            logger.debug("位置信息已上传：" + FastjsonUtil.object2ArrayString(dataList));

                            queueLock.lock();
                            try {
                                queue.subList(0, count).clear();
                                logger.debug("clear 队列长度：" + queue.size());
                            } catch (Exception ex) {
                                logger.error("", ex);
                            } finally {
                                queueLock.unlock();
                            }

                        } else {
                            logger.error("位置信息无法上传：" + result);
                        }
                    } catch (Exception ex){
                        logger.error("", ex);
                    }
                }


                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException ex) {
                    logger.error(ex.toString());
                }
            }
        }
    }

}
