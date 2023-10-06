package org.openobservatory.ooniprobe.item;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;
import localhost.toolkit.widget.recyclerview.HeterogeneousRecyclerItem;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.databinding.ItemMiddleboxesBinding;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Network;
import org.openobservatory.ooniprobe.model.database.Result;

import java.util.Locale;

/**
 * @deprecated
 * It is not possible to run a MiddleBoxesSuite anymore
 * so the MiddleboxesItem is not gonna be shown anymore in the Test results
 */
@Deprecated
public class MiddleboxesItem extends HeterogeneousRecyclerItem<Result, MiddleboxesItem.ViewHolder> {
	private final View.OnClickListener onClickListener;
	private final View.OnLongClickListener onLongClickListener;

	public MiddleboxesItem(Result extra, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener) {
		super(extra);
		this.onClickListener = onClickListener;
		this.onLongClickListener = onLongClickListener;
	}

	@Override public ViewHolder onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup) {
		return new ViewHolder(ItemMiddleboxesBinding.inflate(layoutInflater, viewGroup, false));
	}

	@Override public void onBindViewHolder(ViewHolder viewHolder) {
		viewHolder.itemView.setTag(extra);
		viewHolder.itemView.setOnClickListener(onClickListener);
		viewHolder.itemView.setOnLongClickListener(onLongClickListener);
		viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.getContext(), extra.is_viewed ? android.R.color.transparent : R.color.color_yellow0));
		viewHolder.binding.asnName.setText(Network.toString(viewHolder.binding.asnName.getContext(), extra.network));
		viewHolder.binding.startTime.setText(DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), "yMdHm"), extra.start_time));
		if (extra.countAnomalousMeasurements() > 0) {
			viewHolder.binding.status.setText(R.string.TestResults_Overview_MiddleBoxes_Found);
			viewHolder.binding.status.setTextColor(ContextCompat.getColor(viewHolder.binding.status.getContext(), R.color.color_yellow9));
			DrawableCompat.setTint(DrawableCompat.wrap(viewHolder.binding.status.getCompoundDrawablesRelative()[0]).mutate(), ContextCompat.getColor(viewHolder.binding.status.getContext(), R.color.color_yellow9));
		} else if (extra.countCompletedMeasurements() == 0) {
			viewHolder.binding.status.setText(R.string.TestResults_Overview_MiddleBoxes_Failed);
			viewHolder.binding.status.setTextColor(ContextCompat.getColor(viewHolder.binding.status.getContext(), R.color.color_gray9));
			DrawableCompat.setTint(DrawableCompat.wrap(viewHolder.binding.status.getCompoundDrawablesRelative()[0]).mutate(), ContextCompat.getColor(viewHolder.binding.status.getContext(), R.color.color_gray9));
		} else {
			viewHolder.binding.status.setText(R.string.TestResults_Overview_MiddleBoxes_NotFound);
			viewHolder.binding.status.setTextColor(ContextCompat.getColor(viewHolder.binding.status.getContext(), R.color.color_gray9));
			DrawableCompat.setTint(DrawableCompat.wrap(viewHolder.binding.status.getCompoundDrawablesRelative()[0]).mutate(), ContextCompat.getColor(viewHolder.binding.status.getContext(), R.color.color_gray9));
		}
		boolean allUploaded = true;
		for (Measurement m : extra.getMeasurements())
			allUploaded = allUploaded && (m.isUploaded() || m.is_failed);
		viewHolder.binding.startTime.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, allUploaded ? 0 : R.drawable.cloudoff, 0);
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		ItemMiddleboxesBinding binding;

		ViewHolder(ItemMiddleboxesBinding binding) {
			super(binding.getRoot());
			this.binding = binding;
		}
	}
}
