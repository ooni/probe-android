package org.openobservatory.ooniprobe.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.databinding.ItemCircumventionBinding;
import org.openobservatory.ooniprobe.databinding.ItemExperimentalBinding;
import org.openobservatory.ooniprobe.databinding.ItemFailedBinding;
import org.openobservatory.ooniprobe.databinding.ItemInstantmessagingBinding;
import org.openobservatory.ooniprobe.databinding.ItemMiddleboxesBinding;
import org.openobservatory.ooniprobe.databinding.ItemPerformanceBinding;
import org.openobservatory.ooniprobe.databinding.ItemWebsitesBinding;
import org.openobservatory.ooniprobe.fragment.resultHeader.ResultHeaderPerformanceFragment;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Network;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.test.suite.CircumventionSuite;
import org.openobservatory.ooniprobe.test.suite.ExperimentalSuite;
import org.openobservatory.ooniprobe.test.suite.InstantMessagingSuite;
import org.openobservatory.ooniprobe.test.suite.MiddleBoxesSuite;
import org.openobservatory.ooniprobe.test.suite.PerformanceSuite;
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite;
import org.openobservatory.ooniprobe.test.test.Dash;
import org.openobservatory.ooniprobe.test.test.Ndt;

import java.util.Locale;
import java.util.Objects;

public class ResultListAdapter extends PagingDataAdapter<Result, ResultListAdapter.ViewHolder> {
    private final View.OnClickListener onClickListener;
    private final View.OnLongClickListener onLongClickListener;

    public ResultListAdapter(@NonNull DiffUtil.ItemCallback<Result> diffCallback, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener) {
        super(diffCallback);
        this.onClickListener = onClickListener;
        this.onLongClickListener = onLongClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewBinding binding;
        switch (viewType) {
            case -1:
                binding = ItemFailedBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                break;
            case 0:
                binding = ItemWebsitesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                break;
            case 1:
                binding = ItemInstantmessagingBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                break;
            case 2:
                binding = ItemMiddleboxesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                break;
            case 3:
                binding = ItemPerformanceBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                break;
            case 4:
                binding = ItemCircumventionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                break;
            case 5:
            default:
                binding = ItemExperimentalBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                break;
        }
        return new ViewHolder<>(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            Result result = Objects.requireNonNull(getItem(position));
            holder.itemView.setTag(result);
            holder.itemView.setOnClickListener(onClickListener);
            holder.itemView.setOnLongClickListener(onLongClickListener);
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), result.is_viewed ? android.R.color.transparent : R.color.color_yellow0));
            Context context = holder.itemView.getContext();
            switch (holder.getItemViewType()) {
                case -1: {
                    ViewHolder<ItemFailedBinding> vHolder = (ViewHolder<ItemFailedBinding>) holder;
                    vHolder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.color_gray2));
                    vHolder.binding.testName.setTextColor(ContextCompat.getColor(context, R.color.color_gray6));
                    vHolder.binding.icon.setImageResource(result.getTestSuite().getIcon());
                    vHolder.binding.testName.setText(result.getTestSuite().getTitle());
                    String failure_msg = context.getString(R.string.TestResults_Overview_Error);
                    if (result.failure_msg != null)
                        failure_msg += " - " + result.failure_msg;
                    vHolder.binding.subtitle.setText(failure_msg);
                    vHolder.binding.startTime.setText(DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), "yMdHm"), result.start_time));
                }
                break;
                case 0: {

                    ViewHolder<ItemWebsitesBinding> websitesBinding = (ViewHolder<ItemWebsitesBinding>) holder;
                    websitesBinding.binding.asnName.setText(Network.toString(websitesBinding.binding.asnName.getContext(), result.network));
                    websitesBinding.binding.startTime.setText(DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), "yMdHm"), result.start_time));

                    Long blocked = result.countAnomalousMeasurements();
                    Long tested = result.countTotalMeasurements();
                    websitesBinding.binding.failedMeasurements.setText(websitesBinding.binding.failedMeasurements.getContext().getResources().getQuantityString(R.plurals.TestResults_Overview_Websites_Blocked, blocked.intValue(), blocked.toString()));
                    websitesBinding.binding.testedMeasurements.setText(websitesBinding.binding.failedMeasurements.getContext().getResources().getQuantityString(R.plurals.TestResults_Overview_Websites_Tested, tested.intValue(), tested.toString()));
                    websitesBinding.binding.failedMeasurements.setTextColor(ContextCompat.getColor(websitesBinding.binding.failedMeasurements.getContext(), blocked == 0 ? R.color.color_gray9 : R.color.color_yellow9));
                    DrawableCompat.setTint(DrawableCompat.wrap(websitesBinding.binding.failedMeasurements.getCompoundDrawablesRelative()[0]).mutate(), ContextCompat.getColor(websitesBinding.binding.failedMeasurements.getContext(), blocked == 0 ? R.color.color_gray9 : R.color.color_yellow9));
                    boolean allUploaded = true;
                    for (Measurement m : result.getMeasurements())
                        allUploaded = allUploaded && (m.isUploaded() || m.is_failed);
                    websitesBinding.binding.startTime.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, allUploaded ? 0 : R.drawable.cloudoff, 0);

                }
                break;
                case 1: {
                    ViewHolder<ItemInstantmessagingBinding> iBinding = (ViewHolder<ItemInstantmessagingBinding>) holder;
                    iBinding.binding.asnName.setText(Network.toString(iBinding.binding.asnName.getContext(), result.network));
                    iBinding.binding.startTime.setText(DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), "yMdHm"), result.start_time));
                    Long blocked = result.countAnomalousMeasurements();
                    Long available = result.countOkMeasurements();
                    iBinding.binding.failedMeasurements.setText(iBinding.binding.failedMeasurements.getContext().getResources().getQuantityString(R.plurals.TestResults_Overview_InstantMessaging_Blocked, blocked.intValue(), blocked.toString()));
                    iBinding.binding.okMeasurements.setText(iBinding.binding.failedMeasurements.getContext().getResources().getQuantityString(R.plurals.TestResults_Overview_InstantMessaging_Available, available.intValue(), available.toString()));
                    iBinding.binding.failedMeasurements.setTextColor(ContextCompat.getColor(iBinding.binding.failedMeasurements.getContext(), blocked == 0 ? R.color.color_gray9 : R.color.color_yellow9));
                    DrawableCompat.setTint(DrawableCompat.wrap(iBinding.binding.failedMeasurements.getCompoundDrawablesRelative()[0]).mutate(), ContextCompat.getColor(iBinding.binding.failedMeasurements.getContext(), blocked == 0 ? R.color.color_gray9 : R.color.color_yellow9));
                    boolean allUploaded = true;
                    for (Measurement m : result.getMeasurements())
                        allUploaded = allUploaded && (m.isUploaded() || m.is_failed);
                    iBinding.binding.startTime.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, allUploaded ? 0 : R.drawable.cloudoff, 0);
                }
                break;
                case 2: {
                    ViewHolder<ItemMiddleboxesBinding> mBinding = (ViewHolder<ItemMiddleboxesBinding>) holder;
                    mBinding.binding.asnName.setText(Network.toString(mBinding.binding.asnName.getContext(), result.network));
                    mBinding.binding.startTime.setText(DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), "yMdHm"), result.start_time));
                    if (result.countAnomalousMeasurements() > 0) {
                        mBinding.binding.status.setText(R.string.TestResults_Overview_MiddleBoxes_Found);
                        mBinding.binding.status.setTextColor(ContextCompat.getColor(mBinding.binding.status.getContext(), R.color.color_yellow9));
                        DrawableCompat.setTint(DrawableCompat.wrap(mBinding.binding.status.getCompoundDrawablesRelative()[0]).mutate(), ContextCompat.getColor(mBinding.binding.status.getContext(), R.color.color_yellow9));
                    } else if (result.countCompletedMeasurements() == 0) {
                        mBinding.binding.status.setText(R.string.TestResults_Overview_MiddleBoxes_Failed);
                        mBinding.binding.status.setTextColor(ContextCompat.getColor(mBinding.binding.status.getContext(), R.color.color_gray9));
                        DrawableCompat.setTint(DrawableCompat.wrap(mBinding.binding.status.getCompoundDrawablesRelative()[0]).mutate(), ContextCompat.getColor(mBinding.binding.status.getContext(), R.color.color_gray9));
                    } else {
                        mBinding.binding.status.setText(R.string.TestResults_Overview_MiddleBoxes_NotFound);
                        mBinding.binding.status.setTextColor(ContextCompat.getColor(mBinding.binding.status.getContext(), R.color.color_gray9));
                        DrawableCompat.setTint(DrawableCompat.wrap(mBinding.binding.status.getCompoundDrawablesRelative()[0]).mutate(), ContextCompat.getColor(mBinding.binding.status.getContext(), R.color.color_gray9));
                    }
                    boolean allUploaded = true;
                    for (Measurement m : result.getMeasurements())
                        allUploaded = allUploaded && (m.isUploaded() || m.is_failed);
                    mBinding.binding.startTime.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, allUploaded ? 0 : R.drawable.cloudoff, 0);

                }
                break;
                case 3: {
                    ViewHolder<ItemPerformanceBinding> pBinding = (ViewHolder<ItemPerformanceBinding>) holder;
                    pBinding.binding.asnName.setText(Network.toString(pBinding.binding.asnName.getContext(), result.network));
                    pBinding.binding.startTime.setText(DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), "yMdHm"), result.start_time));
                    Measurement dashM = result.getMeasurement(Dash.NAME);
                    Measurement ndtM = result.getMeasurement(Ndt.NAME);
                    pBinding.binding.quality.setText(dashM == null ? R.string.TestResults_NotAvailable : dashM.getTestKeys().getVideoQuality(false));
                    pBinding.binding.upload.setText(ndtM == null ? context.getString(R.string.TestResults_NotAvailable) : context.getString(R.string.twoParam, ndtM.getTestKeys().getUpload(context), context.getString(ndtM.getTestKeys().getUploadUnit())));
                    pBinding.binding.download.setText(ndtM == null ? context.getString(R.string.TestResults_NotAvailable) : context.getString(R.string.twoParam, ndtM.getTestKeys().getDownload(context), context.getString(ndtM.getTestKeys().getDownloadUnit())));
                    pBinding.binding.quality.setAlpha(dashM == null ? ResultHeaderPerformanceFragment.ALPHA_DIS : ResultHeaderPerformanceFragment.ALPHA_ENA);
                    pBinding.binding.upload.setAlpha(ndtM == null ? ResultHeaderPerformanceFragment.ALPHA_DIS : ResultHeaderPerformanceFragment.ALPHA_ENA);
                    pBinding.binding.download.setAlpha(ndtM == null ? ResultHeaderPerformanceFragment.ALPHA_DIS : ResultHeaderPerformanceFragment.ALPHA_ENA);
                    boolean allUploaded = true;
                    for (Measurement m : result.getMeasurements())
                        allUploaded = allUploaded && (m.isUploaded() || m.is_failed);
                    pBinding.binding.startTime.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, allUploaded ? 0 : R.drawable.cloudoff, 0);

                }
                break;
                case 4: {
                    ViewHolder<ItemCircumventionBinding> cBinding = (ViewHolder<ItemCircumventionBinding>) holder;
                    cBinding.binding.asnName.setText(Network.toString(cBinding.binding.asnName.getContext(), result.network));
                    cBinding.binding.startTime.setText(DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), "yMdHm"), result.start_time));
                    Long blocked = result.countAnomalousMeasurements();
                    Long available = result.countOkMeasurements();
                    cBinding.binding.failedMeasurements.setText(cBinding.binding.failedMeasurements.getContext().getResources().getQuantityString(R.plurals.TestResults_Overview_Circumvention_Blocked, blocked.intValue(), blocked.toString()));
                    cBinding.binding.okMeasurements.setText(cBinding.binding.failedMeasurements.getContext().getResources().getQuantityString(R.plurals.TestResults_Overview_Circumvention_Available, available.intValue(), available.toString()));
                    cBinding.binding.failedMeasurements.setTextColor(ContextCompat.getColor(cBinding.binding.failedMeasurements.getContext(), blocked == 0 ? R.color.color_gray9 : R.color.color_yellow9));
                    DrawableCompat.setTint(DrawableCompat.wrap(cBinding.binding.failedMeasurements.getCompoundDrawablesRelative()[0]).mutate(), ContextCompat.getColor(cBinding.binding.failedMeasurements.getContext(), blocked == 0 ? R.color.color_gray9 : R.color.color_yellow9));
                    boolean allUploaded = true;
                    for (Measurement m : result.getMeasurements())
                        allUploaded = allUploaded && (m.isUploaded() || m.is_failed);
                    cBinding.binding.startTime.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, allUploaded ? 0 : R.drawable.cloudoff, 0);

                }
                break;
                case 5:
                default: {
                    ViewHolder<ItemExperimentalBinding> defaultHolder = (ViewHolder<ItemExperimentalBinding>) holder;
                    defaultHolder.binding.asnName.setText(Network.toString(defaultHolder.binding.asnName.getContext(), result.network));
                    boolean allUploaded = true;
                    for (Measurement m : result.getMeasurements())
                        allUploaded = allUploaded && (m.isUploaded() || m.is_failed);
                    defaultHolder.binding.startTime.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, allUploaded ? 0 : R.drawable.cloudoff, 0);
                }
                break;
            }
        } catch (Exception e) {

        }
    }

    @Override
    public int getItemViewType(int position) {
        Result result = Objects.requireNonNull(getItem(position));
        if (result.countTotalMeasurements() == 0) {
            return -1;
        }
        switch (result.test_group_name) {
            case WebsitesSuite.NAME:
                return 0;
            case InstantMessagingSuite.NAME:
                return 1;
            case MiddleBoxesSuite.NAME:
                return 2;
            case PerformanceSuite.NAME:
                return 3;
            case CircumventionSuite.NAME:
                return 4;
            case ExperimentalSuite.NAME:
            default:
                return 5;
        }
    }

    public static class ViewHolder<T extends ViewBinding> extends RecyclerView.ViewHolder {
        private final T binding;

        public ViewHolder(@NonNull T binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
