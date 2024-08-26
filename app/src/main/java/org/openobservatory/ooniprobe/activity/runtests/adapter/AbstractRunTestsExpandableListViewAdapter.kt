package org.openobservatory.ooniprobe.activity.runtests.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import org.openobservatory.ooniprobe.R
import org.openobservatory.ooniprobe.activity.runtests.RunTestsViewModel
import org.openobservatory.ooniprobe.activity.runtests.RunTestsViewModel.Companion.SELECT_ALL
import org.openobservatory.ooniprobe.activity.runtests.RunTestsViewModel.Companion.SELECT_NONE
import org.openobservatory.ooniprobe.activity.runtests.RunTestsViewModel.Companion.SELECT_SOME
import org.openobservatory.ooniprobe.activity.runtests.models.ChildItem
import org.openobservatory.ooniprobe.activity.runtests.models.GroupItem
import org.openobservatory.ooniprobe.test.test.AbstractTest


/**
 * Adapter class for the ExpandableListView in RunTestsActivity.
 * @param groupedListData List of GroupItem objects.
 * @param viewModel RunTestsViewModel object.
 */
open class AbstractRunTestsExpandableListViewAdapter(
	private val groupedListData: List<Any>,
	private val viewModel: RunTestsViewModel
) : BaseExpandableListAdapter() {
	/**
	 * @return Number of groups in the list.
	 */
	override fun getGroupCount(): Int {
		return groupedListData.size
	}

	/**
	 * @param groupPosition Position of the group in the list.
	 * @return Number of children in the group.
	 */
	override fun getChildrenCount(groupPosition: Int): Int = when (val group = groupedListData[groupPosition]) {
		is GroupItem -> group.nettests.size
		else -> 0
	}

	/**
	 * @param groupPosition Position of the group in the list.
	 * @return GroupItem object.
	 */
	override fun getGroup(groupPosition: Int): Any = groupedListData[groupPosition]

	/**
	 * @param groupPosition Position of the group in the list.
	 * @param childPosition Position of the child in the group.
	 * @return ChildItem object.
	 */
	override fun getChild(groupPosition: Int, childPosition: Int): ChildItem = when (val group = groupedListData[groupPosition]) {
		is GroupItem -> group.nettests[childPosition]
		else -> throw IllegalArgumentException("GroupItem expected")
	}

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
	 * @return True if the IDs are stable.
	 */
	override fun hasStableIds(): Boolean = false

	/**
	 * @param groupPosition Position of the group in the list.
	 * @param isExpanded True if the group is expanded.
	 * @param convertView View object.
	 * @param parent ViewGroup object.
	 * @return View object.
	 */
	override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup): View? {

		val groupItem = getGroup(groupPosition)

		when(groupItem){
			is GroupItem -> {
				var convertView = LayoutInflater.from(parent.context).inflate(R.layout.run_tests_group_list_item, parent, false)
				convertView.findViewById<TextView>(R.id.group_name).text = groupItem.title
				val icon = convertView.findViewById<ImageView>(R.id.group_icon)
				icon.setImageResource(groupItem.getDisplayIcon(parent.context))
				icon.setColorFilter(groupItem.color)
				val groupSelectionIndicator = convertView.findViewById<ImageView>(R.id.group_select_indicator)
				val selectedAllBtnStatus = viewModel.selectedAllBtnStatus.getValue()
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
						if (isNotSelectedAnyGroupItem(groupedListData)) {
							viewModel.setSelectedAllBtnStatus(SELECT_NONE)
						} else {
							viewModel.setSelectedAllBtnStatus(SELECT_SOME)
						}
					} else {
						groupItem.selected = true
						for (childItem in groupItem.nettests) {
							childItem.selected = true
						}
						if (isSelectedAllItems(groupedListData)) {
							viewModel.setSelectedAllBtnStatus(SELECT_ALL)
						} else {
							viewModel.setSelectedAllBtnStatus(SELECT_SOME)
						}
					}
					notifyDataSetChanged()
				}
				convertView.findViewById<ImageView>(R.id.group_indicator)?.let { groupIndicator ->
					if (isExpanded) {
						groupIndicator.setImageResource(R.drawable.expand_less)
					} else {
						groupIndicator.setImageResource(R.drawable.expand_more)
					}
				}
				return convertView
			}
			else -> {
				return LayoutInflater.from(parent.context).inflate(R.layout.run_tests_group_divider, parent, false).apply {
					findViewById<TextView>(R.id.name).text = groupItem.toString()
				}
			}
		}
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
	): View? {
		var convertView =
			convertView ?: LayoutInflater.from(parent.context).inflate(R.layout.run_tests_child_list_item, parent, false)
		val childItem = getChild(groupPosition, childPosition)
		val groupItem = getGroup(groupPosition)
		val nettest = AbstractTest.getTestByName(childItem.name)
		convertView.findViewById<TextView>(R.id.child_name)?.apply {
			text = when (nettest.labelResId) {
				R.string.Test_Experimental_Fullname -> {
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
			when (groupItem) {
				is GroupItem -> {
					setOnClickListener {
						if (childItem.selected) {
							childItem.selected = false
							if (isNotSelectedAnyChildItems(groupItem.nettests)) {
								groupItem.selected = false
							}
							if (isNotSelectedAnyItems(groupedListData)) {
								viewModel.setSelectedAllBtnStatus(SELECT_NONE)
							} else {
								viewModel.setSelectedAllBtnStatus(SELECT_SOME)
							}
						} else {
							childItem.selected = true
							groupItem.selected = true
							if (isSelectedAllItems(groupedListData)) {
								viewModel.setSelectedAllBtnStatus(SELECT_ALL)
							} else {
								viewModel.setSelectedAllBtnStatus(SELECT_SOME)
							}
						}
						notifyDataSetChanged()
					}
				}
			}
		}
		return convertView
	}

	/**
	 * @param groupPosition Position of the group in the list.
	 * @param childPosition Position of the child in the group.
	 * @return True if the child is selectable.
	 */
	override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = false

	/**
	 * @param groupItemsList List of GroupItem objects.
	 * @return True if no group item in the list is selected.
	 */
	private fun isNotSelectedAnyGroupItem(groupItemsList: List<Any>): Boolean {
		for (groupItem in groupItemsList.filterIsInstance<GroupItem>()) {
			if (groupItem.selected) {
				return false
			}
		}
		return true
	}

	/**
	 * @param childItemList List of ChildItem objects.
	 * @return True if no child item in the list is selected.
	 */
	private fun isNotSelectedAnyChildItems(childItemList: List<ChildItem>): Boolean {
		for (childItem in childItemList) {
			if (childItem.selected) {
				return false
			}
		}
		return true
	}

	/**
	 * @param childItemList List of ChildItem objects.
	 * @return True if all child items in the list are selected.
	 */
	private fun isSelectAllChildItems(childItemList: List<ChildItem>): Boolean {
		for (childItem in childItemList) {
			if (!childItem.selected) {
				return false
			}
		}
		return true
	}

	/**
	 * @param groupItemList List of GroupItem objects.
	 * @return True if all group items in the list are selected.
	 */
	private fun isSelectedAllItems(groupItemList: List<Any>): Boolean {
		for (groupItem in groupItemList.filterIsInstance<GroupItem>()) {
			if (!groupItem.selected) {
				return false
			}
			if (!isSelectAllChildItems(groupItem.nettests)) {
				return false
			}
		}
		return true
	}

	/**
	 * @param groupItemList List of GroupItem objects.
	 * @return True if no group item in the list is selected.
	 */
	private fun isNotSelectedAnyItems(groupItemList: List<Any>): Boolean {
		for (groupItem in groupItemList.filterIsInstance<GroupItem>()) {
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
