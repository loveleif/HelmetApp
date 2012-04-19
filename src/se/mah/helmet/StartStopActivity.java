package se.mah.helmet;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
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
	BluetoothService bs;
	
	 // Message types sent from the BluetoothService Handler
    public static final int MESSAGE_READ = 1;
    
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		bs = new BluetoothService(getApplicationContext());
		
		setContentView(R.layout.on_off);
		
		//SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		Button btnTest = (Button) findViewById(R.id.btnTest);
		btnTest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
        		// TODO
            }
        });
		
		Button btnAlarmTest = (Button) findViewById(R.id.btnTestAlarm);
		btnAlarmTest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
        		Intent intent = new Intent(getApplicationContext(), DataRecieve.class);
        		intent.putExtra(DataRecieve.JSON_DATA_KEY, "{\"type\":\"alarm\",\"severity\":5}");
        	    intent.setAction(DataRecieve.ACTION_RECIEVE_DATA);
        	    startService(intent);
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
    			Toast.makeText(getApplicationContext(), "Connecting to device...", Toast.LENGTH_SHORT).show();
    			// TODO Tillfällig lösning för att testa. Skriv in MAC-adressen manuellt.
    			bs.connect(BluetoothAdapter.getDefaultAdapter().getRemoteDevice("00:06:66:04:DC:0C"));
    			txtvStatus.setText("Status: Connected");
    		} else {
    			Log.d(TAG, "About to stop accident detection.");
    			Toast.makeText(getApplicationContext(), "Disconnecting from device.", Toast.LENGTH_SHORT).show();
    			bs.disconnect();
    			txtvStatus.setText("Status: Disconnected");
    		}
    	}
    }
}
