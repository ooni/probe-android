package org.openobservatory.ooniprobe.fragment.dynamicprogressbar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.openobservatory.ooniprobe.R
import org.openobservatory.ooniprobe.databinding.RunDynamicProgressBarBinding

/**
 * A [Fragment] subclass that displays a dynamic progress bar and handles user actions.
 * The progress bar can be in one of the following states: [ProgressType.ADD_LINK], [ProgressType.UPDATE_LINK], [ProgressType.REVIEW_LINK].
 * The user actions are handled through the OnActionListener interface.
 * Use the [OONIRunDynamicProgressBar.newInstance] factory method to create an instance of this fragment.
 */
class OONIRunDynamicProgressBar : Fragment() {
    private var progressType: ProgressType? = null
    private var onActionListener: OnActionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            progressType =
                ProgressType.valueOf(it.getString(PROGRESS_TYPE) ?: ProgressType.ADD_LINK.name)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = RunDynamicProgressBarBinding.inflate(inflater, container, false)
        binding.actionButton.setOnClickListener {
            onActionListener?.onActionButtonCLicked()
        }
        binding.iconButton.setOnClickListener {
            onActionListener?.onCloseButtonClicked()
        }
        when (progressType) {
            ProgressType.ADD_LINK -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.iconButton.visibility = View.GONE
                binding.actionButton.text = getString(R.string.Modal_Cancel)
                binding.progressText.text = getString(R.string.Dashboard_Progress_AddLink_Label)
            }

            ProgressType.UPDATE_LINK -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.iconButton.visibility = View.GONE
                binding.progressText.text = getString(R.string.Dashboard_Progress_UpdateLink_Label)
            }

            ProgressType.REVIEW_LINK -> {
                binding.progressBar.visibility = View.GONE
                binding.iconButton.visibility = View.VISIBLE
                binding.actionButton.text = getString(R.string.Dashboard_Progress_ReviewLink_Action)
                binding.progressText.text = getString(R.string.Dashboard_Progress_ReviewLink_Label)
            }

            else -> {
                binding.progressBar.visibility = View.GONE
                binding.iconButton.visibility = View.GONE
                binding.progressText.text = getString(R.string.Modal_Error)
            }
        }
        return binding.root
    }

    companion object {
        private const val PROGRESS_TYPE = "PROGRESS_TYPE"

        @JvmStatic
        var TAG: String = OONIRunDynamicProgressBar::class.java.name

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param progressType The type of progress to be displayed.
         * @param onActionListener The listener for user actions.
         * @return A new instance of DynamicProgressFragment.
         */
        @JvmStatic
        fun newInstance(
            progressType: ProgressType,
            onActionListener: OnActionListener?
        ): OONIRunDynamicProgressBar {
            return OONIRunDynamicProgressBar().apply {
                arguments = Bundle().apply {
                    putString(PROGRESS_TYPE, progressType.name)
                }
                this.onActionListener = onActionListener
            }
        }
    }
}

/**
 * Enum representing the type of progress to be displayed in the DynamicProgressFragment.
 */
enum class ProgressType {
    ADD_LINK, UPDATE_LINK, REVIEW_LINK
}

/**
 * Interface for handling user actions in the DynamicProgressFragment.
 */
interface OnActionListener {
    /**
     * Called when the action button is clicked.
     */
    fun onActionButtonCLicked()

    /**
     * Called when the icon button is clicked.
     */
    fun onCloseButtonClicked()
}