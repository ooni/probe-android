// Part of measurement-kit <https://measurement-kit.github.io/>.
// Measurement-kit is free software. See AUTHORS and LICENSE for more
// information on the copying conditions.

package io.github.measurement_kit.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

//CURRENTLY NOT USED (will be needed in the future)
public class TestCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String testName = intent.getAction();
        Log.v(TAG, "received complete: " + testName);
        // TODO: it's not clear to me how to proceed from here; specifically whether it's
        // safe to call the activity from here, or whether we should cache what we have and
        // wait for the activity to poll us.
    }

    private static final String TAG = "test-complete-receiver";
}
