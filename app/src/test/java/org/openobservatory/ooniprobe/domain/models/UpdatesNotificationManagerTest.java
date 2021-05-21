package org.openobservatory.ooniprobe.domain.models;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.domain.UpdatesNotificationManager;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

public class UpdatesNotificationManagerTest {

    private final PreferenceManager preferenceManagerMock = mock(PreferenceManager.class);

    @Test
    public void testShouldShow() {
        // Arrange
        when(preferenceManagerMock.getAppOpenCount()).thenReturn(5L);
        when(preferenceManagerMock.isNotifications()).thenReturn(false);
        when(preferenceManagerMock.isAskNotificationDialogDisabled()).thenReturn(false);

        UpdatesNotificationManager manager = build();
        // Act
        boolean value = manager.shouldShow();

        // Assert
        Assert.assertTrue(value);
    }

    @Test
    public void testShouldNotShowWhenNotMultipleOf5() {
        // Arrange
        when(preferenceManagerMock.getAppOpenCount()).thenReturn(6L);
        when(preferenceManagerMock.isNotifications()).thenReturn(false);
        when(preferenceManagerMock.isAskNotificationDialogDisabled()).thenReturn(false);

        UpdatesNotificationManager manager = build();
        // Act
        boolean value = manager.shouldShow();

        // Assert
        assertFalse(value);
    }

    @Test
    public void testShouldNotShowWhenAskingDisabled() {
        // Arrange
        when(preferenceManagerMock.getAppOpenCount()).thenReturn(5L);
        when(preferenceManagerMock.isNotifications()).thenReturn(false);
        when(preferenceManagerMock.isAskNotificationDialogDisabled()).thenReturn(true);

        UpdatesNotificationManager manager = build();
        // Act
        boolean value = manager.shouldShow();

        // Assert
        assertFalse(value);
    }

    @Test
    public void testShouldNotShowWhenAlreadyEnabled() {
        // Arrange
        when(preferenceManagerMock.getAppOpenCount()).thenReturn(5L);
        when(preferenceManagerMock.isNotifications()).thenReturn(true);
        when(preferenceManagerMock.isAskNotificationDialogDisabled()).thenReturn(false);

        UpdatesNotificationManager manager = build();
        // Act
        boolean value = manager.shouldShow();

        // Assert
        assertFalse(value);
    }

    @Test
    public void testGetUpdates() {
        // Arrange
        UpdatesNotificationManager manager = build();

        // Act
        manager.getUpdates(true);

        // Assert
        Mockito.verify(preferenceManagerMock, times(1)).setNotificationsFromDialog(true);
    }

    @Test
    public void testDisableUpdates() {
        // Arrange
        UpdatesNotificationManager manager = build();

        // Act
        manager.getUpdates(false);

        // Assert
        Mockito.verify(preferenceManagerMock, times(1)).setNotificationsFromDialog(false);
    }



    @Test
    public void testDisableAskNotificationDialog() {
        // Arrange
        UpdatesNotificationManager manager = build();

        // Act
        manager.disableAskNotificationDialog();

        // Assert
        Mockito.verify(preferenceManagerMock, times(1)).disableAskNotificationDialog();
    }

    private UpdatesNotificationManager build() {
        return new UpdatesNotificationManager(preferenceManagerMock);
    }
}