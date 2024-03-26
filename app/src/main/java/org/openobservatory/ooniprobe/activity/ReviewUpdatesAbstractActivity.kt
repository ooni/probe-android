package org.openobservatory.ooniprobe.activity

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar
import org.openobservatory.ooniprobe.activity.reviewdescriptorupdates.ReviewDescriptorUpdatesActivity

open class ReviewUpdatesAbstractActivity : AbstractActivity() {
    var reviewUpdatesLauncher: ActivityResultLauncher<Intent>? = null

    fun registerReviewLauncher(view: View, reviewCompletedListener: () -> Unit?) {
        reviewUpdatesLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result: ActivityResult ->
                if (result.resultCode == RESULT_OK) {
                    reviewCompletedListener()
                    result.data?.let { intent: Intent ->
                        intent.getStringExtra(ReviewDescriptorUpdatesActivity.RESULT_MESSAGE)
                            ?.let { message: String ->
                                Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                                    .setAnchorView(view).show()
                            }
                    }
                }
            }
    }
}