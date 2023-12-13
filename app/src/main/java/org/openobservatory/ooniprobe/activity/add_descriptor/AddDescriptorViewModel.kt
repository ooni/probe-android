package org.openobservatory.ooniprobe.activity.add_descriptor

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.checkbox.MaterialCheckBox.CheckedState
import org.openobservatory.engine.OONIRunNettest
import org.openobservatory.ooniprobe.activity.add_descriptor.adapter.GroupedItem
import org.openobservatory.ooniprobe.common.LocaleUtils
import org.openobservatory.ooniprobe.common.TestDescriptorManager
import org.openobservatory.ooniprobe.model.database.TestDescriptor
import org.openobservatory.ooniprobe.model.database.getValueForKey

class AddDescriptorViewModel(
    var descriptorManager: TestDescriptorManager
) : ViewModel() {
    @CheckedState
    val selectedAllBtnStatus: MutableLiveData<Int> =
        MutableLiveData(MaterialCheckBox.STATE_CHECKED)
    var descriptor: MutableLiveData<TestDescriptor> = MutableLiveData()
    val finishActivity: MutableLiveData<Boolean> = MutableLiveData()
    fun onDescriptorChanged(descriptor: TestDescriptor) {
        this.descriptor.value = descriptor
    }

    fun getName(): String {
        return descriptor.value?.let { descriptor ->
            descriptor.nameIntl.getValueForKey(LocaleUtils.sLocale.language) ?: descriptor.name
        } ?: ""
    }

    fun getDescription(): String {
        return descriptor.value?.let { descriptor ->
            descriptor.descriptionIntl.getValueForKey(LocaleUtils.sLocale.language)
                ?: descriptor.description
        } ?: ""
    }

    fun getShortDescription(): String {
        return descriptor.value?.let { descriptor ->
            descriptor.shortDescriptionIntl.getValueForKey(LocaleUtils.sLocale.language)
                ?: descriptor.shortDescription
        } ?: ""
    }

    fun setSelectedAllBtnStatus(@CheckedState selectedStatus: Int) {
        selectedAllBtnStatus.postValue(selectedStatus)
    }

    fun onAddButtonClicked(selectedNettest: List<GroupedItem>, automatedUpdates: Boolean) {
        descriptor.value?.let { descriptor ->
            descriptorManager.addDescriptor(
                descriptor = descriptor.apply {
                    isAutoUpdate = automatedUpdates
                    nettests = selectedNettest.filter { it.selected }.map { nettest ->
                        OONIRunNettest(
                            name = nettest.name,
                            inputs = nettest.inputs
                        )
                    }
                },
            ).also {
                finishActivity()
            }
        } ?: throw IllegalStateException("Descriptor is null")
    }

    fun finishActivity() {
        finishActivity.value = true
    }
}