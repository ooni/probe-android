package org.openobservatory.ooniprobe.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import org.openobservatory.ooniprobe.R
import org.openobservatory.ooniprobe.model.database.Measurement
import org.openobservatory.ooniprobe.test.test.*

data class MeasurementGroup(val title: String, val measurements: List<Measurement>)


class ResultDetailExpandableListAdapter(
    private val items: List<Any>,
    private val onClickListener: View.OnClickListener
) : BaseExpandableListAdapter() {

    override fun getGroupCount() = items.size

    override fun getChildrenCount(listPosition: Int): Int = items[listPosition].let {
        when (it) {
            is MeasurementGroup -> it.measurements.size

            else -> 0
        }
    }

    override fun getGroup(listPosition: Int): Any {
        return when {
            items[listPosition] is MeasurementGroup -> (items[listPosition] as MeasurementGroup).title
            else -> items[listPosition]
        }
    }

    override fun getChild(listPosition: Int, expandedListPosition: Int): Measurement? = when {
        items[listPosition] is MeasurementGroup -> (items[listPosition] as MeasurementGroup).measurements[expandedListPosition]
        else -> null
    }

    override fun getGroupId(listPosition: Int): Long = listPosition.toLong()

    override fun getChildId(listPosition: Int, expandedListPosition: Int): Long = expandedListPosition.toLong()

    override fun hasStableIds(): Boolean = false

    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean = true

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val measurement = getChild(groupPosition, childPosition)

        val root = convertView ?: LayoutInflater.from(parent.context)
            .inflate(R.layout.item_measurement, parent, false)

        measurement?.let {
            bindMeasurement(it, root)
            root.apply {
                setPaddingRelative(96,0,0,0)
                setBackgroundColor(parent.context.resources.getColor(R.color.color_gray0))
            }
        }?: run {
            root.visibility = View.GONE
        }

        return root
    }


    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val groupItem = getGroup(groupPosition)

        val root = convertView ?: LayoutInflater.from(parent.context)
            .inflate(R.layout.item_measurement, parent, false)

        when (groupItem) {
            is Measurement -> bindMeasurement(groupItem, root)

            else -> root.findViewById<TextView>(R.id.text).apply {
                text = groupItem.toString()
                setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0,
                    0,
                    if (isExpanded) R.drawable.advanced else R.drawable.chevron_right,
                    0
                )
            }
        }
        return root
    }

    private fun bindMeasurement(
        measurement: Measurement,
        view: View
    ) {
        view.tag = measurement
        view.setOnClickListener(onClickListener)
        view.findViewById<TextView>(R.id.text).also { textView ->

            val test: AbstractTest = measurement.getTest()

            val endDrawable: Int = when {
                measurement.is_failed -> R.drawable.error_24dp
                measurement.is_anomaly && measurement.isUploaded -> R.drawable.exclamation_24dp
                measurement.is_anomaly -> R.drawable.exclamation_cloudoff
                measurement.isUploaded -> R.drawable.tick_green_24dp
                else -> R.drawable.tick_green_cloudoff
            }

            if (measurement.test_name == WebConnectivity.NAME) {
                if (measurement.url != null) {
                    textView.text = measurement.url.url
                    textView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        measurement.url.getCategoryIcon(textView.context),
                        0,
                        endDrawable,
                        0
                    )
                }
            } else {
                when (measurement.getTest().labelResId) {
                    R.string.Test_Experimental_Fullname -> textView.text = measurement.getTest().name
                    else -> textView.setText(test.labelResId)
                }
                textView.setCompoundDrawablesRelativeWithIntrinsicBounds(test.iconResId, 0, endDrawable, 0)
            }
        }
        if (arrayListOf(
                Dash.NAME,
                Ndt.NAME,
                HttpHeaderFieldManipulation.NAME,
                HttpInvalidRequestLine.NAME
            ).contains(measurement.test_name)
        ) {
            val c: Context = view.context
            view.findViewById<View>(R.id.pref_group).visibility = View.VISIBLE
            val data1: TextView = view.findViewById(R.id.data1)
            val data2: TextView = view.findViewById(R.id.data2)
            view.findViewById<TextView>(R.id.text).setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                if (measurement.is_failed || measurement.isUploaded) 0 else R.drawable.cloudoff,
                0
            )
            when (measurement.test_name) {
                Dash.NAME -> {
                    data1.apply {
                        setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.video_quality, 0, 0, 0)
                        setText(measurement.getTestKeys().getVideoQuality(true))
                    }
                    data2.visibility = View.GONE
                }

                Ndt.NAME -> {
                    data1.apply {
                        setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.download_black, 0, 0, 0)
                        text = c.getString(
                            R.string.twoParam,
                            measurement.getTestKeys().getDownload(c),
                            c.getString(measurement.getTestKeys().getDownloadUnit())
                        )
                    }
                    data2.apply {
                        visibility = View.VISIBLE
                        setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.upload_black, 0, 0, 0)
                        text = c.getString(
                            R.string.twoParam,
                            measurement.getTestKeys().getUpload(c),
                            c.getString(measurement.getTestKeys().getUploadUnit())
                        )
                    }
                }

                HttpHeaderFieldManipulation.NAME, HttpInvalidRequestLine.NAME -> {
                    data1.apply {
                        setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.test_middle_boxes_small, 0, 0, 0)
                        (if (measurement.is_anomaly) c.getString(R.string.TestResults_Overview_MiddleBoxes_Found) else c.getString(
                            R.string.TestResults_Overview_MiddleBoxes_NotFound
                        )).also { text = it }
                    }
                    data2.visibility = View.GONE
                }
            }
        }
    }

}
