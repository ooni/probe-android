package org.openobservatory.ooniprobe.test.test;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.model.jsonresult.JsonResult;
import org.openobservatory.ooniprobe.model.settings.Settings;

public class Psiphon extends AbstractTest {
    public static final String NAME = "psiphon";
    private static final String MK_NAME = "Psiphon";

    public Psiphon() {
        super(NAME, MK_NAME, R.string.Test_Psiphon_Fullname, R.drawable.test_psiphon, R.string.urlTestPsi, 20);
    }

    @Override public void run(Context c, PreferenceManager pm, Gson gson, Result result, int index, AbstractTest.TestCallback testCallback) {
        Settings settings = new Settings(c, pm);
        run(c, pm, gson, settings, result, index, testCallback);
    }

    @Override public void onEntry(Context c, PreferenceManager pm, @NonNull JsonResult json, Measurement measurement) {
        super.onEntry(c, pm, json, measurement);
        measurement.is_anomaly = json.test_keys.failure != null;
    }

}
