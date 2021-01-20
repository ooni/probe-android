package org.openobservatory.ooniprobe.item;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.fragment.resultHeader.ResultHeaderPerformanceFragment;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Network;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.test.test.Dash;
import org.openobservatory.ooniprobe.test.test.Ndt;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import localhost.toolkit.widget.recyclerview.HeterogeneousRecyclerItem;

public class PerformanceItem extends HeterogeneousRecyclerItem<Result, PerformanceItem.ViewHolder> {
	private final View.OnClickListener onClickListener;
	private final View.OnLongClickListener onLongClickListener;

	public PerformanceItem(Result extra, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener) {
		super(extra);
		this.onClickListener = onClickListener;
		this.onLongClickListener = onLongClickListener;
	}

	@Override public ViewHolder onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup) {
		return new ViewHolder(layoutInflater.inflate(R.layout.item_performance, viewGroup, false));
	}

	@Override public void onBindViewHolder(ViewHolder viewHolder) {
		Context c = viewHolder.itemView.getContext();
		viewHolder.itemView.setTag(extra);
		viewHolder.itemView.setOnClickListener(onClickListener);
		viewHolder.itemView.setOnLongClickListener(onLongClickListener);
		viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(c, extra.is_viewed ? android.R.color.transparent : R.color.color_yellow0));
		viewHolder.asnName.setText(Network.toString(viewHolder.asnName.getContext(), extra.network));
		viewHolder.startTime.setText(DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), "yMdHm"), extra.start_time));
		Measurement dashM = extra.getMeasurement(Dash.NAME);
		Measurement ndtM = extra.getMeasurement(Ndt.NAME);
		viewHolder.quality.setText(dashM == null ? R.string.TestResults_NotAvailable : dashM.getTestKeys().getVideoQuality(false));
		viewHolder.upload.setText(ndtM == null ? c.getString(R.string.TestResults_NotAvailable) : c.getString(R.string.twoParam, ndtM.getTestKeys().getUpload(c), c.getString(ndtM.getTestKeys().getUploadUnit())));
		viewHolder.download.setText(ndtM == null ? c.getString(R.string.TestResults_NotAvailable) : c.getString(R.string.twoParam, ndtM.getTestKeys().getDownload(c), c.getString(ndtM.getTestKeys().getDownloadUnit())));
		viewHolder.quality.setAlpha(dashM == null ? ResultHeaderPerformanceFragment.ALPHA_DIS : ResultHeaderPerformanceFragment.ALPHA_ENA);
		viewHolder.upload.setAlpha(ndtM == null ? ResultHeaderPerformanceFragment.ALPHA_DIS : ResultHeaderPerformanceFragment.ALPHA_ENA);
		viewHolder.download.setAlpha(ndtM == null ? ResultHeaderPerformanceFragment.ALPHA_DIS : ResultHeaderPerformanceFragment.ALPHA_ENA);
		boolean allUploaded = true;
		for (Measurement m : extra.getMeasurements())
			allUploaded = allUploaded && (m.isUploaded() || m.is_failed);
		viewHolder.startTime.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, allUploaded ? 0 : R.drawable.cloudoff, 0);
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.asnName) TextView asnName;
		@BindView(R.id.startTime) TextView startTime;
		@BindView(R.id.upload) TextView upload;
		@BindView(R.id.download) TextView download;
		@BindView(R.id.quality) TextView quality;

		ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
