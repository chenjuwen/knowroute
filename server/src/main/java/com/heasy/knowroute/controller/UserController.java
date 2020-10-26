package com.heasy.knowroute.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.heasy.knowroute.api.ResponseCode;
import com.heasy.knowroute.api.WebResponse;
import com.heasy.knowroute.bean.UserBean;
import com.heasy.knowroute.service.UserService;
import com.heasy.knowroute.utils.JsonUtil;
import com.heasy.knowroute.utils.StringUtil;

@RestController
public class UserController extends BaseController{
	@Autowired
	private UserService userService;
	
	@RequestMapping(value="/login", method=RequestMethod.POST)
	public WebResponse login(HttpServletRequest request){
		String phone = StringUtil.trimToEmpty(request.getParameter("phone"));
		if(!StringUtil.isMobile(phone)) {
			return WebResponse.failure(ResponseCode.PHONE_INVALID);
		}
		
		int id = userService.login(phone);
		if(id > 0) {
			return WebResponse.success(JsonUtil.toJSONString("id", String.valueOf(id)));
		}else {
			return WebResponse.failure(ResponseCode.LOGIN_ERROR);
		}
	}

	@RequestMapping(value="/updatePosition", method=RequestMethod.POST, consumes="application/json")
	public WebResponse updatePosition(@RequestBody UserBean userBean) {
		boolean b = userService.updatePositionInfo(userBean.getId(), 
				userBean.getLongitude(), userBean.getLatitude(), userBean.getAddress());
		if(b) {
			return WebResponse.success();
		}else {
			return WebResponse.failure(ResponseCode.FAILURE);
		}
	}
	
}
