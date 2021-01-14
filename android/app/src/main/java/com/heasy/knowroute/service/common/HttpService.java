package com.heasy.knowroute.service.common;

import com.heasy.knowroute.bean.ResponseBean;
import com.heasy.knowroute.bean.ResponseCode;
import com.heasy.knowroute.core.HeasyContext;
import com.heasy.knowroute.core.okhttp.interceptor.GenericHeaderInterceptor;
import com.heasy.knowroute.core.okhttp.OkHttpClientHelper;
import com.heasy.knowroute.core.okhttp.RequestBuilder;
import com.heasy.knowroute.core.okhttp.interceptor.LogInterceptor;
import com.heasy.knowroute.core.okhttp.interceptor.RetryInterceptor;
import com.heasy.knowroute.core.utils.FastjsonUtil;
import com.heasy.knowroute.core.utils.StringUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Map;

import okhttp3.Request;

/**
 * Created by Administrator on 2020/10/5.
 */
public class HttpService {
    private static final Logger logger = LoggerFactory.getLogger(HttpService.class);
    private static OkHttpClientHelper okHttpClientHelper;
    private  static  String apiRootAddress;

    private static OkHttpClientHelper getOkHttpClientHelper(){
        if(okHttpClientHelper == null){
            okHttpClientHelper = new OkHttpClientHelper();
            okHttpClientHelper
                    .addInterceptor(new RetryInterceptor())
                    .addNetworkInterceptor(new LogInterceptor())
                    .addNetworkInterceptor(new GenericHeaderInterceptor())
                    .build();
        }
        return okHttpClientHelper;
    }

    /**
     * 获取服务端的API根地址
     * @param heasyContext
     * @return
     */
    public static String getApiRootAddress(HeasyContext heasyContext) {
        if(StringUtil.isEmpty(apiRootAddress)){
            apiRootAddress = heasyContext.getServiceEngine().getConfigurationService().getConfigBean().getApiAddress();
            logger.debug("apiRootAddress=" + apiRootAddress);
        }
        return apiRootAddress;
    }

    public static ResponseBean get(HeasyContext heasyContext, String requestUrl){
        try {
            String result = getOkHttpClientHelper().synGet(getApiRootAddress(heasyContext) + requestUrl);

            if (StringUtil.isNotEmpty(result)) {
                ResponseBean responseBean = FastjsonUtil.string2JavaBean(result, ResponseBean.class);
                //logger.debug("httpGet response:" + FastjsonUtil.object2String(responseBean));
                return responseBean;
            }

        }catch (SocketTimeoutException | SocketException | UnknownHostException ex){
            return ResponseBean.failure(ResponseCode.NETWORK_ERROR);
        }catch (Exception ex){
            logger.error("", ex);
        }
        return ResponseBean.failure(ResponseCode.SERVICE_CALL_ERROR);
    }

    public static ResponseBean post(String url, Map<String,String> params){
        try {
            Request request = new RequestBuilder()
                    .url(url)
                    .setFormParamMap(params)
                    .build();

            String result = getOkHttpClientHelper().post(request);
            //logger.debug("httpPost response:" + result);

            if(StringUtil.isNotEmpty(result)){
                ResponseBean responseBean = FastjsonUtil.string2JavaBean(result, ResponseBean.class);
                return responseBean;
            }

        }catch (SocketTimeoutException | SocketException | UnknownHostException ex){
            return ResponseBean.failure(ResponseCode.NETWORK_ERROR);
        }catch (Exception ex){
            logger.error("", ex);
        }
        return ResponseBean.failure(ResponseCode.SERVICE_CALL_ERROR);
    }

    public static ResponseBean postJson(HeasyContext heasyContext, String requestUrl, String jsonData){
        try {
            String result = getOkHttpClientHelper().postJSON(getApiRootAddress(heasyContext) + requestUrl, jsonData);
            //logger.debug("httpPost response:" + result);

            if(StringUtil.isNotEmpty(result)){
                ResponseBean responseBean = FastjsonUtil.string2JavaBean(result, ResponseBean.class);
                return responseBean;
            }

        }catch (SocketTimeoutException | SocketException | UnknownHostException ex){
            return ResponseBean.failure(ResponseCode.NETWORK_ERROR);
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
