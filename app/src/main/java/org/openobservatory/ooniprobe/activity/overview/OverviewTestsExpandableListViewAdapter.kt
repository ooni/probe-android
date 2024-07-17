package org.openobservatory.ooniprobe.activity.overview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.checkbox.MaterialCheckBox
import org.openobservatory.ooniprobe.BuildConfig
import org.openobservatory.ooniprobe.R
import org.openobservatory.ooniprobe.common.OONITests
import org.openobservatory.ooniprobe.test.test.AbstractTest
import org.openobservatory.ooniprobe.test.test.Experimental
import org.openobservatory.ooniprobe.test.test.WebConnectivity

class OverviewTestsExpandableListViewAdapter(
    private val items: List<TestGroupItem>,
    private val viewModel: OverviewViewModel,
) : BaseExpandableListAdapter() {

    override fun getGroupCount(): Int = items.size

    override fun getChildrenCount(groupPosition: Int): Int = items[groupPosition].inputs?.size ?: 0

    override fun getGroup(groupPosition: Int): TestGroupItem = items[groupPosition]

    override fun getChild(groupPosition: Int, childPosition: Int): String =
        items[groupPosition].inputs?.get(childPosition) ?: ""

    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    override fun hasStableIds(): Boolean = false

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup,
    ): View {
        val view = convertView ?: LayoutInflater.from(parent.context)
            .inflate(R.layout.overview_test_group_list_item, parent, false)
        val groupItem = getGroup(groupPosition)
        val groupIndicator = view.findViewById<ImageView>(R.id.group_indicator)

        when (viewModel.descriptor.value?.name) {
            OONITests.EXPERIMENTAL.label -> {
                view.findViewById<TextView>(R.id.group_name).text = groupItem.name
                view.findViewById<ImageView>(R.id.group_icon).visibility = View.GONE
            }

            else -> {
                val testSuite = AbstractTest.getTestByName(groupItem.name)
                view.findViewById<TextView>(R.id.group_name).text = when (testSuite) {
                    is Experimental -> testSuite.name
                    is WebConnectivity -> when (BuildConfig.FLAVOR_brand == "dw") {
                        true -> "Test websites automatically"
                        else -> parent.context.resources.getText(testSuite.labelResId)
                    }
                    else -> parent.context.resources.getText(testSuite.labelResId)
                }
                when(testSuite.iconResId){
                    0 -> view.findViewById<ImageView>(R.id.group_icon).visibility = View.GONE
                    else -> {
                        view.findViewById<ImageView>(R.id.group_icon).apply {
                            visibility = View.VISIBLE
                            setImageResource(testSuite.iconResId)
                        }
                    }
                }
            }
        }

        val groupCheckBox = view.findViewById<ImageView>(R.id.groupCheckBox)

        val selectedAllBtnStatus = viewModel.selectedAllBtnStatus.value
        if (selectedAllBtnStatus == OverviewViewModel.SELECT_ALL) {
            groupItem.selected = true
        } else if (selectedAllBtnStatus == OverviewViewModel.SELECT_NONE) {
            groupItem.selected = false
        }

        groupCheckBox.setOnClickListener {
            if (groupItem.selected) {
                groupItem.selected = false
                viewModel.disableTest(groupItem.name)
                notifyDataSetChanged()
                if (isNotSelectedAnyGroupItem()) {
                    viewModel.setSelectedAllBtnStatus(OverviewViewModel.SELECT_NONE)
                } else {
                    viewModel.setSelectedAllBtnStatus(OverviewViewModel.SELECT_SOME)
                }
            } else {
                groupItem.selected = true
                viewModel.enableTest(groupItem.name)
                notifyDataSetChanged()

                if (isSelectedAllItems()) {
                    viewModel.setSelectedAllBtnStatus(OverviewViewModel.SELECT_ALL)
                } else {
                    viewModel.setSelectedAllBtnStatus(OverviewViewModel.SELECT_SOME)
                }
            }
        }

        groupCheckBox.setImageResource(
            when (groupItem.selected) {
                true -> R.drawable.check_box
                false -> R.drawable.check_box_outline_blank
            }
        )

        /**
         * Hide checkbox for experimental tests.
         * Experimental tests are not configurable in the settings.
         * Tests in this category are not permanent and are subject to change.
         * The checkbox is hidden to prevent the user from mistakenly thinking they can be configured.
         */
        viewModel.descriptor.value?.run {
            if (name == OONITests.EXPERIMENTAL.label) {
                groupCheckBox.visibility = View.GONE
            } else {
                groupCheckBox.visibility = View.VISIBLE
            }
        }

        if (items.count() > 1 && groupItem.inputs?.isNotEmpty() == true) {
            if (isExpanded) {
                groupIndicator.setImageResource(R.drawable.expand_less)
            } else {
                groupIndicator.setImageResource(R.drawable.expand_more)
            }
        } else {
            groupIndicator.visibility = View.INVISIBLE
        }

        return view
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val view = convertView ?: LayoutInflater.from(parent.context)
            .inflate(R.layout.overview_test_child_list_item, parent, false)

        view.findViewById<TextView>(R.id.text).apply {
            text = getChild(groupPosition, childPosition)
        }
        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    fun isNotSelectedAnyGroupItem(): Boolean {
        for (groupItem in items) {
            if (groupItem.selected) {
                return false
            }
        }
        return true
    }

    fun isSelectedAllItems(): Boolean {
        for (groupItem in items) {
            if (!groupItem.selected) {
                return false
            }
        }
        return true
    }
}