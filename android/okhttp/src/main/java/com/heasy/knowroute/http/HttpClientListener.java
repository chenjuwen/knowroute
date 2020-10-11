package com.heasy.knowroute.http;

import okhttp3.Response;

public interface HttpClientListener {
	void onReponse(Response response);
}
