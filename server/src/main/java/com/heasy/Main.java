package com.heasy;

import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.heasy.knowroute.common.LoggerFactory;

@SpringBootApplication
public class Main{
	private static Logger logger = LoggerFactory.getLogger(Main.class);
	public static void main(String[] args){
		logger.info("Application run by jar...");
		
		SpringApplication springApp = new SpringApplication(Main.class);
		springApp.run(args);
	}
}
