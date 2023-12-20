package org.openobservatory.ooniprobe.activity.add_descriptor.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.checkbox.MaterialCheckBox.STATE_CHECKED
import com.google.android.material.checkbox.MaterialCheckBox.STATE_INDETERMINATE
import com.google.android.material.checkbox.MaterialCheckBox.STATE_UNCHECKED
import org.openobservatory.engine.OONIRunNettest
import org.openobservatory.ooniprobe.R
import org.openobservatory.ooniprobe.activity.add_descriptor.AddDescriptorViewModel
import org.openobservatory.ooniprobe.test.test.AbstractTest


/**
 * An extension of [OONIRunNettest] class
 * used to track the selected state of nettests in the [ExpandableListView].
 */
class GroupedItem(
    override var name: String,
    override var inputs: List<String>?,
    var selected: Boolean = false
) : OONIRunNettest(name = name, inputs = inputs)

/**
 * Adapter class for the [ExpandableListView] in [AddDescriptorActivity].
 * @param nettests List of GroupedItem objects.
 * @param viewModel AddDescriptorViewModel object.
 */
class AddDescriptorExpandableListAdapter(
    val nettests: List<GroupedItem>,
    val viewModel: AddDescriptorViewModel
) : BaseExpandableListAdapter() {

    /**
     * @return Number of groups in the list.
     */
    override fun getGroupCount(): Int = nettests.size

    /**
     * @param groupPosition Position of the group in the list.
     * @return Number of children in the group.
     */
    override fun getChildrenCount(groupPosition: Int): Int =
        nettests[groupPosition].inputs?.size ?: 0

    /**
     * @param groupPosition Position of the group in the list.
     * @return [GroupedItem] object.
     */
    override fun getGroup(groupPosition: Int): GroupedItem = nettests[groupPosition]

    /**
     * @param groupPosition Position of the group in the list.
     * @param childPosition Position of the child in the group.
     * @return string item at position.
     */
    override fun getChild(groupPosition: Int, childPosition: Int): String? =
        nettests[groupPosition].inputs?.get(childPosition)

    /**
     * @param groupPosition Position of the group in the list.
     * @return Group position.
     */
    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    /**
     * @param groupPosition Position of the group in the list.
     * @param childPosition Position of the child in the group.
     * @return Child position.
     */
    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    /**
     * @return true if the same ID always refers to the same object.
     */
    override fun hasStableIds(): Boolean = false

    /**
     * @param groupPosition Position of the group in the list.
     * @param isExpanded true if the group is expanded.
     * @param convertView View of the group.
     * @param parent Parent view.
     * @return View of the group.
     */
    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup,
    ): View {
        val view = convertView ?: LayoutInflater.from(parent.context)
            .inflate(R.layout.nettest_group_list_item, parent, false)
        val groupItem = getGroup(groupPosition)
        val groupIndicator = view.findViewById<ImageView>(R.id.group_indicator)

        val abstractNettest = AbstractTest.getTestByName(groupItem.name)
        view.findViewById<TextView>(R.id.group_name).text =
            when (abstractNettest.labelResId == R.string.Test_Experimental_Fullname) {
                true -> groupItem.name
                false -> parent.context.resources.getText(abstractNettest.labelResId)
            }

        val groupCheckBox = view.findViewById<MaterialCheckBox>(R.id.groupCheckBox)
        val selectedAllBtnStatus = viewModel.selectedAllBtnStatus.value
        if (selectedAllBtnStatus == STATE_CHECKED) {
            groupItem.selected = true
        } else if (selectedAllBtnStatus == STATE_UNCHECKED) {
            groupItem.selected = false
        }

        groupCheckBox.setOnClickListener {
            if (groupItem.selected) {
                groupItem.selected = false
                //viewModel.disableTest(groupItem.name)
                notifyDataSetChanged()
                if (isNotSelectedAnyGroupItem()) {
                    viewModel.setSelectedAllBtnStatus(STATE_UNCHECKED)
                } else {
                    viewModel.setSelectedAllBtnStatus(STATE_INDETERMINATE)
                }
            } else {
                groupItem.selected = true
                //viewModel.enableTest(groupItem.name)
                notifyDataSetChanged()

                if (isSelectedAllItems()) {
                    viewModel.setSelectedAllBtnStatus(STATE_CHECKED)
                } else {
                    viewModel.setSelectedAllBtnStatus(STATE_INDETERMINATE)
                }
            }
        }

        groupCheckBox.isChecked = groupItem.selected

        if (groupItem.inputs?.isNotEmpty() == true) {
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

    /**
     * @param groupPosition Position of the group in the list.
     * @param childPosition Position of the child in the group.
     * @param isLastChild True if the child is the last child in the group.
     * @param convertView View object.
     * @param parent ViewGroup object.
     * @return View object.
     */
    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val view = convertView ?: LayoutInflater.from(parent.context)
            .inflate(R.layout.nettest_child_list_item, parent, false)

        view.findViewById<TextView>(R.id.text).apply {
            text = getChild(groupPosition, childPosition)
        }
        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = false

    fun isNotSelectedAnyGroupItem(): Boolean {
        for (groupItem in nettests) {
            if (groupItem.selected) {
                return false
            }
        }
        return true
    }

    fun isSelectedAllItems(): Boolean {
        for (groupItem in nettests) {
            if (!groupItem.selected) {
                return false
            }
        }
        return true
    }
}