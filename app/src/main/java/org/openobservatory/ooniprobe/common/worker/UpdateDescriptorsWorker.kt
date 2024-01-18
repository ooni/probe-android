package org.openobservatory.ooniprobe.common.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.openobservatory.ooniprobe.R
import org.openobservatory.ooniprobe.common.Application
import org.openobservatory.ooniprobe.common.PreferenceManager
import org.openobservatory.ooniprobe.common.TestDescriptorManager
import org.openobservatory.ooniprobe.common.ThirdPartyServices
import org.openobservatory.ooniprobe.model.database.TestDescriptor
import org.openobservatory.ooniprobe.model.database.shouldUpdate
import javax.inject.Inject

var d: UpdateDescriptorsWorkerDependencies = UpdateDescriptorsWorkerDependencies()

class AutoUpdateDescriptorsWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val app = applicationContext.applicationContext as Application
        app.serviceComponent.inject(d)
        return try {
            Log.d(TAG, "Fetching descriptors from input")

            makeStatusNotification(
                applicationContext = applicationContext,
                message = "Starting update for descriptors",
                channelName = UPDATE_DESCRIPTOR_CHANNEL
            )
            val updatedDescriptors: ArrayList<TestDescriptor> = ArrayList()

            for (descriptor in d.testDescriptorManager.getDescriptorWithAutoUpdateEnabled()) {
                Log.d(TAG, "Fetching updates for ${descriptor.runId}")

                makeStatusNotification(
                    applicationContext = applicationContext,
                    message = "Fetching updates for ${descriptor.name}",
                    channelName = UPDATE_DESCRIPTOR_CHANNEL
                )

                val updatedDescriptor: TestDescriptor =
                    d.testDescriptorManager.fetchDescriptorFromRunId(
                        descriptor.runId,
                        applicationContext
                    )

                if (descriptor.shouldUpdate(updatedDescriptor)) {
                    updatedDescriptor.isAutoUpdate = descriptor.isAutoUpdate
                    updatedDescriptor.isAutoRun = descriptor.isAutoRun

                    Log.d(TAG, "Saving updates for ${descriptor.runId}")
                    makeStatusNotification(
                        applicationContext = applicationContext,
                        message = "Saving updates for ${descriptor.name}",
                        channelName = UPDATE_DESCRIPTOR_CHANNEL
                    )

                    updatedDescriptor.save()
                    updatedDescriptors.add(updatedDescriptor)
                }
            }

            val outputData = Data.Builder()
                .putString(
                    KEY_UPDATED_DESCRIPTORS,
                    (applicationContext as Application).gson.toJson(updatedDescriptors)
                ).build()

            makeStatusNotification(
                applicationContext = applicationContext,
                message = "Descriptor updates complete",
                channelName = UPDATE_DESCRIPTOR_CHANNEL
            )

            Result.success(outputData)

        } catch (exception: Exception) {
            Log.e(TAG, "Error Updating")
            makeStatusNotification(
                applicationContext = applicationContext,
                message = "Error Updating",
                channelName = UPDATE_DESCRIPTOR_CHANNEL
            )
            exception.printStackTrace()
            ThirdPartyServices.logException(exception)
            Result.failure()
        }
    }

    companion object {
        @JvmField
        var UPDATED_DESCRIPTORS_WORK_NAME =
            "${AutoUpdateDescriptorsWorker::class.java.name}.UPDATED_DESCRIPTORS_WORK_NAME"

        private val TAG = AutoUpdateDescriptorsWorker::class.java.simpleName

        private val UPDATE_DESCRIPTOR_CHANNEL: String =
            AutoUpdateDescriptorsWorker::class.java.simpleName

        private val KEY_UPDATED_DESCRIPTORS =
            "${AutoUpdateDescriptorsWorker::class.java.name}.KEY_UPDATED_DESCRIPTORS"
    }
}

class ManualUpdateDescriptorsWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val app = applicationContext.applicationContext as Application
        app.serviceComponent.inject(d)

        return try {
            Log.d(TAG, "Fetching descriptors from input")
            makeStatusNotification(
                applicationContext = applicationContext,
                message = "Starting update for descriptors",
                channelName = UPDATE_DESCRIPTOR_CHANNEL
            )

            val updatedDescriptors: ArrayList<TestDescriptor> = ArrayList()

            for (descriptor in d.testDescriptorManager.getDescriptorWithAutoUpdateDisabled()) {
                Log.d(TAG, "Fetching updates for ${descriptor.runId}")
                makeStatusNotification(
                    applicationContext = applicationContext,
                    message = "Fetching updates for ${descriptor.name}",
                    channelName = UPDATE_DESCRIPTOR_CHANNEL
                )

                val updatedDescriptor: TestDescriptor =
                    d.testDescriptorManager.fetchDescriptorFromRunId(
                        descriptor.runId,
                        applicationContext
                    )
                updatedDescriptors.add(updatedDescriptor)
            }
            val outputData = Data.Builder()
                .putString(
                    KEY_UPDATED_DESCRIPTORS,
                    (applicationContext as Application).gson.toJson(updatedDescriptors)
                ).build()
            makeStatusNotification(
                applicationContext = applicationContext,
                message = "fetching updates complete",
                channelName = UPDATE_DESCRIPTOR_CHANNEL
            )

            Result.success(outputData)

        } catch (exception: Exception) {
            Log.e(TAG, "Error Updating")
            makeStatusNotification(
                applicationContext = applicationContext,
                message = "Error Updating",
                channelName = UPDATE_DESCRIPTOR_CHANNEL
            )
            exception.printStackTrace()
            ThirdPartyServices.logException(exception)
            Result.failure()
        }
    }

    companion object {
        @JvmField
        var UPDATED_DESCRIPTORS_WORK_NAME =
            "${AutoUpdateDescriptorsWorker::class.java.name}.UPDATED_DESCRIPTORS_WORK_NAME"

        private val TAG = AutoUpdateDescriptorsWorker::class.java.simpleName

        private val UPDATE_DESCRIPTOR_CHANNEL: String =
            AutoUpdateDescriptorsWorker::class.java.simpleName

        private val KEY_UPDATED_DESCRIPTORS =
            "${AutoUpdateDescriptorsWorker::class.java.name}.KEY_UPDATED_DESCRIPTORS"
    }
}

class UpdateDescriptorsWorkerDependencies {
    @Inject
    lateinit var testDescriptorManager: TestDescriptorManager

    @Inject
    lateinit var preferenceManager: PreferenceManager
}

private fun makeStatusNotification(
    applicationContext: Context,
    title: String = "Descriptor Update",
    message: String,
    channelName: String
) {

    // Make a channel if necessary
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        val importance: Int = NotificationManager.IMPORTANCE_HIGH
        val channel =
            NotificationChannel(channelName, "Run Descriptor Updates", importance)
        channel.description =
            "Shows notification related to updates being made to Run Descriptors"

        // Add the channel
        val notificationManager: NotificationManager = applicationContext.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    // Create the notification
    val builder: NotificationCompat.Builder =
        NotificationCompat.Builder(applicationContext, channelName)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setVibrate(LongArray(0))

    // Show the notification
    if (ActivityCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return
    }
    NotificationManagerCompat.from(applicationContext)
        .notify(System.currentTimeMillis().toInt(), builder.build())
}
