package com.heasy.knowroute.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.heasy.knowroute.common.CustomHttpServletRequestWrapper;
import com.heasy.knowroute.utils.RequestUtil;

/**
 * log拦截器
 */
@Component
public class LogInterceptor extends HandlerInterceptorAdapter{
	private static Logger logger = LoggerFactory.getLogger(LogInterceptor.class);
	
	//方法被调用前执行
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, 
			Object handler) throws Exception {
		String userAgent = request.getHeader("user-agent");
		String method = request.getMethod();
		String queryString = request.getQueryString();
		String requestURI = request.getRequestURI();
		String front = request.getHeader("front");
		String remoteAddress = RequestUtil.getClientIp(request);
		
		String requestBody = "";
		if(request instanceof CustomHttpServletRequestWrapper) {
			requestBody = ((CustomHttpServletRequestWrapper)request).getRequestBody();
		}
		
		logger.debug("\n userAgent = {} \n method = {} \n queryString = {} \n requestBody = {} \n requestURI = {} \n front = {} \n remoteAddress = {}", 
				userAgent, method, queryString, requestBody, requestURI, front, remoteAddress);
		
		//返回true，则继续调用下一个拦截器
		return true;
	}	
}
