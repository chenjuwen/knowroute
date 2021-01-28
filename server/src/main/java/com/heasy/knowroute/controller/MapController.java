package com.heasy.knowroute.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.heasy.knowroute.bean.MessageBean;
import com.heasy.knowroute.bean.UserBean;
import com.heasy.knowroute.bean.VersionBean;
import com.heasy.knowroute.common.RequestLimitAnnotation;
import com.heasy.knowroute.service.MessageService;
import com.heasy.knowroute.service.UserService;
import com.heasy.knowroute.service.VersionService;

import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@Controller
@RequestLimitAnnotation
public class MapController extends BaseController{
	@Autowired
	private UserService userService;
	
	@Autowired
	private MessageService messageService;
	
	@Autowired
	private VersionService versionService;
	
	/**
	 * 一键求助
	 * @param modelMap
	 * @param userId 用户ID
	 * @param phone 用户手机号
	 * @return
	 */
	@RequestMapping(value="/helpme", method=RequestMethod.GET)
	public String helpme(ModelMap modelMap, @RequestParam(value="u") String userId, 
			@RequestParam(value="p") String phone) {
		Double longitude = null;
		Double latitude = null;
		String address = "";
		
		UserBean userBean = userService.getUserById(Integer.parseInt(userId));
		if(userBean != null) {
			if(userBean.getPhone().equals(phone)) {
				longitude = userBean.getLongitude();
				latitude = userBean.getLatitude();
				address = userBean.getAddress();
			}
		}
		
		modelMap.put("longitude", longitude); //经度
		modelMap.put("latitude", latitude); //纬度
		modelMap.put("phone", phone); //手机号
		modelMap.put("address", address); //地址
		
		return "helpme";
	}

	/**
	 * 邀请好友
	 * @param modelMap
	 * @param mid	消息ID
	 * @param longitude 经度
	 * @param latitude 纬度
	 * @return
	 */
	@RequestMapping(value="/invite", method=RequestMethod.GET)
	public String invite(ModelMap modelMap, @RequestParam(value="mid") int mid, 
			@RequestParam(value="lng") double longitude, @RequestParam(value="lat") double latitude) {
		MessageBean messageBean = messageService.getMessage(mid);
		if(messageBean != null) {
			UserBean userBean = userService.getUserById(Integer.parseInt(messageBean.getSender()));
			if(userBean != null) {
				String senderPhone = userBean.getPhone();

				modelMap.put("mid", String.valueOf(mid));
				modelMap.put("longitude", longitude); //经度
				modelMap.put("latitude", latitude); //纬度
				modelMap.put("phone", senderPhone); //手机号
			}
		}
		
		//最新版本号
		VersionBean versionBean = versionService.getLatestVersion();
		if(versionBean != null) {
			modelMap.put("version", versionBean.getVname());
		}else {
			modelMap.put("version", "");
		}
		
		return "invite_friend";
	}
	
}
