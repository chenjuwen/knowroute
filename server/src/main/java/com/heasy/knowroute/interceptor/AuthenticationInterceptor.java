package com.heasy.knowroute.interceptor;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.heasy.knowroute.bean.ResponseCode;
import com.heasy.knowroute.bean.WebResponse;
import com.heasy.knowroute.common.CustomHttpServletRequestWrapper;
import com.heasy.knowroute.common.DataSecurityAnnotation;
import com.heasy.knowroute.common.EnumConstants;
import com.heasy.knowroute.service.FriendService;
import com.heasy.knowroute.utils.JWTUtil;
import com.heasy.knowroute.utils.JsonUtil;
import com.heasy.knowroute.utils.StringUtil;

/**
 * 身份认证拦截器
 */
@Component
public class AuthenticationInterceptor extends HandlerInterceptorAdapter{
	private static Logger logger = LoggerFactory.getLogger(AuthenticationInterceptor.class);
	
	//是否需要验证token的开关
	@Value("${token.verify.enabled}")
	private boolean tokenVerifyEnabled = true;
	
	@Autowired
	private FriendService friendService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, 
			Object handler) throws Exception {
		String requestURI = request.getRequestURI();
		
		//排除
		if(requestURI.indexOf("swagger") >= 0 || requestURI.indexOf("webjars") >= 0) {
			return true;
		}
		
		logger.debug("tokenVerifyEnabled=" + tokenVerifyEnabled);
		//需要验证token
		if(tokenVerifyEnabled) {
			String token = request.getHeader("token");
			logger.debug("token=" + token);
			
			if(!JWTUtil.verify(token)) {
				responseError(response, ResponseCode.TOKEN_ERROR);
				return false;
			}
			
			//验证数据安全
			boolean b = verifyDataSecurity(request, handler, token);
			logger.debug("b=" + b);
			if(!b) {
				responseError(response, ResponseCode.NO_DATA_ACCESS);
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * 验证数据安全
	 */
	private boolean verifyDataSecurity(HttpServletRequest request, Object handler, String token) {
		try {
			if(handler instanceof HandlerMethod){
				HandlerMethod handlerMethod = (HandlerMethod) handler;
				Method method = handlerMethod.getMethod();
				
				DataSecurityAnnotation methodAnnotation = method.getAnnotation(DataSecurityAnnotation.class);
	            if(methodAnnotation != null){
	            	//token中获取到的值
	            	String claimFieldName = methodAnnotation.claimField();
	            	String compareValueFrom = JWTUtil.getClaimFromToken(token, claimFieldName);
	            	logger.debug("compareValueFrom=" + compareValueFrom);
	            	
	            	//请求中获取到的值
	            	String paramType = methodAnnotation.paramType();
	            	String compareValueTo = "";
	            	if(EnumConstants.PARAM_TYPE_QUERY.equals(paramType)) {
	                	String paramKey = methodAnnotation.paramKey();
	                	compareValueTo = request.getParameter(paramKey);
	            	}else if(EnumConstants.PARAM_TYPE_PATH.equals(paramType)) {
	            		String[] arr = request.getRequestURI().split("/");
	            		arr = Arrays.copyOfRange(arr, arr.length-method.getParameterCount(), arr.length);
	            		logger.debug(Arrays.toString(arr));
	            		
	            		int paramIndex = methodAnnotation.paramIndex();
	            		compareValueTo = arr[paramIndex];
	            	}else if(EnumConstants.PARAM_TYPE_BODY.equals(paramType)) {
	            		String requestBody = "";
	            		if(request instanceof CustomHttpServletRequestWrapper) {
	            			requestBody = ((CustomHttpServletRequestWrapper)request).getRequestBody();
	            		}
	            		
	            		if(StringUtil.isNotEmpty(requestBody)) {
		                	String paramKey = methodAnnotation.paramKey();
	            			compareValueTo = JsonUtil.getString(JsonUtil.string2object(requestBody), paramKey);
	            		}
	            	}
	        		logger.debug("compareValueTo=" + compareValueTo);
	
	            	//数据角色
	            	String dataRole = methodAnnotation.dataRole();
	            	logger.debug("dataRole=" + dataRole);
	            	if(EnumConstants.DATA_ROLE_SELF.equals(dataRole)) {
	            		if(!compareValueFrom.equals(compareValueTo)) {//不是本人
	            			return false;
	            		}
	            	}else if(EnumConstants.DATA_ROLE_FRIEND.equals(dataRole)) {
	            		if(!compareValueFrom.equals(compareValueTo)) {//不是本人
	            			boolean b = false;
	            			
	            			if(JWTUtil.CLAIM_USERID.equals(claimFieldName)) { //比较userId
	            				b = friendService
	            					.isFriendRelationship(Integer.parseInt(compareValueFrom), Integer.parseInt(compareValueTo));
	            			}else { //比较phone
	            				b = friendService
		            				.isFriendRelationship(compareValueFrom, compareValueTo);
	            			}
	            			
	            			logger.debug("isFriendRelationship=" + b);
	            			if(!b) { //不是好友
	            				return false;
	            			}
	            		}
	            	}
	            }
			}
		}catch(Exception ex) {
			logger.error("", ex);
		}
		
		return true;
	}
	
	private void responseError(HttpServletResponse response, ResponseCode responseCode)throws Exception{
		response.setContentType("application/json;charset=UTF-8");
		OutputStream out = response.getOutputStream();
		
		String responseContent = JsonUtil.object2String(WebResponse.failure(responseCode));
		logger.debug(responseContent);
		
		out.write(responseContent.getBytes("UTF-8"));
        out.flush();
        out.close();
	}
	
}
