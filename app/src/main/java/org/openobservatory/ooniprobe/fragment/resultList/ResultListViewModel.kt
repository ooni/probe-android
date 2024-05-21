package org.openobservatory.ooniprobe.fragment.resultList

import android.text.format.DateFormat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.openobservatory.ooniprobe.domain.GetResults
import java.text.SimpleDateFormat
import java.util.Locale

class ResultListViewModel(val getResults: GetResults) : ViewModel() {

    var results: MutableLiveData<List<Any>> = MutableLiveData()

    fun init(testGroupNameFilter: String?) {
        results.value = getResults.getGroupedByMonth(testGroupNameFilter)
                .flatMap {
                    mutableSetOf<Any>(
                            SimpleDateFormat(
                                    DateFormat.getBestDateTimePattern(
                                            Locale.getDefault(),
                                            "MMMMyyyy"
                                    ), Locale.getDefault()
                            ).format(it.groupedDate)
                    ).apply { addAll(it.resultsList) }
                }
    }

}