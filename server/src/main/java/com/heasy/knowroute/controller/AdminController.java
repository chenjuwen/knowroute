package com.heasy.knowroute.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.heasy.knowroute.bean.ResponseCode;
import com.heasy.knowroute.bean.WebResponse;
import com.heasy.knowroute.common.RequestLimitAnnotation;
import com.heasy.knowroute.service.SMSService;
import com.heasy.knowroute.utils.JWTUtil;
import com.heasy.knowroute.utils.JsonUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(tags="系统管理")
@RestController
@RequestMapping("/admin")
@RequestLimitAnnotation
public class AdminController extends BaseController{
	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
	
	@Autowired
	private SMSService smsService;
	
	@ApiOperation(value="ignoreSmsSend", notes="设置是否忽略验证码短信发送")
	@ApiImplicitParams({
		@ApiImplicitParam(name="token", paramType="header", required=true, dataType="String"),
		@ApiImplicitParam(name="v", paramType="query", required=true, dataType="Boolean")
	})
	@RequestMapping(value="/ignoreSmsSend", method=RequestMethod.GET)
	public WebResponse ignoreSmsSend(@RequestHeader(value="token") String token, 
			@RequestParam(value="v") Boolean v) {
		logger.debug("token=" + token);
		if(!JWTUtil.verify(token)) {
			return WebResponse.failure(ResponseCode.TOKEN_ERROR);
		}
		
		if(!isSuperAdministrator(token)) {
			return WebResponse.failure(ResponseCode.NO_ACCESS);
		}
		
		smsService.setIgnoreSend(v);
		return WebResponse.success(JsonUtil.toJSONString("ignoreSmsSend", String.valueOf(smsService.isIgnoreSend())));
	}
	
	@ApiOperation(value="captchaMaxCount", notes="设置每天可以获取验证码的最大次数(1-100之间)")
	@ApiImplicitParams({
		@ApiImplicitParam(name="token", paramType="header", required=true, dataType="String"),
		@ApiImplicitParam(name="count", paramType="query", required=true, dataType="Integer")
	})
	@RequestMapping(value="/captchaMaxCount", method=RequestMethod.GET)
	public WebResponse captchaMaxCount(@RequestHeader(value="token") String token, 
			@RequestParam(value="count") Integer count) {
		logger.debug("token=" + token);
		if(!JWTUtil.verify(token)) {
			return WebResponse.failure(ResponseCode.TOKEN_ERROR);
		}
		
		if(!isSuperAdministrator(token)) {
			return WebResponse.failure(ResponseCode.NO_ACCESS);
		}
		
		if(count < 1) count = 1;
		if(count > 100) count = 100;
		UserController.GET_CAPTCHA_MAX_COUNT = count;
		
		return WebResponse.success(JsonUtil.toJSONString("captchaMaxCount", String.valueOf(UserController.GET_CAPTCHA_MAX_COUNT)));
	}
	
	private boolean isSuperAdministrator(String token) {
		String phone = JWTUtil.getClaimFromToken(token, JWTUtil.CLAIM_PHONE);
		return "13798189352".equals(phone);
	}
}
