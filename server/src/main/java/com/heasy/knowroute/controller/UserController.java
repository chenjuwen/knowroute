package com.heasy.knowroute.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.heasy.knowroute.bean.ResponseCode;
import com.heasy.knowroute.bean.SimpleUserBean;
import com.heasy.knowroute.bean.UserBean;
import com.heasy.knowroute.bean.WebResponse;
import com.heasy.knowroute.common.DataSecurityAnnotation;
import com.heasy.knowroute.common.EnumConstants;
import com.heasy.knowroute.common.RequestLimitAnnotation;
import com.heasy.knowroute.service.DataCacheService;
import com.heasy.knowroute.service.SMSService;
import com.heasy.knowroute.service.UserService;
import com.heasy.knowroute.utils.DatetimeUtil;
import com.heasy.knowroute.utils.JWTUtil;
import com.heasy.knowroute.utils.JsonUtil;
import com.heasy.knowroute.utils.StringUtil;
import com.heasy.knowroute.vo.UserVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(tags="用户管理")
@RestController
@RequestMapping("/user")
@RequestLimitAnnotation
public class UserController extends BaseController{
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private static final String CAPTCHA_KEY_PREFIX = "captcha:";
    
	/**
	 * 每天获取验证码的最大次数
	 */
	public static int GET_CAPTCHA_MAX_COUNT = 3;
    
	@Autowired
	private UserService userService;
	
	@Autowired
	private SMSService smsService;
	
	@Autowired
	private DataCacheService dataCacheService;

	@ApiOperation(value="getCaptcha", notes="获取登陆验证码")
	@ApiImplicitParams({
		@ApiImplicitParam(name="phone", paramType="query", required=true, dataType="String")
	})
	@RequestMapping(value="/getCaptcha", method=RequestMethod.GET)
	public WebResponse getCaptcha(@RequestParam(value="phone") String phone) {
		if(!StringUtil.isMobile(phone)) {
			return WebResponse.failure(ResponseCode.PHONE_INVALID);
		}
		
		String dayKey = CAPTCHA_KEY_PREFIX + phone + DatetimeUtil.getToday("yyyyMMdd");
		if(dataCacheService.exists(dayKey)) {
			long count = (Long)dataCacheService.get(dayKey);
			logger.debug("count=" + count);
			if(count >= GET_CAPTCHA_MAX_COUNT) {
				logger.debug("获取验证码的次数达到最大值：phone=" + phone + ", count=" + GET_CAPTCHA_MAX_COUNT);
				return WebResponse.failure(ResponseCode.GET_CAPTCHA_ERROR);
			}
		}
		
//		if(dataCacheService.exists(CAPTCHA_KEY_PREFIX + phone)) {
//			return WebResponse.failure(ResponseCode.GET_CAPTCHA_ERROR);
//		}

		//获取6位长度的验证码
		String captcha = StringUtil.getRandomNumber(6);
		logger.debug("captcha=" + captcha);
		
		//发送短信
		boolean b = smsService.sendVerificationCode(phone, captcha);
		if(b) {
			dataCacheService.set(CAPTCHA_KEY_PREFIX + phone, captcha, DataCacheService.secondForMinute); //有效期60秒
			
			if(dataCacheService.exists(dayKey)) {
				long value = (Long)dataCacheService.get(dayKey);
				dataCacheService.set(dayKey, new Long(value + 1), DataCacheService.secondForDay);
			}else {
				dataCacheService.set(dayKey, new Long(1), DataCacheService.secondForDay);
			}
			
			return WebResponse.success();
		}else {
			return WebResponse.failure(ResponseCode.GET_CAPTCHA_ERROR);
		}
	}

	@ApiOperation(value="login", notes="系统登陆")
	@ApiImplicitParams({
		@ApiImplicitParam(name="phone", paramType="query", required=true, dataType="String"),
		@ApiImplicitParam(name="captcha", paramType="query", required=true, dataType="String")
	})
	@RequestMapping(value="/login", method=RequestMethod.POST)
	public WebResponse login(HttpServletRequest request){
		String phone = StringUtil.trimToEmpty(request.getParameter("phone"));
		String captcha = StringUtil.trimToEmpty(request.getParameter("captcha"));
		logger.debug("start login: phone=" + phone + ", captcha=" + captcha);
		
		if(!StringUtil.isMobile(phone)) {
			return WebResponse.failure(ResponseCode.PHONE_INVALID);
		}

		String validCaptcha = (String)dataCacheService.get(CAPTCHA_KEY_PREFIX + phone);
		logger.debug("validCaptcha=" + validCaptcha);
		
		if(StringUtil.isEmpty(captcha) || StringUtil.isEmpty(validCaptcha)) {
			return WebResponse.failure(ResponseCode.CAPTCHA_INVALID);
		}
		
		if(!captcha.equalsIgnoreCase(validCaptcha)) {
			return WebResponse.failure(ResponseCode.CAPTCHA_INVALID);
		}
		
		try {
			int id = userService.login(phone);
			if(id > 0) {
				dataCacheService.delete(CAPTCHA_KEY_PREFIX + phone);
				
				//生成token
				UserBean user = userService.getUserById(id);
				String token = JWTUtil.generateToken(String.valueOf(user.getId()), phone);
				String expiresDate = DatetimeUtil.formatDate(JWTUtil.getExpiresDate(token));
				
				String data = JsonUtil.toJSONString("userId", String.valueOf(id), "phone", phone, 
						"nickname", user.getNickname(), "token", token, "expiresDate", expiresDate);
				
				return WebResponse.success(data);
			}
		}catch(Exception ex) {
			logger.error("", ex);
		}
		return WebResponse.failure(ResponseCode.LOGIN_ERROR);
	}

	@DataSecurityAnnotation(dataRole=EnumConstants.DATA_ROLE_FRIEND, 
			paramType=EnumConstants.PARAM_TYPE_QUERY, paramKey="id")
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
		SimpleUserBean bean = userService.getUserByPhone(phone);
		if(bean != null) {
			String data = JsonUtil.object2String(bean);
			return new WebResponse(ResponseCode.SUCCESS, data);
		}else {
			return WebResponse.failure(ResponseCode.NO_DATA);
		}
	}

	@DataSecurityAnnotation(paramType=EnumConstants.PARAM_TYPE_BODY, paramKey="userId")
	@ApiOperation(value="updateNickname", notes="更新用户昵称")
	@ApiImplicitParams({
		@ApiImplicitParam(name="vo", paramType="body", required=true, dataType="UserVO")
	})
	@RequestMapping(value="/updateNickname", method=RequestMethod.POST)
	public WebResponse updateNickname(@RequestBody UserVO vo){
		String userId = vo.getUserId();
		String newNickname = vo.getNewNickname();
		
		userService.updateNickname(Integer.parseInt(userId), newNickname);
		return WebResponse.success();
	}

	@DataSecurityAnnotation(paramType=EnumConstants.PARAM_TYPE_QUERY, paramKey="id")
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
	
	@RequestLimitAnnotation(seconds=60*60, maxCount=1) //一个小时只能访问一次
	@ApiOperation(value="refreshToken", notes="刷新Token")
	@ApiImplicitParams({
		@ApiImplicitParam(name="token", paramType="header", required=true, dataType="String")
	})
	@RequestMapping(value="/refreshToken", method=RequestMethod.GET)
	public WebResponse refreshToken(@RequestHeader(value="token") String token) {
		logger.debug("token=" + token);
		if(!JWTUtil.verify(token)) {
			return WebResponse.failure(ResponseCode.REFRESH_TOKEN_ERROR);
		}
		
		Date expiresDate = JWTUtil.getExpiresDate(token);
		long differMinutes = DatetimeUtil.differMinutes(DatetimeUtil.nowDate(), expiresDate);
		if(differMinutes <= JWTUtil.REFRESH_TOKEN_REMAINING_MINUTES) {
			String userId = JWTUtil.getClaimFromToken(token, JWTUtil.CLAIM_USERID);
			String phone = JWTUtil.getClaimFromToken(token, JWTUtil.CLAIM_PHONE);

			UserBean user = userService.getUserById(Integer.parseInt(userId));
			
			String newToken = JWTUtil.generateToken(userId, phone);
			String newExpiresDate = DatetimeUtil.formatDate(JWTUtil.getExpiresDate(newToken));
			
			String data = JsonUtil.toJSONString("userId", userId, "phone", phone, 
					"nickname", user.getNickname(), "token", newToken, "expiresDate", newExpiresDate);
			return WebResponse.success(data);
		}else {
			return WebResponse.failure(ResponseCode.REFRESH_TOKEN_ERROR);
		}
	}
	
}
