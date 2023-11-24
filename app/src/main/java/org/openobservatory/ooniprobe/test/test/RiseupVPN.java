package org.openobservatory.ooniprobe.test.test;

import static org.openobservatory.ooniprobe.model.jsonresult.TestKeys.BLOCKED;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.AppLogger;
import org.openobservatory.ooniprobe.common.MapUtility;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.model.jsonresult.JsonResult;
import org.openobservatory.ooniprobe.model.settings.Settings;

@Deprecated
public class RiseupVPN extends AbstractTest {
    public static final String NAME = "riseupvpn";

    public RiseupVPN() {
        // NOTE: this test has been demoted to experimental and such the icon resource `R.drawable.test_riseupvpn` is not displayed anymore.
        super(NAME, R.string.Test_RiseupVPN_Fullname, 0, R.string.urlTestRvpn, 15);
    }

    @Override public void run(Context c, PreferenceManager pm, AppLogger logger, Gson gson, Result result, int index, AbstractTest.TestCallback testCallback) {
        Settings settings = new Settings(c, pm, isAutoRun());
        run(c, pm,logger, gson, settings, result, index, testCallback);
    }

    @Override public void onEntry(Context c, PreferenceManager pm, @NonNull JsonResult json, Measurement measurement) {
        super.onEntry(c, pm, json, measurement);
        //When json.test_keys.transport_status is null the test is failed so the result of is_anomaly doesn't matter.
        if (json.test_keys == null || json.test_keys.transport_status == null) {
            measurement.is_failed = true;
            return;
        }
        boolean isTransportBlocked = false;
            isTransportBlocked = MapUtility.getOrDefaultCompat(json.test_keys.transport_status, "openvpn", "ok").equals(BLOCKED) ||
                    MapUtility.getOrDefaultCompat(json.test_keys.transport_status, "obfs4", "ok").equals(BLOCKED);
        measurement.is_anomaly = !json.test_keys.ca_cert_status || json.test_keys.api_failure != null || isTransportBlocked;
    }

}
