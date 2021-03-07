package com.heasy.knowroute.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.heasy.knowroute.interceptor.AuthenticationInterceptor;
import com.heasy.knowroute.interceptor.LogInterceptor;
import com.heasy.knowroute.interceptor.RequestLimitInterceptor;

/**
 * WebMVC配置类
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	@Autowired
    private LogInterceptor logInterceptor;
	@Autowired
    private AuthenticationInterceptor authenticationInterceptor;
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
		
		registry.addInterceptor(authenticationInterceptor).addPathPatterns("/**")
			.excludePathPatterns("/", "/mqtt/**", "/index", "/download", "/structure", "/helpme", "/position/insert", "/aboutme", "/invite", "/user/getCaptcha", "/message/confirm", "/user/login", "/doc.html", "/js/**", "/css/**", "/images/**", "/**/*.js", "/**/*.css");
		
		registry.addInterceptor(requestLimitInterceptor).addPathPatterns("/**")
			.excludePathPatterns("/js/**", "/css/**", "/images/**");
	}
	
}
