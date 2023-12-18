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
		when (selectedStatus) {
			SELECT_ALL -> {
				OONITests.INSTANT_MESSAGING.nettests.forEach {
					enableTest(it.name)
				}
				OONITests.CIRCUMVENTION.nettests.forEach {
					enableTest(it.name)
				}
				OONITests.PERFORMANCE.nettests.forEach {
					enableTest(it.name)
				}
				enableTest(OONITests.EXPERIMENTAL.label)
			}

			SELECT_NONE -> {
				OONITests.INSTANT_MESSAGING.nettests.forEach {
					disableTest(it.name)
				}
				OONITests.CIRCUMVENTION.nettests.forEach {
					disableTest(it.name)
				}
				OONITests.PERFORMANCE.nettests.forEach {
					disableTest(it.name)
				}
				disableTest(OONITests.EXPERIMENTAL.label)
			}
		}
	}

	fun disableTest(name: String) {
		preferenceManager.disableTest(name)
	}

	fun enableTest(name: String) {
		preferenceManager.enableTest(name)
	}

	companion object {
		const val SELECT_ALL = "SELECT_ALL"
		const val SELECT_SOME = "SELECT_SOME"
		const val SELECT_NONE = "SELECT_NONE"
	}
}
