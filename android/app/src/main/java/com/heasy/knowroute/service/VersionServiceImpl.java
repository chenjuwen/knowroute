package com.heasy.knowroute.service;

import com.heasy.knowroute.bean.ResponseBean;
import com.heasy.knowroute.bean.ResponseCode;
import com.heasy.knowroute.bean.VersionInfoBean;
import com.heasy.knowroute.core.service.AbstractService;
import com.heasy.knowroute.core.utils.VersionUtil;
import com.heasy.knowroute.service.common.HttpService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2020/9/26.
 */
public class VersionServiceImpl extends AbstractService implements VersionService {
    private static final Logger logger = LoggerFactory.getLogger(VersionServiceImpl.class);

    @Override
    public void init() {
        successInit = true;
    }

    @Override
    public VersionInfoBean getVersionInfo() {
        VersionInfoBean versionInfoBean = new VersionInfoBean();
        try {
            //获取最新的版本号
            String lastedVersion = "";
            String url = "version/lasted";
            ResponseBean responseBean = HttpService.get(getHeasyContext(), url);
            if (responseBean.getCode() == ResponseCode.SUCCESS.code()) {
                lastedVersion = (String) responseBean.getData();
                versionInfoBean.setLastedVersion(lastedVersion);
            }

            //当前版本
            String currentVersion = VersionUtil.getVersionName(getHeasyContext().getServiceEngine().getAndroidContext());
            versionInfoBean.setCurrentVersion(currentVersion);
            logger.debug("lastedVersion: " + lastedVersion + ", currentVersion: " + currentVersion);

            if (lastedVersion.compareTo(currentVersion) > 0) {
                String downloadURL = HttpService.getApiRootAddress(getHeasyContext()) + "download?filename=knowroute-" + lastedVersion + ".apk";
                logger.debug(downloadURL);
                versionInfoBean.setLastedVersionURL(downloadURL);
            }
        }catch (Exception ex){
            logger.error("", ex);
        }
        return versionInfoBean;
    }

}
