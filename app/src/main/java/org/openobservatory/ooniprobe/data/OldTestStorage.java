package org.openobservatory.ooniprobe.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.io.File;

public class OldTestStorage {
	private static final String PREFS_NAME = "OONIPROBE_APP";
	private static final String TESTS = "Test";
	private static final String NEW_TESTS = "new_tests";

	public static Boolean oldTestsDetected(Context context) {
		// used for retrieving arraylist from json formatted string
		return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).contains(TESTS);
	}

	public static void removeAllTests(Context context) {
		SharedPreferences settings;
		Editor editor;
		settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		editor = settings.edit();
		editor.remove(NEW_TESTS);
		editor.remove(TESTS);
		editor.apply();
		File dirFiles = context.getFilesDir();
		for (String strFile : dirFiles.list()) {
			if (strFile.contains("test-") || strFile.equals("hosts.txt") || strFile.equals("GeoIPASNum.dat") || strFile.equals("GeoIP.dat") || strFile.equals("global.txt")) {
				File file = new File(dirFiles, strFile);
				file.delete();
			}
		}
	}
}

