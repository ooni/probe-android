package org.openobservatory.ooniprobe;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;

import io.fabric.sdk.android.Fabric;

public class ooniprobeApp extends Application {
    static {
        System.loadLibrary("measurement_kit");
    }
    //for further reference http://stackoverflow.com/questions/11411395/how-to-get-current-foreground-activity-context-in-android
    @Override
    public void onCreate() {
        super.onCreate();
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final Boolean send_crash = preferences.getBoolean("send_crash", true);
        CrashlyticsCore core = new CrashlyticsCore.Builder().disabled(send_crash).build();
        Fabric.with(this, new Crashlytics.Builder().core(core).build());

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("webui/font-fira-sans-bold.5310ca5fb41a915987df5663660da770.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        FirebaseApp.initializeApp(this);
    }
}
