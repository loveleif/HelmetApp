package se.mah.helmet;

import java.util.List;

import se.mah.helmet.entity.Alarm;
import se.mah.helmet.entity.Contact;
import se.mah.helmet.storage.AccDbAdapter;
import se.mah.helmet.storage.AlarmDbAdapter;
import se.mah.helmet.storage.ContactDbAdapter;
import se.mah.helmet.storage.LocDbAdapter;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

/**
 * The main Service for the HelmetApp. Handles sending and recieving bluetooth data, saving
 * data in the local database (via various DbAdapters) and sending alarms.
 * 
 */ 
public class HelmetService extends Service {
	private static final String TAG = HelmetService.class.getSimpleName();
	
	public static final String ACTION_ACKNOWLEDGE_ALARM = "se.mah.helmet.ACTION_ACKNOWLEDGE_ALARM";
	public static final String ACTION_SEND_ALARM = "se.mah.ACTION_SEND_ALARM";
	public static final String ACTION_START_BLUETOOTH_SERVICE = "se.mah.helmet.ACTION_START_BLUETOOTH_SERVICE";
	public static final String ACTION_STOP_BLUETOOTH_SERVICE = "se.mah.helmet.ACTION_STOP_BLUETOOTH_SERVICE";
	public static final String JSON_DATA_KEY = "json_data";
	public static final String ALARM_ID_KEY = "alarm_id";
	public static final String BLUETOOTH_MAC_ADRESS_KEY = "se.mah.helmet.BLUETOOTH_MAC_ADRESS_KEY";

	private ContactDbAdapter contactDb;
	private AccDbAdapter accDb;
	private AlarmDbAdapter alarmDb;
	private LocDbAdapter locDb;
	
	private boolean alarmActive = true;
	
	private BluetoothService bluetooth = new BluetoothService() {
		@Override
		public void recieveData(byte[] buffer, int size) {
			String data = new String(buffer, 0, size);
			Log.d(TAG, "BT recieve: " + data);
			dataRecieve.recieve(data);
		}
	};

	private DataRecieve dataRecieve = new DataRecieve() {
		@Override
		public void saveAccData(double x, double y, double z) {
			//accDb.insertData(tripId, accX, accY, accZ);
		}

		@Override
		public long saveAlarm(Alarm alarm) {
			return alarmDb.insert(alarm);
		}

		@Override
		public List<Contact> getAllContacts() {
			return contactDb.getAllObjects();
		}

		@Override
		public Location getLastLocation() {
			return locDb.getLastObject();
		}

		@Override
		public void acknowledgeAlarm() {
			startActivity(new Intent(
				getApplicationContext(), AlarmAcknowledgeActivity.class)
				.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
		);
		}
	};
	
	@Override
	public void onCreate() {
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
		
		// PLEASE NOTE! The following methods will make modifications to
		// intent and re-use it to start other services.
		if (action.equals(ACTION_ACKNOWLEDGE_ALARM)) acknowledgeAlarm(intent);
		else if (action.equals(ACTION_SEND_ALARM)) sendAlarm(intent);
		else if (action.equals(ACTION_START_BLUETOOTH_SERVICE)) startBluetooth(intent);
		else if (action.equals(ACTION_STOP_BLUETOOTH_SERVICE)) stopBluetooth();
		
		return START_STICKY;
	};

	private void stopBluetooth() {
		bluetooth.disconnect();
	}

	private void startBluetooth(Intent intent) {
		BluetoothDevice device = 
				BluetoothAdapter
				.getDefaultAdapter()
				.getRemoteDevice(
						intent.getStringExtra(BLUETOOTH_MAC_ADRESS_KEY)
				);
		bluetooth.connect(device);
	}

	private void sendAlarm(Intent intent) {
		Alarm alarm = null;
		try {
		alarm = alarmDb.getObject(intent.getLongExtra(ALARM_ID_KEY, -1));
		} catch (Exception e) {	}
		
		if (alarmActive) {
			dataRecieve.sendAlarm(alarm);
			// TODO Temp lösning
			alarmActive = false;
		}
	}

	private void acknowledgeAlarm(Intent intent) {
		intent.setClass(getApplicationContext(), AlarmAcknowledgeActivity.class);
		startActivity(intent);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}
