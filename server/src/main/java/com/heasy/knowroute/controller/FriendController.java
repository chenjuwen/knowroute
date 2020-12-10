package com.heasy.knowroute.controller;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.heasy.knowroute.api.ResponseCode;
import com.heasy.knowroute.api.WebResponse;
import com.heasy.knowroute.bean.FriendBean;
import com.heasy.knowroute.bean.MessageBean;
import com.heasy.knowroute.common.EnumConstants;
import com.heasy.knowroute.service.FriendService;
import com.heasy.knowroute.service.MessageService;
import com.heasy.knowroute.utils.JsonUtil;
import com.heasy.knowroute.utils.StringUtil;

import net.sf.json.JSONArray;

@RestController
@RequestMapping("/friend")
public class FriendController extends BaseController{
    private static final Logger logger = LoggerFactory.getLogger(FriendController.class);
    
	@Autowired
	private FriendService friendService;
	
	@Autowired
	private MessageService messageService;
    
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

	@RequestMapping(value="/invite", method=RequestMethod.POST, consumes="application/json")
	public WebResponse invite(@RequestBody Map<String,String> map) {
		String userId = map.get("userId");
		String phone = map.get("phone");
		
		if(StringUtil.isEmpty(userId) || StringUtil.isEmpty(phone)) {
			return WebResponse.failure(ResponseCode.PARAM_INVALID);
		}

		String category = EnumConstants.MessageCategory.INVITE_FRIEND.name();
		
		MessageBean messageBean = messageService.getMessage(userId, phone, category);
		if(messageBean != null){
			return WebResponse.failure(ResponseCode.FAILURE, "重复邀请");
		}

		MessageBean bean = new MessageBean();
		bean.setContent("请求添加您为好友");
		bean.setCategory(category);
		bean.setSender(userId);
		bean.setReceiver(phone);
		bean.setStatus(0);
		
		int id = messageService.insert(bean);
		if(id > 0) {
			return WebResponse.success(JsonUtil.toJSONString("id", String.valueOf(id)));
		}
        
		return WebResponse.failure();
	}
	
	@RequestMapping(value="/add", method=RequestMethod.POST, consumes="application/json")
	public WebResponse add(@RequestBody Map<String,String> map) {
		String userId = map.get("userId");
		String phone = map.get("phone");
		String friendUserId = map.get("friendUserId");
		
		if(StringUtil.isEmpty(userId) || StringUtil.isEmpty(phone) || StringUtil.isEmpty(friendUserId)) {
			return WebResponse.failure(ResponseCode.PARAM_INVALID);
		}
		
		String category = EnumConstants.MessageCategory.ADD_FRIEND.name();

		MessageBean bean = new MessageBean();
		bean.setContent("请求添加您为好友");
		bean.setCategory(category);
		bean.setSender(userId);
		bean.setReceiver(phone);
		bean.setOwner(Integer.parseInt(friendUserId));
		bean.setStatus(0);
		
		int id = messageService.insert(bean);
		if(id > 0) {
			return WebResponse.success(JsonUtil.toJSONString("id", String.valueOf(id)));
		}
        
		return WebResponse.failure();
	}
	
}
