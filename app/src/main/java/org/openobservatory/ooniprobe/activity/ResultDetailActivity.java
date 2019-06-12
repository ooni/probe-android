package org.openobservatory.ooniprobe.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.ResubmitTask;
import org.openobservatory.ooniprobe.fragment.resultHeader.ResultHeaderDetailFragment;
import org.openobservatory.ooniprobe.fragment.resultHeader.ResultHeaderMiddleboxFragment;
import org.openobservatory.ooniprobe.fragment.resultHeader.ResultHeaderPerformanceFragment;
import org.openobservatory.ooniprobe.fragment.resultHeader.ResultHeaderTBAFragment;
import org.openobservatory.ooniprobe.item.MeasurementItem;
import org.openobservatory.ooniprobe.item.MeasurementPerfItem;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Network;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.model.database.Result_Table;
import org.openobservatory.ooniprobe.test.suite.InstantMessagingSuite;
import org.openobservatory.ooniprobe.test.suite.MiddleBoxesSuite;
import org.openobservatory.ooniprobe.test.suite.PerformanceSuite;
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import localhost.toolkit.app.fragment.ConfirmDialogFragment;
import localhost.toolkit.widget.recyclerview.HeterogeneousRecyclerAdapter;
import localhost.toolkit.widget.recyclerview.HeterogeneousRecyclerItem;

public class ResultDetailActivity extends AbstractActivity implements View.OnClickListener, ConfirmDialogFragment.OnConfirmedListener {
    private static final String ID = "id";
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.pager)
    ViewPager pager;
    @BindView(R.id.recyclerView)
    RecyclerView recycler;
    private ArrayList<HeterogeneousRecyclerItem> items;
    private HeterogeneousRecyclerAdapter<HeterogeneousRecyclerItem> adapter;
    private Result result;
    private Snackbar snackbar;

    public static Intent newIntent(Context context, int id) {
        return new Intent(context, ResultDetailActivity.class).putExtra(ID, id);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        result = SQLite.select().from(Result.class)
                .where(Result_Table.id.eq(getIntent().getIntExtra(ID, 0))).querySingle();
        assert result != null;
        setTheme(result.getTestSuite().getThemeLight());
        setContentView(R.layout.activity_result_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setTitle(result.getTestSuite().getTitle());
        }
        pager.setAdapter(new ResultHeaderAdapter());
        tabLayout.setupWithViewPager(pager);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(layoutManager);
        recycler.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));
        result.is_viewed = true;
        result.save();
        items = new ArrayList<>();
        adapter = new HeterogeneousRecyclerAdapter<>(this, items);
        recycler.setAdapter(adapter);
        snackbar = Snackbar.make(coordinatorLayout, R.string.Snackbar_ResultsSomeNotUploaded_Text, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.Snackbar_ResultsSomeNotUploaded_UploadAll, v1 -> runAsyncTask());
    }

    @Override
    protected void onResume() {
        super.onResume();
        load();
    }

    private void runAsyncTask() {
        new ResubmitAsyncTask(this).execute(result.id, null);
    }

    private void load() {
        result = SQLite.select().from(Result.class).where(Result_Table.id.eq(result.id)).querySingle();
        assert result != null;
        boolean isPerf = result.test_group_name.equals(PerformanceSuite.NAME);
        items.clear();
        for (Measurement measurement : result.getMeasurements())
            items.add(isPerf && !measurement.is_failed ?
                    new MeasurementPerfItem(measurement, this) :
                    new MeasurementItem(measurement, this));
        adapter.notifyTypesChanged();
        if (((Application) getApplication()).getPreferenceManager().isManualUploadResults() &&
                Measurement.selectUploadableWithResultId(result.id).count() != 0)
            snackbar.show();
        else
            snackbar.dismiss();
    }

    @Override
    public void onClick(View v) {
        Measurement measurement = (Measurement) v.getTag();
        ActivityCompat.startActivity(this, MeasurementDetailActivity.newIntent(this, measurement.id), null);
    }

    @Override
    public void onConfirmation(Serializable extra, int buttonClicked) {
        if (buttonClicked == DialogInterface.BUTTON_POSITIVE)
            runAsyncTask();
    }

    private static class ResubmitAsyncTask extends ResubmitTask<ResultDetailActivity> {
        ResubmitAsyncTask(ResultDetailActivity activity) {
            super(activity);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (getActivity() != null) {
                getActivity().result = SQLite.select().from(Result.class)
                        .where(Result_Table.id.eq(getActivity().result.id)).querySingle();
                getActivity().load();
                if (!result)
                    ConfirmDialogFragment.newInstance(null, getActivity().getString(R.string.Modal_UploadFailed_Title),
                            getActivity().getString(R.string.Modal_UploadFailed_Paragraph), null,
                            getActivity().getString(R.string.Modal_Retry), null, null
                    ).show(getActivity().getSupportFragmentManager(), null);
            }
        }
    }

    private class ResultHeaderAdapter extends FragmentPagerAdapter {
        ResultHeaderAdapter() {
            super(getSupportFragmentManager());
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 1)
                return ResultHeaderDetailFragment.newInstance(false, result.getFormattedDataUsageUp(), result.getFormattedDataUsageDown(), result.start_time, result.runtime, true, null, null);
            else if (position == 2)
                return ResultHeaderDetailFragment.newInstance(false, null, null, null, null, null, Network.getCountry(ResultDetailActivity.this, result.network), result.network);
            else switch (result.test_group_name) {
                    case WebsitesSuite.NAME:
                        return ResultHeaderTBAFragment.newInstance(result);
                    case InstantMessagingSuite.NAME:
                        return ResultHeaderTBAFragment.newInstance(result);
                    case MiddleBoxesSuite.NAME:
                        return ResultHeaderMiddleboxFragment.newInstance(result.countAnomalousMeasurements() > 0);
                    case PerformanceSuite.NAME:
                        return ResultHeaderPerformanceFragment.newInstance(result);
                    default:
                        return null;
                }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return "●";
        }
    }
}
