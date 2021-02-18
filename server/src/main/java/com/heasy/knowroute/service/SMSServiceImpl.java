package com.heasy.knowroute.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.heasy.knowroute.utils.JsonUtil;
import com.heasy.knowroute.utils.MD5Util;
import com.heasy.knowroute.utils.ParameterUtil;

/**
 * 短信发送服务
 * 		乐讯通短信API： http://www.loktong.com/sms/smsy
 */
@Service
public class SMSServiceImpl implements SMSService {
    private static final Logger logger = LoggerFactory.getLogger(SMSServiceImpl.class);
	
	@Value("${sms.ignoreSend}")
	private boolean ignoreSend = true;
	
	@Value("${sms.apiAddress}")
	private String apiAddress;
	
	@Value("${sms.username}")
	private String username;
	
	@Value("${sms.password}")
	private String password;
	
	@Value("${sms.token}")
	private String token;
	
	@Value("${sms.templateid}")
	private String templateid;
	
	/**
	 * 发送验证码
	 * @param phone 接收者手机号码
	 * @param captcha 验证码
	 */
    @Override
    public boolean sendVerificationCode(String phone, String captcha) {
    	try {
    		if(ignoreSend) {
    			logger.debug("发送验证码短信 设置为忽略，即不发短信");
    			return true;
    		}
    		
    		String timestamp = String.valueOf(System.currentTimeMillis());
    		
	    	String beforSign = ParameterUtil.toParamString(
	    			"action", "sendtemplate",
	    			"username", username,
	    			"password", password,
	    			"token", token,
	    			"timestamp", timestamp);
	    	
	    	String postData = ParameterUtil.toParamString(
	    			"action", "sendtemplate",
	    			"username", username,
	    			"password", password,
	    			"token", token,
	    			"timestamp", timestamp,
	    			"templateid", templateid,
	    			"param", phone + "|" + captcha,
	    			"sign", MD5Util.md5(beforSign));
	    	
			String result = sendPost(apiAddress, postData);
			logger.debug(result);
			
			String status = JsonUtil.getString(JsonUtil.string2object(result), "returnstatus", "");
			if("success".equalsIgnoreCase(status)) {
				return true;
			}
    	}catch(Exception ex) {
    		logger.error("发送短信验证码失败", ex);
    	}
		return false;
    }
    
    private String sendPost(String url, String param) throws Exception{
        PrintWriter out = null;
        BufferedReader in = null;
        StringBuffer sb = new StringBuffer();
        try {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            
            out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.flush();
            
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
            	sb.append(line);
            }
        } finally {
            try{
                if(out != null) out.close();
                if(in != null) in.close();
            }catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return sb.toString();
    }
    
    @Override
    public boolean isIgnoreSend() {
    	return this.ignoreSend;
    }
    
    @Override
    public void setIgnoreSend(boolean ignoreSend) {
    	this.ignoreSend = ignoreSend;
    }
}
