package com.heasy.knowroute.mqtt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.heasy.knowroute.controller.BaseController;

@RestController
@RequestMapping("/mqtt")
public class MQTTController extends BaseController{
	@Value("${mqtt.topic}")
    private String topic;
	
	@Autowired
    MQTTPublisherGateway mqttGateway;
	
    @GetMapping("publish")
    public String publish(@RequestParam(defaultValue = "MQTT发布者") String payload){
    	//使用默认主题发布
    	mqttGateway.publish(topic, 2, payload);
        return "success";
    }
    
}
