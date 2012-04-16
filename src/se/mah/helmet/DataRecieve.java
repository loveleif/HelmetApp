package se.mah.helmet;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.telephony.SmsManager;

public class DataRecieve {	
	public static final int RECIEVE_FAIL = -1;
	public static final int RECIEVE_OK = 1;
	
	private final AccDbAdapter accDb;
	private final Context context;

	public DataRecieve(Context context) {
		this.context = context;
		accDb = new AccDbAdapter(context);
	}
	
	public int recieve(String input) {
		JSONObject obj;
		String type;
		try {
			obj = (JSONObject) new JSONTokener(input).nextValue();
			type = obj.getString("type");
		} catch (JSONException e) {
			return RECIEVE_FAIL;
		}

		if (type == "alarm")
			return handleAlarm(obj);
		else if (type == "acc_data")
			return handleAccData(obj);
		
		return RECIEVE_FAIL;
	}

	private int handleAccData(JSONObject accData) {
		double accX, accY, accZ;
		try {
			accX = accData.getDouble("accX");
			accY = accData.getDouble("accY");
			accZ = accData.getDouble("accZ");
		} catch (JSONException e) {
			return RECIEVE_FAIL;
		}
		
		accDb.insertData(new Date().toString(), accX, accY, accZ);
		return RECIEVE_OK;
	}

	private int handleAlarm(JSONObject obj) {
		// TODO Spara severity
		// TODO Skicka larm till server
		// Se http://www.androidsnippets.com/executing-a-http-post-request-with-httpclient
		// TODO Smsa kontakter
		// SmsManager.getDefault().sendTextMessage(destinationAddress, scAddress, text, sentIntent, deliveryIntent);
		return RECIEVE_OK;
	}
}
