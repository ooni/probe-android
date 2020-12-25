package org.openobservatory.ooniprobe.item;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.database.Result;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
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
		return new ViewHolder(layoutInflater.inflate(R.layout.item_failed, viewGroup, false));
	}

	@Override public void onBindViewHolder(ViewHolder viewHolder) {
		viewHolder.itemView.setTag(extra);
		viewHolder.itemView.setOnClickListener(onClickListener);
		viewHolder.itemView.setOnLongClickListener(onLongClickListener);
		viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.getContext(), R.color.color_gray2));
		viewHolder.testName.setTextColor(ContextCompat.getColor(viewHolder.itemView.getContext(), R.color.color_gray6));
		viewHolder.icon.setImageResource(extra.getTestSuite().getIcon());
		viewHolder.testName.setText(extra.getTestSuite().getTitle());
		String failure_msg = viewHolder.itemView.getContext().getString(R.string.TestResults_Overview_Error);
		if (extra.failure_msg != null)
			failure_msg += " - " + extra.failure_msg;
		viewHolder.subtitle.setText(failure_msg);
		viewHolder.startTime.setText(DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), "yMdHm"), extra.start_time));
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.testName) TextView testName;
		@BindView(R.id.subtitle) TextView subtitle;
		@BindView(R.id.startTime) TextView startTime;
		@BindView(R.id.icon) ImageView icon;

		ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}