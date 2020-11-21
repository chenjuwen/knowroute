package com.heasy.knowroute.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.heasy.knowroute.api.WebResponse;
import com.heasy.knowroute.service.FriendService;
import com.heasy.knowroute.utils.JsonUtil;

@RestController
@RequestMapping("/friend")
public class FriendController extends BaseController{
    private static final Logger logger = LoggerFactory.getLogger(FriendController.class);
    
	@Autowired
	private FriendService friendService;
    
	@RequestMapping(value="/check", method=RequestMethod.GET)
	public WebResponse check(@RequestParam(value="userId") Integer userId,
			@RequestParam(value="phone") String phone){
		try {
			String result = friendService.checkFriend(userId, phone);
			return WebResponse.success(JsonUtil.toJSONString("result", result));
		}catch(Exception ex) {
			logger.error("", ex);
		}
		return WebResponse.failure();
	}
	
}
