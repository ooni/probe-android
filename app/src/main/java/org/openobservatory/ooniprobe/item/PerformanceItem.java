package org.openobservatory.ooniprobe.item;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.Network;
import org.openobservatory.ooniprobe.model.Result;
import org.openobservatory.ooniprobe.model.TestKeys;
import org.openobservatory.ooniprobe.test.test.Dash;
import org.openobservatory.ooniprobe.test.test.Ndt;

import butterknife.BindView;
import butterknife.ButterKnife;
import localhost.toolkit.widget.HeterogeneousRecyclerItem;

public class PerformanceItem extends HeterogeneousRecyclerItem<Result, PerformanceItem.ViewHolder> {
	private View.OnClickListener onClickListener;
	private View.OnLongClickListener onLongClickListener;

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
		viewHolder.asnName.setText(Network.getAsnName(c, extra.getMeasurement().network));
		viewHolder.startTime.setText(DateFormat.getDateFormat(c).format(extra.start_time));
		TestKeys dash = extra.getMeasurement(Dash.NAME).getTestKeys();
		TestKeys ndt = extra.getMeasurement(Ndt.NAME).getTestKeys();
		viewHolder.quality.setText(dash.getVideoQuality(c, false));
		viewHolder.upload.setText(c.getString(R.string.twoParam, ndt.getUpload(c), ndt.getUploadUnit(c)));
		viewHolder.download.setText(c.getString(R.string.twoParam, ndt.getDownload(c), ndt.getDownloadUnit(c)));
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
