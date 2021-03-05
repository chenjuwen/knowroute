package com.heasy.knowroute.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.heasy.knowroute.bean.MqttAclBean;
import com.heasy.knowroute.bean.MqttAuthBean;
import com.heasy.knowroute.utils.JsonUtil;

@RestController
@RequestMapping(value="/mqtt")
public class MqttController extends BaseController{
    private static final Logger logger = LoggerFactory.getLogger(MqttController.class);
    
	@RequestMapping(value="/auth", method=RequestMethod.POST)
	public ResponseEntity<String> auth(@RequestBody MqttAuthBean bean){
		logger.info("\n" + JsonUtil.object2String(bean));
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value="/acl", method=RequestMethod.POST)
	public ResponseEntity<String> acl(@RequestBody MqttAclBean bean){
		logger.info("\n" + JsonUtil.object2String(bean));
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value="/test", method=RequestMethod.POST)
	public String test(@RequestBody String content){
		String msg = "";
		try {
			msg = JsonUtil.string2object(content).toString(4);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		logger.info("\n" + msg);
		return "OK";
	}

}
