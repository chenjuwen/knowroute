package com.heasy.knowroute.common;

import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.heasy.knowroute.api.ResponseCode;
import com.heasy.knowroute.utils.JsonUtil;

/**
 * API防刷拦截器
 */
@Component
public class AccessControlInterceptor extends HandlerInterceptorAdapter{
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, 
			Object handler) throws Exception {
		if(handler instanceof HandlerMethod){
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			
			//获取方法注解
			OnlyAdminAnnotation onlyAdminAnnotation = handlerMethod.getMethodAnnotation(OnlyAdminAnnotation.class);
			if(onlyAdminAnnotation != null){
				Object obj = request.getSession().getAttribute(Constants.SESSION_USER_KEY);
				if(obj == null) {
					response.setContentType("application/json;charset=UTF-8");
					OutputStream out = response.getOutputStream();
					String responseContent = JsonUtil.toJSONString(String.valueOf(ResponseCode.NO_ACCESS.code()), ResponseCode.NO_ACCESS.message());
					out.write(responseContent.getBytes("UTF-8"));
	                out.flush();
	                out.close();
	            	return false;
				}
			}
		}
		
		return true;
	}
}
