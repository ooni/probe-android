package org.openobservatory.ooniprobe.common

import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This class is used to store the state of the test group.
 * It is used to communicate the state of the test group between the [org.openobservatory.ooniprobe.test.TestAsyncTask] and any [androidx.lifecycle.Observer].
 *
 * Note: Ideally, this class should used to replace Broadcasts from [org.openobservatory.ooniprobe.test.TestAsyncTask]
 *          and "org.openobservatory.ooniprobe.activity.RunningActivity" broadcast events
 */
@Singleton
class TestStateRepository @Inject constructor() {
    var testGroupStatus = MutableLiveData(TestGroupStatus.NOT_STARTED)
}

enum class TestGroupStatus { NOT_STARTED, RUNNING, FINISHED }