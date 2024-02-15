package org.openobservatory.ooniprobe.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingSource;
import androidx.paging.PagingState;

import com.google.common.collect.Iterables;
import com.raizlabs.android.dbflow.sql.language.SQLOperator;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.openobservatory.ooniprobe.common.ThirdPartyServices;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.model.database.Result_Table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
        int highestId = loadParams.getKey() != null ? loadParams.getKey() : 0;

        ArrayList<SQLOperator> conditions = (testGroupNameFilter != null && !testGroupNameFilter.isEmpty())
                ? new ArrayList<>(Collections.singletonList(Result_Table.test_group_name.is(testGroupNameFilter)))
                : new ArrayList<>();
        if (highestId > 0) {
            conditions.add(Result_Table.id.lessThan(highestId));
        }

        List<Result> response = SQLite.select().from(Result.class)
                .where(conditions.toArray(new SQLOperator[0]))
                .limit(loadParams.getLoadSize())
                .orderBy(Result_Table.id, false)
                .queryList();

        Integer prevKey = null;
        try {
            if (loadParams.getKey() != null)
              prevKey = (!response.isEmpty()) ? Objects.requireNonNull(Iterables.getFirst(response, null)).id - 20 : null;
        } catch (Exception e) {
            ThirdPartyServices.logException(e);
        }
        // This API defines that it's out of data when a page returns empty. When out of
        // data, we return `null` to signify no more pages should be loaded
        Integer nextKey = (!response.isEmpty()) ? Iterables.getLast(response).id : null;
        return new LoadResult.Page<>(
                response,
                prevKey,
                nextKey
        );

    }
}
