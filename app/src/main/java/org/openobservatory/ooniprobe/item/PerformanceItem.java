package org.openobservatory.ooniprobe.item;

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
import org.openobservatory.ooniprobe.test.impl.Dash;

import butterknife.BindView;
import butterknife.ButterKnife;
import localhost.toolkit.widget.HeterogeneousRecyclerItem;

public class PerformanceItem extends HeterogeneousRecyclerItem<Result, PerformanceItem.ViewHolder> {
	private View.OnLongClickListener onLongClickListener;

	public PerformanceItem(Result extra, View.OnLongClickListener onLongClickListener) {
		super(extra);
		this.onLongClickListener = onLongClickListener;
	}

	@Override public ViewHolder onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup) {
		return new ViewHolder(layoutInflater.inflate(R.layout.item_performance, viewGroup, false));
	}

	@Override public void onBindViewHolder(ViewHolder viewHolder) {
		viewHolder.itemView.setTag(extra);
		viewHolder.itemView.setOnLongClickListener(onLongClickListener);
		viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.getContext(), extra.is_viewed ? android.R.color.transparent : R.color.color_yellow0));
		viewHolder.asnName.setText(Network.getAsnName(viewHolder.asnName.getContext(), extra.getMeasurement().network));
		viewHolder.startTime.setText(DateFormat.getDateFormat(viewHolder.startTime.getContext()).format(extra.start_time));
		viewHolder.quality.setText(extra.getMeasurement(Dash.NAME).getTestKeys().getVideoQuality(viewHolder.quality.getContext(), false));
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
