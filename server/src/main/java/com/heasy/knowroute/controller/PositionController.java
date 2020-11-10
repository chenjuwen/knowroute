package com.heasy.knowroute.controller;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.heasy.knowroute.api.ResponseCode;
import com.heasy.knowroute.api.WebResponse;
import com.heasy.knowroute.bean.PointBean;
import com.heasy.knowroute.bean.PositionBean;
import com.heasy.knowroute.service.PositionService;
import com.heasy.knowroute.service.UserService;
import com.heasy.knowroute.utils.DatetimeUtil;

import net.sf.json.JSONArray;

@RestController
@RequestMapping("/position")
public class PositionController extends BaseController{
    private static final Logger logger = LoggerFactory.getLogger(PositionController.class);
    
	@Autowired
	private PositionService positionService;

    @Autowired
    private UserService userService;
    
	@RequestMapping(value="/insert", method=RequestMethod.POST, consumes="application/json")
	public WebResponse insert(@RequestBody PositionBean positionBean){
		try {
			//更新用户的位置信息
			userService.updatePositionInfo(positionBean.getUserId(), 
					positionBean.getLongitude(), positionBean.getLatitude(), 
					positionBean.getAddress());
			
			boolean b = positionService.insert(positionBean);
			if(b) {
				return WebResponse.success();
			}
		}catch(Exception ex) {
			logger.error("", ex);
		}
		
		return WebResponse.failure();
	}
	
	@RequestMapping(value="/getPoints", method=RequestMethod.GET)
    public WebResponse getPoints(@RequestParam(value="userId") Integer userId,
    		@RequestParam(value="startDate") @DateTimeFormat(pattern = DatetimeUtil.DEFAULT_PATTERN_DT2)  Date startDate,
    		@RequestParam(value="endDate") @DateTimeFormat(pattern = DatetimeUtil.DEFAULT_PATTERN_DT2)  Date endDate) {
		try {
			if(startDate.after(endDate)){
				return WebResponse.failure(ResponseCode.FAILURE, "开始时间不能大于结束时间");
            }

            if(DatetimeUtil.differHours(startDate, endDate) > 24){
				return WebResponse.failure(ResponseCode.FAILURE, "轨迹查询时间段必须在24小时内");
            }
            
			List<PointBean> list = positionService.getPoints(userId, startDate, endDate);
			if(!CollectionUtils.isEmpty(list)) {
				return WebResponse.success(JSONArray.fromObject(list).toString(2));
			}else {
				return WebResponse.success("[]");
			}
		}catch(Exception ex) {
			logger.error("", ex);
			return WebResponse.failure(ResponseCode.FAILURE, "获取数据失败");
		}
    }
	
}
