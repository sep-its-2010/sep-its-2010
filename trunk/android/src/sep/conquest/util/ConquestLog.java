package sep.conquest.util;

import android.util.Log;

/**
 * ConquestLog represents a protocol for the e-puck Conquest application.
 * 
 * @author Andreas Wilhelm
 *
 */
public final class ConquestLog {
	
	public static final String TAG = "Conquest";
	public static final boolean ENABLED = true;
	
	public static void addMessage(Object obj, String msg) {
		if (ENABLED) Log.v(TAG, obj.getClass().toString() + ": " + msg);
	}
}
