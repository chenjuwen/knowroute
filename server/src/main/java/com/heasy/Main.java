package com.heasy;

import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

import com.heasy.knowroute.common.LoggerFactory;

@SpringBootApplication
//自动注册通过注解开发的Servlet、Filter、Linstener
@ServletComponentScan
public class Main{
	private static Logger logger = LoggerFactory.getLogger(Main.class);
	public static void main(String[] args){
		logger.info("Application run by jar...");
		
		SpringApplication springApp = new SpringApplication(Main.class);
		springApp.run(args);
	}
}
