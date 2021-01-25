package com.heasy.knowroute.core.okhttp.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * 请求-响应日志拦截器
 */
public class LogInterceptor implements Interceptor {
    private static Logger logger = LoggerFactory.getLogger(LogInterceptor.class);
    @Override
    public Response intercept(Chain chain) throws IOException {
        long start = System.currentTimeMillis();

        String url = null;
        String mothod = null;
        String requestBody = null;
        Response response = null;
        String responseCode = null;
        String responseBody = null;
        String errorMessage = null;

        try {
            Request request = chain.request();
            url = request.url().toString();
            mothod = request.method().toLowerCase();
            requestBody = getRequestBody(request);

            response = chain.proceed(request);
            responseCode = String.valueOf(response.code());
            responseBody = response.body().string();

            MediaType mediaType = response.body().contentType();
            response = response.newBuilder().body(ResponseBody.create(mediaType, responseBody)).build();

        } catch (Exception ex) {
            errorMessage = ex.toString();
           logger.error("", ex);
        } finally {
            long end = System.currentTimeMillis();
            String duration = String.valueOf(end - start);
            logger.info("\n responseTime={}\n method={}\n requestUrl={}\n params={}\n responseCode={}\n result={}\n errorMessage={}",
                                duration, mothod, url, requestBody, responseCode, responseBody, errorMessage);
        }
        return response;
    }

    private String getRequestBody(Request request) {
        String requestContent = "";
        if (request == null) {
            return requestContent;
        }

        RequestBody requestBody = request.body();
        if (requestBody == null) {
            return requestContent;
        }

        try {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            Charset charset = Charset.forName("utf-8");
            requestContent = buffer.readString(charset);
        } catch (Exception ex) {
            logger.error(ex.toString());
        }
        return requestContent;
    }

}
