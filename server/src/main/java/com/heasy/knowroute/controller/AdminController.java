package com.heasy.knowroute.controller;

import javax.websocket.server.PathParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.heasy.knowroute.bean.ResponseCode;
import com.heasy.knowroute.bean.WebResponse;
import com.heasy.knowroute.common.RequestLimitAnnotation;
import com.heasy.knowroute.interceptor.AuthenticationInterceptor;
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
	
	@ApiOperation(value="sms", notes="设置是否忽略验证码短信发送")
	@ApiImplicitParams({
		@ApiImplicitParam(name="token", paramType="header", required=true, dataType="String"),
		@ApiImplicitParam(name="enabled", paramType="path", required=true, dataType="Boolean")
	})
	@RequestMapping(value="/sms/{enabled}", method=RequestMethod.POST)
	public WebResponse sms(@RequestHeader(value="token") String token, 
			@PathParam(value="enabled") Boolean enabled) {
		logger.debug("token=" + token);
		if(!JWTUtil.verify(token)) {
			return WebResponse.failure(ResponseCode.TOKEN_ERROR);
		}
		
		if(!isSuperAdministrator(token)) {
			return WebResponse.failure(ResponseCode.NO_ACCESS);
		}
		
		smsService.setIgnoreSend(enabled);
		return WebResponse.success(JsonUtil.toJSONString("ignoreSmsSend", String.valueOf(smsService.isIgnoreSend())));
	}
	
	@ApiOperation(value="captcha", notes="设置每天可以获取验证码的最大次数(1-100之间)")
	@ApiImplicitParams({
		@ApiImplicitParam(name="token", paramType="header", required=true, dataType="String"),
		@ApiImplicitParam(name="maxCount", paramType="path", required=true, dataType="Integer")
	})
	@RequestMapping(value="/captcha/{maxCount}", method=RequestMethod.POST)
	public WebResponse captcha(@RequestHeader(value="token") String token, 
			@PathParam(value="maxCount") Integer maxCount) {
		logger.debug("token=" + token);
		if(!JWTUtil.verify(token)) {
			return WebResponse.failure(ResponseCode.TOKEN_ERROR);
		}
		
		if(!isSuperAdministrator(token)) {
			return WebResponse.failure(ResponseCode.NO_ACCESS);
		}
		
		if(maxCount < 1) maxCount = 1;
		if(maxCount > 100) maxCount = 100;
		UserController.GET_CAPTCHA_MAX_COUNT = maxCount;
		
		return WebResponse.success(JsonUtil.toJSONString("captchaMaxCount", String.valueOf(UserController.GET_CAPTCHA_MAX_COUNT)));
	}
	
	/**
	 * 
	 * @param token
	 * @param verify 0表示不启用，1表示启用
	 */
	@ApiOperation(value="token", notes="是否启用token验证功能")
	@ApiImplicitParams({
		@ApiImplicitParam(name="token", paramType="header", required=true, dataType="String"),
		@ApiImplicitParam(name="verify", paramType="path", required=true, dataType="Integer")
	})
	@RequestMapping(value="/token/{verify}", method=RequestMethod.POST)
	public WebResponse token(@RequestHeader(value="token") String token, 
			@PathParam(value="verify") Integer verify) {
		if(!JWTUtil.verify(token)) {
			return WebResponse.failure(ResponseCode.TOKEN_ERROR);
		}
		
		if(!isSuperAdministrator(token)) {
			return WebResponse.failure(ResponseCode.NO_ACCESS);
		}
		
		AuthenticationInterceptor.setTokenVerifyEnabled(verify==1);
		
		return WebResponse.success(JsonUtil.toJSONString("enabled", String.valueOf(AuthenticationInterceptor.isTokenVerifyEnabled())));
	}
	
	private boolean isSuperAdministrator(String token) {
		String phone = JWTUtil.getClaimFromToken(token, JWTUtil.CLAIM_PHONE);
		return "13798189352".equals(phone);
	}
}
