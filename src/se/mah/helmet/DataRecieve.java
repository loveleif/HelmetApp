package se.mah.helmet;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import se.mah.helmet.entity.Contact;
import android.content.Context;
import android.telephony.SmsManager;
import android.util.Log;

public class DataRecieve {	
	public static final int RECIEVE_FAIL = -1;
	public static final int RECIEVE_OK = 1;
	private static final String TAG = DataRecieve.class.getSimpleName();
	
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
		
		// TODO Spara severity
		// TODO Skicka larm till server
		// Se http://www.androidsnippets.com/executing-a-http-post-request-with-httpclient
		// TODO Smsa kontakter
		
		return RECIEVE_OK;
	}
}
