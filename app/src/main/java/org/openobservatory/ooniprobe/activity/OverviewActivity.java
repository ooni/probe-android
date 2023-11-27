package org.openobservatory.ooniprobe.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.text.TextUtilsCompat;
import androidx.core.view.ViewCompat;

import org.openobservatory.engine.BaseNettest;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.overview.OverviewTestsExpandableListViewAdapter;
import org.openobservatory.ooniprobe.activity.overview.OverviewViewModel;
import org.openobservatory.ooniprobe.common.OONIDescriptor;
import org.openobservatory.ooniprobe.common.OONITests;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.ReadMorePlugin;
import org.openobservatory.ooniprobe.databinding.ActivityOverviewBinding;
import org.openobservatory.ooniprobe.model.database.Result;

import java.util.Locale;

import javax.inject.Inject;

import io.noties.markwon.Markwon;

public class OverviewActivity extends AbstractActivity {
    private static final String TEST = "test";

    ActivityOverviewBinding binding;

    @Inject
    PreferenceManager preferenceManager;

    @Inject
    OverviewViewModel viewModel;

    private OONIDescriptor<BaseNettest> descriptor;

    public static Intent newIntent(Context context, OONIDescriptor<BaseNettest> descriptor) {
        return new Intent(context, OverviewActivity.class).putExtra(TEST, descriptor);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        descriptor = (OONIDescriptor) getIntent().getSerializableExtra(TEST);
        binding = ActivityOverviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel.updateDescriptor(descriptor);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(descriptor.getTitle());
        setThemeColor(ContextCompat.getColor(this, descriptor.getColor()));
        binding.icon.setImageResource(descriptor.getDisplayIcon(this));
        binding.customUrl.setVisibility(descriptor.getName().equals(OONITests.WEBSITES.name()) ? View.VISIBLE : View.GONE);
        Markwon markwon = Markwon.builder(this).usePlugin(new ReadMorePlugin(getString(R.string.OONIRun_ReadMore), getString(R.string.OONIRun_ReadLess))).build();
        if (descriptor.getName().equals(OONITests.EXPERIMENTAL.name())) {
            markwon.setMarkdown(binding.desc, descriptor.getDescription());
            if (TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                binding.desc.setTextDirection(View.TEXT_DIRECTION_RTL);
            }
        } else {
            markwon.setMarkdown(binding.desc, descriptor.getDescription());
        }
        Result lastResult = Result.getLastResult(descriptor.getName());
        if (lastResult == null) {
            binding.lastTime.setText(R.string.Dashboard_Overview_LastRun_Never);
        } else {
            binding.lastTime.setText(DateUtils.getRelativeTimeSpanString(lastResult.start_time.getTime()));
        }

        OverviewTestsExpandableListViewAdapter adapter = new OverviewTestsExpandableListViewAdapter(descriptor.getOverviewExpandableListViewData());
        binding.expandableListView.setAdapter(adapter);
        // Expand all groups
        for (int i = 0; i < adapter.getGroupCount(); i++) {
            binding.expandableListView.expandGroup(i);
        }

        setUpOnCLickListeners();
    }

    public void setThemeColor(int color) {
        Window window = getWindow();
        window.setStatusBarColor(color);
        binding.toolbar.setBackgroundColor(color);
        binding.appbarLayout.setBackgroundColor(color);
        binding.collapsingToolbar.setBackgroundColor(color);
    }

    private void setUpOnCLickListeners() {
        binding.customUrl.setOnClickListener(view -> customUrlClick());
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.runtime.setText(getString(R.string.twoParam, getString(descriptor.getDataUsage()), getString(R.string.Dashboard_Card_Seconds, String.valueOf(descriptor.getRuntime(this, preferenceManager)))));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    void customUrlClick() {
        startActivity(new Intent(this, CustomWebsiteActivity.class));
    }
}
