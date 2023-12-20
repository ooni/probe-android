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

/**
 * ViewModel for the AddDescriptorActivity. This class is responsible for preparing and managing the data for the AddDescriptorActivity.
 * It handles the communication of the Activity with the rest of the application (e.g. calling business logic classes).
 *
 * @property descriptorManager Instance of TestDescriptorManager which is responsible for managing the test descriptors.
 * @property selectedAllBtnStatus LiveData holding the state of the "Select All" button in the UI.
 * @property descriptor LiveData holding the OONIRunDescriptor object that the user is currently interacting with in the UI.
 */
class AddDescriptorViewModel constructor(
    var descriptorManager: TestDescriptorManager
) : ViewModel() {
    @CheckedState
    val selectedAllBtnStatus: MutableLiveData<Int> =
        MutableLiveData(MaterialCheckBox.STATE_CHECKED)
    var descriptor: MutableLiveData<OONIRunDescriptor> = MutableLiveData()
    val finishActivity: MutableLiveData<Boolean> = MutableLiveData()

    /**
     * This method is called when the activity is created.
     * It sets the descriptor value of this ViewModel.
     * @param descriptor is the new descriptor
     */
    fun onDescriptorChanged(descriptor: OONIRunDescriptor) {
        this.descriptor.value = descriptor
    }

    /**
     * This method is used to get the name of the descriptor.
     * Used by the UI during data binding.
     * @return the name of the descriptor.
     */
    fun getName(): String {
        return descriptor.value?.let { descriptor ->
            descriptor.nameIntl[LocaleUtils.sLocale.language] ?: descriptor.name
        } ?: ""
    }

    /**
     * This method is used to get the name of the descriptor.
     * Used by the UI during data binding.
     * @return the name of the descriptor.
     */
    fun getDescription(): String {
        return descriptor.value?.let { descriptor ->
            descriptor.descriptionIntl[LocaleUtils.sLocale.language] ?: descriptor.description
        } ?: ""
    }

    /**
     * This method is used to get the short description of the descriptor.
     * Used by the UI during data binding.
     * @return the short description of the descriptor.
     */
    fun getShortDescription(): String {
        return descriptor.value?.let { descriptor ->
            descriptor.shortDescriptionIntl[LocaleUtils.sLocale.language]
                ?: descriptor.shortDescription
        } ?: ""
    }

    /**
     * This method is used to set the state of the "Select All" button in the UI.
     * @param selectedStatus is the new state of the "Select All" button.
     */
    fun setSelectedAllBtnStatus(@CheckedState selectedStatus: Int) {
        selectedAllBtnStatus.postValue(selectedStatus)
    }


    /**
     * This method is called when the "Add Link" button is clicked.
     * It adds the descriptor to the descriptor manager and signals that the activity should finish.
     * @param selectedNettest is the list of selected nettests.
     * @param automatedUpdates is a boolean indicating whether automated updates should be enabled.
     */
    fun onAddButtonClicked(selectedNettest: List<GroupedItem>, automatedUpdates: Boolean) {
        descriptor.value?.let { descriptor ->
            descriptorManager.addDescriptor(
                descriptor = descriptor.apply {
                    nettests = selectedNettest.filter { it.selected }
                },
                automatedUpdates = automatedUpdates
            ).also {
                finishActivity()
            }
        } ?: throw IllegalStateException("Descriptor is null")
    }

    /**
     * This method is used to signal that the activity should finish.
     */
    fun finishActivity() {
        finishActivity.value = true
    }
}
