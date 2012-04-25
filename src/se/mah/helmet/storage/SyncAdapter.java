package se.mah.helmet.storage;

import se.mah.helmet.HttpUtil;
import se.mah.helmet.Prefs;
import se.mah.helmet.entity.AccData;
import se.mah.helmet.entity.Alarm;
import se.mah.helmet.entity.Position;
import se.mah.helmet.entity.Trip;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.http.AndroidHttpClient;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * A VERY primitive class for syncing the android SQLite database with
 * the server.
 * 
 * Synchronization is carried out through the servers RESTful interface
 * (http requests). In it's current form the sync:
 *    - is one way only, phone -> server
 *    - don't verify uploaded data
 *    - only updates location and accelerometer data for new
 *      trips
 *    - will fail ugly under a (large) number of circumstances
 *    - will probably leave the server database in an incomplete state
 *      after a fail
 */
public class SyncAdapter extends Service {
	private static final String TAG = SyncAdapter.class.getSimpleName();

	private AccountManager accountManager;
	private AndroidHttpClient httpClient;
	private String domain;
	private String user;
	public SyncThread syncThread;
	private static final String ACCEPT_HEADER_KEY = "Accept";
	private static final String TYPE_TEXT_PLAIN = "text/plain";

	private byte[] buffer = new byte[1024];
	
	@Override
	public void onCreate() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		domain = prefs.getString(Prefs.SERVER_DOMAIN_KEY, null);
		// TODO Get port from prefs
		domain += ":8080";
		user = prefs.getString(Prefs.SERVER_USER_KEY, null);
		if (domain == null || user == null)
			// TODO Handle differently
			throw new RuntimeException("Missing server settings.");
		domain = "http://" + domain;
		accountManager = AccountManager.get(getApplicationContext());
		httpClient = AndroidHttpClient.newInstance("HelmetAppSyncAdapter");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (syncThread == null) {
			syncThread = new SyncThread();
			syncThread.start();
		}
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		httpClient.close();
	}
	
	private void syncTrips() {
		long lastIdOnServer = getLastIdOnServer(TripDbAdapter.TABLE_TRIP);
		Log.d(TAG, "Trip lastIdOnServer=" + lastIdOnServer);
		TripDbAdapter tripDb = new TripDbAdapter(getApplicationContext());
		tripDb.open();
		Cursor cursor = tripDb.getAllSinceId(lastIdOnServer, null);
		String resourcePath = domain + "/HelmetServer/users/" + user + "/trips";
		String response;
		long serverId;
		while (cursor.moveToNext()) {
			Trip trip = tripDb.getObject(cursor);
			response = HttpUtil.httpPostJson(httpClient, resourcePath, trip.toJson(), buffer);
			serverId = Long.valueOf(response);
			Log.d(TAG, "About to sync acc data.");
			syncAccData(serverId);
			Log.d(TAG, "About to sync loc data.");
			syncLocData(serverId);
			Log.d(TAG, "Synced Trip with id=" + trip.getSourceId());
		}
		cursor.close();
		tripDb.close();
		// TODO
	}
	
	public void syncAlarms() {
		long lastIdOnServer = getLastIdOnServer(AlarmDbAdapter.TABLE_ALARM);
		Log.d(TAG, "Alarm lastIdOnServer=" + lastIdOnServer);
		AlarmDbAdapter alarmDb = new AlarmDbAdapter(getApplicationContext());
		alarmDb.open();

		Cursor cursor = alarmDb.getAllSinceId(lastIdOnServer, null);
		String resourcePath = domain + "/HelmetServer/users/" + user + "/alarms";
		while (cursor.moveToNext()) {
			Alarm alarm = alarmDb.getObject(cursor);
			HttpUtil.httpPostJson(httpClient, resourcePath, alarm.toJson(), buffer);
			Log.d(TAG, "Synced Alarm with id=" + alarm.getId());
		}
		cursor.close();
		alarmDb.close();
		// TODO
	}
	
	private void syncAccData(long tripId) {
		AccDbAdapter accDb = new AccDbAdapter(getApplicationContext());
		accDb.open();
		Cursor cursor = accDb.getDataForTrip(tripId);
		String resourcePath = domain + "/HelmetServer/users/" + user + "/trips/" + tripId + "/data/g";
		AccData data;
		while (cursor.moveToNext()) {
			data = accDb.getObject(cursor);
			HttpUtil.httpPostJson(httpClient, resourcePath, data.toJson(), buffer);
			Log.d(TAG, "Synced AccData with id=" + data.getSourceId() + "(tripId=" + tripId + ")");
		}
		cursor.close();
		accDb.close();
	}
	
	private void syncLocData(long tripId) {
		LocDbAdapter locDb = new LocDbAdapter(getApplicationContext());
		locDb.open();
		Cursor cursor = locDb.getDataForTrip(tripId);
		String resourcePath = domain + "/HelmetServer/users/" + user + "/trips/" + tripId + "/data/loc";
		Position data;
		while (cursor.moveToNext()) {
			data = locDb.getObject(cursor);
			HttpUtil.httpPostJson(httpClient, resourcePath, data.toJson(), buffer);
			Log.d(TAG, "Synced Loc with id=" + data.getSourceId());
		}
		cursor.close();
		locDb.close();
	}

	public void syncContacts() {
		// TODO Auto-generated method stub
	}
	
	/**
	 * Returns the last row id that is stored at the server.
	 */
	private long getLastIdOnServer(String table) {
		String resourcePath;
		if (table == TripDbAdapter.TABLE_TRIP)
			resourcePath = "/HelmetServer/users/" + user + "/trips/last/source-id";
		else
			return -1;

		String answer = HttpUtil.httpGet(
				httpClient, 
				domain + resourcePath, 
				TYPE_TEXT_PLAIN, 
				buffer);
		long lastId;
		try {
			lastId = Long.valueOf(answer);
		} catch (Exception e) {
			return -1;
		}
		return lastId;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Thread used for the sync operations. (Android does not allow
	 * to run http requests in the UI thread)
	 */
	public class SyncThread extends Thread {
		@Override
		public void run() {
			super.run();
			syncAlarms();
			syncTrips();
			syncContacts();
			
			
			syncThread = null;
			stopSelf();
		}
	}

}