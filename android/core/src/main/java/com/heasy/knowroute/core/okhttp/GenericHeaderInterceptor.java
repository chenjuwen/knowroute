package com.heasy.knowroute.core.okhttp;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 网络拦截器：添加公共请求头
 */
public class GenericHeaderInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request newRequest = originalRequest.newBuilder()
                .header("token", "knowroute_token_value")
                .header("front", "knowroute_app")
                .build();
        return chain.proceed(newRequest);
    }
}
