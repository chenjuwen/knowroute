package com.heasy.knowroute.common;

import java.io.IOException;
import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import com.heasy.knowroute.utils.JsonUtil;

@ControllerAdvice
public class GlobalRequestBodyAdvice implements RequestBodyAdvice{
    private static final Logger logger = LoggerFactory.getLogger(GlobalRequestBodyAdvice.class);
    
	@Override
	public boolean supports(MethodParameter methodParameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}
	
	@Override
	public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
		return inputMessage;
	}
	
	@Override
	public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType) {
		logger.info("-----afterBodyRead-----");
		logger.info(parameter.getDeclaringClass().getName() + "::" + parameter.getMethod().getName());
		
		if(body instanceof String) {
			logger.info(body.toString());
		}else {
			logger.info("\nRequestBody:\n"+JsonUtil.object2String(body));
		}
		
		return body;
	}
	
	@Override
	public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter,
			Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
		return body;
	}
}