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
import org.openobservatory.ooniprobe.databinding.ItemWebsitesBinding;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Network;
import org.openobservatory.ooniprobe.model.database.Result;

import java.util.Locale;

public class WebsiteItem extends HeterogeneousRecyclerItem<Result, WebsiteItem.ViewHolder> {
	private final View.OnClickListener onClickListener;
	private final View.OnLongClickListener onLongClickListener;

	public WebsiteItem(Result extra, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener) {
		super(extra);
		this.onClickListener = onClickListener;
		this.onLongClickListener = onLongClickListener;
	}

	@Override
	public ViewHolder onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup) {
		return new ViewHolder(ItemWebsitesBinding.inflate(layoutInflater, viewGroup, false));
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder) {
		viewHolder.itemView.setTag(extra);
		viewHolder.itemView.setOnClickListener(onClickListener);
		viewHolder.itemView.setOnLongClickListener(onLongClickListener);
		viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.getContext(), extra.is_viewed ? android.R.color.transparent : R.color.color_yellow0));
		viewHolder.binding.asnName.setText(Network.toString(viewHolder.binding.asnName.getContext(), extra.network));
		viewHolder.binding.startTime.setText(DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), "yMdHm"), extra.start_time));
		Long blocked = extra.countAnomalousMeasurements();
		Long tested = extra.countTotalMeasurements();
		viewHolder.binding.failedMeasurements.setText(viewHolder.binding.failedMeasurements.getContext().getResources().getQuantityString(R.plurals.TestResults_Overview_Websites_Blocked, blocked.intValue(), blocked.toString()));
		viewHolder.binding.testedMeasurements.setText(viewHolder.binding.failedMeasurements.getContext().getResources().getQuantityString(R.plurals.TestResults_Overview_Websites_Tested, tested.intValue(), tested.toString()));
		viewHolder.binding.failedMeasurements.setTextColor(ContextCompat.getColor(viewHolder.binding.failedMeasurements.getContext(), blocked == 0 ? R.color.color_gray9 : R.color.color_yellow9));
		DrawableCompat.setTint(DrawableCompat.wrap(viewHolder.binding.failedMeasurements.getCompoundDrawablesRelative()[0]).mutate(), ContextCompat.getColor(viewHolder.binding.failedMeasurements.getContext(), blocked == 0 ? R.color.color_gray9 : R.color.color_yellow9));
		boolean allUploaded = true;
		for (Measurement m : extra.getMeasurements())
			allUploaded = allUploaded && (m.isUploaded() || m.is_failed);
		viewHolder.binding.startTime.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, allUploaded ? 0 : R.drawable.cloudoff, 0);
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		ItemWebsitesBinding binding;

		ViewHolder(ItemWebsitesBinding binding) {
			super(binding.getRoot());
			this.binding = binding;
		}
	}
}