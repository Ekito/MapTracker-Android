package com.ekito.maptracker;

//Static wrapper library around AsyncHttpClient

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class MTRestClient {
	
	private static final String BASE_URL = "http://192.168.5.116:9000/";

	private AsyncHttpClient client;
	
	public MTRestClient() {
		client = new AsyncHttpClient();
		client.addHeader("Content-Type", "application/x-www-form-urlencoded");
	}

	public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		client.get(getAbsoluteUrl(url), params, responseHandler);
	}

	public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		client.post(getAbsoluteUrl(url), params, responseHandler);
	}

	private static String getAbsoluteUrl(String relativeUrl) {
		return BASE_URL + relativeUrl;
	}
}
