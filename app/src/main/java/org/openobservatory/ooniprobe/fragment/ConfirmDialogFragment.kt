package org.openobservatory.ooniprobe.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.os.Parcelable
import androidx.annotation.IntDef
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.openobservatory.ooniprobe.R
import java.io.Serializable

class ConfirmDialogFragment(
    serializable: Serializable? = null,
    parcelable: Parcelable? = null,
    title: String? = null,
    message: String? = null,
    positiveButton: String? = null,
    negativeButton: String? = null,
    neutralButton: String? = null
) : DialogFragment(), DialogInterface.OnClickListener {
    companion object {
        private const val MESSAGE = "MESSAGE"
        private const val TITLE = "TITLE"
        private const val POSITIVE_BUTTON = "POSITIVE_BUTTON"
        private const val NEGATIVE_BUTTON = "NEGATIVE_BUTTON"
        private const val NEUTRAL_BUTTON = "NEUTRAL_BUTTON"
        private const val SERIALIZABLE = "SERIALIZABLE"
        private const val PARCELABLE = "PARCELABLE"
    }

    private val listener: OnClickListener
        get() = parentFragment as? OnClickListener ?: requireActivity() as OnClickListener

    init {
        Bundle().apply {
            title?.let { putString(TITLE, it) }
            message?.let { putString(MESSAGE, it) }
            positiveButton?.let { putString(POSITIVE_BUTTON, it) }
            negativeButton?.let { putString(NEGATIVE_BUTTON, it) }
            neutralButton?.let { putString(NEUTRAL_BUTTON, it) }
            serializable?.let { putSerializable(SERIALIZABLE, it) }
            parcelable?.let { putParcelable(PARCELABLE, it) }
        }.let {
            if (!it.isEmpty) arguments = it
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?) =
        MaterialAlertDialogBuilder(requireContext(), R.style.Theme_App_MaterialDialogAlert).apply {
            setTitle(requireArguments().getString(TITLE))
            if (requireArguments().containsKey(MESSAGE)) setMessage(
                HtmlCompat.fromHtml(
                    requireArguments().getString(MESSAGE)!!,
                    HtmlCompat.FROM_HTML_MODE_COMPACT
                )
            )
            setPositiveButton(
                requireArguments().getString(
                    POSITIVE_BUTTON,
                    getString(android.R.string.ok)
                ), this@ConfirmDialogFragment
            )
            setNegativeButton(
                requireArguments().getString(
                    NEGATIVE_BUTTON,
                    getString(android.R.string.cancel)
                ), this@ConfirmDialogFragment
            )
            if (requireArguments().containsKey(NEUTRAL_BUTTON)) setNeutralButton(
                requireArguments().getString(
                    NEUTRAL_BUTTON
                ), this@ConfirmDialogFragment
            )
        }.create()

    override fun onClick(dialog: DialogInterface, which: Int) {
        listener.onConfirmDialogClick(
            requireArguments().getSerializable(SERIALIZABLE),
            requireArguments().getParcelable(PARCELABLE),
            which
        )
    }

    interface OnClickListener {
        fun onConfirmDialogClick(
            serializable: Serializable?,
            parcelable: Parcelable?,
            @ConfirmDialogButton buttonClicked: Int
        )
    }
}

@Retention(AnnotationRetention.SOURCE)
@IntDef(
    DialogInterface.BUTTON_POSITIVE,
    DialogInterface.BUTTON_NEGATIVE,
    DialogInterface.BUTTON_NEUTRAL
)
internal annotation class ConfirmDialogButton
