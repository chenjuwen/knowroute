package com.heasy.knowroute.core.configuration;

import com.heasy.knowroute.core.service.DefaultConfigurationService;
import com.heasy.knowroute.core.utils.Dom4jUtil;

import org.dom4j.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2018/12/13.
 */
public class DefaultConfigLoader extends AbstractConfigLoader<ConfigBean> {
    private static Logger logger = LoggerFactory.getLogger(DefaultConfigurationService.class);

    @Override
    protected ConfigBean parseConfigFile(Document document) {
        try {
            String ROOT_PATH = "/root/";
            ConfigBean configBean = new ConfigBean();

            configBean.setSdcardRootPath(Dom4jUtil.getNodeText(document, ROOT_PATH + "sdcardRootPath"));

            configBean.setActionBasePackage(Dom4jUtil.getNodeText(document, ROOT_PATH + "actionBasePackage"));
            configBean.setServiceBasePackage(Dom4jUtil.getNodeText(document, ROOT_PATH + "serviceBasePackage"));

            configBean.setWebviewLoadBasePath(Dom4jUtil.getNodeText(document, ROOT_PATH + "webviewLoadBasePath"));
            configBean.setMainPage(Dom4jUtil.getNodeText(document, ROOT_PATH + "mainPage"));
            configBean.setApiAddress(Dom4jUtil.getNodeText(document, ROOT_PATH + "apiAddress"));

            return configBean;

        }catch(Exception ex){
            logger.error("failed to execute DefaultConfigLoader", ex);
        }
        return null;
    }

}
