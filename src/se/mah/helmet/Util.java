package se.mah.helmet;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;

public class Util {

	public static boolean isServiceRunning(ActivityManager manager, String className) {
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
            if (className.equals(service.service.getClassName()))
                return true;
        return false;		
	}
}
