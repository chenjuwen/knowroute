package com.heasy.knowroute.common;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * HttpServletRequest的getInputStream()和getReader()都只能读取一次。
 * 解决办法：先将 Request Body 保存，然后通过 Servlet 自带的 HttpServletRequestWrapper 类覆盖 getReader() 和 getInputStream() 方法，使流从保存的body读取
 */
public class CustomHttpServletRequestWrapper extends HttpServletRequestWrapper {
	private String body;
	
	public CustomHttpServletRequestWrapper(HttpServletRequest request) throws IOException{
		super(request);

        StringBuilder sb = new StringBuilder();
		BufferedReader bufferedReader = request.getReader();
        String line = bufferedReader.readLine();
        while(line != null){
            sb.append(line);
            line = bufferedReader.readLine();
        }
        bufferedReader.close();
        
        body = sb.toString();
	}
	
	@Override
	public ServletInputStream getInputStream() throws IOException {
		final ByteArrayInputStream inputStream = new ByteArrayInputStream(body.getBytes());
        return new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return inputStream.read();
            }
            
            @Override
            public boolean isFinished() {
            	return false;
            }
            
            @Override
            public boolean isReady() {
            	return false;
            }
            
            @Override
            public void setReadListener(ReadListener listener) {
            	
            }
        };
	}
	
	@Override
	public BufferedReader getReader() throws IOException {
		return new BufferedReader(new InputStreamReader(getInputStream()));
	}
	
	public String getRequestBody() {
		return body;
	}
}
