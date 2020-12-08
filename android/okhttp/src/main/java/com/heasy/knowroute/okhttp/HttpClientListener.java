package com.heasy.knowroute.okhttp;

import okhttp3.Response;

public interface HttpClientListener {
	void onReponse(Response response);
}
