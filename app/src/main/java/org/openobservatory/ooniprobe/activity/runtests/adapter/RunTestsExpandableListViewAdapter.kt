package org.openobservatory.ooniprobe.activity.runtests.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import org.openobservatory.ooniprobe.R
import org.openobservatory.ooniprobe.activity.runtests.RunTestsViewModel
import org.openobservatory.ooniprobe.activity.runtests.RunTestsViewModel.Companion.SELECT_NONE
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

	override fun getChildrenCount(groupPosition: Int): Int = mGroupListData[groupPosition].nettests.size

	override fun getGroup(groupPosition: Int): GroupItem = mGroupListData[groupPosition]

	override fun getChild(groupPosition: Int, childPosition: Int): ChildItem =
		mGroupListData[groupPosition].nettests[childPosition]

	override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

	override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

	override fun hasStableIds(): Boolean = false

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
		} else if (selectedAllBtnStatus == SELECT_NONE) {
			groupItem.selected = false
			for (childItem in groupItem.nettests) {
				childItem.selected = false
			}
		} else if (isSelectAllChildItems(groupItem.nettests)) {
			groupItem.selected = true
		}
		if (groupItem.selected) {
			if (isSelectAllChildItems(groupItem.nettests)) {
				groupSelectionIndicator.setImageResource(R.drawable.check_box)
				// NOTE: This is the only place where ExperimentalSuite.NAME is used.
				// This doesn't follow the normal rule where the component tests make up the suite.
				if (groupItem.name == ExperimentalSuite.NAME) {
					mViewModel.enableTest(ExperimentalSuite.NAME)
				}
			} else {
				groupSelectionIndicator.setImageResource(R.drawable.check_box_outline_blank)
				if (groupItem.name == ExperimentalSuite.NAME) {
					mViewModel.disableTest(ExperimentalSuite.NAME)
				}
			}
		} else {
			groupSelectionIndicator.setImageResource(R.drawable.check_box_outline_blank)
			if (groupItem.name == ExperimentalSuite.NAME) {
				mViewModel.disableTest(ExperimentalSuite.NAME)
			}
		}
		groupSelectionIndicator.setOnClickListener {
			if (groupItem.selected && isSelectAllChildItems(groupItem.nettests)) {
				groupItem.selected = false
				for (childItem in groupItem.nettests) {
					childItem.selected = false
				}
				if (isNotSelectedAnyGroupItem(mGroupListData)) {
					mViewModel.setSelectedAllBtnStatus(SELECT_NONE)
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
		convertView.findViewById<ImageView>(R.id.child_select).apply {
			setImageResource(
				when (childItem.selected) {
					true -> R.drawable.check_box
					false -> R.drawable.check_box_outline_blank
				}
			)
			setOnClickListener {
				if (childItem.selected) {
					childItem.selected = false
					mViewModel.disableTest(childItem.name)
					if (isNotSelectedAnyChildItems(groupItem.nettests)) {
						groupItem.selected = false
					}
					if (isNotSelectedAnyItems(mGroupListData)) {
						mViewModel.setSelectedAllBtnStatus(SELECT_NONE)
					} else {
						mViewModel.setSelectedAllBtnStatus(SELECT_SOME)
					}
				} else {
					childItem.selected = true
					mViewModel.enableTest(childItem.name)
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

	override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = false

	private fun isNotSelectedAnyGroupItem(groupItemsList: List<GroupItem>): Boolean {
		for (groupItem in groupItemsList) {
			if (groupItem.selected) {
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
