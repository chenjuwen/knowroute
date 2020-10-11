package com.heasy.knowroute.service;

import com.heasy.knowroute.bean.DataBean;
import com.heasy.knowroute.core.service.Service;

import java.util.List;

/**
 * Created by Administrator on 2020/9/26.
 */
public interface DataService extends Service{
    List<DataBean> getAllData();
    DataBean getByPeriod(String period);
    boolean deleteByPeriod(String period);
    boolean add(String period, String data);
    boolean update(String period, String data);
}
