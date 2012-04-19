package se.mah.helmet;

import java.util.Date;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.text.format.DateFormat;

public class Util {

	public static boolean isServiceRunning(ActivityManager manager, String className) {
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
            if (className.equals(service.service.getClassName()))
                return true;
        return false;		
	}
	
	public static String getDateFormatISO8601(Date date) {
		return DateFormat.format("yyyy-MM-dd HH:mm:ss.SSSZ", date).toString();
	}
}
