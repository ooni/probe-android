package org.openobservatory.ooniprobe.activity.runtests

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.openobservatory.ooniprobe.common.PreferenceManager
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
		const val NOT_SELECT_ANY = "NOT_SELECT_ANY"
	}
}
