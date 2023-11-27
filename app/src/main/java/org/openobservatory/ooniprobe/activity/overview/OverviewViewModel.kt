package org.openobservatory.ooniprobe.activity.overview

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.openobservatory.engine.BaseNettest
import org.openobservatory.ooniprobe.activity.runtests.RunTestsViewModel
import org.openobservatory.ooniprobe.common.OONIDescriptor
import org.openobservatory.ooniprobe.common.PreferenceManager
import javax.inject.Inject

class TestGroupItem(
    var selected: Boolean, override var name: String, override var inputs: List<String>?,
) : BaseNettest(name = name, inputs = inputs)


class OverviewViewModel() : ViewModel() {
    var descriptor: MutableLiveData<OONIDescriptor<BaseNettest>> = MutableLiveData()
    lateinit var preferenceManager: PreferenceManager

    @Inject
    constructor(preferenceManager: PreferenceManager) : this() {
        this.preferenceManager = preferenceManager
    }


    val selectedAllBtnStatus: MutableLiveData<String> = MutableLiveData()

    init {
        selectedAllBtnStatus.postValue(RunTestsViewModel.SELECT_SOME)
    }

    fun setSelectedAllBtnStatus(selectedStatus: String) {
        selectedAllBtnStatus.postValue(selectedStatus)
        when (selectedStatus) {
            SELECT_ALL -> {
                // enableTest(testName)
            }

            SELECT_NONE -> {
                // disableTest(testName)
            }
        }
    }

    fun updateDescriptor(descriptor: OONIDescriptor<BaseNettest>) {
        this.descriptor.postValue(descriptor)
    }

    companion object {
        const val SELECT_ALL = "SELECT_ALL"
        const val SELECT_SOME = "SELECT_SOME"
        const val SELECT_NONE = "SELECT_NONE"
    }
}