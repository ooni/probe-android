package org.openobservatory.ooniprobe.fragment.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.openobservatory.engine.BaseNettest
import org.openobservatory.ooniprobe.common.OONIDescriptor
import org.openobservatory.ooniprobe.common.PreferenceManager
import org.openobservatory.ooniprobe.common.TestDescriptorManager
import javax.inject.Inject

class DashboardViewModel @Inject constructor(
    private val preferenceManager: PreferenceManager,
    private val descriptorManager: TestDescriptorManager
) : ViewModel() {
    private val oonTestsTitle: String = "OONI Tests"
    private val oonTests = descriptorManager.getDescriptors()
    private val groupedItemList = MutableLiveData<List<Any>>()
    val items = MutableLiveData<List<OONIDescriptor<BaseNettest>>>(oonTests)

    fun getGroupedItemList(): LiveData<List<Any>> {
        if (groupedItemList.value == null) {
            fetchItemList()
        }
        return groupedItemList
    }

    private fun fetchItemList() {

        val groupedItems = items.value!!.sortedBy { !it.isEnabled(preferenceManager) }
            .groupBy {
                return@groupBy if (oonTests.contains(it)) {
					oonTestsTitle
                } else {
                    ""
                }
            }

        val groupedItemList = mutableListOf<Any>()
        groupedItems.forEach { (status, itemList) ->
			groupedItemList.add(status)
            groupedItemList.addAll(itemList)
        }

        this.groupedItemList.value = groupedItemList
    }
}
