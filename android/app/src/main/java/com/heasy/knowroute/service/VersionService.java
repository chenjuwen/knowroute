package com.heasy.knowroute.service;

import com.heasy.knowroute.bean.VersionInfoBean;
import com.heasy.knowroute.core.service.Service;

/**
 * Created by Administrator on 2020/9/26.
 */
public interface VersionService extends Service{
    VersionInfoBean getVersionInfo();
}
