package se.mah.helmet;

import se.mah.helmet.storage.LocDbAdapter;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class LocLogService extends Service {
	private static final String TAG = LocLogService.class.getSimpleName();
	public static final String KEY_MIN_TIME = "minTime";
	public static final String KEY_MIN_DISTANCE = "minDistance";
	public static final long DEFAULT_MIN_TIME = 5000;
	public static final float DEFAULT_MIN_DISTANCE = 1;
	private static final String KEY_TRIP_ID = "tripId";
	private LocationManager lm;
	private LocDbAdapter db = null;
	private long tripId;
	private LocationListener locListener;

	private class LocLogLocationListener implements LocationListener {

		// @Override
		public void onLocationChanged(Location location) {
			Log.d(TAG, "Logging location " + location.toString());
			if (location == null)
				return;
			db.insertLocation(tripId, location);
		}

		// @Override
		public void onProviderDisabled(String provider) {
			Log.w(TAG, provider
					+ " disabled by user, position logging cancelled.");
			// TODO Do more?
		}

		// @Override
		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub

		}

		// @Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "Created service.");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
			throws SQLException {
		Log.d(TAG, "Received start id " + startId + ": " + intent);

		tripId = intent.getLongExtra(KEY_TRIP_ID, -1);

		// Open database
		// TODO Handle exceptions differently?
		db = new LocDbAdapter(getApplicationContext());
		db.open();

		locListener = new LocLogLocationListener();
		
		// Start logging
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				intent.getLongExtra(KEY_MIN_TIME, DEFAULT_MIN_TIME),
				intent.getFloatExtra(KEY_MIN_DISTANCE, DEFAULT_MIN_DISTANCE),
				locListener);
		Log.d(TAG, "Location logging started.");

		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		lm.removeUpdates(locListener);
		locListener = null;
		// TODO Handle exception?
		db.close();

		Log.d(TAG, "Destroyed service.");
	}

	/**
	 * Convenience method for creating an Intent that can start the service.
	 * 
	 * @param context
	 *            Context within wich the Serivce should run
	 * @param minTime
	 *            minumum time in ms between location updates
	 * @param minDistance
	 *            minimum distance in m between location updates
	 * @return Intent that can be used to start this Service.
	 */
	public static Intent newStartIntent(Context context, long tripId,
			long minTime, float minDistance) {
		final Intent intent = new Intent(context, LocLogService.class);

		Bundle bundle = new Bundle(3);
		bundle.putLong(LocLogService.KEY_TRIP_ID, tripId);
		bundle.putLong(LocLogService.KEY_MIN_TIME, minTime);
		bundle.putFloat(LocLogService.KEY_MIN_DISTANCE, minDistance);
		intent.putExtras(bundle);

		return intent;
	}

	public static Intent newStartIntent(Context context, long tripId) {
		return newStartIntent(context, tripId, DEFAULT_MIN_TIME, DEFAULT_MIN_DISTANCE);
	}
}
