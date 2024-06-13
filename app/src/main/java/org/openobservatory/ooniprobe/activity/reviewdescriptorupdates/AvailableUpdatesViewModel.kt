package org.openobservatory.ooniprobe.activity.reviewdescriptorupdates

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import org.openobservatory.ooniprobe.model.database.ITestDescriptor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AvailableUpdatesViewModel @Inject() constructor(var gson: Gson) : ViewModel() {
    var descriptors: MutableLiveData<List<ITestDescriptor>> = MutableLiveData()
    var descriptorString: MutableLiveData<String> = MutableLiveData()

    fun setDescriptorsWith(descriptorJson: String) {
        descriptorString.value = descriptorJson
        descriptors.value = gson.fromJson(descriptorJson, Array<ITestDescriptor>::class.java).toList()
    }

    fun getUpdatedDescriptor(runId: Long): String {
        return gson.toJson(arrayOf(descriptors.value?.find { it.runId == runId }))
    }
}