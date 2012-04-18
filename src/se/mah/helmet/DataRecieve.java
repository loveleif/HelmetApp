package se.mah.helmet;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import se.mah.helmet.entity.Contact;
import se.mah.helmet.storage.AccDbAdapter;
import se.mah.helmet.storage.ContactsDbAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DataRecieve extends Activity {	
	public static final int RECIEVE_FAIL = -1;
	public static final int RECIEVE_OK = 1;
	private static final String TAG = DataRecieve.class.getSimpleName();
	private static final int ALARM_SEND_TIMEOUT_REQUEST = 1;
	
	private final ContactsDbAdapter contactDb;
	private final AccDbAdapter accDb;
	private final Context context;

	public DataRecieve(Context context) {
		Log.d(TAG, "context="+context.toString());
		this.context = context;
		accDb = new AccDbAdapter(context);
		accDb.open();
		contactDb = new ContactsDbAdapter(context);
		contactDb.open();
	}
	
	public int recieve(String input) {
		JSONObject obj;
		String type;
		try {
			obj = (JSONObject) new JSONTokener(input).nextValue();
			type = obj.getString("type");
		} catch (JSONException e) {
			Log.e(TAG, "Failed to parse JSON.");
			return RECIEVE_FAIL;
		}

		Log.d(TAG, "data type: " + type);
		if (type.equals("alarm"))
			return handleAlarm(obj);
		else if (type.equals("acc_data"))
			return handleAccData(obj);
		
		Log.e(TAG, "Unknown data type.");
		return RECIEVE_FAIL;
	}

	private int handleAccData(JSONObject accData) {
		double accX, accY, accZ;
		try {
			accX = accData.getDouble("accX");
			accY = accData.getDouble("accY");
			accZ = accData.getDouble("accZ");
		} catch (JSONException e) {
			Log.e(TAG, "Unknown JSON accelerometer data file.");
			return RECIEVE_FAIL;
		}
		
		// Bug: NullPointerException
		//accDb.insertData(new Date().toString(), accX, accY, accZ);
		return RECIEVE_OK;
	}

	private int handleAlarm(JSONObject obj) {
		Intent intent = new Intent(context, AlarmAcknowledgeActivity.class);
	    startActivityForResult(intent, ALARM_SEND_TIMEOUT_REQUEST);
		
		// TODO Spara severity
		// TODO Skicka larm till server
		// Se http://www.androidsnippets.com/executing-a-http-post-request-with-httpclient
		// TODO Smsa kontakter
		
		return RECIEVE_OK;
	}
	
	private void sendAlarm() {
		String alarmMsg = "Help me Obi-Wan. You're my only hope. ";
		// TODO Få in koordinaterna där också, ta tiden
		// Severity i SMS?
		
		for (Contact contact : contactDb.fetchAllContacts()) {
			// VARNING! Kommentera denna raden efter varje test
			/*SmsManager.getDefault().sendTextMessage(
					contact.getPhoneNbr(), 
					null, // SC adress
					alarmMsg, 
					null, 
					null);*/
			Log.i(TAG, "Sending alarm to " + contact.toString());
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case ALARM_SEND_TIMEOUT_REQUEST:
			if (resultCode == AlarmAcknowledgeActivity.RESULT_SEND_ALARM)
				sendAlarm();
			else if (resultCode == AlarmAcknowledgeActivity.RESULT_ALARM_CANCELLED)
				Log.i(TAG, "Alarm cancelled.");
			else
				Log.e(TAG, "Invalid result code.");
			break;
		}
	}
}
