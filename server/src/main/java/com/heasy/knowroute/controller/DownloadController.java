package com.heasy.knowroute.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.heasy.knowroute.common.RequestLimitAnnotation;
import com.heasy.knowroute.utils.StringUtil;

import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@Controller
@RequestLimitAnnotation
public class DownloadController {
	private static Logger logger = LoggerFactory.getLogger(DownloadController.class);
	
	@RequestMapping(value="/download", method=RequestMethod.GET)
	public void downloadFile(HttpServletRequest request, HttpServletResponse response){
		String basePath = "./download";
		
		String filename = StringUtil.trimToEmpty(request.getParameter("filename"));
		logger.debug("filename=" + filename);
		
		if(StringUtil.isNotEmpty(filename)){
			response.setHeader("content-type", "application/octet-stream");
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment;filename=" + filename);
			
			byte[] buff = new byte[4096];
		    BufferedInputStream inStream = null;
		    OutputStream outStream = null;
		    try {
		    	outStream = response.getOutputStream();
		    	inStream = new BufferedInputStream(new FileInputStream(new File(basePath + File.separator + filename)));
		    	int i = inStream.read(buff);
		    	while (i != -1) {
		    		outStream.write(buff, 0, i);
		    		i = inStream.read(buff);
		    	}
		    	
		    } catch (IOException ex) {
				logger.error("download file error", ex);
		    } finally {
		    	if (inStream != null) {
		    		try {
		    			inStream.close();
		    		} catch (IOException ex) {
		    			ex.printStackTrace();
		    		}
		    	}
		    	
		    	if (outStream != null) {
		    		try {
		    			outStream.close();
		    		} catch (IOException ex) {
		    			ex.printStackTrace();
		    		}
		    	}
		    }
		}else {
			OutputStream outStream = null;
		    try {
		    	outStream = response.getOutputStream();
		    	outStream.write("param filename is empty".getBytes());
		    } catch (IOException ex) {
				logger.error("response error", ex);
		    } finally {
		    	if (outStream != null) {
		    		try {
		    			outStream.close();
		    		} catch (IOException ex) {
		    			ex.printStackTrace();
		    		}
		    	}
		    }
		}
	}
	
}
