package se.mah.helmet;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import se.mah.helmet.entity.Alarm;
import se.mah.helmet.entity.Contact;
import se.mah.helmet.storage.AccDbAdapter;
import se.mah.helmet.storage.AlarmDbAdapter;
import se.mah.helmet.storage.ContactDbAdapter;
import se.mah.helmet.storage.LocDbAdapter;
import android.app.Service;
import android.content.Intent;
import android.database.SQLException;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

public class DataRecieve extends Service {
	public static final int RECIEVE_FAIL = -1;
	public static final int RECIEVE_OK = 1;
	private static final String TAG = DataRecieve.class.getSimpleName();
	public static final String ACTION_SEND_ALARM = "se.mah.helmet.SEND_ALARM";
	public static final String ACTION_RECIEVE_DATA = "se.mah.helmet.RECIEVE_DATA";
	public static final String ACTION_CONFIRM_ALARM = "se.mah.helmet.CONFIRM_ALARM";
	public static final String JSON_DATA_KEY = "json_data";
	public static final String ALARM_ID_KEY = "alarm_id";
	
	private ContactDbAdapter contactDb;
	private AccDbAdapter accDb;
	private AlarmDbAdapter alarmDb;
	private LocDbAdapter locDb;
	
	@Override
	public void onCreate() {
		super.onCreate();

		accDb = new AccDbAdapter(getApplicationContext());
		accDb.open();
		contactDb = new ContactDbAdapter(getApplicationContext());
		contactDb.open();
		alarmDb = new AlarmDbAdapter(getApplicationContext());
		alarmDb.open();
		locDb = new LocDbAdapter(getApplicationContext());
		locDb.open();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String action = intent.getAction();
		Log.d(TAG, "Recieved start command, action=" + action);
		if (action.equals(ACTION_RECIEVE_DATA)) {
			recieve(intent.getStringExtra(JSON_DATA_KEY));
		} else if (action.equals(ACTION_SEND_ALARM)) {
			sendAlarm();
		}

		return super.onStartCommand(intent, flags, startId);
	}
	
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
		
		//accDb.insertData();
		return RECIEVE_OK;
	}

	private int handleAlarm(JSONObject obj) {
		Log.i(TAG, "About to handle alarm...");
		long alarmId = -1;
		try {
			alarmId = alarmDb.insert(new Alarm(-1, (short) obj.getInt("severity")));
			Log.d(TAG, "Inserted new alarm in db with id=" + alarmId);
		} catch (JSONException e) {
			Log.e(TAG, "Unable to parse alarm JSON.");
		} catch (SQLException e) {
			Log.e(TAG, "Unable to save alarm to database");
		}

		Intent intent = new Intent(getApplicationContext(), AlarmAcknowledgeActivity.class);
		intent.putExtra(ALARM_ID_KEY, alarmId);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    startActivity(intent);

	    // TODO Skicka larm till server
		// Se http://www.androidsnippets.com/executing-a-http-post-request-with-httpclient
		return RECIEVE_OK;
	}
	
	private void sendAlarm() {
		Log.i(TAG, "About to send alarm.");
		String alarmMsg = "Help me Obi-Wan. You're my only hope.";
		Location loc = locDb.getLastObject();
		alarmMsg += " Lat: " + loc.getLatitude() + ", Long: " + loc.getLongitude();
		// TODO Få in koordinaterna där också, ta tiden
		// Severity i SMS?
		
		for (Contact contact : contactDb.getAllObjects()) {
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
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}
