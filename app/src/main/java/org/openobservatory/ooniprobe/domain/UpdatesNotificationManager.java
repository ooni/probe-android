package org.openobservatory.ooniprobe.domain;

import org.openobservatory.ooniprobe.common.PreferenceManager;

import javax.inject.Inject;

public class UpdatesNotificationManager {

    private final PreferenceManager pm;

    @Inject
    public UpdatesNotificationManager(PreferenceManager pm) {
        this.pm = pm;
    }

    public boolean shouldShow() {
        return pm.getAppOpenCount() != 0
                && pm.getAppOpenCount() % PreferenceManager.NOTIFICATION_DIALOG_COUNT == 0
                && !pm.isNotifications()
                && !pm.isAskNotificationDialogDisabled();
    }

    public boolean shouldShowAutoTest() {
        return pm.getAppOpenCount() != 0
                && pm.getAppOpenCount() % PreferenceManager.AUTOTEST_DIALOG_COUNT == 0
                && !pm.isAutomaticTestEnabled()
                && !pm.isAskAutomaticTestDialogDisabled();
    }

    public void getUpdates(boolean notificationUpdates) {
        pm.setNotificationsFromDialog(notificationUpdates);
    }

    public void disableAskNotificationDialog() {
        pm.disableAskNotificationDialog();
    }
}
