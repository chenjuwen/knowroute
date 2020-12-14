package com.heasy.knowroute.controller;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.heasy.knowroute.api.ResponseCode;
import com.heasy.knowroute.api.WebResponse;
import com.heasy.knowroute.bean.ContactBean;
import com.heasy.knowroute.bean.MessageBean;
import com.heasy.knowroute.bean.UserBean;
import com.heasy.knowroute.common.EnumConstants;
import com.heasy.knowroute.service.ContactService;
import com.heasy.knowroute.service.MessageService;
import com.heasy.knowroute.service.UserService;
import com.heasy.knowroute.utils.DatetimeUtil;

import net.sf.json.JSONArray;

@RestController
@RequestMapping("/contact")
public class ContactController extends BaseController{
	@Autowired
	private ContactService contactService;
	
	@Autowired
	private MessageService messageService;
	
	@Autowired
	private UserService userService;
	
	@RequestMapping(value="/save", method=RequestMethod.POST, consumes="application/json")
	public WebResponse save(@RequestBody ContactBean contactBean){
		List<ContactBean> contactList = contactService.getAll(contactBean.getUserId());
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

	@RequestMapping(value="/delete", method=RequestMethod.GET)
    public WebResponse delete(@RequestParam(value="id") Integer id) {
		boolean b = contactService.delete(id);
		if(b) {
			return WebResponse.success();
		}else {
			return WebResponse.failure(ResponseCode.FAILURE);
		}
    }

	@RequestMapping(value="/getAll", method=RequestMethod.GET)
    public WebResponse getAll(@RequestParam(value="userId") Integer userId) {
		List<ContactBean> contactList = contactService.getAll(userId);
		if(!CollectionUtils.isEmpty(contactList)) {
			return WebResponse.success(JSONArray.fromObject(contactList).toString(2));
		}else {
			return WebResponse.success("[]");
		}
    }
	

	/**
	 * 紧急求助的站内消息
	 */
	@RequestMapping(value="/notify", method=RequestMethod.POST, consumes="application/json")
	public WebResponse notify(@RequestBody Map<String,String> map) {
		String userId = map.get("userId");
		String helpPhone = map.get("helpPhone");
		String friendPhone = map.get("friendPhone");

		UserBean friendUser = userService.getUserByPhone(friendPhone);
		if(friendUser != null) {
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
