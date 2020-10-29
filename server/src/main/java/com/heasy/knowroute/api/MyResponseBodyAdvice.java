package com.heasy.knowroute.api;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import net.sf.json.JSONObject;

/**
 * 使用@ControllerAdvice和ResponseBodyAdvice接口拦截Controller方法默认返回参数，统一处理返回值/响应体
 */
@RestControllerAdvice
public class MyResponseBodyAdvice implements ResponseBodyAdvice<Object>{
	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}
	
	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
			ServerHttpResponse response) {
		if(body instanceof WebResponse) {
			System.out.println(JSONObject.fromObject(body).toString(2));
			return (WebResponse)body;
		}else {
			System.out.println(body);
			return body;
		}
	}
}
