package com.heasy.knowroute.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import com.heasy.knowroute.bean.UserBean;
import com.heasy.knowroute.service.CaptcheService;
import com.heasy.knowroute.service.UserService;
import com.heasy.knowroute.utils.JsonUtil;
import com.heasy.knowroute.utils.StringUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(tags="用户管理")
@RestController
@RequestMapping("/user")
public class UserController extends BaseController{
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
	@Autowired
	private UserService userService;
	
	@Autowired
	private CaptcheService captcheService;

	@ApiOperation(value="getCaptche", notes="获取登陆验证码")
	@ApiImplicitParams({
		@ApiImplicitParam(name="phone", paramType="query", required=true, dataType="String")
	})
	@RequestMapping(value="/getCaptche", method=RequestMethod.GET)
	public WebResponse getCaptche(@RequestParam(value="phone") String phone) {
		if(!StringUtil.isMobile(phone)) {
			return WebResponse.failure(ResponseCode.PHONE_INVALID);
		}

		String captche = StringUtil.getFourDigitRandomNumber();
		captcheService.set(phone, captche);
		logger.info("captche=" + captche);
		
		return WebResponse.success(JsonUtil.toJSONString("captche", captche));
	}

	@ApiOperation(value="login", notes="系统登陆")
	@ApiImplicitParams({
		@ApiImplicitParam(name="phone", paramType="query", required=true, dataType="String"),
		@ApiImplicitParam(name="phone", paramType="query", required=true, dataType="String")
	})
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
		
		try {
			int id = userService.login(phone);
			if(id > 0) {
				captcheService.delete(phone);
				UserBean newUser = userService.getUserById(id);
				String data = JsonUtil.toJSONString("id", String.valueOf(id), "nickname", newUser.getNickname());
				return WebResponse.success(data);
			}
		}catch(Exception ex) {
			logger.error("", ex);
		}
		return WebResponse.failure(ResponseCode.LOGIN_ERROR);
	}

	@ApiOperation(value="getById", notes="根据id获取用户信息")
	@ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="query", required=true, dataType="Integer")
	})
	@RequestMapping(value="/getById", method=RequestMethod.GET)
	public WebResponse getById(@RequestParam(value="id") Integer id){
		UserBean bean = userService.getUserById(id);
		if(bean != null) {
			String data = JsonUtil.object2String(bean);
			return new WebResponse(ResponseCode.SUCCESS, data);
		}else {
			return WebResponse.failure(ResponseCode.NO_DATA);
		}
	}

	@ApiOperation(value="getByPhone", notes="根据手机号获取用户信息")
	@ApiImplicitParams({
		@ApiImplicitParam(name="phone", paramType="query", required=true, dataType="String")
	})
	@RequestMapping(value="/getByPhone", method=RequestMethod.GET)
	public WebResponse getByPhone(@RequestParam(value="phone") String phone){
		UserBean bean = userService.getUserByPhone(phone);
		if(bean != null) {
			String data = JsonUtil.object2String(bean);
			return new WebResponse(ResponseCode.SUCCESS, data);
		}else {
			return WebResponse.failure(ResponseCode.NO_DATA);
		}
	}

	@ApiOperation(value="updateNickname", notes="更新用户昵称")
	@ApiImplicitParams({
		@ApiImplicitParam(name="map", paramType="body", required=true, dataType="Map<String,String>")
	})
	@RequestMapping(value="/updateNickname", method=RequestMethod.POST, consumes="application/json")
	public WebResponse updateNickname(@RequestBody Map<String,String> map){
		String userId = map.get("userId");
		String newNickname = map.get("newNickname");
		
		userService.updateNickname(Integer.parseInt(userId), newNickname);
		return WebResponse.success();
	}

	@ApiOperation(value="cancel", notes="注销账户")
	@ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="query", required=true, dataType="Integer")
	})
	@RequestMapping(value="/cancel", method=RequestMethod.GET)
	public WebResponse cancel(@RequestParam(value="id") Integer id){
		UserBean bean = userService.getUserById(id);
		if(bean != null) {
			userService.cancelAccount(id, bean.getPhone());
			return WebResponse.success();
		}else {
			return WebResponse.failure();
		}
	}
	
}
