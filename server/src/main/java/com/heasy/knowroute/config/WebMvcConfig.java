package com.heasy.knowroute.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.heasy.knowroute.common.AccessControlInterceptor;

/**
 * WebMVC配置类
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	@Autowired
    private AccessControlInterceptor accessControlInterceptor;

	/**
	 * 设置默认的访问页面
	 */
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("redirect:/index");
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(accessControlInterceptor);
	}
	
}
