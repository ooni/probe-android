package org.openobservatory.ooniprobe.activity.runtests

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.openobservatory.ooniprobe.common.OONITests
import org.openobservatory.ooniprobe.common.PreferenceManager
import org.openobservatory.ooniprobe.common.disableTest
import org.openobservatory.ooniprobe.common.enableTest
import javax.inject.Inject


class RunTestsViewModel() : ViewModel() {
	lateinit var preferenceManager: PreferenceManager

	@Inject
	constructor(preferenceManager: PreferenceManager) : this() {
		this.preferenceManager = preferenceManager
	}

	val selectedAllBtnStatus: MutableLiveData<String> = MutableLiveData()

	init {
		selectedAllBtnStatus.postValue(SELECT_SOME)
	}

	fun setSelectedAllBtnStatus(selectedStatus: String) {
		selectedAllBtnStatus.postValue(selectedStatus)
	}

	companion object {
		const val SELECT_ALL = "SELECT_ALL"
		const val SELECT_SOME = "SELECT_SOME"
		const val SELECT_NONE = "SELECT_NONE"
	}
}
