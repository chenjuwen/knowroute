package com.heasy.knowroute.core.okhttp.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RetryInterceptor implements Interceptor {
    private static Logger logger = LoggerFactory.getLogger(RetryInterceptor.class);
    private int maxRetryCount = 3;
    private int currentRetryCount = 0;

    public RetryInterceptor(){

    }

    public RetryInterceptor(int maxRetryCount){
        this.maxRetryCount = maxRetryCount;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        return retry(chain);
    }

    private Response retry(Chain chain){
        Response response = null;
        Request request = chain.request();
        try {
            response = chain.proceed(request);
            while (!response.isSuccessful() && currentRetryCount < maxRetryCount) {
                currentRetryCount++;
                logger.debug("重试次数：" + currentRetryCount);
                response = retry(chain);
            }
        } catch (Exception ex){
            logger.error(ex.toString());
        }
        return response;
    }

}
