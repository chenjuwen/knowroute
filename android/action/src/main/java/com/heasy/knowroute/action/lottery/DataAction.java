package com.heasy.knowroute.action.lottery;

import com.alibaba.fastjson.JSONObject;
import com.heasy.knowroute.bean.DataBean;
import com.heasy.knowroute.core.Constants;
import com.heasy.knowroute.core.HeasyContext;
import com.heasy.knowroute.core.annotation.JSActionAnnotation;
import com.heasy.knowroute.core.utils.FastjsonUtil;
import com.heasy.knowroute.core.utils.ParameterUtil;
import com.heasy.knowroute.core.webview.Action;
import com.heasy.knowroute.service.DataService;
import com.heasy.knowroute.service.DataServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JSActionAnnotation(name = "DataAction")
public class DataAction implements Action {
    private static final Logger logger = LoggerFactory.getLogger(DataAction.class);

    @Override
    public String execute(HeasyContext heasyContext, String jsonData, String extend) {
        JSONObject jsonObject = FastjsonUtil.string2JSONObject(jsonData);
        DataService dataService = heasyContext.getServiceEngine().getService(DataServiceImpl.class);

        if("list".equalsIgnoreCase(extend)) {
            String maxRecordCount = FastjsonUtil.getString(jsonObject, "maxRecordCount"); //返回的最大记录数
            int MAX_RECORD_COUNT = Integer.parseInt(maxRecordCount);


            List<DataBean> list = dataService.getAllData();
            while(list != null && MAX_RECORD_COUNT > 0 && list.size() > MAX_RECORD_COUNT){
                list.remove(0);
            }

            return FastjsonUtil.object2String(list);
        }else if("delete".equalsIgnoreCase(extend)){
            String period = FastjsonUtil.getString(jsonObject, "period");
            boolean b = dataService.deleteByPeriod(period);
            if(!b){
                return "删除数据失败！";
            }
        }else if("save".equalsIgnoreCase(extend)){
            String period = FastjsonUtil.getString(jsonObject, "period");
            String data = FastjsonUtil.getString(jsonObject, "data");

            DataBean bean = dataService.getByPeriod(period);
            if(bean != null){
                return "期数 " + period + " 已存在！";
            }

            boolean b = dataService.add(period, data);
            if(!b){
                return "添加数据失败！";
            }
        }else if("getByPeriod".equalsIgnoreCase(extend)){
            String period = FastjsonUtil.getString(jsonObject, "period");
            String url = FastjsonUtil.getString(jsonObject, "url");

            DataBean bean = dataService.getByPeriod(period);

            Map<String, String> params = new HashMap<>();
            params.put("period", bean.getPeriod());
            params.put("data", bean.getData());

            return heasyContext.getJsInterface().pageTransfer(url, ParameterUtil.toParamString(params, false));
        }else if("update".equalsIgnoreCase(extend)){
            String period = FastjsonUtil.getString(jsonObject, "period");
            String data = FastjsonUtil.getString(jsonObject, "data");

            DataBean bean = dataService.getByPeriod(period);
            if(bean == null){
                return "数据不存在！";
            }

            boolean b = dataService.update(period, data);
            if(!b){
                return "修改数据失败！";
            }
        }

        return Constants.SUCCESS;
    }
}
