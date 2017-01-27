package org.openobservatory.ooniprobe;

import android.app.Application;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class ooniprobeApp extends Application {
    //for further reference http://stackoverflow.com/questions/11411395/how-to-get-current-foreground-activity-context-in-android
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/FiraSans-Bold.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}