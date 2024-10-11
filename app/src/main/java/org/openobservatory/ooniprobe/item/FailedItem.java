package org.openobservatory.ooniprobe.item;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.base.Optional;

import org.openobservatory.engine.BaseNettest;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.AbstractDescriptor;
import org.openobservatory.ooniprobe.databinding.ItemFailedBinding;
import org.openobservatory.ooniprobe.model.database.Result;

import java.util.Date;
import java.util.Locale;

import localhost.toolkit.widget.recyclerview.HeterogeneousRecyclerItem;

public class FailedItem extends HeterogeneousRecyclerItem<Result, FailedItem.ViewHolder> {
	private final View.OnClickListener onClickListener;
	private final View.OnLongClickListener onLongClickListener;

	public FailedItem(Result extra, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener) {
		super(extra);
		this.onClickListener = onClickListener;
		this.onLongClickListener = onLongClickListener;
	}

	@Override public ViewHolder onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup) {
		return new ViewHolder(ItemFailedBinding.inflate(layoutInflater, viewGroup, false));
	}

	@Override public void onBindViewHolder(ViewHolder viewHolder) {
		viewHolder.itemView.setTag(extra);
		viewHolder.itemView.setOnClickListener(onClickListener);
		viewHolder.itemView.setOnLongClickListener(onLongClickListener);
		viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.getContext(), R.color.color_gray2));
		viewHolder.binding.testName.setTextColor(ContextCompat.getColor(viewHolder.itemView.getContext(), R.color.color_gray6));
		Optional<AbstractDescriptor<BaseNettest>> possibleDescriptor = extra.getDescriptor(viewHolder.itemView.getContext());
		if (possibleDescriptor.isPresent()) {
			AbstractDescriptor<BaseNettest> descriptor = possibleDescriptor.get();
			viewHolder.binding.icon.setImageResource(descriptor.getDisplayIcon(viewHolder.itemView.getContext()));
			viewHolder.binding.testName.setText(descriptor.getTitle());
		} else {
			viewHolder.binding.testName.setText(extra.test_group_name);
		}

		String failure_msg = viewHolder.itemView.getContext().getString(R.string.TestResults_Overview_Error);
		if (extra.failure_msg != null) {
			failure_msg += " - " + extra.failure_msg;
		} else {
			// NOTE: If the test is running for more than 5 minutes, we assume it's stuck or failed,
			// and we show the default error message.
			long MAX_DURATION = MILLISECONDS.convert(5, MINUTES);
			long duration = new Date().getTime() - extra.start_time.getTime();
			if (duration < MAX_DURATION) {
				failure_msg = viewHolder.itemView.getContext()
						.getString(R.string.Dashboard_Running_Running)
						.replace(":","");
			}
		}
		viewHolder.binding.subtitle.setText(failure_msg);
		viewHolder.binding.startTime.setText(DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), "yMdHm"), extra.start_time));
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		ItemFailedBinding binding;

		ViewHolder(ItemFailedBinding binding) {
			super(binding.getRoot());
			this.binding = binding;
		}
	}
}