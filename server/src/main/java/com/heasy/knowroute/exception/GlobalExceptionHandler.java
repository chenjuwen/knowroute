package com.heasy.knowroute.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.heasy.knowroute.api.ResponseCode;
import com.heasy.knowroute.api.WebResponse;

/**
 * Controller层的全局捕获异常类
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * 返回json格式的异常信息
	 * RestException为自定义异常类
     */
    @ExceptionHandler(value = RestException.class)
    @ResponseBody
    public WebResponse restErrorHandler(RestException ex) {
    	logger.error("restErrorHandler...");
    	logger.error("", ex);

    	WebResponse webResponse = WebResponse.failure(ex.getCode(), ex.getMessage());
        return webResponse;
    }

    @ExceptionHandler(value = Throwable.class)
    @ResponseBody
    public WebResponse throwableErrorHandler(Throwable ex){
    	logger.error("throwableErrorHandler...");
    	logger.error("", ex);
    	
    	WebResponse webResponse = WebResponse.failure(ResponseCode.FAILURE, ex.toString());
        return webResponse;
    }
    
}
