package se.mah.helmet;

import java.util.Date;
import java.util.List;

import se.mah.helmet.entity.Alarm;
import se.mah.helmet.entity.Contact;
import se.mah.helmet.storage.AccDbAdapter;
import se.mah.helmet.storage.AlarmDbAdapter;
import se.mah.helmet.storage.ContactDbAdapter;
import se.mah.helmet.storage.LocDbAdapter;
import se.mah.helmet.storage.TripDbAdapter;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

public class HelmetService extends Service {
	private static final String TAG = HelmetService.class.getSimpleName();
	
	public static final String ACTION_ACKNOWLEDGE_ALARM = "se.mah.helmet.ACTION_ACKNOWLEDGE_ALARM";
	public static final String ACTION_SEND_ALARM = "se.mah.ACTION_SEND_ALARM";
	public static final String ACTION_START = "se.mah.helmet.ACTION_START";
	public static final String ACTION_PAUSE = "se.mah.helmet.ACTION_STOP";
	public static final String JSON_DATA_KEY = "json_data";
	public static final String ALARM_ID_KEY = "alarm_id";
	public static final String BLUETOOTH_MAC_ADRESS_KEY = "bt_mac_adress";
	private static final String TRIP_ID_KEY = "trip_id";

	// Database access
	private ContactDbAdapter contactDb;
	private AccDbAdapter accDb;
	private AlarmDbAdapter alarmDb;
	private LocDbAdapter locDb;
	private TripDbAdapter tripDb;
	
	// TODO Temporary solution
	private boolean alarmActive = true;
	
	/**
	 * Handles bluetooth connecting, reading and writing.
	 */
	private BluetoothService bluetooth = new BluetoothService() {
		@Override
		public void recieveData(byte[] buffer, int size) {
			String data = new String(buffer, 0, size);
			Log.d(TAG, "BT recieve: " + data);
			dataRecieve.recieve(data);
		}
	};

	/**
	 * Handles recieving data. From reading json strings to taking actions on
	 * the read data.
	 */
	private DataRecieve dataRecieve = new DataRecieve() {
		@Override
		public void saveAccData(double accX, double accY, double accZ) {
			accDb.insertData(tripId, accX, accY, accZ);
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
			Log.d(TAG, "About to open AlarmAcknowledgeActivity.");
			startActivity(new Intent(
					getApplicationContext(), AlarmAcknowledgeActivity.class)
					.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
			);
		}
	};


	private long tripId = -1;
	
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
		tripDb = new TripDbAdapter(getApplicationContext());
		tripDb.open();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		pause();
		accDb = null;
		contactDb = null;
		alarmDb = null;
		locDb = null;
		tripDb = null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String action = intent.getAction();
		
		if (action.equals(ACTION_ACKNOWLEDGE_ALARM)) acknowledgeAlarm(intent);
		else if (action.equals(ACTION_SEND_ALARM)) sendAlarm(intent);
		else if (action.equals(ACTION_START)) start(intent);
		else if (action.equals(ACTION_PAUSE)) pause();
		
		return START_STICKY;
	};
	
	/**
	 * Convenience method for creating a start intent with action ACTION_START.
	 * 
	 * @param context context for service
	 * @param bluetoothMacAdress mac adress of bluetooth device to connect to
	 * @return
	 */
	public static Intent newStartIntent(Context context, String bluetoothMacAdress) {
		return new Intent(context, HelmetService.class)
						.setAction(ACTION_START)
						.putExtra(BLUETOOTH_MAC_ADRESS_KEY, bluetoothMacAdress);
	}

	/**
	 * Convenience method for creating a start intent with action ACTION_SEND_ALARM.
	 * 
	 * @param context context for service
	 * @param alarmId alarm id
	 * @return
	 */
	public static Intent newSendAlarmIntent(Context context, long alarmId) {
		return new Intent(context, HelmetService.class)
						.setAction(ACTION_SEND_ALARM)
						.putExtra(ALARM_ID_KEY, alarmId);
	}

	private void pause() {
		bluetooth.disconnect();
		stopService(new Intent(getApplicationContext(), LocLogService.class));
	}

	private void start(Intent intent) {
		// Create new trip
		tripId  = tripDb.insertTrip(new Date().toString());
		
		// Start Bluetooth communications
		BluetoothDevice device = 
				BluetoothAdapter
				.getDefaultAdapter()
				.getRemoteDevice(
						intent.getStringExtra(BLUETOOTH_MAC_ADRESS_KEY)
				);
		bluetooth.connect(device);
		
		// Start location logging
		// TODO get constants from settings
		startService(LocLogService.newStartIntent(
				getApplicationContext(), // Context
				tripId, // Trip id
				5000, // Min time (ms)
				100 // Min dist (m)
			));
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
		return null;
	}
}
