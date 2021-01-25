package com.heasy.knowroute.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.heasy.knowroute.common.JWTInterceptor;
import com.heasy.knowroute.common.LogInterceptor;

/**
 * WebMVC配置类
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	@Autowired
    private LogInterceptor logInterceptor;
	@Autowired
    private JWTInterceptor jwtInterceptor;
	
	/**
	 * 设置默认的访问页面
	 */
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("redirect:/index");
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		//注册拦截器
		registry.addInterceptor(logInterceptor).addPathPatterns("/**");
		
		registry.addInterceptor(jwtInterceptor).addPathPatterns("/**")
			.excludePathPatterns("/", "/index", "/download", "/helpme", "/aboutme", "/invite", "/user/getCaptcha", "/user/login", "/doc.html", "/js/**", "/css/**", "/images/**", "/**/*.js", "/**/*.css");
	}
	
}