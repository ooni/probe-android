package org.openobservatory.ooniprobe.activity.runtests.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import org.openobservatory.ooniprobe.R
import org.openobservatory.ooniprobe.activity.runtests.RunTestsViewModel
import org.openobservatory.ooniprobe.activity.runtests.models.ChildItem
import org.openobservatory.ooniprobe.activity.runtests.models.GroupItem
import org.openobservatory.ooniprobe.test.test.AbstractTest


class RunTestsExpandableListViewAdapter(
        private val groupedListData: List<GroupItem>,
        private val viewModel: RunTestsViewModel
) : AbstractRunTestsExpandableListViewAdapter(groupedListData, viewModel) {

    override fun getChildrenCount(groupPosition: Int): Int = when (groupedListData[groupPosition].nettests.isNotEmpty()) {
        true -> groupedListData[groupPosition].nettests[0].inputs?.size ?: 0
        false -> 0
    }

    override fun getChild(groupPosition: Int, childPosition: Int): ChildItem = ChildItem(
            name = groupedListData[groupPosition].nettests[0].inputs?.get(childPosition)
                    ?: "", selected = false,
            inputs = null)

    override fun getChildView(
            groupPosition: Int,
            childPosition: Int,
            isLastChild: Boolean,
            convertView: View?,
            parent: ViewGroup
    ): View? {
        var convertView =
                convertView
                        ?: LayoutInflater.from(parent.context).inflate(R.layout.run_tests_child_list_item, parent, false)
        val childItem = getChild(groupPosition, childPosition)
        convertView.findViewById<TextView>(R.id.child_name)?.apply {
            text = childItem.name
        }
        convertView.findViewById<ImageView>(R.id.child_select).apply {
            visibility = View.GONE
        }
        return convertView
    }
}