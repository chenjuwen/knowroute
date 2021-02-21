package com.heasy.knowroute.service;

import java.util.Date;
import java.util.List;

import com.heasy.knowroute.bean.PointBean;
import com.heasy.knowroute.bean.PositionBean;

public interface PositionService {
	public void insert(List<PositionBean> positionList);
	public List<PointBean> getPoints(int userId, Date startDate, Date endDate);
	
	/**
	 * 清除历史轨迹数据
	 * @param userId 用户ID
	 * @param monthsAgo 几个月前的，0 表示清除全部数据
	 */
	public void cleanup(int userId, int monthsAgo);
}
