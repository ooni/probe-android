package org.openobservatory.ooniprobe.activity.runtests.adapter

import org.openobservatory.ooniprobe.activity.runtests.RunTestsViewModel
import org.openobservatory.ooniprobe.activity.runtests.models.GroupItem


/**
 * Adapter class for the ExpandableListView in RunTestsActivity.
 * @param groupedListData List of GroupItem objects.
 * @param viewModel RunTestsViewModel object.
 */
class RunTestsExpandableListViewAdapter(
        private val groupedListData: List<GroupItem>,
        private val viewModel: RunTestsViewModel
) : AbstractRunTestsExpandableListViewAdapter(groupedListData, viewModel)