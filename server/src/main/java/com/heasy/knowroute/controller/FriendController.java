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
import com.heasy.knowroute.utils.JsonUtil;
import com.heasy.knowroute.utils.StringUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONArray;

@Api(tags="好友管理")
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

	@ApiOperation(value="check", notes="检查好友状态")
	@ApiImplicitParams({
		@ApiImplicitParam(name="userId", paramType="query", required=true, dataType="Integer"),
		@ApiImplicitParam(name="phone", paramType="query", required=true, dataType="String")
	})
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
	
	@ApiOperation(value="list", notes="获取好友列表")
	@ApiImplicitParams({
		@ApiImplicitParam(name="userId", paramType="query", required=true, dataType="Integer")
	})
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

	@ApiOperation(value="invite", notes="添加邀请好友的站内信")
	@ApiImplicitParams({
		@ApiImplicitParam(name="map", paramType="body", required=true, dataType="Map<String,String>")
	})
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
	
	@ApiOperation(value="add", notes="添加好友的站内信")
	@ApiImplicitParams({
		@ApiImplicitParam(name="map", paramType="body", required=true, dataType="Map<String,String>")
	})
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
	
	@ApiOperation(value="confirmAdd", notes="确认是否加为好友")
	@ApiImplicitParams({
		@ApiImplicitParam(name="map", paramType="body", required=true, dataType="Map<String,String>")
	})
	@RequestMapping(value="/confirmAdd", method=RequestMethod.POST, consumes="application/json")
	public WebResponse confirmAdd(@RequestBody Map<String,String> map) {
		String id = map.get("id");
		String pass = map.get("pass");
		
		if(StringUtil.isEmpty(id) || StringUtil.isEmpty(pass)) {
			return WebResponse.failure(ResponseCode.PARAM_INVALID);
		}

		boolean b = friendService.confirmAdd(Integer.parseInt(id), pass);
		if(b) {
			return WebResponse.success();
		}else {
			return WebResponse.failure();
		}
	}

	@ApiOperation(value="updateNickname", notes="更新好友昵称")
	@ApiImplicitParams({
		@ApiImplicitParam(name="map", paramType="body", required=true, dataType="Map<String,String>")
	})
	@RequestMapping(value="/updateNickname", method=RequestMethod.POST, consumes="application/json")
	public WebResponse updateNickname(@RequestBody Map<String,String> map) {
		String id = map.get("id");
		String newNickname = map.get("newNickname");
		
		friendService.updateNickname(Integer.parseInt(id), newNickname);
		return WebResponse.success();
	}

	@ApiOperation(value="delete", notes="删除好友")
	@ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="path", required=true, dataType="Integer")
	})
	@RequestMapping(value="/delete/{id}", method=RequestMethod.POST, consumes="application/json")
	public WebResponse delete(@PathVariable Integer id) {
		boolean b = friendService.delete(id);
		if(b) {
			return WebResponse.success();
		}else {
			return WebResponse.failure();
		}
	}
	
}
