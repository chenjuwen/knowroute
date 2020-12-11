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

import com.heasy.knowroute.api.WebResponse;
import com.heasy.knowroute.bean.MessageBean;
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
			int id = messageService.insert(messageBean);
			if(id > 0) {
				return WebResponse.success(JsonUtil.toJSONString("id", String.valueOf(id)));
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
