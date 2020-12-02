package com.heasy.knowroute.http;

import com.heasy.knowroute.core.utils.StringUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class RequestBuilder {
	private Request.Builder builder;

	private Map<String, String> formParamMap = new HashMap<>();
	private Map<String, FileWrapper> fileParamMap = new HashMap<>();
	private RequestBody requestBody;

	public RequestBuilder(){
		builder = new Request.Builder();
	}
	
	public RequestBuilder url(String url){
		builder.url(url);
		return this;
	}
	
	public RequestBuilder addHeader(String name, String value){
		builder.addHeader(name, value);
		return this;
	}
	
	public RequestBuilder addFormParam(String name, String value){
		this.formParamMap.put(name, value);
		return this;
	}

	public RequestBuilder setFormParamMap(Map<String, String> paramMap){
		this.formParamMap = paramMap;
		return this;
	}
	
	public RequestBuilder addFileParam(FileWrapper fileWrapper){
		this.fileParamMap.put(StringUtil.getUUIDString(), fileWrapper);
		return this;
	}
	
	public RequestBuilder addFileParam(String fileFieldKeyName, String filename, MediaType mediaType, File file){
		this.fileParamMap.put(StringUtil.getUUIDString(), new FileWrapper(fileFieldKeyName, filename, mediaType, file));
		return this;
	}

	public RequestBuilder requestBody(RequestBody requestBody){
		this.requestBody = requestBody;
		return this;
	}
	
	private RequestBody createRequestBody(){
		if (fileParamMap != null && !fileParamMap.isEmpty()) {
			MultipartBody.Builder builder = new MultipartBody.Builder();
			builder.setType(MultipartBody.FORM);

			for (String key : fileParamMap.keySet()) {
				FileWrapper fileWrapper = fileParamMap.get(key);
				builder.addFormDataPart(fileWrapper.getName(), fileWrapper.getFilename(), RequestBody.create(fileWrapper.getMediaType(), fileWrapper.getFile()));
			}

			if (formParamMap != null && !formParamMap.isEmpty()) {
				for (String key : formParamMap.keySet()) {
					builder.addFormDataPart(key, formParamMap.get(key));
				}
			}

			return builder.build();

		} else if (formParamMap != null && !formParamMap.isEmpty()) {
			FormBody.Builder builder = new FormBody.Builder();
			for (String key : formParamMap.keySet()) {
				builder.add(key, formParamMap.get(key));
			}

			return builder.build();
		}else {
			return new FormBody.Builder().build();
		}
	}

	public Request build(){
		if(this.requestBody != null) {
			builder.post(this.requestBody);
		}else{
			builder.post(createRequestBody());
		}

		return builder.build();
	}
	
}
