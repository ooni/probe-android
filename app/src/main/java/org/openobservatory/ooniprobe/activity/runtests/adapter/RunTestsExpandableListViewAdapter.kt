package org.openobservatory.ooniprobe.activity.runtests.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import org.openobservatory.ooniprobe.R
import org.openobservatory.ooniprobe.activity.runtests.RunTestsViewModel
import org.openobservatory.ooniprobe.activity.runtests.RunTestsViewModel.Companion.NOT_SELECT_ANY
import org.openobservatory.ooniprobe.activity.runtests.RunTestsViewModel.Companion.SELECT_ALL
import org.openobservatory.ooniprobe.activity.runtests.RunTestsViewModel.Companion.SELECT_SOME
import org.openobservatory.ooniprobe.activity.runtests.models.ChildItem
import org.openobservatory.ooniprobe.activity.runtests.models.GroupItem
import org.openobservatory.ooniprobe.test.suite.AbstractSuite
import org.openobservatory.ooniprobe.test.suite.ExperimentalSuite
import org.openobservatory.ooniprobe.test.test.AbstractTest


class RunTestsExpandableListViewAdapter(
	private val mContext: Context,
	private val mGroupListData: List<GroupItem>,
	private val mViewModel: RunTestsViewModel
) : BaseExpandableListAdapter() {
	override fun getGroupCount(): Int {
		return mGroupListData.size
	}

	override fun getChildrenCount(groupPosition: Int): Int {
		return mGroupListData[groupPosition].nettests.size
	}

	override fun getGroup(groupPosition: Int): GroupItem {
		return mGroupListData[groupPosition]
	}

	override fun getChild(groupPosition: Int, childPosition: Int): ChildItem {
		return mGroupListData[groupPosition].nettests[childPosition]
	}

	override fun getGroupId(groupPosition: Int): Long {
		return groupPosition.toLong()
	}

	override fun getChildId(groupPosition: Int, childPosition: Int): Long {
		return childPosition.toLong()
	}

	override fun hasStableIds(): Boolean {
		return false
	}

	override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup): View? {
		var convertView =
			convertView ?: LayoutInflater.from(mContext).inflate(R.layout.run_tests_group_list_item, parent, false)
		val groupItem = getGroup(groupPosition)
		val testSuite = AbstractSuite.getTestSuiteByName(groupItem.name)
		convertView.findViewById<TextView>(R.id.group_name).text = parent.context.resources.getText(testSuite.title)
		convertView.findViewById<ImageView>(R.id.group_icon).setImageResource(testSuite.icon)
		val groupIndicator = convertView.findViewById<ImageView>(R.id.group_indicator)
		val groupSelectionIndicator = convertView.findViewById<ImageView>(R.id.group_select_indicator)
		val selectedAllBtnStatus = mViewModel.selectedAllBtnStatus.getValue()
		if (selectedAllBtnStatus == SELECT_ALL) {
			groupItem.selected = true
			for (childItem in groupItem.nettests) {
				childItem.selected = true
			}
		} else if (selectedAllBtnStatus == NOT_SELECT_ANY) {
			groupItem.selected = false
			for (childItem in groupItem.nettests) {
				childItem.selected = false
			}
		}
		if (groupItem.selected) {
			if (isSelectAllChildItems(groupItem.nettests)) {
				groupSelectionIndicator.setImageResource(R.drawable.check_box)
			} else {
				groupSelectionIndicator.setImageResource(R.drawable.check_box_outline_blank)
			}
		} else {
			groupSelectionIndicator.setImageResource(R.drawable.check_box_outline_blank)
		}
		groupSelectionIndicator.setOnClickListener {
			if (groupItem.selected && isSelectAllChildItems(groupItem.nettests)) {
				groupItem.selected = false
				for (childItem in groupItem.nettests) {
					childItem.selected = false
				}
				if (isNotSelectedAnyGroupItem(mGroupListData)) {
					mViewModel.setSelectedAllBtnStatus(NOT_SELECT_ANY)
				} else {
					mViewModel.setSelectedAllBtnStatus(SELECT_SOME)
				}
			} else {
				groupItem.selected = true
				for (childItem in groupItem.nettests) {
					childItem.selected = true
				}
				if (isSelectedAllItems(mGroupListData)) {
					mViewModel.setSelectedAllBtnStatus(SELECT_ALL)
				} else {
					mViewModel.setSelectedAllBtnStatus(SELECT_SOME)
				}
			}
			notifyDataSetChanged()
		}
		if (isExpanded) {
			groupIndicator.setImageResource(R.drawable.expand_less)
		} else {
			groupIndicator.setImageResource(R.drawable.expand_more)
		}
		return convertView
	}

	override fun getChildView(
		groupPosition: Int,
		childPosition: Int,
		isLastChild: Boolean,
		convertView: View?,
		parent: ViewGroup
	): View? {
		var convertView =
			convertView ?: LayoutInflater.from(mContext).inflate(R.layout.run_tests_child_list_item, parent, false)
		val childItem = getChild(groupPosition, childPosition)
		val groupItem = getGroup(groupPosition)
		val nettest = AbstractTest.getTestByName(childItem.name)
		convertView.findViewById<TextView>(R.id.child_name)?.apply {
			text = when (groupItem.name) {
				ExperimentalSuite.NAME -> {
					childItem.name
				}

				else -> {
					parent.context.resources.getText(nettest.labelResId)
				}
			}
		}
		convertView.findViewById<CheckBox>(R.id.child_select).apply {
			isChecked = childItem.selected
			setOnCheckedChangeListener { buttonView, isChecked ->

				childItem.selected = isChecked
				if (childItem.selected) {
					if (isNotSelectedAnyChildItems(groupItem.nettests)) {
						groupItem.selected = false
					}
					if (isNotSelectedAnyItems(mGroupListData)) {
						mViewModel.setSelectedAllBtnStatus(NOT_SELECT_ANY)
					} else {
						mViewModel.setSelectedAllBtnStatus(SELECT_SOME)
					}
				} else {
					groupItem.selected = true
					if (isSelectedAllItems(mGroupListData)) {
						mViewModel.setSelectedAllBtnStatus(SELECT_ALL)
					} else {
						mViewModel.setSelectedAllBtnStatus(SELECT_SOME)
					}
				}
				notifyDataSetChanged()
			}
		}
		return convertView
	}

	override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
		return false
	}

	private fun isNotSelectedAnyGroupItem(groupItemsList: List<GroupItem>): Boolean {
		for (groupItem in groupItemsList) {
			if (groupItem.selected) {
				return false
			}
		}
		return true
	}

	private fun isSelectedAllGroupItems(groupItemsList: List<GroupItem>): Boolean {
		for (groupItem in groupItemsList) {
			if (!groupItem.selected) {
				return false
			}
		}
		return true
	}

	private fun isNotSelectedAnyChildItems(childItemList: List<ChildItem>): Boolean {
		for (childItem in childItemList) {
			if (childItem.selected) {
				return false
			}
		}
		return true
	}

	private fun isSelectAllChildItems(childItemList: List<ChildItem>): Boolean {
		for (childItem in childItemList) {
			if (!childItem.selected) {
				return false
			}
		}
		return true
	}

	private fun isSelectedAllItems(groupItemList: List<GroupItem>?): Boolean {
		for (groupItem in groupItemList!!) {
			if (!groupItem.selected) {
				return false
			}
			if (!isSelectAllChildItems(groupItem.nettests)) {
				return false
			}
		}
		return true
	}

	private fun isNotSelectedAnyItems(groupItemList: List<GroupItem>?): Boolean {
		for (groupItem in groupItemList!!) {
			if (groupItem.selected) {
				return false
			}
			if (!isNotSelectedAnyChildItems(groupItem.nettests)) {
				return false
			}
		}
		return true
	}
}
