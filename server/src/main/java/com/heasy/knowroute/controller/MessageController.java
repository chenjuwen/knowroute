package com.heasy.knowroute.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.heasy.knowroute.api.ResponseCode;
import com.heasy.knowroute.api.WebResponse;
import com.heasy.knowroute.bean.MessageBean;
import com.heasy.knowroute.common.EnumConstants;
import com.heasy.knowroute.service.MessageService;
import com.heasy.knowroute.utils.JsonUtil;

@RestController
@RequestMapping("/message")
public class MessageController extends BaseController{
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
    
	@Autowired
	private MessageService messageService;
    
	@RequestMapping(value="/insert", method=RequestMethod.POST)
	public WebResponse insert(@RequestBody MessageBean messageBean){
		try {
			//邀请好友
			if(EnumConstants.MessageCategory.INVITE_FRIEND.name().equals(messageBean.getCategory())) {
				MessageBean bean = messageService.getMessage(
						messageBean.getSender(), messageBean.getReceiver(), messageBean.getCategory());
				//邀请信息已发送
				if(bean != null){
					return WebResponse.failure(ResponseCode.FAILURE, "重复邀请");
				}else {
					int id = messageService.insert(messageBean);
					if(id > 0) {
						return WebResponse.success(JsonUtil.toJSONString("id", String.valueOf(id)));
					}
				}
			}else {
				int id = messageService.insert(messageBean);
				if(id > 0) {
					return WebResponse.success(JsonUtil.toJSONString("id", String.valueOf(id)));
				}
			}
		}catch(Exception ex) {
			logger.error("", ex);
		}
		return WebResponse.failure();
	}
    
	/**
	 * 完成消息的处理
	 */
	@RequestMapping(value="/confirm", method=RequestMethod.POST)
	public WebResponse confirm(@RequestBody MessageBean messageBean){
		try {
			logger.debug(JsonUtil.object2String(messageBean));
			messageService.confirmMessage(messageBean.getId(), messageBean.getResult());
			return WebResponse.success();
		}catch(Exception ex) {
			logger.error("", ex);
		}
		return WebResponse.failure();
	}
	
}
