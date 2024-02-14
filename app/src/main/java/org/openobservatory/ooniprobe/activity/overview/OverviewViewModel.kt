package org.openobservatory.ooniprobe.activity.overview

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.openobservatory.engine.BaseNettest
import org.openobservatory.ooniprobe.activity.AbstractActivity
import org.openobservatory.ooniprobe.activity.OverviewActivity
import org.openobservatory.ooniprobe.activity.runtests.RunTestsViewModel
import org.openobservatory.ooniprobe.common.AbstractDescriptor
import org.openobservatory.ooniprobe.common.OONIDescriptor
import org.openobservatory.ooniprobe.common.OONITests
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
        selectedAllBtnStatus.postValue(SELECT_SOME)
    }


    /**
     * Set the status of the selected all button.
     * This method will also update the preference of the tests based on the selected status.
     * For experimental tests, a single button is used to enable/disable all tests.
     * @param selectedStatus the status of the selected all button
     */
    fun setSelectedAllBtnStatus(selectedStatus: String) {
        selectedAllBtnStatus.postValue(selectedStatus)
        descriptor.value?.let { desc ->

            when (selectedStatus) {
                SELECT_ALL -> {
                    when (desc.name) {
                        OONITests.EXPERIMENTAL.label -> {
                            enableTest(name = desc.name)
                        }

                        else -> {
                            descriptor.value?.nettests?.forEach {
                                enableTest(name = it.name)
                            }
                        }
                    }
                }

                SELECT_NONE -> {
                    when (desc.name) {
                        OONITests.EXPERIMENTAL.label -> {
                            disableTest(name = desc.name)
                        }

                        else -> {
                            descriptor.value?.nettests?.forEach {
                                disableTest(name = it.name)
                            }
                        }
                    }
                }

                else -> {}
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
