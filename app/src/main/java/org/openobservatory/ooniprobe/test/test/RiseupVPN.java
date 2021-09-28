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
        boolean isTransportBlocked = false;
            isTransportBlocked = getOrDefault(json.test_keys.transport_status, "openvpn", "ok").equals("blocked") ||
                    getOrDefault(json.test_keys.transport_status, "obfs4", "ok").equals("blocked");
        measurement.is_anomaly = !json.test_keys.ca_cert_status || json.test_keys.api_failure != null || isTransportBlocked;
    }

    public Object getOrDefault(HashMap map, Object key, Object defaultValue) {
        //ask your app running more modern API as level 24 (Build.VERSION_CODES.N(ougat))
        return (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) ?
                map.getOrDefault(key, defaultValue) :
        // if not, then need to solve with similar code of original code in next below
        ((map.get(key) != null) ? map.get(key) : defaultValue);
    }

}
