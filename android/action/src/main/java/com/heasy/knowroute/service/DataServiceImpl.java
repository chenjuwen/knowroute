package com.heasy.knowroute.service;

import com.alibaba.fastjson.JSONArray;
import com.heasy.knowroute.action.ResponseBean;
import com.heasy.knowroute.action.ResponseCode;
import com.heasy.knowroute.bean.DataBean;
import com.heasy.knowroute.core.service.AbstractService;
import com.heasy.knowroute.core.utils.FastjsonUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2020/9/26.
 */
public class DataServiceImpl extends AbstractService implements DataService {
    private static final Logger logger = LoggerFactory.getLogger(DataServiceImpl.class);

    @Override
    public void init() {

    }

    @Override
    public List<DataBean> getAllData() {
        List<DataBean> list = new ArrayList<>();
        try{
            String requestUrl = "/data/getAllData";
            ResponseBean responseBean = HttpService.httpGet(getHeasyContext(), requestUrl);
            if(responseBean.getCode() == ResponseCode.SUCCESS.code()){
                JSONArray jsonArray = JSONArray.parseArray((String)responseBean.getData());
                list = jsonArray.toJavaList(DataBean.class);
            }
        }catch (Exception ex){
            logger.error("", ex);
        }
        return list;
    }

    @Override
    public DataBean getByPeriod(String period) {
        try{
            String requestUrl = "/data/getByPeriod?period=" + period;
            ResponseBean responseBean = HttpService.httpGet(getHeasyContext(), requestUrl);
            if(responseBean.getCode() == ResponseCode.SUCCESS.code()){
                return FastjsonUtil.string2JavaBean((String)responseBean.getData(), DataBean.class);
            }
        }catch (Exception ex){
            logger.error("", ex);
        }
        return null;
    }

    @Override
    public boolean deleteByPeriod(String period) {
        try{
            String requestUrl = "/data/deleteByPeriod?period=" + period;
            ResponseBean responseBean = HttpService.httpGet(getHeasyContext(), requestUrl);
            if(responseBean.getCode() == ResponseCode.SUCCESS.code()){
                return true;
            }
        }catch (Exception ex){
            logger.error("", ex);
        }
        return false;
    }

    @Override
    public boolean add(String period, String data) {
        try{
            String requestUrl = "/data/add?period=" + period + "&data=" + data;
            ResponseBean responseBean = HttpService.httpGet(getHeasyContext(), requestUrl);
            if(responseBean.getCode() == ResponseCode.SUCCESS.code()){
                return true;
            }
        }catch (Exception ex){
            logger.error("", ex);
        }
        return false;
    }

    @Override
    public boolean update(String period, String data) {
        try{
            String requestUrl = "/data/update?period=" + period + "&data=" + data;
            ResponseBean responseBean = HttpService.httpGet(getHeasyContext(), requestUrl);
            if(responseBean.getCode() == ResponseCode.SUCCESS.code()){
                return true;
            }
        }catch (Exception ex){
            logger.error("", ex);
        }
        return false;
    }
}