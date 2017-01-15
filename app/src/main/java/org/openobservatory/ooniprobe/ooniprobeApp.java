package org.openobservatory.ooniprobe;

import android.app.Application;

import org.openobservatory.ooniprobe.activity.MainActivity;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class ooniprobeApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/HelveticaNeue-Roman.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    private MainActivity mCurrentActivity = null;
    public MainActivity getCurrentActivity(){
        return mCurrentActivity;
    }
    public void setCurrentActivity(MainActivity mCurrentActivity){
        this.mCurrentActivity = mCurrentActivity;
    }
}