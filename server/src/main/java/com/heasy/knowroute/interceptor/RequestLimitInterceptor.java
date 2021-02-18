package com.heasy.knowroute.interceptor;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.heasy.knowroute.bean.ResponseCode;
import com.heasy.knowroute.bean.WebResponse;
import com.heasy.knowroute.common.RequestLimitAnnotation;
import com.heasy.knowroute.service.DataCacheService;
import com.heasy.knowroute.utils.JsonUtil;
import com.heasy.knowroute.utils.RequestUtil;

/**
 * 请求限制拦截器：防刷
 */
@Component
public class RequestLimitInterceptor extends HandlerInterceptorAdapter{
	private static Logger logger = LoggerFactory.getLogger(RequestLimitInterceptor.class);
	
	@Autowired
	private DataCacheService dataCacheService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if(handler instanceof HandlerMethod){
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			
			Method method = handlerMethod.getMethod();
			RequestLimitAnnotation methodAnnotation = method.getAnnotation(RequestLimitAnnotation.class);
			RequestLimitAnnotation classAnnotation = method.getDeclaringClass().getAnnotation(RequestLimitAnnotation.class);
			
			RequestLimitAnnotation requestLimit = methodAnnotation != null ? methodAnnotation : classAnnotation;
            if(requestLimit != null){
        		String remoteAddress = RequestUtil.getClientIp(request);        		
        		String limitKey = remoteAddress + ":" + handlerMethod.getBeanType().getName() + ":" + handlerMethod.getMethod().getName();
        		logger.debug("limitKey=" + limitKey);
        				
            	if(isLimit(request, requestLimit, limitKey)){
            		responseError(response, ResponseCode.REQUEST_LIMIT);
                    return false;
                }
            }
		}
		
		return true;
	}
	
	private boolean isLimit(HttpServletRequest request, RequestLimitAnnotation requestLimit, String limitKey){
		Long count = (Long)dataCacheService.get(limitKey);
		logger.debug("count=" + count);
        if(count == null) {
        	dataCacheService.set(limitKey, new AtomicLong(1), requestLimit.seconds());
        }else {
        	if(count.intValue() >= requestLimit.maxCount()) {
            	logger.debug("访问受限");
        		return true;
        	}else {
        		dataCacheService.incr(limitKey);
        	}
        }
		return false;
    }
	
	private void responseError(HttpServletResponse response, ResponseCode responseCode)throws Exception{
		response.setContentType("application/json;charset=UTF-8");
		OutputStream out = response.getOutputStream();
		
		String responseContent = JsonUtil.object2String(WebResponse.failure(responseCode));
		logger.debug(responseContent);
		
		out.write(responseContent.getBytes("UTF-8"));
        out.flush();
        out.close();
	}
}
