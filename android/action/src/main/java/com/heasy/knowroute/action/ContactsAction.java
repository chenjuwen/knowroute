package com.heasy.knowroute.action;

import com.alibaba.fastjson.JSONObject;
import com.heasy.knowroute.core.Constants;
import com.heasy.knowroute.core.HeasyContext;
import com.heasy.knowroute.core.annotation.JSActionAnnotation;
import com.heasy.knowroute.core.utils.FastjsonUtil;
import com.heasy.knowroute.core.webview.Action;
import com.heasy.knowroute.service.HttpService;
import com.heasy.knowroute.service.LoginService;
import com.heasy.knowroute.service.LoginServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2020/10/23.
 */
@JSActionAnnotation(name = "ContactsAction")
public class ContactsAction implements Action {
    private static final Logger logger = LoggerFactory.getLogger(ContactsAction.class);

    @Override
    public String execute(HeasyContext heasyContext, String jsonData, String extend) {
        JSONObject jsonObject = FastjsonUtil.string2JSONObject(jsonData);
        LoginService loginService = heasyContext.getServiceEngine().getService(LoginServiceImpl.class);

        if("save".equalsIgnoreCase(extend)){
            String contact_name = FastjsonUtil.getString(jsonObject, "contact_name");
            String contact_phone = FastjsonUtil.getString(jsonObject, "contact_phone");

            if(contact_phone.equalsIgnoreCase(loginService.getPhone())){
                return "不能添加自己为紧急联系人";
            }

            String url = "contact/save";
            String data = FastjsonUtil.toJSONString("userId", String.valueOf(loginService.getUserId()),
                    "contactName", contact_name,
                    "contactPhone", contact_phone);

            ResponseBean responseBean = HttpService.httpPost(heasyContext, url, data);
            if(responseBean.getCode() == ResponseCode.SUCCESS.code()){
                return Constants.SUCCESS;
            }else{
                return HttpService.getFailureMessage(responseBean);
            }

        }else if("update".equalsIgnoreCase(extend)){
            String id = FastjsonUtil.getString(jsonObject, "id");
            String contact_name = FastjsonUtil.getString(jsonObject, "contact_name");
            String contact_phone = FastjsonUtil.getString(jsonObject, "contact_phone");

            if(contact_phone.equalsIgnoreCase(loginService.getPhone())){
                return "不能添加自己为紧急联系人";
            }

            String url = "contact/update";
            String data = FastjsonUtil.toJSONString("id", String.valueOf(id),
                    "userId", String.valueOf(loginService.getUserId()),
                    "contactName", contact_name,
                    "contactPhone", contact_phone);

            ResponseBean responseBean = HttpService.httpPost(heasyContext, url, data);
            if(responseBean.getCode() == ResponseCode.SUCCESS.code()){
                return Constants.SUCCESS;
            }else{
                return HttpService.getFailureMessage(responseBean);
            }

        }else if("delete".equalsIgnoreCase(extend)){
            String id = FastjsonUtil.getString(jsonObject, "id");
            String url = "contact/delete?id=" + id;

            ResponseBean responseBean = HttpService.httpGet(heasyContext, url);
            if(responseBean.getCode() == ResponseCode.SUCCESS.code()){
                return Constants.SUCCESS;
            }else{
                return HttpService.getFailureMessage(responseBean);
            }

        }else if("getAll".equalsIgnoreCase(extend)){
            String url = "contact/getAll?userId=" + loginService.getUserId();
            ResponseBean responseBean = HttpService.httpGet(heasyContext, url);
            if(responseBean.getCode() == ResponseCode.SUCCESS.code()){
                return (String)responseBean.getData();
            }else{
                return "[]";
            }
        }

        return Constants.SUCCESS;
    }
}
