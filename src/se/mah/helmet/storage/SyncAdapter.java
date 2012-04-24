package se.mah.helmet.storage;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicHeader;

import se.mah.helmet.HttpUtil;
import se.mah.helmet.Prefs;
import se.mah.helmet.entity.Trip;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

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
		if (syncThread != null)
			return START_NOT_STICKY;

		syncThread = new SyncThread();
		syncThread.start();
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
			Log.d(TAG, trip.toJson());
			HttpUtil.httpPostJson(httpClient, resourcePath, trip.toJson());
		}
		tripDb.close();
		// TODO
	}
	
	private void syncAccData(List<Long> trips) {
		long lastIdOnServer = getLastIdOnServer(AccDbAdapter.TABLE_ACC);
		
		// TODO
	}
	
	private void syncLocData(List<Long> trips) {
		// TODO
	}
	
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
	
	public class SyncThread extends Thread {
		@Override
		public void run() {
			super.run();
			syncTrips();
			
			syncThread = null;
			stopSelf();
		}
	}
}