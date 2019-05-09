package org.openobservatory.ooniprobe.item;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Network;
import org.openobservatory.ooniprobe.model.database.Result;

import java.util.Locale;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import localhost.toolkit.widget.HeterogeneousRecyclerItem;

public class MiddleboxesItem extends HeterogeneousRecyclerItem<Result, MiddleboxesItem.ViewHolder> {
	private final View.OnClickListener onClickListener;
	private final View.OnLongClickListener onLongClickListener;

	public MiddleboxesItem(Result extra, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener) {
		super(extra);
		this.onClickListener = onClickListener;
		this.onLongClickListener = onLongClickListener;
	}

	@Override public ViewHolder onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup) {
		return new ViewHolder(layoutInflater.inflate(R.layout.item_middleboxes, viewGroup, false));
	}

	@Override public void onBindViewHolder(ViewHolder viewHolder) {
		viewHolder.itemView.setTag(extra);
		viewHolder.itemView.setOnClickListener(onClickListener);
		viewHolder.itemView.setOnLongClickListener(onLongClickListener);
		viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.getContext(), extra.is_viewed ? android.R.color.transparent : R.color.color_yellow0));
		viewHolder.asnName.setText(Network.toString(viewHolder.asnName.getContext(), extra.network));
		viewHolder.startTime.setText(DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), "yMdHm"), extra.start_time));
		if (extra.countAnomalousMeasurements() > 0) {
			viewHolder.status.setText(R.string.TestResults_Overview_MiddleBoxes_Found);
			viewHolder.status.setTextColor(ContextCompat.getColor(viewHolder.status.getContext(), R.color.color_yellow9));
			DrawableCompat.setTint(DrawableCompat.wrap(viewHolder.status.getCompoundDrawablesRelative()[0]).mutate(), ContextCompat.getColor(viewHolder.status.getContext(), R.color.color_yellow9));
		} else if (extra.countCompletedMeasurements() == 0) {
			viewHolder.status.setText(R.string.TestResults_Overview_MiddleBoxes_Failed);
			viewHolder.status.setTextColor(ContextCompat.getColor(viewHolder.status.getContext(), R.color.color_gray9));
			DrawableCompat.setTint(DrawableCompat.wrap(viewHolder.status.getCompoundDrawablesRelative()[0]).mutate(), ContextCompat.getColor(viewHolder.status.getContext(), R.color.color_gray9));
		} else {
			viewHolder.status.setText(R.string.TestResults_Overview_MiddleBoxes_NotFound);
			viewHolder.status.setTextColor(ContextCompat.getColor(viewHolder.status.getContext(), R.color.color_gray9));
			DrawableCompat.setTint(DrawableCompat.wrap(viewHolder.status.getCompoundDrawablesRelative()[0]).mutate(), ContextCompat.getColor(viewHolder.status.getContext(), R.color.color_gray9));
		}
		boolean allUploaded = true;
		for (Measurement m : extra.getMeasurements())
			allUploaded = allUploaded && ((m.is_uploaded && m.report_id != null) || m.is_failed);
		viewHolder.startTime.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, allUploaded ? 0 : R.drawable.cloudoff, 0);
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.asnName) TextView asnName;
		@BindView(R.id.startTime) TextView startTime;
		@BindView(R.id.status) TextView status;

		ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
