package com.heasy.knowroute.api;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

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
		System.out.println("------------------");
		System.out.println("MyResponseBodyAdvice >> beforeBodyWrite");
		System.out.println("------------------");
		
		if(body instanceof WebResponse) {
			return (WebResponse)body;
		}else {
			return body;
		}
	}
}
