package se.mah.helmet;

import java.util.Date;

import se.mah.helmet.entity.Alarm;
import se.mah.helmet.entity.Position;
import se.mah.helmet.storage.AccDbAdapter;
import se.mah.helmet.storage.AlarmDbAdapter;
import se.mah.helmet.storage.LocDbAdapter;
import se.mah.helmet.storage.SyncAdapter;
import se.mah.helmet.storage.TripDbAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

// TODO Implementera handler osv

/**
 * Activity that starts and stops the HelmetService.
 *
 */
public class StartStopActivity extends Activity {
	public static final String TAG = StartStopActivity.class.getSimpleName();
	private ToggleButton onOff;
	private TextView txtvStatus;

	// Message types sent from the BluetoothService Handler
	public static final int MESSAGE_READ = 1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.on_off);

		// SharedPreferences prefs =
		// PreferenceManager.getDefaultSharedPreferences(this);

		Button btnTest = (Button) findViewById(R.id.btnTest);
		btnTest.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Runnable test = new Runnable() {
					public void run() {
						startService(new Intent(getApplicationContext(), SyncAdapter.class));
					}
				};
				Thread t = new Thread(test);
				t.start();
				
			}
		});

		Button btnAlarmTest = (Button) findViewById(R.id.btnTestAlarm);
		btnAlarmTest.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				TripDbAdapter tripDb = new TripDbAdapter(getApplicationContext());
				tripDb.open();
				tripDb.insertTrip("Trip " + new Date().toString());
				tripDb.close();
			}
		});

		Button btnAddTripTest = (Button) findViewById(R.id.btnTestAddTripData);
		btnAddTripTest.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				TripDbAdapter tripDb = new TripDbAdapter(getApplicationContext());
				tripDb.open();
				long tripId = tripDb.insertTrip("Trip " + new Date().toString());
				tripDb.close();
				LocDbAdapter locDb = new LocDbAdapter(getApplicationContext());
				locDb.open();
				Position pos;
				pos = new Position("TestProvider");
				pos.setLatitude(14);
				pos.setLongitude(13);
				pos.setTime(123456789);
				locDb.insertLocation(tripId, pos);
				pos = new Position("TestProvider");
				pos.setLatitude(14);
				pos.setLongitude(13);
				pos.setTime(123456789);
				locDb.insertLocation(tripId, pos);
				locDb.close();
				
				AccDbAdapter accDb = new AccDbAdapter(getApplicationContext());
				accDb.open();
				for (int i = 0; i < 4; i++)
					accDb.insertData(tripId, Math.random() * 16, Math.random() * 16, Math.random() * 16);

			}
		});
		
		Button btnTestLarm = (Button) findViewById(R.id.btnCreateAlarm);
		btnTestLarm.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Alarm alarm = new Alarm(-1l, (short) 11);
				AlarmDbAdapter alarmDb = new AlarmDbAdapter(getApplicationContext());
				alarmDb.open();
				long alarmId = alarmDb.insert(alarm);
				alarmDb.close();
				Intent intent = new Intent(getApplicationContext(), HelmetService.class);
				intent.setAction(HelmetService.ACTION_ACKNOWLEDGE_ALARM);
				startService(intent);
			}
		});


		
		onOff = (ToggleButton) findViewById(R.id.alarmToggleBtn);
		onOff.setOnClickListener(new OnOffListener());
		txtvStatus = (TextView) findViewById(R.id.labelOnOffStatus);
	}

	/**
	 * Listener for the ToggleButton that turns HelmetService on/off.
	 *
	 */
	private class OnOffListener implements OnClickListener {

		public void onClick(View v) {
			if (onOff.isChecked()) {
				Log.d(TAG, "About to start accident detection.");
				Toast.makeText(getApplicationContext(),
						"Connecting to device...", Toast.LENGTH_SHORT).show();
				// TODO Tillfällig lösning för att testa. Skriv in MAC-adressen
				// manuellt.
				startService(HelmetService.newStartIntent(getApplicationContext(), "00:06:66:04:DC:0C"));
				txtvStatus.setText("Status: Connected");
			} else {
				Log.d(TAG, "About to stop accident detection.");
				Toast.makeText(getApplicationContext(),
						"Disconnecting from device.", Toast.LENGTH_SHORT)
						.show();
				Intent hssIntent = new Intent(getApplicationContext(), HelmetService.class);
				hssIntent.setAction(HelmetService.ACTION_PAUSE);
				startService(hssIntent);
				txtvStatus.setText("Status: Disconnected");
			}
		}
	}
}
