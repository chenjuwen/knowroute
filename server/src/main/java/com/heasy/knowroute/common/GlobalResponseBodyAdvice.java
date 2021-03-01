package com.heasy.knowroute.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.heasy.knowroute.bean.WebResponse;
import com.heasy.knowroute.utils.JsonUtil;

/**
 * 响应统一处理：
 * 		使用@ControllerAdvice和ResponseBodyAd	vice接口拦截Controller方法默认返回参数，统一处理返回值/响应体
 */
@RestControllerAdvice
public class GlobalResponseBodyAdvice implements ResponseBodyAdvice<Object>{
	private static Logger logger = LoggerFactory.getLogger(GlobalResponseBodyAdvice.class);
	
	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}
	
	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
			ServerHttpResponse response) {
		if(body instanceof WebResponse) {
			logger.debug("ResponseBody = " + JsonUtil.object2String(body));
		}else {
			if(body != null){
				logger.debug("ResponseBody = " + body.toString());
			}
		}
		return body;
	}
}
