package org.openobservatory.ooniprobe.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingSource;
import androidx.paging.PagingState;

import com.raizlabs.android.dbflow.sql.language.SQLOperator;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.model.database.Result_Table;

import java.util.List;

import kotlin.coroutines.Continuation;

public class QueryDataSource extends PagingSource<Integer, Result> {
    private final String testGroupNameFilter;

    public QueryDataSource(String testGroupNameFilter) {
        this.testGroupNameFilter = testGroupNameFilter;
    }

    @Nullable
    @Override
    public Integer getRefreshKey(@NonNull PagingState<Integer, Result> pagingState) {
        return 1;
    }

    @Nullable
    @Override
    public LoadResult.Page<Integer, Result> load(@NonNull LoadParams<Integer> loadParams, @NonNull Continuation<? super LoadResult<Integer, Result>> continuation) {
        // Key may be null during a refresh, if no explicit key is passed into Pager
        // construction. Use 0 as default, because our API is indexed started at index 0
        int pageNumber = loadParams.getKey() != null ? loadParams.getKey() : 1;

        SQLOperator[] conditions = (testGroupNameFilter != null && !testGroupNameFilter.isEmpty())
                ? new SQLOperator[]{Result_Table.test_group_name.is(testGroupNameFilter)}
                : new SQLOperator[0];

        // Suspending network load via Retrofit. This doesn't need to be wrapped in a
        // withContext(Dispatcher.IO) { ... } block since Retrofit's Coroutine
        // CallAdapter dispatches on a worker thread.
        List<Result> response = SQLite.select().from(Result.class)
                .where(conditions)
                .limit(20)
                .offset((pageNumber - 1) * 20)
                .orderBy(Result_Table.start_time, false)
                .queryList();

        // Since 0 is the lowest page number, return null to signify no more pages should
        // be loaded before it.
        Integer prevKey = (pageNumber > 0) ? pageNumber - 1 : null;

        // This API defines that it's out of data when a page returns empty. When out of
        // data, we return `null` to signify no more pages should be loaded
        Integer nextKey = (!response.isEmpty()) ? pageNumber + 1 : null;
        return new LoadResult.Page<>(
                response,
                prevKey,
                nextKey
        );

    }
}
