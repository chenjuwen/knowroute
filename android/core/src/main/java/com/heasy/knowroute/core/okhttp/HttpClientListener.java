package com.heasy.knowroute.core.okhttp;

import java.io.IOException;

import okhttp3.Response;

public interface HttpClientListener {
	void onResponse(Response response);
	void onFailure(IOException ex);
}
