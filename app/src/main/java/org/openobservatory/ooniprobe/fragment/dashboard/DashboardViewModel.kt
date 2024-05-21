package org.openobservatory.ooniprobe.fragment.dashboard

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.openobservatory.engine.BaseNettest
import org.openobservatory.ooniprobe.common.AbstractDescriptor
import org.openobservatory.ooniprobe.common.PreferenceManager
import org.openobservatory.ooniprobe.common.TestDescriptorManager
import org.openobservatory.ooniprobe.model.database.ITestDescriptor
import org.openobservatory.ooniprobe.model.database.InstalledDescriptor
import org.openobservatory.ooniprobe.model.database.TestDescriptor
import javax.inject.Inject

class DashboardViewModel @Inject constructor(
        private val preferenceManager: PreferenceManager,
        private val descriptorManager: TestDescriptorManager
) : ViewModel(), DefaultLifecycleObserver {
    private var pendingUpdates: MutableLiveData<List<ITestDescriptor>> = MutableLiveData()
    private var ooniRunDescriptors: List<InstalledDescriptor> = emptyList()
    private val oonTestsTitle: String = "OONI Tests"
    private val oonRunLinksTitle: String = "OONI RUN Links"
    private val oonTests = descriptorManager.getDescriptors()
    private val groupedItemList = MutableLiveData<List<Any>>()
    private val items = MutableLiveData<List<AbstractDescriptor<BaseNettest>>>(oonTests)

    init {
        ooniRunDescriptors = descriptorManager.getRunV2Descriptors().map {
            InstalledDescriptor(it, getTags(it))
        }
    }

    private fun getTags(descriptor: TestDescriptor): List<String> {
        pendingUpdates.value.let { updates ->
            return if (updates?.any { it.runId == descriptor.runId } == true) {
                listOf("updated")
            } else {
                emptyList()
            }
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        ooniRunDescriptors = descriptorManager.getRunV2Descriptors().map {
            InstalledDescriptor(it, getTags(it))
        }
        fetchItemList()
    }

    fun getGroupedItemList(): LiveData<List<Any>> {
        if (groupedItemList.value == null) {
            fetchItemList()
        }
        return groupedItemList
    }

    fun getItemList(): LiveData<List<AbstractDescriptor<BaseNettest>>> {
        return items.value?.let { MutableLiveData(it + ooniRunDescriptors) } ?: MutableLiveData()
    }

    private fun fetchItemList() {

        val groupedItems = items.value!!.groupBy {
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
        if (ooniRunDescriptors.isNotEmpty()) {
            if (groupedItems.isNotEmpty()) {
                groupedItemList.add(oonRunLinksTitle)
            }
            groupedItemList.addAll(ooniRunDescriptors)
        }
        this.groupedItemList.value = groupedItemList
    }

    fun updateDescriptorWith(descriptors: List<ITestDescriptor>) {
        pendingUpdates.value = descriptors
        ooniRunDescriptors = descriptorManager.getRunV2Descriptors().map {
            InstalledDescriptor(it, getTags(it))
        }
        fetchItemList()
    }
}
