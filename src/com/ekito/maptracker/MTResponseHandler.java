package com.ekito.maptracker;

import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;

public class MTResponseHandler extends AsyncHttpResponseHandler {
	
	private TextView mStatusTV;

	public MTResponseHandler(TextView statusTV) {
		mStatusTV = statusTV;
	}
	
	@Override
	public void onStart() {
		mStatusTV.setText("sending last result to server...");
		super.onStart();
	}
	
	@Override
	public void onFailure(Throwable arg0, String arg1) {
		mStatusTV.setText("server returned "+arg1+" for last result");
		super.onFailure(arg0, arg1);
	}
	
	@Override
	public void onFinish() {
		mStatusTV.setText("last result sent to server with success");
		super.onFinish();
	}
}
