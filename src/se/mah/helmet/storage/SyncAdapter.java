package se.mah.helmet.storage;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicHeader;

import se.mah.helmet.Prefs;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
	private static final String TAG = SyncAdapter.class.getSimpleName();
	private final Context context;
	private final AccountManager accountManager;
	private final AndroidHttpClient httpClient;
	private String domain;
	private String user;
	private static final String ACCEPT_HEADER_KEY = "Accept";
	private static final String TYPE_TEXT_PLAIN = "text/plain";
	private byte[] buffer = new byte[8*1024];
	
	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		domain = prefs.getString(Prefs.SERVER_DOMAIN, null);
		user = prefs.getString(Prefs.SERVER_USER, null);
		if (domain == null || user == null)
			// TODO Handle differently
			throw new RuntimeException("Missing server settings.");
		domain = "http://" + domain;
		
		this.context = context;
		accountManager = AccountManager.get(context);
		httpClient = AndroidHttpClient.newInstance("HelmetAppSyncAdapter");
	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
		// TODO Auto-generated method stub
	}

	private void syncTrip(String userName) {
		long lastIdOnServer = getLastIdOnServer(TripDbAdapter.TABLE_TRIP);
		// tripDb.fetchAllWhere
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
			resourcePath = "/users/" + user + "/trips/last";
		else
			return -1;
		HttpUriRequest request = new HttpGet(domain + resourcePath);
		request.addHeader(new BasicHeader(ACCEPT_HEADER_KEY, TYPE_TEXT_PLAIN));
		
		HttpResponse response;
		InputStream is;
		int size;
		try {
			response = httpClient.execute(request);
			is = response.getEntity().getContent();
			size = is.read(buffer);
		} catch (IOException e) {
			Log.e(TAG, "Http request failed: " + request);
			return -1;
		}
		return Long.parseLong(new String(buffer, 0, size));
	}
}