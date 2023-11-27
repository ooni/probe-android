package org.openobservatory.ooniprobe.activity.overview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import org.openobservatory.ooniprobe.R

class OverviewTestsExpandableListViewAdapter(
    private val items: List<TestGroupItem>,
) : BaseExpandableListAdapter() {

    private val selectedGroups = HashSet<Int>()

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
        val groupTextView: TextView = view.findViewById(R.id.group_name)
        val groupIndicator = view.findViewById<ImageView>(R.id.group_indicator)

        groupTextView.text = groupItem.name

        val groupCheckBox: SwitchCompat = view.findViewById(R.id.groupCheckBox)
        groupCheckBox.isChecked = selectedGroups.contains(groupPosition)

        groupCheckBox.setOnClickListener {
            if (groupCheckBox.isChecked) {
                selectedGroups.add(groupPosition)
            } else {
                selectedGroups.remove(groupPosition)
            }
        }
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
            setBackgroundColor(parent.context.resources.getColor(R.color.color_gray1))
        }
        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    fun getSelectedGroups(): Set<Int> {
        return selectedGroups
    }
}