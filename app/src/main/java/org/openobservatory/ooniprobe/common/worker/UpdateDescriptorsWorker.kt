package org.openobservatory.ooniprobe.common.worker

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.openobservatory.ooniprobe.common.Application
import org.openobservatory.ooniprobe.common.PreferenceManager
import org.openobservatory.ooniprobe.common.TestDescriptorManager
import org.openobservatory.ooniprobe.common.ThirdPartyServices
import org.openobservatory.ooniprobe.model.database.TestDescriptor
import org.openobservatory.ooniprobe.model.database.shouldUpdate
import javax.inject.Inject

var d: UpdateDescriptorsWorkerDependencies = UpdateDescriptorsWorkerDependencies()
const val PROGRESS = "PROGRESS"

class AutoUpdateDescriptorsWorker(
        context: Context,
        workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {

        setProgressAsync(Data.Builder().putInt(PROGRESS, 0).build())

        val app = applicationContext.applicationContext as Application
        app.serviceComponent.inject(d)

        return try {
            Log.d(TAG, "Fetching descriptors from input")

            val updatedDescriptors: ArrayList<TestDescriptor> = ArrayList()

            for (descriptor in d.testDescriptorManager.getRunV2Descriptors(
                    autoUpdate = true,
                    expired = false
            )) {

                Log.d(TAG, "Fetching updates for ${descriptor.runId}")

                d.testDescriptorManager.fetchDescriptorFromRunId(
                        descriptor.runId,
                        applicationContext
                )?.let { updatedDescriptor ->
                    if (descriptor.shouldUpdate(updatedDescriptor)) {
                        updatedDescriptor.isAutoUpdate = descriptor.isAutoUpdate

                        Log.d(TAG, "Saving updates for ${descriptor.runId}")

                        updatedDescriptor.save()
                        updatedDescriptors.add(updatedDescriptor)
                    }
                }
            }

            val outputData = Data.Builder()
                .putString(
                    KEY_UPDATED_DESCRIPTORS,
                    (applicationContext as Application).gson.toJson(updatedDescriptors)
                ).build()

            Log.e(TAG, "Descriptor updates complete")

            Result.success(outputData)

        } catch (exception: Exception) {
            Log.e(TAG, "Error Updating")
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

        setProgressAsync(Data.Builder().putInt(PROGRESS, 0).build())

        val app = applicationContext.applicationContext as Application
        app.serviceComponent.inject(d)

        return try {
            Log.d(TAG, "Fetching descriptors from input")

            val updatedDescriptors: ArrayList<TestDescriptor> = ArrayList()

            val descriptors = inputData.getLongArray(KEY_DESCRIPTOR_IDS)?.let {
                d.testDescriptorManager.getDescriptorsFromIds(it.toTypedArray())
            } ?: run {
                d.testDescriptorManager.getRunV2Descriptors(expired = false)
            }

            if (descriptors.isEmpty()) {
                Log.e(TAG, "No descriptors to update")
                return Result.success()
            }

            for (descriptor in descriptors) {
                Log.d(TAG, "Fetching updates for ${descriptor.runId}")

                d.testDescriptorManager.fetchDescriptorFromRunId(
                        descriptor.runId,
                        applicationContext
                )?.let { updatedDescriptor ->

                    /**
                     * NOTE(aanorbel): Refine this logic to only update if the descriptor has changed.
                     * Consider explicit version compare.
                     */
                    if (descriptor.shouldUpdate(updatedDescriptor)) {
                        if (descriptor.isAutoUpdate) {
                            updatedDescriptor.isAutoUpdate = true

                            Log.d(TAG, "Saving updates for ${descriptor.runId}")

                            updatedDescriptor.save()
                        } else {
                            Log.d(TAG, "Not saving updates for ${descriptor.runId}")
                            updatedDescriptors.add(updatedDescriptor)
                        }
                    }
                }
            }
            val outputData = Data.Builder()
                .putString(
                    KEY_UPDATED_DESCRIPTORS,
                    (applicationContext as Application).gson.toJson(updatedDescriptors)
                ).build()

            Log.e(TAG, "fetching updates complete")

            setProgressAsync(Data.Builder().putInt(PROGRESS, 100).build())
            if (updatedDescriptors.isEmpty()) {
                Log.e(TAG, "No descriptors were updated")
                Result.success()
            } else {
                Result.success(outputData)
            }

        } catch (exception: Exception) {
            Log.e(TAG, "Error Updating")
            exception.printStackTrace()
            ThirdPartyServices.logException(exception)
            Result.failure()
        }
    }

    companion object {
        @JvmField
        var UPDATED_DESCRIPTORS_WORK_NAME =
                "${ManualUpdateDescriptorsWorker::class.java.name}.UPDATED_DESCRIPTORS_WORK_NAME"

        private val TAG = ManualUpdateDescriptorsWorker::class.java.simpleName

        private val UPDATE_DESCRIPTOR_CHANNEL: String =
                ManualUpdateDescriptorsWorker::class.java.simpleName

        @JvmField
        var KEY_UPDATED_DESCRIPTORS =
                "${ManualUpdateDescriptorsWorker::class.java.name}.KEY_UPDATED_DESCRIPTORS"

        @JvmField
        var KEY_DESCRIPTOR_IDS =
                "${ManualUpdateDescriptorsWorker::class.java.name}.KEY_DESCRIPTOR_IDS"
    }
}

class UpdateDescriptorsWorkerDependencies {
    @Inject
    lateinit var testDescriptorManager: TestDescriptorManager

    @Inject
    lateinit var preferenceManager: PreferenceManager
}
