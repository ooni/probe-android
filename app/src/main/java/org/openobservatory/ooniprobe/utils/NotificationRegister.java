package org.openobservatory.ooniprobe.utils;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class NotificationRegister extends FirebaseInstanceIdService {
    private static final String TAG = "NotificationRegister";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        NotificationService.getInstance(getApplicationContext()).setDevice_token(refreshedToken);
        NotificationService.getInstance(getApplicationContext()).sendRegistrationToServer();
    }
}
