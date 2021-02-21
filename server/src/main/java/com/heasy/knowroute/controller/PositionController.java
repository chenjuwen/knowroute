package com.heasy.knowroute.controller;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.heasy.knowroute.bean.PointBean;
import com.heasy.knowroute.bean.PositionBean;
import com.heasy.knowroute.bean.ResponseCode;
import com.heasy.knowroute.bean.WebResponse;
import com.heasy.knowroute.common.DataSecurityAnnotation;
import com.heasy.knowroute.common.EnumConstants;
import com.heasy.knowroute.common.RequestLimitAnnotation;
import com.heasy.knowroute.service.PositionService;
import com.heasy.knowroute.service.UserService;
import com.heasy.knowroute.utils.DatetimeUtil;
import com.heasy.knowroute.utils.JsonUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(tags="轨迹点管理")
@RestController
@RequestMapping("/position")
@RequestLimitAnnotation
public class PositionController extends BaseController{
    private static final Logger logger = LoggerFactory.getLogger(PositionController.class);
    
	@Autowired
	private PositionService positionService;

    @Autowired
    private UserService userService;

    @RequestLimitAnnotation(seconds=1, maxCount=1)
	@ApiOperation(value="insert", notes="添加轨迹点信息")
	@ApiImplicitParams({
		@ApiImplicitParam(name="positionList", paramType="body", required=true, dataType="List<PositionBean>")
	})
	@RequestMapping(value="/insert", method=RequestMethod.POST, consumes="application/json")
	public WebResponse insert(@RequestBody List<PositionBean> positionList){
		try {
			if(CollectionUtils.isNotEmpty(positionList)) {
				PositionBean positionBean = positionList.get(positionList.size()-1);
				
				//更新用户的位置信息
				userService.updatePositionInfo(positionBean.getUserId(), 
						positionBean.getLongitude(), positionBean.getLatitude(), 
						positionBean.getAddress(), positionBean.getTimes());
				
				positionService.insert(positionList);
			}
			
			return WebResponse.success();
			
		}catch(Exception ex) {
			logger.error("", ex);
			return WebResponse.failure();
		}
	}

	@DataSecurityAnnotation(dataRole=EnumConstants.DATA_ROLE_FRIEND, 
			paramType=EnumConstants.PARAM_TYPE_QUERY, paramKey="userId")
	@ApiOperation(value="getPoints", notes="获取某段时间的轨迹点")
	@ApiImplicitParams({
		@ApiImplicitParam(name="userId", paramType="query", required=true, dataType="Integer"),
		@ApiImplicitParam(name="startDate", paramType="query", required=true, dataType="Date"),
		@ApiImplicitParam(name="endDate", paramType="query", required=true, dataType="Date")
	})
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
				return WebResponse.success(JsonUtil.object2ArrayString(list));
			}else {
				return WebResponse.success("[]");
			}
		}catch(Exception ex) {
			logger.error("", ex);
			return WebResponse.failure(ResponseCode.FAILURE, "获取数据失败");
		}
    }


	@DataSecurityAnnotation(paramType=EnumConstants.PARAM_TYPE_PATH, paramIndex=0)
	@ApiOperation(value="cleanup", notes="清除历史轨迹数据")
	@ApiImplicitParams({
		@ApiImplicitParam(name="userId", paramType="path", required=true, dataType="Integer"),
		@ApiImplicitParam(name="monthsAgo", paramType="path", required=true, dataType="Integer")
	})
	@RequestMapping(value="/cleanup/{userId}/{monthsAgo}", method=RequestMethod.POST)
    public WebResponse cleanup(@PathVariable Integer userId, @PathVariable Integer monthsAgo) {
		positionService.cleanup(userId, monthsAgo);
		return WebResponse.success();
    }
	
}
