package org.openobservatory.ooniprobe;

import android.app.Application;

import org.openobservatory.ooniprobe.activity.MainActivity;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class ooniprobeApp extends Application {
    //for further reference http://stackoverflow.com/questions/11411395/how-to-get-current-foreground-activity-context-in-android
    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/HelveticaNeue-Roman.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}