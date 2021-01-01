package com.heasy.knowroute.service;

import com.heasy.knowroute.bean.ResponseBean;
import com.heasy.knowroute.bean.ResponseCode;
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
    public String getCurrentVersion() {
        return VersionUtil.getVersionName(getHeasyContext().getServiceEngine().getAndroidContext());
    }

    @Override
    public String getLastedVersion() {
        try {
            String url = "version/lasted";
            ResponseBean responseBean = HttpService.get(getHeasyContext(), url);
            if (responseBean.getCode() == ResponseCode.SUCCESS.code()) {
                String lastedVersion = (String) responseBean.getData();
                return lastedVersion;
            }
        }catch (Exception ex){
            logger.error("", ex);
        }
        return "";
    }

    @Override
    public String getLastedVersionDownloadURL() {
        String lastedVersionDownloadURL = "";
        try {
            String lastedVersion = getLastedVersion();
            String currentVersion = getCurrentVersion();
            logger.debug("lastedVersion: " + lastedVersion + ", currentVersion: " + currentVersion);

            if (lastedVersion.compareTo(currentVersion) > 0) {
                String downloadURL = HttpService.getApiRootAddress(getHeasyContext()) + "download?filename=knowroute-" + lastedVersion + ".apk";
                logger.debug(downloadURL);
                lastedVersionDownloadURL = downloadURL;
            }
        }catch (Exception ex){
            logger.error("", ex);
        }
        return lastedVersionDownloadURL;
    }

}
