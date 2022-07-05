package org.openobservatory.ooniprobe.fragment;

import android.text.format.DateFormat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.PagingDataTransforms;
import androidx.paging.PagingLiveData;

import org.jetbrains.annotations.Nullable;
import org.openobservatory.ooniprobe.adapters.ResultListAdapter.UiModel;
import org.openobservatory.ooniprobe.domain.QueryDataSource;
import org.openobservatory.ooniprobe.model.database.Result;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ResultListViewModel extends ViewModel {
    public LiveData<PagingData<UiModel>> pagingData;
    private final Executor bgExecutor = Executors.newFixedThreadPool(4);

    public final void init(@Nullable final String testGroupNameFilter) {
        Pager<Integer, Result> pager = new Pager<>(
                new PagingConfig(
                        30,
                        20,
                        false,
                        15,
                        80,
                        Integer.MIN_VALUE
                ),
                () -> new QueryDataSource(testGroupNameFilter)
        );

        this.pagingData = Transformations.map(
                PagingLiveData.getLiveData(pager),
                (PagingData<Result> pagingData) -> {
                    // First convert items in stream to UiModel.UserModel.
                    PagingData<UiModel.ResultModel> uiModelPagingData = PagingDataTransforms.map(
                            pagingData, bgExecutor, UiModel.ResultModel::new);
                    // Insert UiModel.SeparatorModel, which produces PagingData of
                    // generic type UiModel.
                    return PagingDataTransforms.insertSeparators(
                            uiModelPagingData,
                            bgExecutor,
                            this::generateSeparators);
                }
        );
    }

    private UiModel generateSeparators(@Nullable UiModel.ResultModel before, @Nullable UiModel.ResultModel after) {
        if (after != null && (before == null || shouldSeparate(before, after))) {
            // separator - after is first item
            return new UiModel.SeparatorModel(
                    new SimpleDateFormat(
                            DateFormat.getBestDateTimePattern(
                                    Locale.getDefault(),
                                    "MMMMyyyy"
                            ), Locale.getDefault()
                    ).format(after.getItem().start_time)
            );
        } else {
            // no separator - either end of list, or first
            return null;
        }
    }

    private boolean shouldSeparate(UiModel.ResultModel before, UiModel.ResultModel after) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMM");
        return !fmt.format(before.getItem().start_time).equals(fmt.format(after.getItem().start_time));
    }
}
