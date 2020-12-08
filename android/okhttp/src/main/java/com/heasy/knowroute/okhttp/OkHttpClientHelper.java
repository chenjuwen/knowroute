package com.heasy.knowroute.okhttp;

import com.heasy.knowroute.core.utils.FileUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/2/12.
 */
public class OkHttpClientHelper {
    private static Logger logger = LoggerFactory.getLogger(OkHttpClientHelper.class);

    private static final int DEFAULT_CONNECT_TIMEOUT_MILLSECONDS = 10000;
    private static final int DEFAULT_READ_TIMEOUT_MILLSECONDS = 60000;

    private OkHttpClient.Builder builder;
    private OkHttpClient okHttpClient;

    public OkHttpClientHelper(){
        builder = new OkHttpClient.Builder();

        DefaultCookieJar cookieJar = new DefaultCookieJar(new MemoryCookieStore());

        builder.connectTimeout(DEFAULT_CONNECT_TIMEOUT_MILLSECONDS, TimeUnit.MILLISECONDS)
        	    .readTimeout(DEFAULT_READ_TIMEOUT_MILLSECONDS, TimeUnit.MILLISECONDS)
                .cookieJar(cookieJar);
    }
    
    public OkHttpClientHelper connectTimeout(long timeout, TimeUnit unit){
        builder.connectTimeout(timeout, unit);
        return this;
    }

    public OkHttpClientHelper pingInterval(long timeout, TimeUnit unit){
        builder.pingInterval(timeout, unit);
        return this;
    }

    public OkHttpClientHelper readTimeout(long timeout, TimeUnit unit){
        builder.readTimeout(timeout, unit);
        return this;
    }

    public OkHttpClientHelper writeTimeout(long timeout, TimeUnit unit){
        builder.writeTimeout(timeout, unit);
        return this;
    }

    public OkHttpClientHelper cookieJar(CookieJar cookieJar){
        builder.cookieJar(cookieJar);
        return this;
    }

    public OkHttpClientHelper addInterceptor(Interceptor interceptor){
        builder.addInterceptor(interceptor);
        return this;
    }

    public OkHttpClientHelper addNetworkInterceptor(Interceptor interceptor){
        builder.addNetworkInterceptor(interceptor);
        return this;
    }
    
    public void build(){
    	okHttpClient = builder.build();
    }

    /**
     * 同步get
     * @param url
     * @return
     */
    public String synGet(String url)throws Exception{
    	String result = null;
        Response response = null;
        try {
            Request request = createGetRequest(url);
            response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                result = response.body().string();
            }
        }finally {
    	    if(response != null){
                response.close();
            }
        }
        return result;
    }

    public String synGet(String url, Map<String, String> params)throws Exception{
        StringBuilder sb = new StringBuilder(url);

        int i = 0;
        if(params != null && params.size() > 0){
            sb.append("?");
            for(String key : params.keySet()) {
                String value = params.get(key);
                if (i != 0) {
                    sb.append('&');
                }

                sb.append(key);
                sb.append('=');
                sb.append(URLEncoder.encode(value, "utf-8"));

                i++;
            }
        }

        return synGet(sb.toString());
    }
    
    public byte[] synGetBytes(String url)throws Exception{
    	byte[] result = null;
        Response response = null;

        try{
            Request request = createGetRequest(url);
            response = okHttpClient.newCall(request).execute();
            if(response.isSuccessful()){
                result = response.body().bytes();
            }
        }finally {
            if(response != null){
                response.close();
            }
        }
        return result;
    }

    /**
     * 异步get
     * @param url
     * @param listener
     */
    public void asynGet(String url, HttpClientListener listener){
        Request request = createGetRequest(url);
        okHttpClient.newCall(request).enqueue(new DefaultCallback(listener));
    }
    
    /**
     * 异步post json格式内容
     * @param url
     * @param jsonData
     * @param listener
     */
    public void postJSON(String url, String jsonData, HttpClientListener listener){
		MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    	RequestBody body = FormBody.create(JSON, jsonData);
        Request request = createPostRequest(url, body);
        okHttpClient.newCall(request).enqueue(new DefaultCallback(listener));
    }

    /**
     * post
     */
    public String post(Request request)throws Exception{
        Response response = null;
        try{
            response = okHttpClient.newCall(request).execute();
            if(response.isSuccessful()){
                String value = response.body().string();
                return value;
            }else{
                logger.error(response.message());
                return "";
            }
        }finally {
            if(response != null){
                response.close();
            }
        }
    }

    /**
     * 同步post json格式内容
     * @param url
     * @param jsonData
     * @return
     * @throws Exception
     */
    public String postJSON(String url, String jsonData)throws Exception{
        Response response = null;
        try{
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = FormBody.create(JSON, jsonData);
            Request request = createPostRequest(url, body);
            response = okHttpClient.newCall(request).execute();

            if(response.isSuccessful()){
                String value = response.body().string();
                return value;
            }else{
                logger.error(response.message());
                return "";
            }
        }finally {
            if(response != null){
                response.close();
            }
        }
    }
    
    public void download(String url, final String destFilePath){
    	Request request = createGetRequest(url);
    	okHttpClient.newCall(request).enqueue(new DefaultCallback(new HttpClientListener() {
			@Override
			public void onReponse(Response response) {
				if(response.isSuccessful()){
					InputStream inputStream = response.body().byteStream();
					FileUtil.writeFile(inputStream, destFilePath);
		            logger.info("file download to: " + destFilePath);
				}
			}
		}));
    }

    private Request createPostRequest(String url, RequestBody body){
        RequestBuilder builder = new RequestBuilder().url(url).requestBody(body);
        return builder.build();
    }

    private Request createGetRequest(String url){
        return new Request.Builder().url(url).build();
    }

}
