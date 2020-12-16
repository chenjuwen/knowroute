package com.heasy.knowroute.controller;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.heasy.knowroute.api.ResponseCode;
import com.heasy.knowroute.api.WebResponse;
import com.heasy.knowroute.bean.FriendBean;
import com.heasy.knowroute.bean.MessageBean;
import com.heasy.knowroute.bean.UserBean;
import com.heasy.knowroute.common.EnumConstants;
import com.heasy.knowroute.service.FriendService;
import com.heasy.knowroute.service.MessageService;
import com.heasy.knowroute.service.UserService;
import com.heasy.knowroute.utils.DatetimeUtil;
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
	
	@Autowired
	private UserService userService;
    
	/**
	 *  好友状态检查
	 * @param userId
	 * @param phone 好友的手机号
	 */
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
	
	/**
	 * 好友列表数据
	 */
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

	/**
	 * 邀请好友的站内消息
	 */
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
		bean.setTitle("邀请添加您为好友");
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
	
	/**
	 * 添加好友的站内消息
	 */
	@RequestMapping(value="/add", method=RequestMethod.POST, consumes="application/json")
	public WebResponse add(@RequestBody Map<String,String> map) {
		String userId = map.get("userId");
		String friendUserId = map.get("friendUserId");
		String phone = map.get("phone");
		
		if(StringUtil.isEmpty(userId) || StringUtil.isEmpty(friendUserId) || StringUtil.isEmpty(phone)) {
			return WebResponse.failure(ResponseCode.PARAM_INVALID);
		}
		
		String category = EnumConstants.MessageCategory.ADD_FRIEND.name();

		MessageBean bean = new MessageBean();
		bean.setTitle("请求添加您为好友");
		bean.setCategory(category);
		bean.setSender(userId);
		bean.setReceiver(phone);
		bean.setOwner(Integer.parseInt(friendUserId));
		bean.setStatus(0);
		
		UserBean userBean = userService.getUserById(Integer.parseInt(userId));
		if(userBean != null) {
			bean.setSenderNickname(userBean.getNickname());
			bean.setSenderPhone(userBean.getPhone());
		}
		
		int id = messageService.insert(bean);
		if(id > 0) {
			return WebResponse.success(JsonUtil.toJSONString("id", String.valueOf(id)));
		}
        
		return WebResponse.failure();
	}
	
	/**
	 * 确认是否加为好友
	 */
	@RequestMapping(value="/confirmAdd", method=RequestMethod.POST, consumes="application/json")
	public WebResponse confirmAdd(@RequestBody Map<String,String> map) {
		String id = map.get("id");
		String pass = map.get("pass");
		
		if(StringUtil.isEmpty(id) || StringUtil.isEmpty(pass)) {
			return WebResponse.failure(ResponseCode.PARAM_INVALID);
		}

		MessageBean messageBean = messageService.getMessage(Integer.parseInt(id));
		if(messageBean != null) {
			if(pass.equalsIgnoreCase("yes")) {
				messageService.confirmMessage(Integer.parseInt(id), "已同意");
				
				//添加好友关系
				UserBean receiverUser = userService.getUserByPhone(messageBean.getReceiver());
				friendService.insert(Integer.parseInt(messageBean.getSender()), messageBean.getReceiver());
				friendService.insert(receiverUser.getId(), messageBean.getSenderPhone());
				
				//添加站内消息
				MessageBean bean = new MessageBean();
				bean.setTitle("好友提醒");
				bean.setContent(messageBean.getReceiver() + "同意了您的好友邀请");
				bean.setCategory(EnumConstants.MessageCategory.GENERAL.name());
				bean.setCreateDate(DatetimeUtil.nowDate());
				bean.setOwner(new Integer(messageBean.getSender()));
				bean.setStatus(1);
				messageService.insert(bean);
				
				return WebResponse.success();
			}else {
				messageService.confirmMessage(Integer.parseInt(id), "已忽略");
				
				//添加站内消息
				MessageBean bean = new MessageBean();
				bean.setTitle("好友提醒");
				bean.setContent(messageBean.getReceiver() + "拒绝了您的好友邀请");
				bean.setCategory(EnumConstants.MessageCategory.GENERAL.name());
				bean.setCreateDate(DatetimeUtil.nowDate());
				bean.setOwner(new Integer(messageBean.getSender()));
				bean.setStatus(1);
				messageService.insert(bean);
				
				return WebResponse.success();
			}
		}
		
		return WebResponse.failure();
	}
	
	@RequestMapping(value="/updateNickname", method=RequestMethod.POST, consumes="application/json")
	public WebResponse updateNickname(@RequestBody Map<String,String> map) {
		try {
			String id = map.get("id");
			String newNickname = map.get("newNickname");
			
			boolean b = friendService.updateNickname(Integer.parseInt(id), newNickname);
			if(b) {
				return WebResponse.success();
			}
		}catch(Exception ex) {
			logger.error("", ex);
		}
		return WebResponse.failure();
	}
	
	@RequestMapping(value="/delete/{id}", method=RequestMethod.POST, consumes="application/json")
	public WebResponse delete(@PathVariable Integer id) {
		try {
			boolean b = friendService.delete(id);
			if(b) {
				return WebResponse.success();
			}
		}catch(Exception ex) {
			logger.error("", ex);
		}
		return WebResponse.failure();
	}
	
	
}
