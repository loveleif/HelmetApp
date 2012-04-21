package se.mah.helmet;

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
				// TODO
			}
		});

		Button btnAlarmTest = (Button) findViewById(R.id.btnTestAlarm);
		btnAlarmTest.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), AlarmAcknowledgeActivity.class));
			}
		});

		onOff = (ToggleButton) findViewById(R.id.alarmToggleBtn);
		onOff.setOnClickListener(new OnOffListener());
		txtvStatus = (TextView) findViewById(R.id.labelOnOffStatus);
	}

	private class OnOffListener implements OnClickListener {

		public void onClick(View v) {
			if (onOff.isChecked()) {
				Log.d(TAG, "About to start accident detection.");
				Toast.makeText(getApplicationContext(),
						"Connecting to device...", Toast.LENGTH_SHORT).show();
				// TODO Tillfällig lösning för att testa. Skriv in MAC-adressen
				// manuellt.
				Intent hsIntent = new Intent(getApplicationContext(), HelmetService.class);
				hsIntent.setAction(HelmetService.ACTION_START);
				hsIntent.putExtra(HelmetService.BLUETOOTH_MAC_ADRESS_KEY, "00:06:66:04:DC:0C");
				startService(hsIntent);
				txtvStatus.setText("Status: Connected");
			} else {
				Log.d(TAG, "About to stop accident detection.");
				Toast.makeText(getApplicationContext(),
						"Disconnecting from device.", Toast.LENGTH_SHORT)
						.show();
				Intent hssIntent = new Intent(getApplicationContext(), HelmetService.class);
				hssIntent.setAction(HelmetService.ACTION_STOP);
				startService(hssIntent);
				txtvStatus.setText("Status: Disconnected");
			}
		}
	}
}
