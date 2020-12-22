package com.heasy.knowroute.service;

import java.util.List;

import com.heasy.knowroute.bean.FixedPointCategoryBean;

public interface FixedPointCategoryService {
	public List<FixedPointCategoryBean> list(int userId);
	public boolean insert(int userId, String name);
	public boolean update(int id, String name);
	public boolean delete(int id);
	public boolean topping(int id);
	public boolean cancelTopping(int id);
}
