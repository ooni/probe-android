package org.openobservatory.ooniprobe.test.test;

import static org.openobservatory.ooniprobe.model.jsonresult.TestKeys.BLOCKED;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.MapUtility;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.model.jsonresult.JsonResult;
import org.openobservatory.ooniprobe.model.settings.Settings;

import java.util.HashMap;

public class RiseupVPN extends AbstractTest {
    public static final String NAME = "riseupvpn";

    public RiseupVPN() {
        super(NAME, R.string.Test_RiseupVPN_Fullname, R.drawable.test_riseupvpn, R.string.urlTestRvpn, 15);
    }

    @Override public void run(Context c, PreferenceManager pm, Gson gson, Result result, int index, AbstractTest.TestCallback testCallback) {
        Settings settings = new Settings(c, pm);
        run(c, pm, gson, settings, result, index, testCallback);
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
