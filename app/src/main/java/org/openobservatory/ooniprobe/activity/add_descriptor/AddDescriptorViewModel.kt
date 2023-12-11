package org.openobservatory.ooniprobe.activity.add_descriptor

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.ViewModelFactoryDsl
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.checkbox.MaterialCheckBox.CheckedState
import org.openobservatory.engine.OONIRunDescriptor
import org.openobservatory.ooniprobe.activity.add_descriptor.adapter.GroupedItem
import org.openobservatory.ooniprobe.common.LocaleUtils
import org.openobservatory.ooniprobe.common.TestDescriptorManager
import javax.inject.Inject

class AddDescriptorViewModel constructor(
    var descriptorManager: TestDescriptorManager
) : ViewModel() {
    @CheckedState
    val selectedAllBtnStatus: MutableLiveData<Int> =
        MutableLiveData(MaterialCheckBox.STATE_INDETERMINATE)
    var descriptor: MutableLiveData<OONIRunDescriptor> = MutableLiveData()
    val finishActivity: MutableLiveData<Boolean> = MutableLiveData()
    fun onDescriptorChanged(descriptor: OONIRunDescriptor) {
        this.descriptor.value = descriptor
    }

    fun getName(): String {
        return descriptor.value?.let { descriptor ->
            descriptor.nameIntl[LocaleUtils.sLocale.language] ?: descriptor.name
        } ?: ""
    }

    fun getDescription(): String {
        return descriptor.value?.let { descriptor ->
            descriptor.descriptionIntl[LocaleUtils.sLocale.language] ?: descriptor.description
        } ?: ""
    }

    fun getShortDescription(): String {
        return descriptor.value?.let { descriptor ->
            descriptor.shortDescriptionIntl[LocaleUtils.sLocale.language]
                ?: descriptor.shortDescription
        } ?: ""
    }

    fun setSelectedAllBtnStatus(@CheckedState selectedStatus: Int) {
        selectedAllBtnStatus.postValue(selectedStatus)
    }

    fun onAddButtonClicked(selectedNettest: List<GroupedItem>) {
        descriptor.value?.let { descriptor ->
            descriptorManager.addDescriptor(descriptor.apply {
                nettests = selectedNettest.filter { it.selected }
            }).also {
                finishActivity()
            }
        } ?: throw IllegalStateException("Descriptor is null")
    }

    fun finishActivity() {
        finishActivity.value = true
    }
}