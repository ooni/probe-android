package org.openobservatory.ooniprobe.fragment.resultList

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import org.openobservatory.ooniprobe.R
import org.openobservatory.ooniprobe.common.OONITests
import org.openobservatory.ooniprobe.databinding.ItemCircumventionBinding
import org.openobservatory.ooniprobe.databinding.ItemDateBinding
import org.openobservatory.ooniprobe.databinding.ItemExperimentalBinding
import org.openobservatory.ooniprobe.databinding.ItemFailedBinding
import org.openobservatory.ooniprobe.databinding.ItemInstantmessagingBinding
import org.openobservatory.ooniprobe.databinding.ItemPerformanceBinding
import org.openobservatory.ooniprobe.databinding.ItemTestsuiteBinding
import org.openobservatory.ooniprobe.databinding.ItemWebsitesBinding
import org.openobservatory.ooniprobe.fragment.resultHeader.ResultHeaderPerformanceFragment
import org.openobservatory.ooniprobe.model.database.Measurement
import org.openobservatory.ooniprobe.model.database.Network
import org.openobservatory.ooniprobe.model.database.Result
import org.openobservatory.ooniprobe.test.test.Dash
import org.openobservatory.ooniprobe.test.test.Ndt
import java.util.Date
import java.util.Locale
import java.util.Objects
import java.util.concurrent.TimeUnit

class ResultListAdapter(
        private val onClickListener: View.OnClickListener,
        private val onLongClickListener: View.OnLongClickListener,
) : ListAdapter<Any, ViewHolder<*>>(ResultComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<*> {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(when (viewType) {
            ViewItemType.WEBSITES.viewType -> ItemWebsitesBinding.inflate(layoutInflater, parent, false)
            ViewItemType.INSTANT_MESSAGING.viewType -> ItemInstantmessagingBinding.inflate(layoutInflater, parent, false)
            ViewItemType.PERFORMANCE.viewType -> ItemPerformanceBinding.inflate(layoutInflater, parent, false)
            ViewItemType.CIRCUMVENTION.viewType -> ItemCircumventionBinding.inflate(layoutInflater, parent, false)
            ViewItemType.EXPERIMENTAL.viewType -> ItemExperimentalBinding.inflate(layoutInflater, parent, false)
            ViewItemType.RUN.viewType -> ItemExperimentalBinding.inflate(layoutInflater, parent, false)
            ViewItemType.FAILED.viewType -> ItemFailedBinding.inflate(layoutInflater, parent, false)
            ViewItemType.SEPERATOR.viewType -> ItemDateBinding.inflate(layoutInflater, parent, false)
            else -> ItemTestsuiteBinding.inflate(layoutInflater, parent, false)
        })
    }

    override fun onBindViewHolder(holder: ViewHolder<*>, position: Int) {
        try {
            val item = getItem(position)
            holder.itemView.tag = item
            holder.itemView.setOnClickListener(onClickListener)
            holder.itemView.setOnLongClickListener(onLongClickListener)

            val context = holder.itemView.context
            if (item is Result) {

                holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, if (item.is_viewed) android.R.color.transparent else R.color.color_yellow0))

                holder?.let { viewHolder ->

                    viewHolder.binding.let { binding ->
                        when (binding) {
                            is ItemFailedBinding -> {
                                binding.testName.setTextColor(ContextCompat.getColor(context, R.color.color_gray6))
                                item.getDescriptor(context)?.let {
                                    if (it.isPresent) {
                                        binding.icon.setImageResource(it.get().getDisplayIcon(context))
                                        binding.testName.text = it.get().title
                                    } else {
                                        binding.testName.text = item.test_group_name
                                    }
                                }
                                var failureMsg: String = context.getString(R.string.TestResults_Overview_Error)
                                if (item.failure_msg != null) {
                                    failureMsg += " - " + item.failure_msg
                                } else {
                                    // NOTE: If the test is running for more than 5 minutes, we assume it's stuck or failed,
                                    // and we show the default error message.
                                    val MAX_DURATION = TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES)
                                    val duration: Long = Date().time - item.start_time.time
                                    if (duration < MAX_DURATION) {
                                        failureMsg = context.getString(R.string.Dashboard_Running_Running).replace(":", "")
                                    }
                                }
                                binding.subtitle.text = failureMsg
                                binding.startTime.text = DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), "yMdHm"), item.start_time)
                            }

                            is ItemWebsitesBinding -> {
                                binding.asnName.text = Network.toString(context, item.network)
                                binding.startTime.text = DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), "yMdHm"), item.start_time)
                                val blocked: Long = item.countAnomalousMeasurements()
                                val tested: Long = item.countTotalMeasurements()
                                binding.failedMeasurements.text = context.resources.getQuantityString(R.plurals.TestResults_Overview_Websites_Blocked, blocked.toInt(), blocked.toString())
                                binding.testedMeasurements.text = context.resources.getQuantityString(R.plurals.TestResults_Overview_Websites_Tested, tested.toInt(), tested.toString())
                                binding.failedMeasurements.setTextColor(ContextCompat.getColor(context, if (blocked == 0L) R.color.color_gray9 else R.color.color_yellow9))
                                DrawableCompat.setTint(DrawableCompat.wrap(binding.failedMeasurements.getCompoundDrawablesRelative()[0]).mutate(), ContextCompat.getColor(context, if (blocked == 0L) R.color.color_gray9 else R.color.color_yellow9))
                                var allUploaded = true
                                for (m in item.getMeasurements()) allUploaded = allUploaded && (m.isUploaded || m.is_failed)
                                binding.startTime.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, if (allUploaded) 0 else R.drawable.cloudoff, 0)
                            }

                            is ItemInstantmessagingBinding -> {
                                binding.asnName.text = Network.toString(context, item.network)
                                binding.startTime.text = DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), "yMdHm"), item.start_time)
                                val blocked: Long = item.countAnomalousMeasurements()
                                val available: Long = item.countOkMeasurements()
                                binding.failedMeasurements.text = context.resources.getQuantityString(R.plurals.TestResults_Overview_InstantMessaging_Blocked, blocked.toInt(), blocked.toString())
                                binding.okMeasurements.text = context.resources.getQuantityString(R.plurals.TestResults_Overview_InstantMessaging_Available, available.toInt(), available.toString())
                                binding.failedMeasurements.setTextColor(ContextCompat.getColor(context, if (blocked == 0L) R.color.color_gray9 else R.color.color_yellow9))
                                DrawableCompat.setTint(DrawableCompat.wrap(binding.failedMeasurements.getCompoundDrawablesRelative()[0]).mutate(), ContextCompat.getColor(context, if (blocked == 0L) R.color.color_gray9 else R.color.color_yellow9))
                                var allUploaded = true
                                for (m in item.getMeasurements()) allUploaded = allUploaded && (m.isUploaded || m.is_failed)
                                binding.startTime.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, if (allUploaded) 0 else R.drawable.cloudoff, 0)
                            }

                            is ItemPerformanceBinding -> {
                                binding.asnName.text = Network.toString(context, item.network)
                                binding.startTime.text = DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), "yMdHm"), item.start_time)
                                val dashM: Measurement? = item.getMeasurement(Dash.NAME)
                                val ndtM: Measurement? = item.getMeasurement(Ndt.NAME)
                                binding.quality.setText(dashM?.testKeys?.getVideoQuality(false)
                                        ?: R.string.TestResults_NotAvailable)
                                binding.upload.text = if (ndtM == null) {
                                    context.getString(R.string.TestResults_NotAvailable)
                                } else {
                                    context.getString(R.string.twoParam, ndtM.testKeys.getUpload(context), context.getString(ndtM.testKeys.uploadUnit))
                                }
                                binding.download.text = if (ndtM == null) {
                                    context.getString(R.string.TestResults_NotAvailable)
                                } else {
                                    context.getString(R.string.twoParam, ndtM.testKeys.getDownload(context), context.getString(ndtM.testKeys.downloadUnit))
                                }
                                binding.quality.setAlpha(if (dashM == null) {
                                    ResultHeaderPerformanceFragment.ALPHA_DIS
                                } else {
                                    ResultHeaderPerformanceFragment.ALPHA_ENA.toFloat()
                                })
                                binding.upload.setAlpha(if (ndtM == null) {
                                    ResultHeaderPerformanceFragment.ALPHA_DIS
                                } else {
                                    ResultHeaderPerformanceFragment.ALPHA_ENA.toFloat()
                                })
                                binding.download.setAlpha(if (ndtM == null) {
                                    ResultHeaderPerformanceFragment.ALPHA_DIS
                                } else {
                                    ResultHeaderPerformanceFragment.ALPHA_ENA.toFloat()
                                })
                                var allUploaded = true
                                for (m in item.getMeasurements()) {
                                    allUploaded = allUploaded && (m.isUploaded || m.is_failed)
                                }
                                binding.startTime.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, if (allUploaded) 0 else R.drawable.cloudoff, 0)
                            }

                            is ItemCircumventionBinding -> {
                                binding.asnName.text = Network.toString(context, item.network)
                                binding.startTime.text = DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), "yMdHm"), item.start_time)
                                val blocked: Long = item.countAnomalousMeasurements()
                                val available: Long = item.countOkMeasurements()
                                binding.failedMeasurements.text = context.resources.getQuantityString(R.plurals.TestResults_Overview_Circumvention_Blocked, blocked.toInt(), blocked.toString())
                                binding.okMeasurements.text = context.resources.getQuantityString(R.plurals.TestResults_Overview_Circumvention_Available, available.toInt(), available.toString())
                                binding.failedMeasurements.setTextColor(ContextCompat.getColor(context, if (blocked == 0L) R.color.color_gray9 else R.color.color_yellow9))
                                DrawableCompat.setTint(DrawableCompat.wrap(binding.failedMeasurements.getCompoundDrawablesRelative()[0]).mutate(), ContextCompat.getColor(context, if (blocked == 0L) R.color.color_gray9 else R.color.color_yellow9))
                                var allUploaded = true
                                for (m in item.getMeasurements()) allUploaded = allUploaded && (m.isUploaded || m.is_failed)
                                binding.startTime.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, if (allUploaded) 0 else R.drawable.cloudoff, 0)
                            }

                            is ItemExperimentalBinding -> {
                                binding.totalMeasurements.text = String.format("%d measured", item.countTotalMeasurements())
                                binding.asnName.text = Network.toString(context, item.network)
                                binding.startTime.text = DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), "yMdHm"), item.start_time)
                                var allUploaded = true
                                for (m in item.getMeasurements()) allUploaded = allUploaded && (m.isUploaded || m.is_failed)
                                binding.startTime.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, if (allUploaded) 0 else R.drawable.cloudoff, 0)
                                item.getDescriptor(context).get().let { descriptor ->
                                    binding.icon.setImageResource(descriptor.getDisplayIcon(context))
                                    binding.icon.setColorFilter(descriptor.color)
                                    binding.name.text = descriptor.title
                                    binding.name.setTextColor(descriptor.color)
                                }
                            }
                        }
                    }
                }

            } else if (item is String) {
                holder?.binding?.let {
                    if (it is ItemDateBinding) {
                        it.textView.text = item
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemViewType(position: Int): Int {

        val viewTypeMap = mapOf(
                "separator" to ViewItemType.SEPERATOR.viewType,
                OONITests.WEBSITES.toString() to ViewItemType.WEBSITES.viewType,
                OONITests.INSTANT_MESSAGING.toString() to ViewItemType.INSTANT_MESSAGING.viewType,
                OONITests.PERFORMANCE.toString() to ViewItemType.PERFORMANCE.viewType,
                OONITests.CIRCUMVENTION.toString() to ViewItemType.CIRCUMVENTION.viewType,
                OONITests.EXPERIMENTAL.toString() to ViewItemType.EXPERIMENTAL.viewType,
        )

        val iResult: Any? = getItem(position)
        return when (iResult) {
            is String -> viewTypeMap["separator"] ?: ViewItemType.SEPERATOR.viewType
            is Result -> {
                when {
                    iResult.countTotalMeasurements() == 0L -> ViewItemType.FAILED.viewType
                    iResult.descriptor != null -> ViewItemType.RUN.viewType
                    else -> viewTypeMap[iResult.test_group_name] ?: ViewItemType.FAILED.viewType
                }
            }

            else -> ViewItemType.FAILED.viewType
        }
    }
}

enum class ViewItemType(val viewType: Int) {
    WEBSITES(0), INSTANT_MESSAGING(1), PERFORMANCE(2), CIRCUMVENTION(3), EXPERIMENTAL(4), RUN(5), FAILED(-1), SEPERATOR(-2);
}

class ViewHolder<T : ViewBinding>(val binding: T) : RecyclerView.ViewHolder(binding.root)


class ResultComparator : DiffUtil.ItemCallback<Any>() {
    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        return when {
            oldItem is Result && newItem is Result -> {
                oldItem.id == newItem.id
            }

            oldItem is String && newItem is String -> {
                oldItem == newItem
            }

            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        return Objects.equals(oldItem, newItem)
    }

}
