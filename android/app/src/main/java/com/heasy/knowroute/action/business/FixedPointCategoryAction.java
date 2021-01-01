package com.heasy.knowroute.action.business;

import com.alibaba.fastjson.JSONObject;
import com.heasy.knowroute.bean.ResponseCode;
import com.heasy.knowroute.core.Constants;
import com.heasy.knowroute.core.HeasyContext;
import com.heasy.knowroute.core.annotation.JSActionAnnotation;
import com.heasy.knowroute.core.utils.FastjsonUtil;
import com.heasy.knowroute.core.webview.Action;
import com.heasy.knowroute.service.LoginService;
import com.heasy.knowroute.service.LoginServiceImpl;
import com.heasy.knowroute.service.backend.FixedPointCategoryAPI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JSActionAnnotation(name = "FixedPointCategoryAction")
public class FixedPointCategoryAction implements Action {
    private static final Logger logger = LoggerFactory.getLogger(FixedPointCategoryAction.class);

    @Override
    public String execute(HeasyContext heasyContext, String jsonData, String extend) {
        JSONObject jsonObject = FastjsonUtil.string2JSONObject(jsonData);
        LoginService loginService = heasyContext.getServiceEngine().getService(LoginServiceImpl.class);

        if ("list".equalsIgnoreCase(extend)){
            try {
                return FixedPointCategoryAPI.list();
            }catch (Exception ex){
                logger.error("", ex);
                return "[]";
            }

        }else if("insert".equalsIgnoreCase(extend)){
            try {
                String name = FastjsonUtil.getString(jsonObject, "name");
                return FixedPointCategoryAPI.insert(name);
            }catch (Exception ex){
                logger.error("", ex);
                return ResponseCode.FAILURE.message();
            }

        }else if("update".equalsIgnoreCase(extend)){
            try {
                String id = FastjsonUtil.getString(jsonObject, "id");
                String name = FastjsonUtil.getString(jsonObject, "name");
                return FixedPointCategoryAPI.update(id, name);
            }catch (Exception ex){
                logger.error("", ex);
                return ResponseCode.FAILURE.message();
            }

        }else if("delete".equalsIgnoreCase(extend)){
            try {
                String id = FastjsonUtil.getString(jsonObject, "id");
                return FixedPointCategoryAPI.delete(id);
            }catch (Exception ex){
                logger.error("", ex);
                return ResponseCode.FAILURE.message();
            }

        }else if("topping".equalsIgnoreCase(extend)){
            try {
                String id = FastjsonUtil.getString(jsonObject, "id");
                return FixedPointCategoryAPI.topping(id);
            }catch (Exception ex){
                logger.error("", ex);
                return ResponseCode.FAILURE.message();
            }

        }else if("cancelTopping".equalsIgnoreCase(extend)){
            try {
                String id = FastjsonUtil.getString(jsonObject, "id");
                return FixedPointCategoryAPI.cancelTopping(id);
            }catch (Exception ex){
                logger.error("", ex);
                return ResponseCode.FAILURE.message();
            }
        }

        return Constants.SUCCESS;
    }
}
