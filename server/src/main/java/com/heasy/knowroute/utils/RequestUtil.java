package com.heasy.knowroute.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;


public class RequestUtil {
	/**
	 * 获取本机IP地址
	 */
	public static String getLocalIp(){
		String hostAddress = "";
		String osName = System.getProperty("os.name").toLowerCase();
		if(osName.indexOf("windows") >= 0){
			try {     
				hostAddress = InetAddress.getLocalHost().getHostAddress();
			} catch (Exception ex) {  
			   ex.printStackTrace();
			} 
		}else{
			try {
				for (Enumeration<NetworkInterface> enum1 = NetworkInterface.getNetworkInterfaces(); enum1.hasMoreElements();) {
			    	NetworkInterface networkInterface = enum1.nextElement();
			    	for (Enumeration<InetAddress> enum2 = networkInterface.getInetAddresses(); enum2.hasMoreElements();) {
			    		InetAddress inetAddress = enum2.nextElement();
			    		if(inetAddress.getHostAddress().split("\\.").length == 4){ //IPv4
			    			if (!inetAddress.isAnyLocalAddress() && !inetAddress.isLoopbackAddress()
			            		   && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress()) {
			    				hostAddress = inetAddress.getHostAddress();
			            	   	break;
			    			}
			    		}
			    	}
		           
			    	if(StringUtil.isNotEmpty(hostAddress)){
			    		break;
		           	}
				}
			} catch (Exception ex) {
			   ex.printStackTrace();
			}
		}
		
		return hostAddress;
	}
	
    /**
     * 获取客户端的IP地址
     */
    public static String getClientIp(HttpServletRequest request) {
		String clientIp = "";
    	try{
	        clientIp = request.getHeader("x-forwarded-for");
	
	        if (StringUtil.isEmpty(clientIp) || "unknown".equalsIgnoreCase(clientIp)) {
	            clientIp = request.getHeader("Proxy-Client-IP");
	        }
	
	        if (StringUtil.isEmpty(clientIp) || "unknown".equalsIgnoreCase(clientIp)) {
	            clientIp = request.getHeader("WL-Proxy-Client-IP");
	        }
	
	        if (StringUtil.isEmpty(clientIp) || "unknown".equalsIgnoreCase(clientIp)) {
	            clientIp = request.getHeader("HTTP_CLIENT_IP");
	        }
	
	        if (StringUtil.isEmpty(clientIp) || "unknown".equalsIgnoreCase(clientIp)) {
	            clientIp = request.getHeader("HTTP_X_FORWARDED_FOR");
	        }
	
	        if (StringUtil.isEmpty(clientIp) || "unknown".equalsIgnoreCase(clientIp)) {
	            clientIp = request.getHeader("X-Real-IP");
	        }
	
	        if (StringUtil.isEmpty(clientIp)) {
	            clientIp = request.getRemoteAddr();
	        }
	        
	        //多个IP时只取第一个非unknown的有效IP字符串
	        if(StringUtil.isNotEmpty(clientIp) && clientIp.indexOf(",") > -1) {
	        	String[] ipArray = clientIp.split(",");
				for (int i = 0; i < ipArray.length; i++) {
					if (StringUtil.isNotEmpty(ipArray[i]) && !"unknown".equalsIgnoreCase(ipArray[i])) {
						clientIp = ipArray[i];
						break;
					}
				}
	        }
    	}catch(Exception ex){
    		ex.printStackTrace();
    		clientIp = request.getRemoteAddr();
    	}
        
        return clientIp;
    }
    
    /**
     * 获取web系统的根URL地址，比如 https://localhost:8080/webapp
     */
    public static String getBaseUrl(HttpServletRequest request){
    	String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    	return url;
    }
    
    /**
     * 获取当前的请求URL地址，比如 https://localhost:8080/webapp/queryUser.action
     */
    public static String getRequestURL(HttpServletRequest request){
    	return request.getRequestURL().toString();
    }
    
    public static boolean isAjaxRequest(HttpServletRequest request) {
		if (request.getHeader("accept").indexOf("application/json") > -1 || 
				(request.getHeader("X-Requested-With") != null 
					&& request.getHeader("X-Requested-With").equals("XMLHttpRequest"))
		   ) {
			return true;
		}
		return false;
	}

}
