package se.mah.helmet;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import se.mah.helmet.entity.Alarm;
import se.mah.helmet.entity.Contact;
import android.database.SQLException;
import android.location.Location;
import android.util.Log;

public abstract class DataRecieve {
	private static final String TAG = DataRecieve.class.getSimpleName();

	public static final int RECIEVE_FAIL = -1;
	public static final int RECIEVE_OK = 1;
	
	public int recieve(String input) {
		Log.d(TAG, "About to handle data: " + input);
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
		if (type.equals("alarm")) {
			Log.d(TAG, "Type is alarm");
			return handleAlarm(obj);
		}
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
		
		saveAccData(accX, accY, accZ);
		return RECIEVE_OK;
	}
	
	public abstract void saveAccData(double x, double y, double z);

	private int handleAlarm(JSONObject obj) {
		Log.i(TAG, "About to handle alarm...");
		long alarmId = -1;
		try {
			alarmId = saveAlarm(new Alarm(-1, (short) obj.getInt("severity")));
			Log.d(TAG, "Inserted new alarm in db with id=" + alarmId);
		} catch (JSONException e) {
			Log.e(TAG, "Unable to parse alarm JSON.");
		} catch (SQLException e) {
			Log.e(TAG, "Unable to save alarm to database");
		}
		
		acknowledgeAlarm();

	    // TODO Skicka larm till server
		// Se http://www.androidsnippets.com/executing-a-http-post-request-with-httpclient
		return RECIEVE_OK;
	}
	
	public abstract long saveAlarm(Alarm alarm);
	public abstract void acknowledgeAlarm();
	
	public void sendAlarm(Alarm alarm) {
		Log.i(TAG, "About to send alarm.");
		String alarmMsg = "Help me Obi-Wan. You're my only hope.";
		
		Location loc = null;
		try {
			loc = getLastLocation();
		} catch (Exception e) {	}
		
		if (loc != null)
			alarmMsg += " Lat: " + loc.getLatitude() + ", Long: " + loc.getLongitude();
		Log.d(TAG, "Alarm message: " + alarmMsg);
		// TODO Få in koordinaterna där också, ta tiden
		// Severity i SMS?
		
		for (Contact contact : getAllContacts()) {
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
	
	public abstract List<Contact> getAllContacts();
	public abstract Location getLastLocation();
}
