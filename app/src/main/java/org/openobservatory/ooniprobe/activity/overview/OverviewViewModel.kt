package org.openobservatory.ooniprobe.activity.overview

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.openobservatory.engine.BaseNettest
import org.openobservatory.ooniprobe.activity.AbstractActivity
import org.openobservatory.ooniprobe.activity.OverviewActivity
import org.openobservatory.ooniprobe.activity.runtests.RunTestsViewModel
import org.openobservatory.ooniprobe.common.AbstractDescriptor
import org.openobservatory.ooniprobe.common.PreferenceManager
import org.openobservatory.ooniprobe.common.TestDescriptorManager
import org.openobservatory.ooniprobe.common.disableTest
import org.openobservatory.ooniprobe.common.enableTest
import org.openobservatory.ooniprobe.model.database.InstalledDescriptor
import javax.inject.Inject

class TestGroupItem(
    var selected: Boolean, override var name: String, override var inputs: List<String>?,
) : BaseNettest(name = name, inputs = inputs)


class OverviewViewModel() : ViewModel() {
    var descriptor: MutableLiveData<AbstractDescriptor<BaseNettest>> = MutableLiveData()
    lateinit var preferenceManager: PreferenceManager
    lateinit var descriptorManager: TestDescriptorManager

    @Inject
    constructor(preferenceManager: PreferenceManager, descriptorManager: TestDescriptorManager) : this() {
        this.preferenceManager = preferenceManager
        this.descriptorManager = descriptorManager
    }


    val selectedAllBtnStatus: MutableLiveData<String> = MutableLiveData()

    init {
        selectedAllBtnStatus.postValue(RunTestsViewModel.SELECT_SOME)
    }

    fun setSelectedAllBtnStatus(selectedStatus: String) {
        selectedAllBtnStatus.postValue(selectedStatus)
        when (selectedStatus) {
            SELECT_ALL -> {
                descriptor.value?.nettests?.forEach {
                    enableTest(it.name)
                }
            }

            SELECT_NONE -> {
                descriptor.value?.nettests?.forEach {
                    disableTest(it.name)
                }
            }
        }
    }

    fun disableTest(name: String) {
        descriptor.value?.let {
            preferenceManager.disableTest(
                name = name,
                prefix = it.preferencePrefix(),
                autoRun = true
            )
        }
    }

    fun enableTest(name: String) {
        descriptor.value?.let {
            preferenceManager.enableTest(
                name = name,
                prefix = it.preferencePrefix(),
                autoRun = true
            )
        }
    }


    fun updateDescriptor(descriptor: AbstractDescriptor<BaseNettest>) {
        this.descriptor.postValue(descriptor)
    }

    fun uninstallLinkClicked(activity: AbstractActivity, descriptor: InstalledDescriptor) {
        descriptorManager.delete(descriptor)
        activity.finish()
    }

    fun automaticUpdatesSwitchClicked(isChecked: Boolean) {
        descriptor.value?.let {
            it.descriptor?.isAutoUpdate = isChecked
            it.descriptor?.save()
        }
    }

    companion object {
        const val SELECT_ALL = "SELECT_ALL"
        const val SELECT_SOME = "SELECT_SOME"
        const val SELECT_NONE = "SELECT_NONE"
    }
}