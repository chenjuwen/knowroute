package com.heasy.knowroute.service;

import java.util.List;

import com.heasy.knowroute.bean.FixedPointInfoBean;

public interface FixedPointInfoService{
	List<FixedPointInfoBean> list(int userId, int categoryId);
	int insert(FixedPointInfoBean bean);
	boolean update(FixedPointInfoBean bean);
	boolean deleteById(int userId, int id);
	boolean deleteByCategory(int userId, int categoryId);
	boolean deleteByUser(int userId);
}
