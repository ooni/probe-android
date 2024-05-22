package org.openobservatory.ooniprobe.activity.overview

import android.text.format.DateUtils
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import org.openobservatory.engine.BaseNettest
import org.openobservatory.ooniprobe.activity.AbstractActivity
import org.openobservatory.ooniprobe.common.AbstractDescriptor
import org.openobservatory.ooniprobe.common.Application
import org.openobservatory.ooniprobe.common.OONITests
import org.openobservatory.ooniprobe.common.PreferenceManager
import org.openobservatory.ooniprobe.common.TestDescriptorManager
import org.openobservatory.ooniprobe.common.disableTest
import org.openobservatory.ooniprobe.common.enableTest
import org.openobservatory.ooniprobe.model.database.InstalledDescriptor
import org.openobservatory.ooniprobe.model.database.Result
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class TestGroupItem(
    var selected: Boolean, override var name: String, override var inputs: List<String>?,
) : BaseNettest(name = name, inputs = inputs)


class OverviewViewModel @Inject constructor(var application: Application, var preferenceManager: PreferenceManager, var descriptorManager: TestDescriptorManager) : AndroidViewModel(application) {
    var descriptor: MutableLiveData<AbstractDescriptor<BaseNettest>> = MutableLiveData()

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
        this.descriptor.value = descriptor
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

    fun getIcon(): Int {
        return descriptor.value?.getDisplayIcon(application) ?: 0
    }
    fun getRunTime(): String? {
        return descriptor.value?.getRuntime(application, preferenceManager)?.toString()
    }

    fun getLastTime(): String? {
        return descriptor.value?.let {
            DateUtils.getRelativeTimeSpanString(
                    Result.getLastResult(it.name).start_time.time
            ).toString()
        }
    }

    fun getDescription(): String? {
        return descriptor.value?.let {
            if (it is InstalledDescriptor){
                return String.format(
                        "Created by %s on %s\n\n%s",
                        it.descriptor?.author,
                        it.descriptor?.dateCreated?.let { date -> SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH).format(date) },
                        it.description
                )
            } else {
                return it.description
            }
        }
    }

    companion object {
        const val SELECT_ALL = "SELECT_ALL"
        const val SELECT_SOME = "SELECT_SOME"
        const val SELECT_NONE = "SELECT_NONE"
    }
}
