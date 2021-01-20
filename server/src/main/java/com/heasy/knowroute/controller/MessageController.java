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
import org.springframework.web.bind.annotation.RestController;

import com.heasy.knowroute.bean.MessageBean;
import com.heasy.knowroute.bean.WebResponse;
import com.heasy.knowroute.service.MessageService;
import com.heasy.knowroute.utils.JsonUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(tags="消息管理")
@RestController
@RequestMapping("/message")
public class MessageController extends BaseController{
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
    
	@Autowired
	private MessageService messageService;

	@ApiOperation(value="insert", notes="添加消息")
	@ApiImplicitParams({
		@ApiImplicitParam(name="messageBean", paramType="body", required=true, dataType="MessageBean")
	})
	@RequestMapping(value="/insert", method=RequestMethod.POST)
	public WebResponse insert(@RequestBody MessageBean messageBean){
		int id = messageService.insert(messageBean);
		if(id > 0) {
			return WebResponse.success(JsonUtil.toJSONString("id", String.valueOf(id)));
		}else {
			return WebResponse.failure();
		}
	}
    
	@ApiOperation(value="confirm", notes="确认消息处理情况")
	@ApiImplicitParams({
		@ApiImplicitParam(name="messageBean", paramType="body", required=true, dataType="MessageBean")
	})
	@RequestMapping(value="/confirm", method=RequestMethod.POST)
	public WebResponse confirm(@RequestBody MessageBean messageBean){
		messageService.confirmMessage(messageBean.getId(), messageBean.getResult());
		return WebResponse.success();
	}

	@ApiOperation(value="list", notes="获取某个用户的所有消息")
	@ApiImplicitParams({
		@ApiImplicitParam(name="userId", paramType="path", required=true, dataType="Integer")
	})
	@RequestMapping(value="/list/{userId}", method=RequestMethod.GET)
	public WebResponse list(@PathVariable Integer userId){
		try {
			List<MessageBean> list = messageService.getMessageList(userId);
			if(CollectionUtils.isNotEmpty(list)) {
				String data = JsonUtil.object2ArrayString(list, JsonUtil.getJsonConfigOfDate());
				return WebResponse.success(data);
			}
		}catch(Exception ex) {
			logger.error("", ex);
		}
		return WebResponse.success("[]");
	}
	
}
