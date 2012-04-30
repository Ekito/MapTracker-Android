package com.ekito.maptracker;

import java.sql.Time;
import java.util.Calendar;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;

public class MTActivity extends Activity implements LocationListener {

	enum CostStrategy {LOW, HIGH}

	private static final String TAG = "MTActivity";
	private static final int NOTIFICATION_ID = 1234;

	private static final int LOC_MIN_TIME = 0;
	private static final int LOC_MIN_DIST = 0;
	private static final CostStrategy DEFAULT_STRATEGY = CostStrategy.LOW;

	private MTDeviceUuidFactory mUuidFactory;		// used to get device id
	
	private LocationManager mLocationManager;
	private MTRestClient mClient;
	private MTResponseHandler mHandler;
	private CostStrategy mCostStrategy;
	
	private TextView mDeviceID, mProviderTV, mTimestampTV, mLatitudeTV, mLongitudeTV, mSentToServerTV;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		mDeviceID = (TextView) findViewById(R.id.device_id);
		mProviderTV = (TextView) findViewById(R.id.provider);
		mTimestampTV = (TextView) findViewById(R.id.timestamp);
		mLatitudeTV = (TextView) findViewById(R.id.latitude);
		mLongitudeTV = (TextView) findViewById(R.id.longitude);
		mSentToServerTV = (TextView) findViewById(R.id.sent_to_server);

		mUuidFactory = new MTDeviceUuidFactory(this);
		mDeviceID.setText(mUuidFactory.getDeviceUuid().toString());
		
		mClient = new MTRestClient();
		mHandler = new MTResponseHandler(mSentToServerTV);
		mCostStrategy = DEFAULT_STRATEGY;
	}

	@Override
	protected void onPause() {
		super.onPause();
		stopTracking();
	}

	@Override
	protected void onResume() {
		super.onResume();
		startTracking();
	}

	/** register locationlistener if it was unregistered */
	private void startTracking() {
		Log.d(TAG, "startTracking: request received");
		if (mLocationManager == null) {
			
			// get the right location provider following current strategy
			mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
			String provider = null;
			switch(mCostStrategy) {
			case LOW:
				provider = mLocationManager.getBestProvider(createCoarseCriteria(),true);
				break;
			case HIGH:
				provider = mLocationManager.getBestProvider(createFineCriteria(),true);
				break;
			}

			// provider can be null in case we have no location tracking enabled
			if (provider == null) {
				mLocationManager = null;
				mProviderTV.setText("No providers");
			} else { // otherwise, start tracking
				mLocationManager.requestLocationUpdates(provider, LOC_MIN_TIME, LOC_MIN_DIST, this);
				mProviderTV.setText("Provider: "+provider);
				Log.d(TAG, "startTracking: started");
			}
		}
	}

	/** unregister locationlistener if it was registered */
	private void stopTracking() {
		Log.d(TAG, "stopTracking: request received");
		if (mLocationManager != null) {
			Log.d(TAG, "startTracking: request received");
			mLocationManager.removeUpdates(this);
			mLocationManager = null;
			Log.d(TAG, "stopTracking: stopped");
		}
	}

	/** this criteria will settle for less accuracy, high power, and cost */
	public static Criteria createCoarseCriteria() {
		Criteria c = new Criteria();
		c.setAccuracy(Criteria.ACCURACY_COARSE);
		c.setAltitudeRequired(false);
		c.setBearingRequired(false);
		c.setSpeedRequired(false);
		c.setCostAllowed(true);
		c.setPowerRequirement(Criteria.POWER_HIGH);
		return c;
	}

	/** this criteria needs high accuracy, high power, and cost */
	public static Criteria createFineCriteria() {
		Criteria c = new Criteria();
		c.setAccuracy(Criteria.ACCURACY_FINE);
		c.setAltitudeRequired(false);
		c.setBearingRequired(false);
		c.setSpeedRequired(false);
		c.setCostAllowed(true);
		c.setPowerRequirement(Criteria.POWER_HIGH);
		return c;
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.d(TAG, "onLocationChanged: received location update");

		// display notification
		Intent notificationIntent = new Intent(this, MTActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this,
				0, notificationIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

		builder.setContentIntent(contentIntent)
		.setSmallIcon(R.drawable.ic_launcher)
		.setWhen(System.currentTimeMillis())
		.setAutoCancel(true)
		.setTicker("Locaton updated")
		.setContentText("Timestamped " + new Time(location.getTime()).toString());
		Notification n = builder.getNotification();

		nm.notify(NOTIFICATION_ID, n);

		// send request to server
		RequestParams params = new RequestParams();
		params.put("id", mUuidFactory.getDeviceUuid().toString());
		params.put("latitude", Double.toString(location.getLatitude()));
		params.put("longitude", Double.toString(location.getLongitude()));
		params.put("timestamp", Long.toString(Calendar.getInstance().getTimeInMillis()));

		mClient.post("moveTo", params, mHandler);
		
		// refresh UI
		refreshUI(location);
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.d(TAG, "onProviderDisabled "+provider);
		stopTracking();
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.d(TAG, "onProviderEnabled "+provider);
		startTracking();
	}

	/** reload providers */
	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		Log.d(TAG, "onStatusChanged");
		stopTracking();
		startTracking();
	}
	
	private void refreshUI(Location location) {
		mTimestampTV.setText("Last update at: "+new Time(location.getTime()).toString());
		mLatitudeTV.setText("Latitude: "+Double.toString(location.getLatitude()));
		mLongitudeTV.setText("Longitude: "+Double.toString(location.getLongitude()));
	}
}