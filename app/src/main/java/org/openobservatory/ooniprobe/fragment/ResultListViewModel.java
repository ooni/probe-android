package org.openobservatory.ooniprobe.fragment;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;

import org.openobservatory.ooniprobe.domain.QueryDataSource;
import org.openobservatory.ooniprobe.model.database.Result;

import kotlinx.coroutines.CoroutineScope;

public class ResultListViewModel extends ViewModel {

    public LiveData<PagingData<Result>> pagingData;

    public ResultListViewModel() {
        init(null);
    }

    // Init ViewModel Data
    public void init(@Nullable String testGroupNameFilter) {
        // Define Paging Source
        QueryDataSource moviePagingSource = new QueryDataSource(testGroupNameFilter);

        // CoroutineScope helper provided by the lifecycle-viewmodel-ktx artifact.
        CoroutineScope viewModelScope = ViewModelKt.getViewModelScope(this);
        Pager<Integer, Result> pager = new Pager<>(
                new PagingConfig(/* pageSize = */ 20),
                () -> moviePagingSource);


        PagingLiveData.cachedIn(PagingLiveData.getLiveData(pager), viewModelScope);

        pagingData = PagingLiveData.getLiveData(pager);
    }
}