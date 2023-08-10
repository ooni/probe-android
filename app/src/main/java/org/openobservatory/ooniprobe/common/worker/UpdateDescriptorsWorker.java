package org.openobservatory.ooniprobe.common.worker;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.ThirdPartyServices;
import org.openobservatory.ooniprobe.domain.TestDescriptorManager;
import org.openobservatory.ooniprobe.model.database.TestDescriptor;

import java.util.ArrayList;

public class UpdateDescriptorsWorker extends Worker {
    public static final String UPDATED_DESCRIPTORS_WORK_NAME = String.format("%s.UPDATED_DESCRIPTORS_WORK_NAME", UpdateDescriptorsWorker.class.getName());
    private static final String TAG = UpdateDescriptorsWorker.class.getSimpleName();
    public static final String UPDATE_DESCRIPTOR_CHANNEL = UpdateDescriptorsWorker.class.getSimpleName();
    private static final String KEY_UPDATED_DESCRIPTORS = String.format("%s.KEY_UPDATED_DESCRIPTORS", UpdateDescriptorsWorker.class.getName());

    public UpdateDescriptorsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        try {
            Context applicationContext = getApplicationContext();

            Log.d(TAG, "Fetching descriptors from input");
            makeStatusNotification("Starting update for descriptors");

            ArrayList<TestDescriptor> updatedDescriptors = new ArrayList<>();

            for (TestDescriptor descriptor : TestDescriptorManager.descriptorsWithAutoUpdateEnabled()) {

                Log.d(TAG, String.format("Fetching updates for %d ", descriptor.getRunId()));
                makeStatusNotification(String.format("Fetching updates for %s ", descriptor.getName()));

                TestDescriptor updatedDescriptor = TestDescriptorManager.fetchDescriptorFromRunId(descriptor.getRunId(), applicationContext);

                if (updatedDescriptor.getVersion() > descriptor.getVersion()) {

                    updatedDescriptor.setAutoUpdate(descriptor.isAutoUpdate());
                    updatedDescriptor.setAutoRun(descriptor.isAutoRun());

                    Log.d(TAG, String.format("Saving updates for %d ", descriptor.getRunId()));
                    makeStatusNotification(String.format("Saving updates for %s", descriptor.getName()));

                    updatedDescriptor.save();
                    updatedDescriptors.add(updatedDescriptor);
                }

            }

            Data outputData = new Data.Builder().putString(KEY_UPDATED_DESCRIPTORS, ((Application) applicationContext).getGson().toJson(updatedDescriptors)).build();
            makeStatusNotification("Descriptor updates complete");
            return Result.success(outputData);

        } catch (Exception exception) {
            Log.e(TAG, "Error Updating");
            makeStatusNotification("Error Updating");
            exception.printStackTrace();
            ThirdPartyServices.logException(exception);
            return Result.failure();
        }
    }

    /**
     * Create a Notification that is shown as a heads-up notification if possible.
     *
     * @param message Message shown on the notification
     */
    private void makeStatusNotification(String message) {

        // Make a channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel =
                    new NotificationChannel(UPDATE_DESCRIPTOR_CHANNEL, "Run Descriptor Updates", importance);
            channel.setDescription("Shows notification related to updates being made to Run Descriptors");

            // Add the channel
            NotificationManager notificationManager =
                    (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), UPDATE_DESCRIPTOR_CHANNEL)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Descriptor Update")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setVibrate(new long[0]);

        // Show the notification
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        NotificationManagerCompat.from(getApplicationContext()).notify((int) System.currentTimeMillis(), builder.build());
    }
}

