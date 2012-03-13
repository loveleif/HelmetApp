package se.mah.helmet;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class StartStopActivity extends Activity {
	public static final String TAG = StartStopActivity.class.getSimpleName();
	private ToggleButton onOff;
	private TextView txtvStatus;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.on_off);
		
		onOff = (ToggleButton) findViewById(R.id.toggleButton1);
		onOff.setOnClickListener(new OnOffListener());
		txtvStatus = (TextView) findViewById(R.id.textView2);
	}
	
    private class OnOffListener implements OnClickListener {
    	
    	@Override
    	public void onClick(View v) {
    		if (onOff.isChecked()) {
    			Log.d(TAG, "About to start accident detection.");
    			Toast.makeText(getApplicationContext(), "Connecting to device...", Toast.LENGTH_SHORT).show();
    			Toast.makeText(getApplicationContext(), "Connected!", Toast.LENGTH_SHORT).show();
    			txtvStatus.setText("Status: Connected");
    		} else {
    			Log.d(TAG, "About to stop accident detection.");
    			Toast.makeText(getApplicationContext(), "Disconnecting from device.", Toast.LENGTH_SHORT).show();
    			txtvStatus.setText("Status: Disconnected");
    		}
    	}
    }
}
