package se.mah.helmet;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Activity that gives the user a certain amount of time to cancel an alarm.
 *
 */
public class AlarmAcknowledgeActivity extends Activity {
	public static final String TAG = AlarmAcknowledgeActivity.class.getSimpleName();

	public static final int RESULT_ALARM_CANCELLED = 100;
	public static final int RESULT_SEND_ALARM = 90000;

	private long startTime;
	private long time = 0; // Time left in milliseconds
	private long totalTime = 15000; // Total time before sending alarm (ms)
	private long period = 1000; // Time between updating the GUI count down
	
	private Button btnCancel;
	private TextView txtvCountDown;

	// This handler is used to update the countdown in the GUI
	// and finally sending the alarm.
	private Handler handler = new Handler();
	
	// Called by handler
	private Runnable update = new Runnable() {
		public void run() {
			time = SystemClock.uptimeMillis() - startTime;
			if (time >= totalTime) {
				// Send alarm
				Log.d(TAG, "About to send alarm.");
				startService(HelmetService.newSendAlarmIntent(
						getApplicationContext(), 
						getIntent().getLongExtra(HelmetService.ALARM_ID_KEY, -1)
						));
				finish();
			} else {
				// Update GUI count down
				txtvCountDown.setText((totalTime - time) / 1000 + " seconds left to cancel.");
				handler.postDelayed(update, period);
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_acknowledge);
		
		btnCancel = (Button) findViewById(R.id.btnAlarmAcknowledge);
		txtvCountDown = (TextView) findViewById(R.id.txtvAlarmCountDown);
		
		btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	// Finish without sending alarm if user press the button
                finish();
            }
        });
		
		startTime = SystemClock.uptimeMillis();
		handler.post(update);
	}
}
