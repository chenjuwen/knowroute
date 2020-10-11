package com.heasy.knowroute.controller;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.heasy.knowroute.api.ResponseCode;
import com.heasy.knowroute.api.WebResponse;
import com.heasy.knowroute.bean.DataBean;
import com.heasy.knowroute.bean.UserBean;
import com.heasy.knowroute.common.Constants;
import com.heasy.knowroute.service.DataService;
import com.heasy.knowroute.service.LoginService;
import com.heasy.knowroute.service.UserService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@RestController
@RequestMapping
public class LotteryController extends BaseController{
	@Autowired
	private UserService userService;

	@Autowired
	private LoginService loginService;

	@Autowired
	private DataService dataService;
	
	@RequestMapping("/user/getUser")
	public WebResponse getUser(@RequestParam(value="account") String account){
		UserBean bean = userService.getUser(account);
		if(bean != null) {
			String data = JSONObject.fromObject(bean).toString();
			return new WebResponse(ResponseCode.SUCCESS, data);
		}else {
			return WebResponse.failure(ResponseCode.NO_DATA);
		}
	}
	
	@RequestMapping("/user/changePassword")
	public WebResponse changePassword(@RequestParam(value="account") String account,
			@RequestParam(value="oldPassword") String oldPassword,
			@RequestParam(value="newPassword") String newPassword){
		String result = userService.changePassword(account, oldPassword, newPassword);
		if(Constants.SUCCESS.equals(result)) {
			return WebResponse.success();
		}else {
			return WebResponse.failure(ResponseCode.FAILURE, result);
		}
	}
	
	@RequestMapping("/login/checkLogin")
	public WebResponse checkLogin(@RequestParam(value="account") String account,
			@RequestParam(value="password") String password){
		String result = loginService.checkLogin(account, password);
		if(Constants.SUCCESS.equals(result)) {
			return WebResponse.success();
		}else {
			return WebResponse.failure(ResponseCode.FAILURE, result);
		}
	}
	
	@RequestMapping("/data/getAllData")
	public WebResponse getAllData(){
		List<DataBean> list = dataService.getAllData();
		if(!CollectionUtils.isEmpty(list)) {
			String data = JSONArray.fromObject(list).toString();
			return new WebResponse(ResponseCode.SUCCESS, data);
		}else {
			return WebResponse.failure(ResponseCode.NO_DATA);
		}
	}
	
	@RequestMapping("/data/getByPeriod")
	public WebResponse getByPeriod(@RequestParam(value="period") String period){
		DataBean bean = dataService.getByPeriod(period);
		if(bean != null) {
			String data = JSONObject.fromObject(bean).toString();
			return new WebResponse(ResponseCode.SUCCESS, data);
		}else {
			return WebResponse.failure(ResponseCode.NO_DATA);
		}
	}
	
	@RequestMapping("/data/deleteByPeriod")
	public WebResponse deleteByPeriod(@RequestParam(value="period") String period){
		boolean b = dataService.deleteByPeriod(period);
		if(b) {
			return WebResponse.success();
		}else {
			return WebResponse.failure(ResponseCode.FAILURE, "删除数据失败！");
		}
	}
	
	@RequestMapping("/data/add")
	public WebResponse add(@RequestParam(value="period") String period,
			@RequestParam(value="data") String data){
		DataBean bean = dataService.getByPeriod(period);
		if(bean != null) {
			return WebResponse.failure(ResponseCode.FAILURE, "期数 " + period + " 已存在！");
		}
		
		boolean b = dataService.add(period, data);
		if(b) {
			return WebResponse.success();
		}else {
			return WebResponse.failure(ResponseCode.FAILURE, "添加数据失败！");
		}
	}
	
	@RequestMapping("/data/update")
	public WebResponse update(@RequestParam(value="period") String period,
			@RequestParam(value="data") String data){
		DataBean bean = dataService.getByPeriod(period);
		if(bean == null) {
			return WebResponse.failure(ResponseCode.FAILURE, "数据不存在！");
		}
		
		boolean b = dataService.update(period, data);
		if(b) {
			return WebResponse.success();
		}else {
			return WebResponse.failure(ResponseCode.FAILURE, "修改数据失败！");
		}
	}
	
}
