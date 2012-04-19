package se.mah.helmet.storage;

import java.util.List;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicHeader;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
	private static final String TAG = SyncAdapter.class.getSimpleName();
	private final Context context;
	private final AccountManager accountManager;
	private final AndroidHttpClient httpClient;
	private static final String ACCEPT_HEADER_KEY = "Accept";
	private static final String TYPE_TEXT_PLAIN = "text/plain";
	
	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
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
		// TODO
		HttpUriRequest request = new HttpGet("bla");
		request.addHeader(new BasicHeader(ACCEPT_HEADER_KEY, TYPE_TEXT_PLAIN));
		
		
		//httpClient.execute(request)
		return -1;
	}
}