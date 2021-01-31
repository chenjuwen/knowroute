package com.heasy.knowroute.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.heasy.knowroute.common.JWTInterceptor;
import com.heasy.knowroute.common.LogInterceptor;
import com.heasy.knowroute.common.RequestLimitInterceptor;

/**
 * WebMVC配置类
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	@Autowired
    private LogInterceptor logInterceptor;
	@Autowired
    private JWTInterceptor jwtInterceptor;
	@Autowired
	private RequestLimitInterceptor requestLimitInterceptor;
	
	/**
	 * 设置默认的访问页面
	 */
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("redirect:/index");
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(logInterceptor).addPathPatterns("/**")
			.excludePathPatterns("/js/**", "/css/**", "/images/**");
		
		registry.addInterceptor(jwtInterceptor).addPathPatterns("/**")
			.excludePathPatterns("/", "/index", "/download", "/helpme", "/aboutme", "/invite", "/user/getCaptcha", "/user/login", "/doc.html", "/js/**", "/css/**", "/images/**", "/**/*.js", "/**/*.css");
		
		registry.addInterceptor(requestLimitInterceptor).addPathPatterns("/**")
			.excludePathPatterns("/js/**", "/css/**", "/images/**");
	}
	
}
