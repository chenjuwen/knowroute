package com.heasy.knowroute.controller;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.heasy.knowroute.bean.FriendBean;
import com.heasy.knowroute.bean.MessageBean;
import com.heasy.knowroute.bean.ResponseCode;
import com.heasy.knowroute.bean.UserBean;
import com.heasy.knowroute.bean.WebResponse;
import com.heasy.knowroute.common.DataSecurityAnnotation;
import com.heasy.knowroute.common.EnumConstants;
import com.heasy.knowroute.common.RequestLimitAnnotation;
import com.heasy.knowroute.service.FriendService;
import com.heasy.knowroute.service.MessageService;
import com.heasy.knowroute.service.UserService;
import com.heasy.knowroute.utils.JsonUtil;
import com.heasy.knowroute.utils.StringUtil;
import com.heasy.knowroute.vo.FriendVO;
import com.heasy.knowroute.vo.InviteUserVO;
import com.heasy.knowroute.vo.MessageConfirmVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(tags="好友管理")
@RestController
@RequestMapping("/friend")
@RequestLimitAnnotation
public class FriendController extends BaseController{
    private static final Logger logger = LoggerFactory.getLogger(FriendController.class);
    
	@Autowired
	private FriendService friendService;
	
	@Autowired
	private MessageService messageService;
	
	@Autowired
	private UserService userService;

	@ApiOperation(value="check", notes="检查好友状态")
	@ApiImplicitParams({
		@ApiImplicitParam(name="userId", paramType="query", required=true, dataType="Integer"),
		@ApiImplicitParam(name="phone", paramType="query", required=true, dataType="String")
	})
	@RequestMapping(value="/check", method=RequestMethod.GET)
	public WebResponse check(@RequestParam(value="userId") Integer userId,
			@RequestParam(value="phone") String phone){
		try {
			String result = friendService.checkFriend(userId, phone);
			return WebResponse.success(JsonUtil.toJSONString("result", result));
		}catch(Exception ex) {
			logger.error("", ex);
		}
		return WebResponse.failure();
	}
	
	@DataSecurityAnnotation(paramType=EnumConstants.PARAM_TYPE_QUERY, paramKey="userId")
	@ApiOperation(value="list", notes="获取好友列表")
	@ApiImplicitParams({
		@ApiImplicitParam(name="userId", paramType="query", required=true, dataType="Integer")
	})
	@RequestMapping(value="/list", method=RequestMethod.GET)
	public WebResponse list(@RequestParam(value="userId") Integer userId){
		try {
			List<FriendBean> list = friendService.getFriendList(userId);
			if(CollectionUtils.isNotEmpty(list)) {
				return WebResponse.success(JsonUtil.object2ArrayString(list));
			}
		}catch(Exception ex) {
			logger.error("", ex);
		}
		return WebResponse.success("[]");
	}

	@DataSecurityAnnotation(paramType=EnumConstants.PARAM_TYPE_BODY, paramKey="userId")
	@ApiOperation(value="invite", notes="添加邀请好友的站内信")
	@ApiImplicitParams({
		@ApiImplicitParam(name="vo", paramType="body", required=true, dataType="InviteUserVO")
	})
	@RequestMapping(value="/invite", method=RequestMethod.POST)
	public WebResponse invite(@RequestBody InviteUserVO vo) {
		String userId = vo.getUserId();
		String phone = vo.getPhone();
		
		if(StringUtil.isEmpty(userId) || StringUtil.isEmpty(phone)) {
			return WebResponse.failure(ResponseCode.PARAM_INVALID);
		}

		String category = EnumConstants.MessageCategory.INVITE_FRIEND.name();
		
		MessageBean messageBean = messageService.getMessage(userId, phone, category);
		if(messageBean != null){
			return WebResponse.failure(ResponseCode.FAILURE, "重复邀请");
		}

		MessageBean bean = new MessageBean();
		bean.setTitle("邀请添加您为好友");
		bean.setCategory(category);
		bean.setSender(userId);
		bean.setReceiver(phone);
		bean.setStatus(0);
		
		int id = messageService.insert(bean);
		if(id > 0) {
			return WebResponse.success(JsonUtil.toJSONString("id", String.valueOf(id)));
		}
        
		return WebResponse.failure();
	}

	@DataSecurityAnnotation(paramType=EnumConstants.PARAM_TYPE_BODY, paramKey="userId")
	@ApiOperation(value="add", notes="添加好友的站内信")
	@ApiImplicitParams({
		@ApiImplicitParam(name="vo", paramType="body", required=true, dataType="InviteUserVO")
	})
	@RequestMapping(value="/add", method=RequestMethod.POST)
	public WebResponse add(@RequestBody InviteUserVO vo) {
		String userId = vo.getUserId();
		String friendUserId = vo.getFriendUserId();
		String phone = vo.getPhone();
		
		if(StringUtil.isEmpty(userId) || StringUtil.isEmpty(friendUserId) || StringUtil.isEmpty(phone)) {
			return WebResponse.failure(ResponseCode.PARAM_INVALID);
		}
		
		String category = EnumConstants.MessageCategory.ADD_FRIEND.name();

		MessageBean bean = new MessageBean();
		bean.setTitle("请求添加您为好友");
		bean.setCategory(category);
		bean.setSender(userId);
		bean.setReceiver(phone);
		bean.setOwner(Integer.parseInt(friendUserId));
		bean.setStatus(0);
		
		UserBean userBean = userService.getUserById(Integer.parseInt(userId));
		if(userBean != null) {
			bean.setSenderNickname(userBean.getNickname());
			bean.setSenderPhone(userBean.getPhone());
		}
		
		int id = messageService.insert(bean);
		if(id > 0) {
			return WebResponse.success(JsonUtil.toJSONString("id", String.valueOf(id)));
		}
        
		return WebResponse.failure();
	}
	
	@ApiOperation(value="confirmAdd", notes="确认是否加为好友")
	@ApiImplicitParams({
		@ApiImplicitParam(name="vo", paramType="body", required=true, dataType="MessageConfirmVO")
	})
	@RequestMapping(value="/confirmAdd", method=RequestMethod.POST)
	public WebResponse confirmAdd(@RequestBody MessageConfirmVO vo) {
		String id = vo.getId();
		String pass = vo.getPass();
		
		if(StringUtil.isEmpty(id) || StringUtil.isEmpty(pass)) {
			return WebResponse.failure(ResponseCode.PARAM_INVALID);
		}

		boolean b = friendService.confirmAdd(Integer.parseInt(id), pass);
		if(b) {
			return WebResponse.success();
		}else {
			return WebResponse.failure();
		}
	}

	@DataSecurityAnnotation(paramType=EnumConstants.PARAM_TYPE_BODY, paramKey="userId")
	@ApiOperation(value="updateNickname", notes="更新好友昵称")
	@ApiImplicitParams({
		@ApiImplicitParam(name="vo", paramType="body", required=true, dataType="FriendVO")
	})
	@RequestMapping(value="/updateNickname", method=RequestMethod.POST)
	public WebResponse updateNickname(@RequestBody FriendVO vo) {
		String id = vo.getId();
		String userId = vo.getUserId();
		String newNickname = vo.getNewNickname();
		
		if(StringUtil.isEmpty(id) || StringUtil.isEmpty(userId) || StringUtil.isEmpty(newNickname)) {
			return WebResponse.failure(ResponseCode.PARAM_INVALID);
		}
		
		friendService.updateNickname(Integer.parseInt(id), newNickname, Integer.parseInt(userId));
		return WebResponse.success();
	}

	@DataSecurityAnnotation(paramType=EnumConstants.PARAM_TYPE_PATH, paramIndex=0)
	@ApiOperation(value="delete", notes="删除好友")
	@ApiImplicitParams({
		@ApiImplicitParam(name="userId", paramType="path", required=true, dataType="Integer"),
		@ApiImplicitParam(name="id", paramType="path", required=true, dataType="Integer")
	})
	@RequestMapping(value="/delete/{userId}/{id}", method=RequestMethod.POST)
	public WebResponse delete(@PathVariable Integer userId, @PathVariable Integer id) {
		boolean b = friendService.delete(id, userId);
		if(b) {
			return WebResponse.success();
		}else {
			return WebResponse.failure();
		}
	}

	@ApiOperation(value="forbidLookTrace", notes="设置禁止好友查看轨迹")
	@ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="path", required=true, dataType="Integer"),
		@ApiImplicitParam(name="traceFlag", paramType="path", required=true, dataType="Integer")
	})
	@RequestMapping(value="/forbidLookTrace/{id}/{traceFlag}", method=RequestMethod.POST, consumes="application/json")
	public WebResponse forbidLookTrace(@PathVariable Integer id, @PathVariable Integer traceFlag) {
		friendService.forbidLookTrace(id, traceFlag);
		return WebResponse.success();
	}

	@ApiOperation(value="checkForbid", notes="判断是否禁止好友查看轨迹")
	@ApiImplicitParams({
		@ApiImplicitParam(name="userId", paramType="path", required=true, dataType="Integer"),
		@ApiImplicitParam(name="viewTrackUserId", paramType="path", required=true, dataType="Integer")
	})
	@RequestMapping(value="/checkForbid/{userId}/{viewTrackUserId}", method=RequestMethod.GET)
	public WebResponse checkForbid(@PathVariable Integer userId, @PathVariable Integer viewTrackUserId) {
		boolean b = friendService.checkForbid(userId, viewTrackUserId);
		return WebResponse.success(JsonUtil.toJSONString("forbid", String.valueOf(b)));
	}
	
}
