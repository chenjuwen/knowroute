package com.heasy.knowroute.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * log拦截器
 */
@Component
public class LogInterceptor extends HandlerInterceptorAdapter{
	private static Logger logger = LoggerFactory.getLogger(LogInterceptor.class);
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, 
			Object handler) throws Exception {
//		Enumeration<String> e = request.getHeaderNames();
//		while(e.hasMoreElements()) {
//			String name = e.nextElement();
//			logger.debug(name + "=" + request.getHeader(name));
		
		logger.debug("user-agent=" + request.getHeader("user-agent"));
		logger.debug("Method: " + request.getMethod());
		logger.debug("QueryString: " + request.getQueryString());
		logger.debug("RequestURI: " + request.getRequestURI());
		logger.debug("front=" + request.getHeader("front"));
		
		return true;
	}	
}
