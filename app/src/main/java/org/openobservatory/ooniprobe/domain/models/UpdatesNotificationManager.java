package org.openobservatory.ooniprobe.domain.models;

import org.openobservatory.ooniprobe.common.PreferenceManager;

import javax.inject.Inject;

public class UpdatesNotificationManager {

    private final PreferenceManager pm;

    @Inject
    UpdatesNotificationManager(PreferenceManager pm) {
        this.pm = pm;
    }

    public boolean shouldShow() {
        return pm.getAppOpenCount() != 0
                && pm.getAppOpenCount() % PreferenceManager.NOTIFICATION_DIALOG_COUNT == 0
                && !pm.isNotifications()
                && !pm.isAskNotificationDialogDisabled();
    }

    public void getUpdates(boolean notificationUpdates) {
        pm.setNotificationsFromDialog(notificationUpdates);
    }

    public void disableAskNotificationDialog() {
        pm.disableAskNotificationDialog();
    }
}
