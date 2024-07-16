package org.openobservatory.ooniprobe.common

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import org.openobservatory.ooniprobe.model.database.ITestDescriptor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppUpdatesViewModel @Inject() constructor(var gson: Gson) : ViewModel() {
    var descriptors: MutableLiveData<List<ITestDescriptor>> = MutableLiveData()
    var testRunComplete: MutableLiveData<Boolean> = MutableLiveData()

    fun setDescriptorsWith(descriptorJson: String) {
        descriptors.value = gson.fromJson(descriptorJson, Array<ITestDescriptor>::class.java).toList()
    }

    fun getUpdatedDescriptor(runId: Long): String {
        return gson.toJson(arrayOf(descriptors.value?.find { it.runId == runId }))
    }

    fun clearDescriptors() {
        descriptors.value = emptyList()
    }
}