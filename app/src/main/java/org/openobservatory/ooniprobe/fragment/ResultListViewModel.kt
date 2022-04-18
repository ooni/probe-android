package org.openobservatory.ooniprobe.fragment

import android.text.format.DateFormat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.paging.*
import org.openobservatory.ooniprobe.adapters.ResultListAdapter
import org.openobservatory.ooniprobe.domain.QueryDataSource
import org.openobservatory.ooniprobe.model.database.Result
import java.text.SimpleDateFormat
import java.util.*


class ResultListViewModel : ViewModel() {
    lateinit var pagingData: LiveData<PagingData<ResultListAdapter.UiModel>>

    fun init(testGroupNameFilter: String?) {
        val pager: Pager<Int, Result> = Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                maxSize = 30,
                prefetchDistance = 5,
                initialLoadSize = 10
            ),
            pagingSourceFactory = { QueryDataSource(testGroupNameFilter) },
        )
        pagingData = pager.liveData.map { pagingData: PagingData<Result> ->
            // Map outer stream, so you can perform transformations on
            // each paging generation.
            pagingData
                .map { result ->
                    ResultListAdapter.UiModel.ResultModel(result)
                }
                .insertSeparators { before, after ->
                    when {
                        before == null -> after?.let {
                            ResultListAdapter.UiModel.SeparatorModel(
                                SimpleDateFormat(
                                    DateFormat.getBestDateTimePattern(
                                        Locale.getDefault(),
                                        "MMMMyyyy"
                                    ), Locale.getDefault()
                                ).format(it.item.start_time)
                            )
                        }
                        after == null -> null
                        shouldSeparate(before, after) -> ResultListAdapter.UiModel.SeparatorModel(
                            SimpleDateFormat(
                                DateFormat.getBestDateTimePattern(
                                    Locale.getDefault(),
                                    "MMMMyyyy"
                                ), Locale.getDefault()
                            ).format(after.item.start_time)
                        )
                        // Return null to avoid adding a separator between two items.
                        else -> null
                    }
                }
        }

    }

    private fun shouldSeparate(
        before: ResultListAdapter.UiModel.ResultModel,
        after: ResultListAdapter.UiModel.ResultModel
    ): Boolean {
        val fmt = SimpleDateFormat("yyyyMM")
        return !fmt.format(before.item.start_time).equals(fmt.format(after.item.start_time))
    }

}