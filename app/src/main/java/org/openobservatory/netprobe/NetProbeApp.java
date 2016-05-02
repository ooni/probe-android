package org.openobservatory.netprobe;

import android.app.Application;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by lorenzo on 27/04/16.
 */
public class NetProbeApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Inconsolata.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}