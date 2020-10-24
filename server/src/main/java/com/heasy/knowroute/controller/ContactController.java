package com.heasy.knowroute.controller;

import java.util.List;

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
import com.heasy.knowroute.service.ContactService;

import net.sf.json.JSONArray;

@RestController
@RequestMapping("/contact")
public class ContactController extends BaseController{
	@Autowired
	private ContactService contactService;
	
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
	
}
