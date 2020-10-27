package com.heasy.knowroute.service;

import com.heasy.knowroute.action.ResponseBean;
import com.heasy.knowroute.action.ResponseCode;
import com.heasy.knowroute.core.HeasyContext;
import com.heasy.knowroute.core.utils.FastjsonUtil;
import com.heasy.knowroute.core.utils.StringUtil;
import com.heasy.knowroute.http.OkHttpClientHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.Request;

/**
 * Created by Administrator on 2020/10/5.
 */
public class HttpService {
    private static final Logger logger = LoggerFactory.getLogger(HttpService.class);
    private static OkHttpClientHelper okHttpClientHelper;
    private  static  String apiAddress;

    private static OkHttpClientHelper getOkHttpClientHelper(){
        if(okHttpClientHelper == null){
            okHttpClientHelper = new OkHttpClientHelper();
            okHttpClientHelper.build();
        }
        return okHttpClientHelper;
    }

    /**
     * 获取服务端的API根地址
     * @param heasyContext
     * @return
     */
    public static String getApiAddress(HeasyContext heasyContext) {
        if(StringUtil.isEmpty(apiAddress)){
            apiAddress = heasyContext.getServiceEngine().getConfigurationService().getConfigBean().getApiAddress();
            logger.debug("apiAddress=" + apiAddress);
        }
        return apiAddress;
    }

    public static ResponseBean httpGet(HeasyContext heasyContext, String requestUrl){
        String result = getOkHttpClientHelper().synGet(getApiAddress(heasyContext) + requestUrl);

        if(StringUtil.isEmpty(result)){
            return ResponseBean.failure(ResponseCode.SERVICE_CALL_ERROR);
        }

        ResponseBean responseBean = FastjsonUtil.string2JavaBean(result, ResponseBean.class);
        logger.debug("httpGet response:" + FastjsonUtil.object2String(responseBean));
        return responseBean;
    }

    public static ResponseBean httpPost(Request request){
        try {
            String result = getOkHttpClientHelper().post(request);
            logger.debug("httpPost response:" + result);

            if(StringUtil.isNotEmpty(result)){
                ResponseBean responseBean = FastjsonUtil.string2JavaBean(result, ResponseBean.class);
                return responseBean;
            }

        }catch (Exception ex){
            logger.error("", ex);
        }
        return ResponseBean.failure(ResponseCode.SERVICE_CALL_ERROR);
    }

    public static ResponseBean httpPost(HeasyContext heasyContext, String requestUrl, String jsonData){
        try {
            String result = getOkHttpClientHelper().postJSON(getApiAddress(heasyContext) + requestUrl, jsonData);
            logger.debug("httpPost response:" + result);

            if(StringUtil.isNotEmpty(result)){
                ResponseBean responseBean = FastjsonUtil.string2JavaBean(result, ResponseBean.class);
                return responseBean;
            }

        }catch (Exception ex){
            logger.error("", ex);
        }
        return ResponseBean.failure(ResponseCode.SERVICE_CALL_ERROR);
    }

    public static String getFailureMessage(ResponseBean responseBean){
        String result = responseBean.getData() != null ? (String)responseBean.getData() : responseBean.getMessage();
        return result;
    }

}
