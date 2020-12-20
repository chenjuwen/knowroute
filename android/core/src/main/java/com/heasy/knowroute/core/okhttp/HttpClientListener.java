package com.heasy.knowroute.core.okhttp;

import okhttp3.Response;

public interface HttpClientListener {
	void onReponse(Response response);
}
