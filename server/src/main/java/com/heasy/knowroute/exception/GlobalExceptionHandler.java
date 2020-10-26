package com.heasy.knowroute.exception;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller层的全局捕获异常类
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	public static final String DEFAULT_ERROR_VIEW = "error";
    
    /**
     * 返回json格式的异常信息
	 * RestException为自定义异常类
     */
    @ExceptionHandler(value = RestException.class)
    @ResponseBody
    public Map<String, String> restErrorHandler(RestException ex) {
    	logger.error("restErrorHandler...");
    	logger.error("", ex);
    	
        Map<String, String> map = new HashMap<String, String>();
        map.put("code", ex.getCode());
        map.put("message", ex.getMessage());
        
        return map;
    }
    
    /**
     * 文件上传异常
     */
    @ExceptionHandler(value = MultipartException.class)
    public ModelAndView multipartErrorHandler(MultipartException ex){
    	logger.error("multipartErrorHandler...");
    	logger.error("", ex);
    	
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("url", "");
        modelAndView.addObject("exception", "文件上传出错：" + ex.toString());
        modelAndView.setViewName(DEFAULT_ERROR_VIEW);
        
        return modelAndView;
    }

	/**
	 * 异常信息显示到error页面
	 */
    @ExceptionHandler(value = Throwable.class)
    public ModelAndView throwableErrorHandler(HttpServletRequest request, Throwable ex) throws Exception {
    	logger.error("throwableErrorHandler...");
    	logger.error("", ex);
    	
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("url", request.getRequestURL());
        modelAndView.addObject("exception", ex.toString());
        modelAndView.setViewName(DEFAULT_ERROR_VIEW);
        
        return modelAndView;
    }
    
    /**
     * 把值绑定到Model中，使全局@RequestMapping可以获取到该值，通过 ModelMap 获取
     */
    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("author", "cjm");
    }
    
}
