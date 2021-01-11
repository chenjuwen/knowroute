package com.heasy.knowroute.common;

import java.io.OutputStream;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.heasy.knowroute.api.ResponseCode;
import com.heasy.knowroute.api.WebResponse;
import com.heasy.knowroute.utils.JsonUtil;

/**
 * API防刷拦截器
 */
@Component
public class AccessControlInterceptor extends HandlerInterceptorAdapter{
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, 
			Object handler) throws Exception {
//		Enumeration<String> e = request.getHeaderNames();
//		while(e.hasMoreElements()) {
//			String name = e.nextElement();
//			System.out.println(name + "=" + request.getHeader(name));
//		}
		
		System.out.println("user-agent=" + request.getHeader("user-agent"));
		
//		System.out.println("Method: " + request.getMethod());
//		System.out.println("QueryString: " + request.getQueryString());
//		System.out.println("RequestURI: " + request.getRequestURI());
		
		if(handler instanceof HandlerMethod){
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			System.out.println(handlerMethod.getBeanType().getName());
			System.out.println(handlerMethod.getMethod().getName());
			
			//获取方法注解
			OnlyAdminAnnotation onlyAdminAnnotation = handlerMethod.getMethodAnnotation(OnlyAdminAnnotation.class);
			if(onlyAdminAnnotation != null){
				Object obj = request.getSession().getAttribute(Constants.SESSION_USER_KEY);
				if(obj == null) {
					response.setContentType("application/json;charset=UTF-8");
					OutputStream out = response.getOutputStream();
					
					String responseContent = JsonUtil.object2String(WebResponse.failure(ResponseCode.NO_ACCESS));
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
