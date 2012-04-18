package se.mah.helmet;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AlarmAcknowledgeActivity extends Activity {
	public static final String TAG = AlarmAcknowledgeActivity.class.getSimpleName();

	public static final int RESULT_ALARM_CANCELLED = 100;
	public static final int RESULT_SEND_ALARM = 90000;

	private long startTime;
	private long time = 0; // Time left in milliseconds
	private long totalTime = 30000;
	private long period = 1000;
	
	private Button btnCancel;
	private TextView txtvCountDown;
	
	private Handler handler = new Handler();
	private Runnable update = new Runnable() {
		public void run() {
			time = SystemClock.uptimeMillis() - startTime;
			if (time >= totalTime) {
				setResult(RESULT_SEND_ALARM);
				finish();
			} else {
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
                setResult(RESULT_ALARM_CANCELLED);
                finish();
            }
        });
		
		startTime = SystemClock.uptimeMillis();
		handler.post(update);
	}
	
	private class AlarmTimerTask extends TimerTask {
		@Override
		public void run() {

		}
	}
}
