package com.heasy.knowroute.common;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebFilter(filterName="servletRequestWrapperFilter", urlPatterns="/*")
public class ServletRequestWrapperFilter implements Filter{
    private static final Logger logger = LoggerFactory.getLogger(ServletRequestWrapperFilter.class);
    
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		logger.debug("init ServletRequestWrapperFilter");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		ServletRequest requestWrapper = null;  
        if(request instanceof HttpServletRequest) {  
            requestWrapper = new CustomHttpServletRequestWrapper((HttpServletRequest) request);  
        }  
        
        if(requestWrapper == null) {  
            chain.doFilter(request, response);  
        } else {
            chain.doFilter(requestWrapper, response);  
        }
	}

	@Override
	public void destroy() {
		
	}
	
}
