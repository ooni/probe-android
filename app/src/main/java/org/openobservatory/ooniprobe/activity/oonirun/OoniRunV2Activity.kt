package org.openobservatory.ooniprobe.activity.oonirun

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import org.openobservatory.ooniprobe.R
import org.openobservatory.ooniprobe.activity.AbstractActivity
import org.openobservatory.ooniprobe.activity.adddescriptor.AddDescriptorActivity
import org.openobservatory.ooniprobe.common.TaskExecutor
import org.openobservatory.ooniprobe.common.TestDescriptorManager
import org.openobservatory.ooniprobe.common.ThirdPartyServices
import org.openobservatory.ooniprobe.databinding.ActivityOoniRunV2Binding
import org.openobservatory.ooniprobe.model.database.TestDescriptor
import javax.inject.Inject

/**
 * Activity to handle a v2 link.
 *
 * A v2 link has the following format:
 *
 * 1. ooni://runv2/link_id
 * 2. https://run.test.ooni.org/v2/link_id
 *
 * The activity is started when the user clicks on `Open Link in OONI Probe` or
 * when the system recognizes this app can open this link and launches the app when a link is clicked.
 *
 * It fetches the descriptor from the link and starts the `AddDescriptorActivity`.
 * If the link is invalid, it shows an error message and ends the activity.
 *
 * @see {@link org.openobservatory.ooniprobe.activity.OoniRunActivity} for v1 links.
 */
class OoniRunV2Activity : AbstractActivity() {
    lateinit var binding: ActivityOoniRunV2Binding

    @Inject
    lateinit var descriptorManager: TestDescriptorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        binding = ActivityOoniRunV2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        onNewIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        /**
         * Check if we are starting the activity from a link [Intent.ACTION_VIEW].
         * This is invoked when a v2 link is opened.
         * @see {@link org.openobservatory.ooniprobe.activity.OoniRunActivity.newIntent} for v1 links.
         */
        if (Intent.ACTION_VIEW == intent.action) {
            manageIntent(intent)
        } else {
            // If the intent action is not `Intent.ACTION_VIEW`, end activity.
            Toast.makeText(this, getString(R.string.Modal_Error), Toast.LENGTH_LONG).show()
            finish()
        }
    }

    /**
     * Parses the intent data to extract the link.
     * If the intent does not contain a link, show an error message and end the activity.
     * If the intent contains a link, but it is not a supported link or has a non-numerical `link_id`,
     * show an error message and end the activity.
     * If the intent contains a link, but the `link_id` is zero,
     * show an error message and end the activity.
     * If the intent contains a link with a valid `link_id`,
     * fetch the descriptor from the link and start the `AddDescriptorActivity`.
     *
     * @param intent The intent data.
     */
    private fun manageIntent(intent: Intent) {
        // If the intent does not contain a link, do nothing.
        val uri = intent.data ?: finishWithError().run { return }
        // If the intent contains a link, but it is not a supported link or has a non-numerical `link_id`.
        val possibleRunId: Long = getRunId(uri) ?: finishWithError().run { return }

        // If the intent contains a link, but the `link_id` is zero.
        if (possibleRunId == 0L) {
            finishWithError().run { return }
        }
        val executor = TaskExecutor()
        binding.cancelButton.setOnClickListener {
            executor.cancelTask()
            finishWithError(message = getString(R.string.Modal_Cancel))
        }
        executor.executeTask({
            try {
                return@executeTask descriptorManager.fetchDescriptorFromRunId(
                    possibleRunId,
                    this
                )
            } catch (exception: Exception) {
                exception.printStackTrace()
                ThirdPartyServices.logException(exception)
                return@executeTask null
            }
        }) { descriptorResponse: TestDescriptor? ->
            fetchDescriptorComplete(
                descriptorResponse
            )
        }
    }

    /**
     * Shows an error message and ends the activity.
     *
     * @param message The error message to be shown.
     */
    private fun finishWithError(message: String = getString(R.string.Modal_Error)) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        finish()
    }

    /**
     * The task to fetch the descriptor from the link is completed.
     *
     *
     * This method is called when the `fetchDescriptorFromRunId` task is completed.
     * The `descriptorResponse` is the result of the task.
     * If the task is successful, the `descriptorResponse` is the descriptor.
     * Otherwise, the `descriptorResponse` is null.
     *
     * If the `descriptorResponse` is not null, start the `AddDescriptorActivity`.
     * Otherwise, show an error message.
     *
     * @param descriptorResponse The result of the task.
     * @return null.
     */
    private fun fetchDescriptorComplete(descriptorResponse: TestDescriptor?) {
        descriptorResponse?.let {
            startActivity(AddDescriptorActivity.newIntent(this, descriptorResponse))
        } ?: run {
            Toast.makeText(this, getString(R.string.Modal_Error), Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Extracts the run id from the provided Uri.
     * The run id can be in two different formats:
     *
     * 1. ooni://runv2/link_id
     * 2. https://run.test.ooni.org/v2/link_id
     *
     * The run id is the `link_id` in the link.
     * If the Uri contains a link, but the `link_id` is not a number, null is returned.
     * If the Uri contains a link, but it is not a supported link, null is returned.
     *
     * @param uri The Uri data.
     * @return The run id if the Uri contains a link with a valid `link_id`, or null otherwise.
     */
    private fun getRunId(uri: Uri): Long? {
        val host = uri.host
        try {
            when (host) {
                "runv2" -> {
                    /*
                     * The run id is the first segment of the path.
                     * Launched when `Open Link in OONI Probe` is clicked.
                     * e.g. ooni://runv2/link_id
                     */
                    return uri.pathSegments[0].toLong()
                }

                "run.test.ooni.org" -> {
                    /*
                     * The run id is the second segment of the path.
                     * Launched when the system recognizes this app can open this link
                     * and launches the app when a link is clicked.
                     * e.g. https://run.test.ooni.org/v2/link_id
                     */
                    return uri.pathSegments[1].toLong()
                }

                else -> return null
            }
        } catch (e: Exception) {
            // If the intent contains a link, but the `link_id` is not a number.
            e.printStackTrace()
            return null
        }
    }

}