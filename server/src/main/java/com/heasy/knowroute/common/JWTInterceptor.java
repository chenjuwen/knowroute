package com.heasy.knowroute.common;

import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.heasy.knowroute.bean.ResponseCode;
import com.heasy.knowroute.bean.WebResponse;
import com.heasy.knowroute.utils.JWTUtil;
import com.heasy.knowroute.utils.JsonUtil;

/**
 * JWT拦截器
 */
@Component
public class JWTInterceptor extends HandlerInterceptorAdapter{
	private static Logger logger = LoggerFactory.getLogger(JWTInterceptor.class);
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, 
			Object handler) throws Exception {
		String requestURI = request.getRequestURI();
		logger.debug("requestURI=" + requestURI);
		
		//排除
		if(requestURI.indexOf("swagger") >= 0 || requestURI.indexOf("webjars") >= 0) {
			return true;
		}
		
		//客户端传来的token
		String token = request.getHeader("token");
		logger.debug("token=" + token);
		if(!JWTUtil.verify(token)) {
			responseError(response, ResponseCode.TOKEN_ERROR);
			return false;
		}
		
		return true;
	}
	
	private void responseError(HttpServletResponse response, ResponseCode responseCode)throws Exception{
		response.setContentType("application/json;charset=UTF-8");
		OutputStream out = response.getOutputStream();
		
		String responseContent = JsonUtil.object2String(WebResponse.failure(responseCode));
		out.write(responseContent.getBytes("UTF-8"));
        out.flush();
        out.close();
	}
	
}