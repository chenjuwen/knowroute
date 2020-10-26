package com.heasy.knowroute.common;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 使springboot启用https的同时，也支持http
 */
//@Configuration
public class TomcatConfig {
	@Value("${server.port}")
	private int serverPort;
	
	@Bean
    public ServletWebServerFactory servletWebServerFactory() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory(){
        	protected void postProcessContext(Context context) {
        		SecurityConstraint securityConstraint = new SecurityConstraint();
        		securityConstraint.setUserConstraint("CONFIDENTIAL");
        		
        		SecurityCollection collection = new SecurityCollection();
        		collection.addPattern("/*");
        		
        		securityConstraint.addCollection(collection);
        		context.addConstraint(securityConstraint);
        	};
        };
        
        factory.addAdditionalTomcatConnectors(createStandardConnector());
        return factory;
    }

	private Connector createStandardConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setPort(8080);
        connector.setSecure(false);
        connector.setRedirectPort(serverPort);
        return connector;
    }
}
