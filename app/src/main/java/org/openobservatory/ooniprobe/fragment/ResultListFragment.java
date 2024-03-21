package org.openobservatory.ooniprobe.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.snackbar.Snackbar;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import localhost.toolkit.app.fragment.ConfirmDialogFragment;
import localhost.toolkit.widget.recyclerview.HeterogeneousRecyclerAdapter;
import localhost.toolkit.widget.recyclerview.HeterogeneousRecyclerItem;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.activity.ResultDetailActivity;
import org.openobservatory.ooniprobe.activity.TextActivity;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.ResubmitTask;
import org.openobservatory.ooniprobe.databinding.FragmentResultListBinding;
import org.openobservatory.ooniprobe.domain.GetResults;
import org.openobservatory.ooniprobe.domain.MeasurementsManager;
import org.openobservatory.ooniprobe.domain.models.DatedResults;
import org.openobservatory.ooniprobe.item.*;
import org.openobservatory.ooniprobe.model.database.Network;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.model.database.ResultExtensions;
import org.openobservatory.ooniprobe.model.database.Result_Table;
import org.openobservatory.ooniprobe.test.suite.*;

import javax.inject.Inject;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ResultListFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener, ConfirmDialogFragment.OnConfirmedListener {
    private FragmentResultListBinding binding;

    private ArrayList<HeterogeneousRecyclerItem> items;
    private HeterogeneousRecyclerAdapter<HeterogeneousRecyclerItem> adapter;
    private boolean refresh;
    private Snackbar snackbar;

    @Inject
    MeasurementsManager measurementsManager;

    @Inject
    GetResults getResults;

    @Inject
    PreferenceManager pm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentResultListBinding.inflate(inflater, container, false);
        ((Application) getActivity().getApplication()).getFragmentComponent().inject(this);
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbar);
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.TestResults_Overview_Title);
        reloadHeader();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.recycler.setLayoutManager(layoutManager);
        binding.recycler.addItemDecoration(new DividerItemDecoration(getActivity(), layoutManager.getOrientation()));
        items = new ArrayList<>();
        adapter = new HeterogeneousRecyclerAdapter<>(getActivity(), items);
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

        items.clear();

        String filter = getResources().getStringArray(R.array.filterTestValues)[binding.filterTests.getSelectedItemPosition()];
        List<DatedResults> list = getResults.getGroupedByMonth(filter);

        if (list.isEmpty()) {
            binding.emptyState.setVisibility(View.VISIBLE);
            binding.recycler.setVisibility(View.GONE);
        } else {
            binding.emptyState.setVisibility(View.GONE);
            binding.recycler.setVisibility(View.VISIBLE);
            for (DatedResults group : list) {
                items.add(new DateItem(group.getGroupedDate()));
                for (Result result : group.getResultsList()) {
                    if (ResultExtensions.getStatus(result) == -1){
                        items.add(new ProgressItem(result, this, this));
                    } else if (result.countTotalMeasurements() == 0) {
                        items.add(new FailedItem(result, this, this));
                    } else {
                        switch (result.test_group_name) {
                            case WebsitesSuite.NAME:
                                items.add(new WebsiteItem(result, this, this));
                                break;
                            case InstantMessagingSuite.NAME:
                                items.add(new InstantMessagingItem(result, this, this));
                                break;
                            case MiddleBoxesSuite.NAME:
                                items.add(new MiddleboxesItem(result, this, this));
                                break;
                            case PerformanceSuite.NAME:
                                items.add(new PerformanceItem(result, this, this));
                                break;
                            case CircumventionSuite.NAME:
                                items.add(new CircumventionItem(result, this, this));
                                break;
                            case ExperimentalSuite.NAME:
                                items.add(new ExperimentalItem(result, this, this));
                                break;
                        }
                    }
                }
            }
            adapter.notifyTypesChanged();
        }
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
            }
            else if (i == DialogInterface.BUTTON_NEUTRAL) {
                startActivity(TextActivity.newIntent(getActivity(), TextActivity.TYPE_UPLOAD_LOG, (String) serializable));
            }
            else
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
