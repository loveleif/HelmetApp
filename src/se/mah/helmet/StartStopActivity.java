package se.mah.helmet;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		TextView testv = (TextView) findViewById(R.id.testtw);
		testv.setText(prefs.getString("serverUser", "---"));
		
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
