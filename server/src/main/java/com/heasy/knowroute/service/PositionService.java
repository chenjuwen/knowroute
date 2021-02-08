package com.heasy.knowroute.service;

import java.util.Date;
import java.util.List;

import com.heasy.knowroute.bean.PointBean;
import com.heasy.knowroute.bean.PositionBean;

public interface PositionService {
	public void insert(List<PositionBean> positionList);
	public List<PointBean> getPoints(int userId, Date startDate, Date endDate);
}
