package org.openobservatory.ooniprobe.fragment.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.openobservatory.ooniprobe.BuildConfig
import org.openobservatory.ooniprobe.common.PreferenceManager
import org.openobservatory.ooniprobe.test.TestAsyncTask
import org.openobservatory.ooniprobe.test.suite.AbstractSuite
import javax.inject.Inject

class DashboardViewModel @Inject constructor(private val preferenceManager: PreferenceManager) : ViewModel() {
    private val enabledTitle: String = "Enabled"
    private val groupedItemList = MutableLiveData<List<Any>>()
    val items = MutableLiveData<List<AbstractSuite>>(TestAsyncTask.getSuites())

    fun getGroupedItemList(): LiveData<List<Any>> {
        if (groupedItemList.value == null) {
            fetchItemList()
        }
        return groupedItemList
    }

    private fun fetchItemList() {

        val groupedItems = items.value!!.filter {testSuite ->
            return@filter testSuite.getTestList(preferenceManager).isNotEmpty() || BuildConfig.SHOW_DISABLED_CARDS
        }.sortedBy { it.getTestList(preferenceManager).isEmpty() }
            .groupBy {
                return@groupBy if ((it.getTestList(preferenceManager).isNotEmpty())) {
                    enabledTitle
                } else {
                    ""
                }
            }

        val groupedItemList = mutableListOf<Any>()
        groupedItems.forEach { (status, itemList) ->
            if (status != enabledTitle) {
                groupedItemList.add(status)
            }
            groupedItemList.addAll(itemList)
        }

        this.groupedItemList.value = groupedItemList
    }
}