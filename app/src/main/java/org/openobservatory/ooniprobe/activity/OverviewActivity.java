package org.openobservatory.ooniprobe.activity;

import static org.openobservatory.ooniprobe.activity.overview.OverviewViewModel.SELECT_ALL;
import static org.openobservatory.ooniprobe.activity.overview.OverviewViewModel.SELECT_NONE;
import static org.openobservatory.ooniprobe.activity.overview.OverviewViewModel.SELECT_SOME;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.core.text.TextUtilsCompat;
import androidx.core.view.ViewCompat;

import com.google.android.material.checkbox.MaterialCheckBox;

import org.openobservatory.engine.BaseNettest;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.customwebsites.CustomWebsiteActivity;
import org.openobservatory.ooniprobe.activity.overview.OverviewTestsExpandableListViewAdapter;
import org.openobservatory.ooniprobe.activity.overview.OverviewViewModel;
import org.openobservatory.ooniprobe.common.AbstractDescriptor;
import org.openobservatory.ooniprobe.common.OONITests;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.ReadMorePlugin;
import org.openobservatory.ooniprobe.databinding.ActivityOverviewBinding;
import org.openobservatory.ooniprobe.model.database.InstalledDescriptor;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.model.database.TestDescriptor;

import java.text.SimpleDateFormat;
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

    OverviewTestsExpandableListViewAdapter adapter;

    private AbstractDescriptor<BaseNettest> descriptor;

    public static Intent newIntent(Context context, AbstractDescriptor<BaseNettest> descriptor) {
        return new Intent(context, OverviewActivity.class).putExtra(TEST, descriptor);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        descriptor = (AbstractDescriptor) getIntent().getSerializableExtra(TEST);
        binding = ActivityOverviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel.updateDescriptor(descriptor);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(descriptor.getTitle());
        setThemeColor(descriptor.getColor());
        binding.icon.setImageResource(descriptor.getDisplayIcon(this));
        binding.customUrl.setVisibility(descriptor.getName().equals(OONITests.WEBSITES.getLabel()) ? View.VISIBLE : View.GONE);
        Markwon markwon = Markwon.builder(this).usePlugin(new ReadMorePlugin(getString(R.string.OONIRun_ReadMore), getString(R.string.OONIRun_ReadLess))).build();
        if (descriptor.getName().equals(OONITests.EXPERIMENTAL.name())) {
            markwon.setMarkdown(binding.desc, descriptor.getDescription());
            if (TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                binding.desc.setTextDirection(View.TEXT_DIRECTION_RTL);
            }
        } else {
            if (descriptor instanceof InstalledDescriptor) {
                TestDescriptor testDescriptor = ((InstalledDescriptor) descriptor).getTestDescriptor();
                markwon.setMarkdown(
                        binding.desc,
                        String.format(
                                "Created by %s on %s\n\n%s",
                                testDescriptor.getAuthor(),
                                new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH).format(testDescriptor.getDescriptorCreationTime()),
                                descriptor.getDescription()
                        )
                );
            } else {
                markwon.setMarkdown(binding.desc, descriptor.getDescription());
            }
        }
        Result lastResult = Result.getLastResult(descriptor.getName());
        if (lastResult == null) {
            binding.lastTime.setText(R.string.Dashboard_Overview_LastRun_Never);
        } else {
            binding.lastTime.setText(DateUtils.getRelativeTimeSpanString(lastResult.start_time.getTime()));
        }

        adapter = new OverviewTestsExpandableListViewAdapter(descriptor.overviewExpandableListViewData(preferenceManager), viewModel);
        binding.expandableListView.setAdapter(adapter);

        viewModel.getSelectedAllBtnStatus().observe(this, this::selectAllBtnStatusObserver);
        binding.switchTests.addOnCheckedStateChangedListener((checkBox, state) -> {
            switch (state) {
                case MaterialCheckBox.STATE_CHECKED -> {
                    viewModel.setSelectedAllBtnStatus(SELECT_ALL);
                    adapter.notifyDataSetChanged();
                }
                case MaterialCheckBox.STATE_UNCHECKED -> {
                    viewModel.setSelectedAllBtnStatus(SELECT_NONE);
                    adapter.notifyDataSetChanged();
                }
                case MaterialCheckBox.STATE_INDETERMINATE -> {
                    viewModel.setSelectedAllBtnStatus(SELECT_SOME);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        if (adapter.isSelectedAllItems()) {
            binding.switchTests.setCheckedState(MaterialCheckBox.STATE_CHECKED);
        } else if (adapter.isNotSelectedAnyGroupItem()) {
            binding.switchTests.setCheckedState(MaterialCheckBox.STATE_UNCHECKED);
        } else {
            binding.switchTests.setCheckedState(MaterialCheckBox.STATE_INDETERMINATE);
        }
        binding.switchTests.setEnabled(descriptor.hasPreferencePrefix());
        // Expand all groups
        for (int i = 0; i < adapter.getGroupCount(); i++) {
            binding.expandableListView.expandGroup(i);
        }

        if (descriptor instanceof InstalledDescriptor) {
            binding.uninstallLink.setVisibility(View.VISIBLE);
            binding.automaticUpdatesContainer.setVisibility(View.VISIBLE);
            binding.automaticUpdatesSwitch.setChecked(((InstalledDescriptor) descriptor).getTestDescriptor().isAutoUpdate());
        } else {
            binding.uninstallLink.setVisibility(View.GONE);
            /**
             * We need to set the height to 0 because the layout is broken when the view is gone
             */
            binding.automaticUpdatesContainer.getLayoutParams().height = 0;
        }

        setUpOnCLickListeners();
    }

    private void selectAllBtnStatusObserver(String selectAllBtnStatus) {
        if (!TextUtils.isEmpty(selectAllBtnStatus)) {
            switch (selectAllBtnStatus) {
                case SELECT_ALL -> {
                    binding.switchTests.setCheckedState(MaterialCheckBox.STATE_CHECKED);
                }
                case SELECT_NONE -> {
                    binding.switchTests.setCheckedState(MaterialCheckBox.STATE_UNCHECKED);
                }
                case SELECT_SOME -> {
                    binding.switchTests.setCheckedState(MaterialCheckBox.STATE_INDETERMINATE);
                }
            }
            adapter.notifyDataSetChanged();
        }
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
        binding.uninstallLink.setOnClickListener(view -> viewModel.uninstallLinkClicked(this, (InstalledDescriptor) descriptor));
        binding.automaticUpdatesSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> viewModel.automaticUpdatesSwitchClicked(isChecked));
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
