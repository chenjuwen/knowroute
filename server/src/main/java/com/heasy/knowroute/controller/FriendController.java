package com.heasy.knowroute.controller;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.heasy.knowroute.api.WebResponse;
import com.heasy.knowroute.bean.FriendBean;
import com.heasy.knowroute.service.FriendService;
import com.heasy.knowroute.utils.JsonUtil;

import net.sf.json.JSONArray;

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
	
	@RequestMapping(value="/list", method=RequestMethod.GET)
	public WebResponse list(@RequestParam(value="userId") Integer userId){
		try {
			List<FriendBean> list = friendService.getFriendList(userId);
			if(CollectionUtils.isNotEmpty(list)) {
				return WebResponse.success(JSONArray.fromObject(list).toString());
			}
		}catch(Exception ex) {
			logger.error("", ex);
		}
		return WebResponse.success("[]");
	}
	
}
