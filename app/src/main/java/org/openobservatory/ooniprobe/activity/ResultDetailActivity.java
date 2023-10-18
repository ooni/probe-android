package org.openobservatory.ooniprobe.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayoutMediator;
import localhost.toolkit.app.fragment.ConfirmDialogFragment;
import localhost.toolkit.widget.recyclerview.HeterogeneousRecyclerAdapter;
import localhost.toolkit.widget.recyclerview.HeterogeneousRecyclerItem;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.ResubmitTask;
import org.openobservatory.ooniprobe.databinding.ActivityResultDetailBinding;
import org.openobservatory.ooniprobe.domain.GetResults;
import org.openobservatory.ooniprobe.domain.GetTestSuite;
import org.openobservatory.ooniprobe.fragment.resultHeader.ResultHeaderDetailFragment;
import org.openobservatory.ooniprobe.fragment.resultHeader.ResultHeaderMiddleboxFragment;
import org.openobservatory.ooniprobe.fragment.resultHeader.ResultHeaderPerformanceFragment;
import org.openobservatory.ooniprobe.fragment.resultHeader.ResultHeaderTBAFragment;
import org.openobservatory.ooniprobe.item.MeasurementItem;
import org.openobservatory.ooniprobe.item.MeasurementPerfItem;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Network;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.test.suite.*;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ResultDetailActivity extends AbstractActivity implements View.OnClickListener, ConfirmDialogFragment.OnConfirmedListener {
    private static final String ID = "id";
    private static final String UPLOAD_KEY = "upload";
    private static final String RERUN_KEY = "rerun";

    private ArrayList<HeterogeneousRecyclerItem> items;
    private HeterogeneousRecyclerAdapter<HeterogeneousRecyclerItem> adapter;
    private Result result;
    private Snackbar snackbar;

    @Inject
    GetTestSuite getTestSuite;

    @Inject
    GetResults getResults;

    @Inject
    PreferenceManager preferenceManager;

    public static Intent newIntent(Context context, int id) {
        return new Intent(context, ResultDetailActivity.class).putExtra(ID, id);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        result = getResults.get(getIntent().getIntExtra(ID, 0));
        assert result != null;
        setTheme(result.getTestSuite().getThemeLight());
        ActivityResultDetailBinding binding = ActivityResultDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setTitle(result.getTestSuite().getTitle());
        }
        binding.pager.setAdapter(new ResultHeaderAdapter(this));
        new TabLayoutMediator(binding.tabLayout, binding.pager, (tab, position) ->
                tab.setText("●")
        ).attach();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));
        result.is_viewed = true;
        result.save();
        items = new ArrayList<>();
        adapter = new HeterogeneousRecyclerAdapter<>(this, items);
        binding.recyclerView.setAdapter(adapter);
        snackbar = Snackbar.make(binding.coordinatorLayout, R.string.Snackbar_ResultsSomeNotUploaded_Text, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.Snackbar_ResultsSomeNotUploaded_UploadAll, v1 -> runAsyncTask());
    }

    @Override
    protected void onResume() {
        super.onResume();
        load();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rerun, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        invalidateOptionsMenu();
        if (!result.test_group_name.equals(WebsitesSuite.NAME))
            menu.findItem(R.id.reRun).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reRun:
                new ConfirmDialogFragment.Builder()
                        .withExtra(RERUN_KEY)
                        .withMessage(getString(R.string.Modal_ReRun_Websites_Title, String.valueOf(result.getMeasurements().size())))
                        .withPositiveButton(getString(R.string.Modal_ReRun_Websites_Run))
                        .build().show(getSupportFragmentManager(), null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void reTestWebsites() {
        RunningActivity.runAsForegroundService(this, getTestSuite.getFrom(result).asArray(),this::finish, preferenceManager);
    }

    private void runAsyncTask() {
        new ResubmitAsyncTask(this).execute(result.id, null);
    }

    private void load() {
        result = getResults.get(result.id);
        assert result != null;
        boolean isPerf = result.test_group_name.equals(PerformanceSuite.NAME);
        items.clear();
        List<Measurement> measurements = result.getMeasurementsSorted();
        for (Measurement measurement : measurements)
            items.add(isPerf && !measurement.is_failed ?
                    new MeasurementPerfItem(measurement, this) :
                    new MeasurementItem(measurement, this));
        adapter.notifyTypesChanged();
        if (Measurement.hasReport(this, Measurement.selectUploadableWithResultId(result.id)))
            snackbar.show();
        else
            snackbar.dismiss();
    }

    @Override
    public void onClick(View v) {
        Measurement measurement = (Measurement) v.getTag();
        if (result.test_group_name.equals(ExperimentalSuite.NAME))
            startActivity(TextActivity.newIntent(this, TextActivity.TYPE_JSON, measurement));
        else
            ActivityCompat.startActivity(this, MeasurementDetailActivity.newIntent(this, measurement.id), null);
    }

    @Override
    public void onConfirmation(Serializable extra, int buttonClicked) {
        if (buttonClicked == DialogInterface.BUTTON_POSITIVE && extra.equals(UPLOAD_KEY))
            runAsyncTask();
        else if (buttonClicked == DialogInterface.BUTTON_POSITIVE && extra.equals(RERUN_KEY))
            reTestWebsites();
        else if (buttonClicked == DialogInterface.BUTTON_NEUTRAL)
            startActivity(TextActivity.newIntent(this, TextActivity.TYPE_UPLOAD_LOG, (String)extra));
    }

    private static class ResubmitAsyncTask extends ResubmitTask<ResultDetailActivity> {
        ResubmitAsyncTask(ResultDetailActivity activity) {
            super(activity, activity.preferenceManager.getProxyURL());
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (getActivity() != null) {
                getActivity().result = d.getResults.get(getActivity().result.id);
                getActivity().load();
                if (!result)
                    new ConfirmDialogFragment.Builder()
                            .withExtra(UPLOAD_KEY)
                            .withTitle(getActivity().getString(R.string.Modal_UploadFailed_Title))
                            .withMessage(getActivity().getString(R.string.Modal_UploadFailed_Paragraph, errors.toString(), totUploads.toString()))
                            .withPositiveButton(getActivity().getString(R.string.Modal_Retry))
                            .withNeutralButton(getActivity().getString(R.string.Modal_DisplayFailureLog))
                            .withExtra(String.join("\n", logger.logs))
                            .build().show(getActivity().getSupportFragmentManager(), null);
            }
        }
    }

    private class ResultHeaderAdapter extends FragmentStateAdapter {
        ResultHeaderAdapter(final FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            if (result.test_group_name.equals(ExperimentalSuite.NAME)){
                if (position == 0)
                    return ResultHeaderDetailFragment.newInstance(false, result.getFormattedDataUsageUp(), result.getFormattedDataUsageDown(), result.start_time, result.getRuntime(), true, null, null);
                else if (position == 1)
                    return ResultHeaderDetailFragment.newInstance(false, null, null, null, null, null, Network.getCountry(ResultDetailActivity.this, result.network), result.network);
            }
            if (position == 1)
                return ResultHeaderDetailFragment.newInstance(false, result.getFormattedDataUsageUp(), result.getFormattedDataUsageDown(), result.start_time, result.getRuntime(), true, null, null);
            else if (position == 2)
                return ResultHeaderDetailFragment.newInstance(false, null, null, null, null, null, Network.getCountry(ResultDetailActivity.this, result.network), result.network);
            else switch (result.test_group_name) {
                    default: //Default can no longer be null, so we have to default to something...
                        // NOTE: Perhaps set up a test page?
                    case WebsitesSuite.NAME:
                        return ResultHeaderTBAFragment.newInstance(result);
                    case InstantMessagingSuite.NAME:
                        return ResultHeaderTBAFragment.newInstance(result);
                    case MiddleBoxesSuite.NAME:
                        return ResultHeaderMiddleboxFragment.newInstance(result.countAnomalousMeasurements() > 0);
                    case PerformanceSuite.NAME:
                        return ResultHeaderPerformanceFragment.newInstance(result);
                    case CircumventionSuite.NAME:
                        return ResultHeaderTBAFragment.newInstance(result);
                }
        }


        @Override
        public int getItemCount() {
            if (result.test_group_name.equals(ExperimentalSuite.NAME))
                return 2;
            return 3;
        }
    }
}
