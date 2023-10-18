package org.openobservatory.ooniprobe.item;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import localhost.toolkit.widget.recyclerview.HeterogeneousRecyclerItem;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.databinding.ItemPerformanceBinding;
import org.openobservatory.ooniprobe.fragment.resultHeader.ResultHeaderPerformanceFragment;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Network;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.test.test.Dash;
import org.openobservatory.ooniprobe.test.test.Ndt;

import java.util.Locale;

public class PerformanceItem extends HeterogeneousRecyclerItem<Result, PerformanceItem.ViewHolder> {
	private final View.OnClickListener onClickListener;
	private final View.OnLongClickListener onLongClickListener;

	public PerformanceItem(Result extra, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener) {
		super(extra);
		this.onClickListener = onClickListener;
		this.onLongClickListener = onLongClickListener;
	}

	@Override public ViewHolder onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup) {
		return new ViewHolder(ItemPerformanceBinding.inflate(layoutInflater, viewGroup, false));
	}

	@Override public void onBindViewHolder(ViewHolder viewHolder) {
		Context c = viewHolder.itemView.getContext();
		viewHolder.itemView.setTag(extra);
		viewHolder.itemView.setOnClickListener(onClickListener);
		viewHolder.itemView.setOnLongClickListener(onLongClickListener);
		viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(c, extra.is_viewed ? android.R.color.transparent : R.color.color_yellow0));
		viewHolder.binding.asnName.setText(Network.toString(viewHolder.binding.asnName.getContext(), extra.network));
		viewHolder.binding.startTime.setText(DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), "yMdHm"), extra.start_time));
		Measurement dashM = extra.getMeasurement(Dash.NAME);
		Measurement ndtM = extra.getMeasurement(Ndt.NAME);
		viewHolder.binding.quality.setText(dashM == null ? R.string.TestResults_NotAvailable : dashM.getTestKeys().getVideoQuality(false));
		viewHolder.binding.upload.setText(ndtM == null ? c.getString(R.string.TestResults_NotAvailable) : c.getString(R.string.twoParam, ndtM.getTestKeys().getUpload(c), c.getString(ndtM.getTestKeys().getUploadUnit())));
		viewHolder.binding.download.setText(ndtM == null ? c.getString(R.string.TestResults_NotAvailable) : c.getString(R.string.twoParam, ndtM.getTestKeys().getDownload(c), c.getString(ndtM.getTestKeys().getDownloadUnit())));
		viewHolder.binding.quality.setAlpha(dashM == null ? ResultHeaderPerformanceFragment.ALPHA_DIS : ResultHeaderPerformanceFragment.ALPHA_ENA);
		viewHolder.binding.upload.setAlpha(ndtM == null ? ResultHeaderPerformanceFragment.ALPHA_DIS : ResultHeaderPerformanceFragment.ALPHA_ENA);
		viewHolder.binding.download.setAlpha(ndtM == null ? ResultHeaderPerformanceFragment.ALPHA_DIS : ResultHeaderPerformanceFragment.ALPHA_ENA);
		boolean allUploaded = true;
		for (Measurement m : extra.getMeasurements())
			allUploaded = allUploaded && (m.isUploaded() || m.is_failed);
		viewHolder.binding.startTime.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, allUploaded ? 0 : R.drawable.cloudoff, 0);
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		ItemPerformanceBinding binding;

		ViewHolder(ItemPerformanceBinding binding) {
			super(binding.getRoot());
			this.binding = binding;
		}
	}
}
