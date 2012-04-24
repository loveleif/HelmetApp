package se.mah.helmet.storage;

import se.mah.helmet.HttpUtil;
import se.mah.helmet.Prefs;
import se.mah.helmet.entity.Alarm;
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
 * Class used for syncing the android SQLite database with the application server. The
 * sync is currently 1) one way only, phone -> server 2) does not verify uploaded data.
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

	private byte[] buffer = new byte[8*1024];
	
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
	
	private void syncTrips() {
		long lastIdOnServer = getLastIdOnServer(TripDbAdapter.TABLE_TRIP);
		Log.d(TAG, "Trip lastIdOnServer=" + lastIdOnServer);
		TripDbAdapter tripDb = new TripDbAdapter(getApplicationContext());
		tripDb.open();
		Cursor cursor = tripDb.getAllSinceId(lastIdOnServer, null);
		String resourcePath = domain + "/HelmetServer/users/" + user + "/trips";
		while (cursor.moveToNext()) {
			Trip trip = tripDb.getObject(cursor);
			HttpUtil.httpPostJson(httpClient, resourcePath, trip.toJson());
			syncAccData(trip.getId());
			syncLocData(trip.getId());
			
			Log.d(TAG, "Synced Trip with id=" + trip.getId());
			
		}
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
			HttpUtil.httpPostJson(httpClient, resourcePath, alarm.toJson());
			Log.d(TAG, "Synced Alarm with id=" + alarm.getId());
		}
		alarmDb.close();
		// TODO
	}
	
	private void syncAccData(Long tripId) {
		// TODO
	}
	
	private void syncLocData(Long tripId) {
		// TODO
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
	 * Thread used for the sync operations.
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