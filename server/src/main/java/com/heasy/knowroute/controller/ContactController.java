package com.heasy.knowroute.controller;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.heasy.knowroute.bean.ContactBean;
import com.heasy.knowroute.bean.MessageBean;
import com.heasy.knowroute.bean.ResponseCode;
import com.heasy.knowroute.bean.SimpleUserBean;
import com.heasy.knowroute.bean.WebResponse;
import com.heasy.knowroute.common.DataSecurityAnnotation;
import com.heasy.knowroute.common.EnumConstants;
import com.heasy.knowroute.common.RequestLimitAnnotation;
import com.heasy.knowroute.service.ContactService;
import com.heasy.knowroute.service.FriendService;
import com.heasy.knowroute.service.MessageService;
import com.heasy.knowroute.service.UserService;
import com.heasy.knowroute.utils.DatetimeUtil;
import com.heasy.knowroute.utils.JsonUtil;
import com.heasy.knowroute.utils.StringUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(tags="紧急联系人管理")
@RestController
@RequestMapping("/contact")
@RequestLimitAnnotation
public class ContactController extends BaseController{    
	@Autowired
	private ContactService contactService;
	
	@Autowired
	private MessageService messageService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private FriendService friendService;

	@DataSecurityAnnotation(paramType=EnumConstants.PARAM_TYPE_BODY, paramKey="userId")
	@ApiOperation(value="save", notes="保存联系人信息")
	@ApiImplicitParams({
		@ApiImplicitParam(name="contactBean", paramType="body", required=true, dataType="ContactBean")
	})
	@RequestMapping(value="/save", method=RequestMethod.POST, consumes="application/json")
	public WebResponse save(@RequestBody ContactBean contactBean){
		List<ContactBean> contactList = contactService.list(contactBean.getUserId());
		if(contactList != null && contactList.size() >= 3) {
			return WebResponse.failure(ResponseCode.FAILURE, "最多只能添加3位紧急联系人");
		}
		
		boolean result = contactService.existsContact(contactBean);
		if(result) {
			return WebResponse.failure(ResponseCode.FAILURE, "联系人已存在");
		}
		
		boolean b = contactService.save(contactBean);
		if(b) {
			return WebResponse.success();
		}else {
			return WebResponse.failure(ResponseCode.FAILURE);
		}
	}

	@DataSecurityAnnotation(paramType=EnumConstants.PARAM_TYPE_BODY, paramKey="userId")
	@ApiOperation(value="update", notes="更新联系人信息")
	@ApiImplicitParams({
		@ApiImplicitParam(name="contactBean", paramType="body", required=true, dataType="ContactBean")
	})
	@RequestMapping(value="/update", method=RequestMethod.POST, consumes="application/json")
	public WebResponse update(@RequestBody ContactBean contactBean){
		boolean result = contactService.existsContact(contactBean);
		if(result) {
			return WebResponse.failure(ResponseCode.FAILURE, "联系人已存在");
		}
		
		boolean b = contactService.update(contactBean);
		if(b) {
			return WebResponse.success();
		}else {
			return WebResponse.failure(ResponseCode.FAILURE);
		}
	}

	@DataSecurityAnnotation(paramType=EnumConstants.PARAM_TYPE_PATH, paramIndex=0)
	@ApiOperation(value="delete", notes="删除联系人信息")
	@ApiImplicitParams({
		@ApiImplicitParam(name="userId", paramType="path", required=true, dataType="Integer"),
		@ApiImplicitParam(name="id", paramType="path", required=true, dataType="Integer")
	})
	@RequestMapping(value="/delete/{userId}/{id}", method=RequestMethod.POST)
    public WebResponse delete(@PathVariable Integer userId, @PathVariable Integer id) {
		contactService.delete(userId, id);
		return WebResponse.success();
    }

	@DataSecurityAnnotation(paramType=EnumConstants.PARAM_TYPE_QUERY, paramKey="userId")
	@ApiOperation(value="list", notes="获取所有联系人信息")
	@ApiImplicitParams({
		@ApiImplicitParam(name="userId", paramType="query", required=true, dataType="Integer")
	})
	@RequestMapping(value="/list", method=RequestMethod.GET)
    public WebResponse list(@RequestParam(value="userId") Integer userId) {
		List<ContactBean> contactList = contactService.list(userId);
		if(!CollectionUtils.isEmpty(contactList)) {
			return WebResponse.success(JsonUtil.object2ArrayString(contactList));
		}else {
			return WebResponse.success("[]");
		}
    }

	@DataSecurityAnnotation(paramType=EnumConstants.PARAM_TYPE_BODY, paramKey="userId")
	@ApiOperation(value="notify", notes="发送紧急求助站内消息")
	@ApiImplicitParams({
		@ApiImplicitParam(name="map", paramType="body", required=true, dataType="Map<String,String>")
	})
	@RequestMapping(value="/notify", method=RequestMethod.POST, consumes="application/json")
	public WebResponse notify(@RequestBody Map<String,String> map) {
		String userId = map.get("userId");
		String helpPhone = map.get("helpPhone");
		String friendPhone = map.get("friendPhone");
		
		if(StringUtil.isEmpty(userId) || StringUtil.isEmpty(helpPhone) || StringUtil.isEmpty(friendPhone)) {
			return WebResponse.failure(ResponseCode.PARAM_INVALID);
		}

		SimpleUserBean friendUser = userService.getUserByPhone(friendPhone);
		if(friendUser != null && friendService.isFriendRelationship(helpPhone, friendPhone)) {
			MessageBean bean = new MessageBean();
			bean.setTitle("好友提醒");
			bean.setContent("您的朋友" + helpPhone + "向您发起了紧急求助，点击详情查看TA的位置");
			bean.setCategory(EnumConstants.MessageCategory.SEEK_HELP.name());
			bean.setSender(userId);
			bean.setSenderPhone(helpPhone);
			bean.setReceiver(friendPhone);
			bean.setCreateDate(DatetimeUtil.nowDate());
			bean.setOwner(friendUser.getId());
			bean.setStatus(1);
			messageService.insert(bean);
		}
		
		return WebResponse.success();
	}
	 
}
