package org.openobservatory.ooniprobe.activity.runtests

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.openobservatory.ooniprobe.common.PreferenceManager
import org.openobservatory.ooniprobe.common.disableTest
import org.openobservatory.ooniprobe.common.enableTest
import org.openobservatory.ooniprobe.test.suite.CircumventionSuite
import org.openobservatory.ooniprobe.test.suite.ExperimentalSuite
import org.openobservatory.ooniprobe.test.suite.InstantMessagingSuite
import org.openobservatory.ooniprobe.test.suite.PerformanceSuite
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
				InstantMessagingSuite().getTestList(preferenceManager).forEach {
					enableTest(it.name)
				}
				CircumventionSuite().getTestList(preferenceManager).forEach {
					enableTest(it.name)
				}
				PerformanceSuite().getTestList(preferenceManager).forEach {
					enableTest(it.name)
				}
				enableTest(ExperimentalSuite.NAME)
			}

			SELECT_NONE -> {
				InstantMessagingSuite().getTestList(preferenceManager).forEach {
					disableTest(it.name)
				}
				CircumventionSuite().getTestList(preferenceManager).forEach {
					disableTest(it.name)
				}
				PerformanceSuite().getTestList(preferenceManager).forEach {
					disableTest(it.name)
				}
				disableTest(ExperimentalSuite.NAME)
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
