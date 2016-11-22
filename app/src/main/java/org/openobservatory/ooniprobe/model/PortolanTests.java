// Part of measurement-kit <https://measurement-kit.github.io/>.
// Measurement-kit is free software. See AUTHORS and LICENSE for more
// information on the copying conditions.

package org.openobservatory.ooniprobe.model;

import android.util.Log;

import org.openobservatory.measurement_kit.sync.PortolanSyncApi;

public class PortolanTests {
    public static final String CHECK_PORT = "check_port";
    public static final String TRACEROUTE = "traceroute";

    public static void runTraceroute() {
        Log.v(TAG, "runTraceroute...");
        long prober = PortolanSyncApi.openProber(true, 33434);
        for (int ttl = 1; ttl < 32; ++ttl) {
            Log.v(TAG, "probe with TTL: " + ttl + " ...");
            String[] outStrings = {"", ""};
            int[] outInts = {0, 0, 0};
            double[] outDoubles = {0.0};
            PortolanSyncApi.sendProbe(prober, "208.67.222.222", 53, ttl, 2.0,
                    outStrings, outInts, outDoubles);
            Log.v(TAG, "probe with TTL: " + ttl + " ... done");
            Log.v(TAG, "outStrings: " + outStrings[0] + " " + outStrings[1]);
            Log.v(TAG, "outInts: " + outInts[0] + " " + outInts[1] + " " + outInts[2]);
            Log.v(TAG, "outDoubles: " + outDoubles[0]);
            if (outStrings[0].compareTo("PORT_IS_CLOSED") == 0) {
                Log.v(TAG, "port is closed -> break");
                break;
            }
            if (outStrings[0].compareTo("GOT_REPLY_PACKET") == 0) {
                Log.v(TAG, "got reply packet -> break");
                break;
            }
            if (outStrings[0].compareTo("OTHER") == 0) {
                Log.v(TAG, "other -> break");
                break;
            }
        }
        PortolanSyncApi.closeProber(prober);
        Log.v(TAG, "runTraceroute... done");
    }

    private static final String TAG = "portolan-tests";
}
