package org.openobservatory.ooniprobe.item;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.databinding.ItemOoniRunBinding;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Network;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.test.suite.OONIRunSuite;

import java.util.Locale;

import localhost.toolkit.widget.recyclerview.HeterogeneousRecyclerItem;

public class RunItem extends HeterogeneousRecyclerItem<Result, RunItem.ViewHolder> {
	private final View.OnClickListener onClickListener;
	private final View.OnLongClickListener onLongClickListener;

	public RunItem(Result extra, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener) {
		super(extra);
		this.onClickListener = onClickListener;
		this.onLongClickListener = onLongClickListener;
	}

	@Override public ViewHolder onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup) {
		return new ViewHolder(ItemOoniRunBinding.inflate(layoutInflater, viewGroup,false));
	}

	@Override public void onBindViewHolder(ViewHolder viewHolder) {
		viewHolder.itemView.setTag(extra);
		viewHolder.itemView.setOnClickListener(onClickListener);
		viewHolder.itemView.setOnLongClickListener(onLongClickListener);
		viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.getContext(), extra.is_viewed ? android.R.color.transparent : R.color.color_yellow0));
		viewHolder.binding.asnName.setText(Network.toString(viewHolder.binding.asnName.getContext(), extra.network));
		viewHolder.binding.name.setText(extra.getTestSuite().getTitle());
		viewHolder.binding.totalMeasurements.setText(String.format("%d measured", extra.countTotalMeasurements()));
		viewHolder.binding.icon.setImageDrawable(viewHolder.itemView.getContext().getDrawable(extra.getTestSuite().getIconGradient()));
		viewHolder.binding.icon.setColorFilter(viewHolder.itemView.getResources().getColor(R.color.color_gray7));
		viewHolder.binding.startTime.setText(DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), "yMdHm"), extra.start_time));
		if (extra.test_group_name.equals(OONIRunSuite.NAME)) {
			int color = ((OONIRunSuite)extra.getTestSuite()).getDescriptor().getParsedColor();
			viewHolder.binding.icon.setColorFilter(color);
			viewHolder.binding.name.setTextColor(color);
		}
		boolean allUploaded = true;
		for (Measurement m : extra.getMeasurements())
			allUploaded = allUploaded && (m.isUploaded() || m.is_failed);
		viewHolder.binding.startTime.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, allUploaded ? 0 : R.drawable.cloudoff, 0);
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		ItemOoniRunBinding binding;

		ViewHolder(ItemOoniRunBinding binding) {
			super(binding.getRoot());
			this.binding=binding;
		}
	}
}
