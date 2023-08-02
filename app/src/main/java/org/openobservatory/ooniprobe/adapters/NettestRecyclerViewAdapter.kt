package org.openobservatory.ooniprobe.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.TextView
import org.openobservatory.engine.OONIRunNettest
import org.openobservatory.ooniprobe.R

class NettestRecyclerViewAdapter(var groups: List<OONIRunNettest>) : BaseExpandableListAdapter() {

    override fun getGroupCount() = groups.size

    override fun getChildrenCount(groupPosition: Int) = groups[groupPosition].inputs.size

    override fun getGroup(groupPosition: Int) = groups[groupPosition]

    override fun getChild(groupPosition: Int, childPosition: Int) =
        groups[groupPosition].inputs[childPosition]

    override fun getGroupId(groupPosition: Int) = groupPosition.toLong()

    override fun getChildId(groupPosition: Int, childPosition: Int) = childPosition.toLong()

    override fun hasStableIds() = true

    override fun getGroupView(
        groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup
    ): View {
        val root = convertView ?: LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_expandable_list_item_1, parent, false)
        root.findViewById<TextView>(android.R.id.text1)?.apply {
            text = groups[groupPosition].name
            setPaddingRelative(32,0,0,0)
        }

        return root
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val root = convertView ?: LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_expandable_list_item_1, parent, false)
        root.findViewById<TextView>(android.R.id.text1)?.apply {
            text = groups[groupPosition].inputs[childPosition]
            setBackgroundColor(parent.context.resources.getColor(R.color.color_gray1))
            setPaddingRelative(96,0,0,0)
        }
        return root
    }

    /**
     * If false, [ExpandableListView.setOnChildClickListener] will not be invoked.
     */
    override fun isChildSelectable(groupPosition: Int, childPosition: Int) = true
}