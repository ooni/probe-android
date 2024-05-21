package org.openobservatory.ooniprobe.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.activity.ResultDetailActivity;
import org.openobservatory.ooniprobe.activity.TextActivity;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.ResubmitTask;
import org.openobservatory.ooniprobe.common.TestDescriptorManager;
import org.openobservatory.ooniprobe.databinding.FragmentResultListBinding;
import org.openobservatory.ooniprobe.domain.GetResults;
import org.openobservatory.ooniprobe.domain.MeasurementsManager;
import org.openobservatory.ooniprobe.fragment.resultList.ResultListAdapter;
import org.openobservatory.ooniprobe.fragment.resultList.ResultListViewModel;
import org.openobservatory.ooniprobe.model.database.Network;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.model.database.Result_Table;

import java.io.Serializable;
import java.lang.ref.WeakReference;

import javax.inject.Inject;

import localhost.toolkit.app.fragment.ConfirmDialogFragment;

public class ResultListFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener, ConfirmDialogFragment.OnConfirmedListener {
    private FragmentResultListBinding binding;

    private ResultListAdapter adapter;
    private boolean refresh;
    private Snackbar snackbar;

    @Inject
    MeasurementsManager measurementsManager;

    @Inject
    GetResults getResults;

    @Inject
    PreferenceManager pm;

    @Inject
    TestDescriptorManager descriptorManager;

    private ResultListViewModel viewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentResultListBinding.inflate(inflater, container, false);
        ((Application) getActivity().getApplication()).getFragmentComponent().inject(this);
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbar);

        viewModel = new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ResultListViewModel(getResults);
            }
        }.create(ResultListViewModel.class);

        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.TestResults_Overview_Title);
        reloadHeader();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.recycler.setLayoutManager(layoutManager);
        binding.recycler.addItemDecoration(new DividerItemDecoration(getActivity(), layoutManager.getOrientation()));
        adapter = new ResultListAdapter(this, this);
        binding.recycler.setAdapter(adapter);

        binding.filterTests.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                queryList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                queryList();
            }
        });

        snackbar = Snackbar.make(binding.coordinatorLayout, R.string.Snackbar_ResultsSomeNotUploaded_Text, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.Snackbar_ResultsSomeNotUploaded_UploadAll, v1 ->
                        new ConfirmDialogFragment.Builder()
                                .withExtra(R.string.Modal_ResultsNotUploaded_Title)
                                .withTitle(getString(R.string.Modal_ResultsNotUploaded_Title))
                                .withMessage(getString(R.string.Modal_ResultsNotUploaded_Paragraph))
                                .withPositiveButton(getString(R.string.Modal_ResultsNotUploaded_Button_Upload))
                                .build().show(getChildFragmentManager(), null)
                );
        return binding.getRoot();
    }

    public void reloadHeader() {
        binding.tests.setText(getString(R.string.d, SQLite.selectCountOf().from(Result.class).longValue()));
        binding.networks.setText(getString(R.string.d, SQLite.selectCountOf().from(Network.class).longValue()));
        binding.upload.setText(Result.readableFileSize(SQLite.select(Method.sum(Result_Table.data_usage_up)).from(Result.class).longValue()));
        binding.download.setText(Result.readableFileSize(SQLite.select(Method.sum(Result_Table.data_usage_down)).from(Result.class).longValue()));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (refresh) {
            queryList();
            refresh = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        refresh = true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.delete, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                new ConfirmDialogFragment.Builder()
                        .withExtra(R.id.delete)
                        .withMessage(getString(R.string.Modal_DoYouWantToDeleteAllTests))
                        .withPositiveButton(getString(R.string.Modal_Delete))
                        .build().show(getChildFragmentManager(), null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void queryList() {
        if (measurementsManager.hasUploadables()) {
            snackbar.show();
        } else {
            snackbar.dismiss();
        }

        String filter = getResources().getStringArray(R.array.filterTestValues)[binding.filterTests.getSelectedItemPosition()];
        viewModel.init(filter);
        viewModel.getResults().observe(getViewLifecycleOwner(), resultPagingData -> {
            binding.recycler.post(() -> {
                adapter.submitList(resultPagingData);
                adapter.notifyDataSetChanged();
            });
        });
    }

    @Override
    public void onClick(View v) {
        Result result = (Result) v.getTag();
        if (result.countTotalMeasurements() != 0)
            ActivityCompat.startActivity(getActivity(), ResultDetailActivity.newIntent(getActivity(), result.id), null);
    }

    @Override
    public boolean onLongClick(View v) {
        Result result = (Result) v.getTag();
        new ConfirmDialogFragment.Builder()
                .withExtra(result)
                .withMessage(getString(R.string.Modal_DoYouWantToDeleteThisTest))
                .withPositiveButton(getString(R.string.Modal_Delete))
                .build().show(getChildFragmentManager(), null);
        return true;
    }

    @Override
    public void onConfirmation(Serializable serializable, int i) {
        if (serializable.equals(R.string.Modal_ResultsNotUploaded_Title)) {
            if (i == DialogInterface.BUTTON_POSITIVE) {
                new ResubmitAsyncTask(this, pm.getProxyURL()).execute(null, null);
            } else if (i == DialogInterface.BUTTON_NEUTRAL) {
                startActivity(TextActivity.newIntent(getActivity(), TextActivity.TYPE_UPLOAD_LOG, (String) serializable));
            } else
                snackbar.show();
        } else if (i == DialogInterface.BUTTON_POSITIVE) {
            if (serializable instanceof Result) {
                ((Result) serializable).delete(getActivity());
            } else if (serializable.equals(R.id.delete)) {
                //From https://guides.codepath.com/android/using-dialogfragment
                ProgressDialog pd = new ProgressDialog(getContext());
                pd.setCancelable(false);
                pd.show();
                Result.deleteAll(getActivity());
                pd.dismiss();
            }
            queryList();
            reloadHeader();
        }
    }

    private static class ResubmitAsyncTask extends ResubmitTask<AbstractActivity> {
        private WeakReference<ResultListFragment> wf;

        ResubmitAsyncTask(ResultListFragment f, String proxy) {
            super((AbstractActivity) f.getActivity(), proxy);
            this.wf = new WeakReference<>(f);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            ResultListFragment f = wf.get();
            if (getActivity() != null && f != null) {
                if (f.isAdded())
                    f.queryList();
                if (!result && !getActivity().isFinishing())
                    new ConfirmDialogFragment.Builder()
                            .withTitle(getActivity().getString(R.string.Modal_UploadFailed_Title))
                            .withMessage(getActivity().getString(R.string.Modal_UploadFailed_Paragraph, errors.toString(), totUploads.toString()))
                            .withPositiveButton(getActivity().getString(R.string.Modal_Retry))
                            .withNeutralButton(getActivity().getString(R.string.Modal_DisplayFailureLog))
                            .withExtra(String.join("\n", logger.logs))
                            .build().show(getActivity().getSupportFragmentManager(), null);
            }
        }
    }
}
