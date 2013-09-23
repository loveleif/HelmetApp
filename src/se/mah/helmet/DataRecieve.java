package se.mah.helmet;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import se.mah.helmet.entity.Alarm;
import se.mah.helmet.entity.Contact;
import android.database.SQLException;
import android.location.Location;
import android.telephony.SmsManager;
import android.util.Log;

/**
 * Abstract class that encapsulates handling of recieved data from the embedded
 * system. The recieve method will take a recieved message (json String) and 
 * convert it to data. It will also make sure to take the appropriate actions
 * depending on what data was recieved (alarm or data sample).
 */
public abstract class DataRecieve {
	private static final String TAG = DataRecieve.class.getSimpleName();

	public static final int RECIEVE_FAIL = -1;
	public static final int RECIEVE_OK = 1;
	
	private DecimalFormat coordFormat = new DecimalFormat("0.0000");
	
	/**
	 * Call this method with data recieved from the embedded system.
	 * 
	 * @param input json String
	 * @return RECIEVE_FAIL or RECIEVE_OK
	 */
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
	
	/**
	 * Saves one accelerometer data sample to the database (local SQLite).
	 * 
	 * @param x g-force x-axis
	 * @param y g-force y-axis
	 * @param z g-force z-axis
	 */
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
		return RECIEVE_OK;
	}
	
	/**
	 * Save alarm to the database (local SQLite).
	 * 
	 * @param alarm alarm to save
	 * @return row id of the saved alarm
	 */
	public abstract long saveAlarm(Alarm alarm);
	
	/**
	 * Show GUI that gives the user a certain time to cancel the alarm. This
	 * method also needs to make sure to send the alarm by calling sendAlarm()
	 * if it's not cancelled.
	 */
	public abstract void acknowledgeAlarm();
	
	/**
	 * Send alarm to contacts.
	 * 
	 * @param alarm alarm to send
	 */
	public void sendAlarm(Alarm alarm) {
		Log.i(TAG, "About to send alarm.");
		// TODO Alarmmeddelandet ska in i res/strings.xml
		String alarmMsg = "Help me Obi-Wan. You're my only hope.";
		
		Location loc = null;
		try {
			loc = getLastLocation();
		} catch (Exception e) {	}
		if (loc != null)
			alarmMsg += " Lat: " + coordFormat.format(loc.getLatitude()) 
					 + ", Long: " + coordFormat.format(loc.getLongitude());
		alarmMsg += ". Severity = " + alarm.getSeverity();
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
	
	/**
	 * Get all contacts from local database.
	 * 
	 * @return list of Contacts
	 */
	public abstract List<Contact> getAllContacts();
	/**
	 * Returns the last known location of this device.
	 * @return the last known location of this device 
	 *
	 */
	public abstract Location getLastLocation();
}
