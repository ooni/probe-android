package org.openobservatory.ooniprobe.activity;

import static org.openobservatory.ooniprobe.activity.overview.OverviewViewModel.SELECT_ALL;
import static org.openobservatory.ooniprobe.activity.overview.OverviewViewModel.SELECT_NONE;
import static org.openobservatory.ooniprobe.activity.overview.OverviewViewModel.SELECT_SOME;
import static org.openobservatory.ooniprobe.common.PreferenceManagerExtensionKt.resolveStatus;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.text.TextUtilsCompat;
import androidx.core.view.ViewCompat;
import androidx.databinding.BindingAdapter;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.android.material.snackbar.Snackbar;

import org.openobservatory.engine.BaseNettest;
import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.customwebsites.CustomWebsiteActivity;
import org.openobservatory.ooniprobe.activity.overview.OverviewTestsExpandableListViewAdapter;
import org.openobservatory.ooniprobe.activity.overview.OverviewViewModel;
import org.openobservatory.ooniprobe.activity.overview.RevisionsFragment;
import org.openobservatory.ooniprobe.common.AppUpdatesViewModel;
import org.openobservatory.ooniprobe.activity.reviewdescriptorupdates.ReviewDescriptorUpdatesActivity;
import org.openobservatory.ooniprobe.common.AbstractDescriptor;
import org.openobservatory.ooniprobe.common.OONITests;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.ReadMorePlugin;
import org.openobservatory.ooniprobe.common.TestDescriptorManager;
import org.openobservatory.ooniprobe.common.ThirdPartyServices;
import org.openobservatory.ooniprobe.common.worker.ManualUpdateDescriptorsWorker;
import org.openobservatory.ooniprobe.databinding.ActivityOverviewBinding;
import org.openobservatory.ooniprobe.fragment.ConfirmDialogFragment;
import org.openobservatory.ooniprobe.model.database.InstalledDescriptor;
import org.openobservatory.ooniprobe.model.database.TestDescriptor;
import org.openobservatory.ooniprobe.test.test.WebConnectivity;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

import javax.inject.Inject;

import io.noties.markwon.Markwon;

public class OverviewActivity extends ReviewUpdatesAbstractActivity implements ConfirmDialogFragment.OnClickListener {
    private static final String TEST = "test";

    ActivityOverviewBinding binding;

    @Inject
    PreferenceManager preferenceManager;

    @Inject
    OverviewViewModel viewModel;

    @Inject
    AppUpdatesViewModel updatesViewModel;

    @Inject
    TestDescriptorManager testDescriptorManager;

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
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        onDescriptorLoaded(descriptor);

        binding.customUrl.setVisibility(descriptor.getName().equals(OONITests.WEBSITES.getLabel()) ? View.VISIBLE : View.GONE);

        binding.expandableListView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (binding.expandableListView.getChildCount() > 0) {
                if (adapter.isSelectedAllItems()) {
                    binding.switchTests.setImageResource(R.drawable.check_box);
                } else if (adapter.isNotSelectedAnyGroupItem()) {
                    binding.switchTests.setImageResource(R.drawable.check_box_outline_blank);
                } else {
                    binding.switchTests.setImageResource(R.drawable.check_box_indeterminate);
                }
            }
        });

        binding.switchTests.setOnClickListener( view -> {
            if (adapter.isSelectedAllItems()){
                viewModel.setSelectedAllBtnStatus(SELECT_NONE);
                adapter.notifyDataSetChanged();
            } else{
                viewModel.setSelectedAllBtnStatus(SELECT_ALL);
                adapter.notifyDataSetChanged();
            }
        });

        if (descriptor.getName().equals(OONITests.EXPERIMENTAL.getLabel())) {
            binding.switchTests.setImageResource(
                    resolveStatus(preferenceManager, descriptor.getName(), descriptor.preferencePrefix(), true)?
                            R.drawable.check_box : R.drawable.check_box_outline_blank
            );
        } else {
            if (adapter.isSelectedAllItems()) {
                binding.switchTests.setImageResource(R.drawable.check_box);
            } else if (adapter.isNotSelectedAnyGroupItem()) {
                binding.switchTests.setImageResource(R.drawable.check_box_outline_blank);
            } else {
                binding.switchTests.setImageResource(R.drawable.check_box_indeterminate);
            }
        }

        if (descriptor instanceof InstalledDescriptor installedDescriptor) {

            TestDescriptor testDescriptor = installedDescriptor.getTestDescriptor();

            if (Boolean.TRUE.equals(testDescriptor.expired())) {
                binding.expiredTag.getRoot().setVisibility(View.VISIBLE);
            }

            if (installedDescriptor.isUpdateAvailable()) {
                binding.updatedTag.getRoot().setVisibility(View.VISIBLE);

                binding.reviewUpdates.setVisibility(View.VISIBLE);
                binding.reviewUpdates.setOnClickListener(view -> getReviewUpdatesLauncher().launch(
                        ReviewDescriptorUpdatesActivity.newIntent(
                                OverviewActivity.this,
                                updatesViewModel.getUpdatedDescriptor(testDescriptor.getRunId())
                        )
                ));

            }

            binding.uninstallLink.setVisibility(View.VISIBLE);
            binding.automaticUpdatesContainer.setVisibility(View.VISIBLE);
            binding.automaticUpdatesSwitch.setChecked(installedDescriptor.getTestDescriptor().isAutoUpdate());

            if (BuildConfig.FLAVOR_brand.equals("dw")) {
                binding.uninstallLink.setVisibility(View.GONE);
                binding.revisionsContainer.setVisibility(View.GONE);
                binding.headerContainer.setVisibility(View.GONE);
                binding.automaticUpdatesContainer.setVisibility(View.GONE);
            }

        } else {
            binding.uninstallLink.setVisibility(View.GONE);
            /**
             * We need to set the height to 0 because the layout is broken when the view is gone
             */
            binding.automaticUpdatesContainer.setVisibility(View.GONE);
            binding.automaticUpdatesContainer.getLayoutParams().height = 0;
        }

        setUpOnCLickListeners();
        registerReviewLauncher(binding.getRoot(), () -> {
            binding.reviewUpdates.setVisibility(View.GONE);
            try {
                onDescriptorLoaded(
                        new InstalledDescriptor(testDescriptorManager.getById(viewModel.getDescriptor().getValue().getDescriptor().getRunId()), null)
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    private void onDescriptorLoaded(AbstractDescriptor<BaseNettest> descriptor) {
        setTitle(descriptor.getTitle());
        setThemeColor(descriptor.getColor());
        viewModel.updateDescriptor(descriptor);
        binding.executePendingBindings();

        adapter = new OverviewTestsExpandableListViewAdapter(descriptor.overviewExpandableListViewData(preferenceManager), viewModel);
        binding.expandableListView.setAdapter(adapter);
        // Expand all groups
        for (int i = 0; i < adapter.getGroupCount(); i++) {
            binding.expandableListView.expandGroup(i);
        }

        if (descriptor instanceof InstalledDescriptor installedDescriptor) {
            try {

                if (installedDescriptor.allTests().size() == 1 && installedDescriptor.allTests().get(0).getName().equals(WebConnectivity.NAME)) {
                    binding.expandableListView.setPadding(getResources().getDimensionPixelOffset(R.dimen.overview_test_group_list_padding_small),0,0,0);
                }
                if (Integer.parseInt(installedDescriptor.getTestDescriptor().getRevision()) > 1) {
                    getSupportFragmentManager().beginTransaction().replace(
                            binding.revisionsContainer.getId(),
                            RevisionsFragment.newInstance(
                                    installedDescriptor.getDescriptor().getRunId(),
                                    installedDescriptor.getDescriptor().getPreviousRevision()
                            )
                    ).commit();
                    if (!installedDescriptor.isUpdateAvailable()) {
                        binding.updatedTag.getRoot().setVisibility(View.GONE);
                        binding.reviewUpdates.setVisibility(View.GONE);
                        updatesViewModel.clearDescriptors();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        binding.uninstallLink.setOnClickListener(view -> {
            ConfirmDialogFragment.newInstance(
                            getString(R.string.Modal_CustomURL_Title_NotSaved),
                            getString(R.string.Dashboard_Runv2_Overview_Uninstall_Prompt),
                            getString(R.string.Dashboard_Runv2_Overview_UninstallLink),
                            getString(android.R.string.cancel),
                            null
                    )
                    .show(getSupportFragmentManager(), null);
        });
        binding.automaticUpdatesSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> viewModel.automaticUpdatesSwitchClicked(isChecked));
        if (descriptor instanceof InstalledDescriptor) {
            binding.swipeRefresh.setOnRefreshListener(() -> {
                Data.Builder data = new Data.Builder();
                data.putLongArray(ManualUpdateDescriptorsWorker.KEY_DESCRIPTOR_IDS, new long[]{Objects.requireNonNull(descriptor.getDescriptor()).getRunId()});
                OneTimeWorkRequest manualWorkRequest = new OneTimeWorkRequest.Builder(ManualUpdateDescriptorsWorker.class)
                        .setConstraints(
                                new Constraints.Builder()
                                        .setRequiredNetworkType(NetworkType.CONNECTED)
                                        .build()
                        ).setInputData(data.build())
                        .build();

                WorkManager.getInstance(this)
                        .beginUniqueWork(
                                ManualUpdateDescriptorsWorker.UPDATED_DESCRIPTORS_WORK_NAME,
                                ExistingWorkPolicy.REPLACE,
                                manualWorkRequest
                        ).enqueue();

                WorkManager.getInstance(this)
                        .getWorkInfoByIdLiveData(manualWorkRequest.getId())
                        .observe(this, this::onManualUpdatesFetchComplete);

            });
        } else {
            binding.swipeRefresh.setEnabled(false);
        }
    }


    /**
     * Listens to updates from the {@link ManualUpdateDescriptorsWorker}.
     * <p>
     * This method is called after the {@link ManualUpdateDescriptorsWorker} is enqueued.
     * The {@link ManualUpdateDescriptorsWorker} task is to fetch updates for the descriptors.
     * <p>
     * If the task is successful, the {@link WorkInfo} object will contain the updated descriptors.
     * Otherwise, the {@link WorkInfo} object will be null.
     *
     * @param workInfo The {@link WorkInfo} of the task.
     */
    private void onManualUpdatesFetchComplete(WorkInfo workInfo) {
        if (workInfo != null) {
            switch (workInfo.getState()) {
                case SUCCEEDED -> {

                    String descriptorString = workInfo.getOutputData().getString(ManualUpdateDescriptorsWorker.KEY_UPDATED_DESCRIPTORS);
                    if (descriptorString != null && !descriptorString.isEmpty()) {
                        if (descriptor.getDescriptor().isAutoUpdate()) {
                            testDescriptorManager.updateFromNetwork(descriptorString);
                            try {
                                onDescriptorLoaded(
                                        new InstalledDescriptor(testDescriptorManager.getById(viewModel.getDescriptor().getValue().getDescriptor().getRunId()), null)
                                );
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            binding.reviewUpdates.setVisibility(View.VISIBLE);
                            binding.reviewUpdates.setOnClickListener(view -> getReviewUpdatesLauncher().launch(
                                    ReviewDescriptorUpdatesActivity.newIntent(
                                            OverviewActivity.this,
                                            descriptorString
                                    )
                            ));
                        }
                    }
                    binding.swipeRefresh.setRefreshing(false);
                }

                case FAILED -> {
                    binding.swipeRefresh.setRefreshing(false);
                    Snackbar.make(
                            binding.getRoot(),
                            R.string.Modal_Error,
                            Snackbar.LENGTH_LONG
                    ).show();
                }

                default -> {
                }
            }
        }
    }

    @BindingAdapter({"resource"})
    public static void setImageViewResource(ImageView imageView, int resource) {
        imageView.setImageResource(resource);
    }


    @BindingAdapter({"dataUsage", "runTime"})
    public static void setDataUsage(TextView view, int dataUsage, String runTime) {
        Context context = view.getContext();
        view.setText(
                context.getString(
                        R.string.twoParam,
                        context.getString(dataUsage),
                        context.getString(R.string.Dashboard_Card_Seconds, runTime)
                )
        );
    }

    @BindingAdapter(value = {"richText", "testName"})
    public static void setRichText(TextView view, String richText, String testName) {
        try {
            Context context = view.getContext();
            Markwon markwon = Markwon.builder(context)
                    .usePlugin(new ReadMorePlugin(context.getString(R.string.OONIRun_ReadMore), context.getString(R.string.OONIRun_ReadLess), 400))
                    .build();
            if (Objects.equals(testName, OONITests.EXPERIMENTAL.name())) {
                markwon.setMarkdown(view, richText);
                if (TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                    view.setTextDirection(View.TEXT_DIRECTION_RTL);
                }
            } else {
                markwon.setMarkdown(view, richText);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ThirdPartyServices.logException(e);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    void customUrlClick() {
        startActivity(new Intent(this, CustomWebsiteActivity.class));
    }

    @Override
    public void onConfirmDialogClick(@Nullable Serializable serializable, @Nullable Parcelable parcelable, int buttonClicked) {
        if (buttonClicked == DialogInterface.BUTTON_POSITIVE) {
            viewModel.uninstallLinkClicked(this, (InstalledDescriptor) descriptor);
        }
    }
}
