package org.openobservatory.ooniprobe.activity.overview

import android.text.format.DateUtils
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.openobservatory.ooniprobe.model.database.Result
import org.openobservatory.ooniprobe.test.suite.AbstractSuite
import org.openobservatory.ooniprobe.test.suite.OONIRunSuite
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite

class OverviewViewModel : ViewModel() {
    var suite: MutableLiveData<AbstractSuite?> = MutableLiveData<AbstractSuite?>()
    var name = MutableLiveData<String>()
    var title = MutableLiveData<String>()
    var icon = MutableLiveData<Int>()
    var dataUsage = MutableLiveData<String>()
    var lastTime = MutableLiveData<String>()
    var author = MutableLiveData<String?>()
    var description = MutableLiveData<String>()
    fun onTestSuiteChanged(suite: AbstractSuite) {
        this.suite.value = suite
        name.value = suite.name
        title.value = suite.title
        icon.value = suite.icon
        dataUsage.value = suite.dataUsage.toString()
        // TODO: (aanorbel) Use runID for fetching value, all ooni-run tests register as one with same name.
        lastTime.value = Result.getLastResult(suite.name)?.start_time?.time?.let {
            DateUtils.getRelativeTimeSpanString(
                it
            )?.toString()
        }

        description.value = suite.desc1
        if (suite is OONIRunSuite) {
            author.value = String.format("Author : %s", suite.descriptor.author)
        }
    }
    val authorVisibility: Int
        get() = if (author.value != null) View.VISIBLE else View.GONE

    val customUrlVisibility: Int
        get() = if (name.value == WebsitesSuite.NAME) View.VISIBLE else View.GONE
}