package com.heasy.knowroute.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.heasy.knowroute.api.ResponseCode;
import com.heasy.knowroute.api.WebResponse;
import com.heasy.knowroute.service.CaptcheService;
import com.heasy.knowroute.service.UserService;
import com.heasy.knowroute.utils.JsonUtil;
import com.heasy.knowroute.utils.StringUtil;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController{
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
	@Autowired
	private UserService userService;
	
	@Autowired
	private CaptcheService captcheService;
	
	@RequestMapping(value="/login", method=RequestMethod.POST)
	public WebResponse login(HttpServletRequest request){
		String phone = StringUtil.trimToEmpty(request.getParameter("phone"));
		String captche = StringUtil.trimToEmpty(request.getParameter("captche"));
		logger.debug("start login: phone=" + phone + ", captche=" + captche);
		
		String validCaptche = captcheService.get(phone);
		logger.debug("validCaptche=" + validCaptche);
		
		if(!StringUtil.isMobile(phone)) {
			return WebResponse.failure(ResponseCode.PHONE_INVALID);
		}
		
		if(StringUtil.isEmpty(captche) || StringUtil.isEmpty(validCaptche)) {
			return WebResponse.failure(ResponseCode.CAPTCHE_INVALID);
		}
		
		if(!captche.equalsIgnoreCase(validCaptche)) {
			return WebResponse.failure(ResponseCode.CAPTCHE_INVALID);
		}
		
		int id = userService.login(phone);
		if(id > 0) {
			captcheService.delete(phone);
			return WebResponse.success(JsonUtil.toJSONString("id", String.valueOf(id)));
		}else {
			return WebResponse.failure(ResponseCode.LOGIN_ERROR);
		}
	}

	@RequestMapping(value="/getCaptche", method=RequestMethod.GET)
	public WebResponse getCaptche(@RequestParam(value="phone") String phone) {
		if(!StringUtil.isMobile(phone)) {
			return WebResponse.failure(ResponseCode.PHONE_INVALID);
		}
		
		String captche = StringUtil.getFourDigitRandomNumber();
		logger.info("captche=" + captche);
		
		captcheService.set(phone, captche); 
		return WebResponse.success(JsonUtil.toJSONString("captche", captche));
	}
	
}
