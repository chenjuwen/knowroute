package com.heasy.knowroute.action.business;

import com.alibaba.fastjson.JSONObject;
import com.heasy.knowroute.bean.ResponseCode;
import com.heasy.knowroute.core.Constants;
import com.heasy.knowroute.core.HeasyContext;
import com.heasy.knowroute.core.annotation.JSActionAnnotation;
import com.heasy.knowroute.core.utils.FastjsonUtil;
import com.heasy.knowroute.core.webview.Action;
import com.heasy.knowroute.service.backend.ContactAPI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JSActionAnnotation(name = "ContactsAction")
public class ContactsAction implements Action {
    private static final Logger logger = LoggerFactory.getLogger(ContactsAction.class);

    @Override
    public String execute(HeasyContext heasyContext, String jsonData, String extend) {
        JSONObject jsonObject = FastjsonUtil.string2JSONObject(jsonData);

        if("save".equalsIgnoreCase(extend)){
            try{
                String contact_name = FastjsonUtil.getString(jsonObject, "contact_name");
                String contact_phone = FastjsonUtil.getString(jsonObject, "contact_phone");
                return ContactAPI.save(contact_name, contact_phone);
            }catch (Exception ex){
                logger.error("", ex);
                return ResponseCode.FAILURE.message();
            }

        }else if("update".equalsIgnoreCase(extend)){
            try {
                String id = FastjsonUtil.getString(jsonObject, "id");
                String contact_name = FastjsonUtil.getString(jsonObject, "contact_name");
                String contact_phone = FastjsonUtil.getString(jsonObject, "contact_phone");
                return ContactAPI.update(id, contact_name, contact_phone);
            }catch (Exception ex){
                logger.error("", ex);
                return ResponseCode.FAILURE.message();
            }

        }else if("delete".equalsIgnoreCase(extend)){
            try {
                String id = FastjsonUtil.getString(jsonObject, "id");
                return ContactAPI.delete(id);
            }catch (Exception ex){
                logger.error("", ex);
                return ResponseCode.FAILURE.message();
            }

        }else if("getAll".equalsIgnoreCase(extend)){
            try {
                return ContactAPI.getAll();
            }catch (Exception ex){
                logger.error("", ex);
            }
            return "[]";

        }else if("sendHelp".equalsIgnoreCase(extend)){
            try {
                String phones = FastjsonUtil.getString(jsonObject, "phones");
                return ContactAPI.notify(phones);
            }catch (Exception ex){
                logger.error("", ex);
                return "发送求助信息失败";
            }
        }

        return Constants.SUCCESS;
    }
}
