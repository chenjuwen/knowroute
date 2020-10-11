package com.heasy.knowroute.core.service;

import com.heasy.knowroute.core.configuration.ConfigBean;
import com.heasy.knowroute.core.configuration.DefaultConfigLoader;

/**
 * Created by Administrator on 2018/11/10.
 */
public class DefaultConfigurationService extends AbstractService implements ConfigurationService {
    private ConfigBean configBean;

    @Override
    public void init() {
        initConfigBean();
        successInit = true;
    }

    private void initConfigBean(){
        DefaultConfigLoader loader = new DefaultConfigLoader();
        configBean = loader.loadFromAssets(getHeasyContext().getServiceEngine().getAndroidContext(), ConfigBean.CONFIG_FILE_NAME);
    }

    @Override
    public void unInit() {
        super.unInit();
        configBean = null;
    }

    @Override
    public ConfigBean getConfigBean() {
        return configBean;
    }

}
