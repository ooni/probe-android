package org.openobservatory.ooniprobe.item;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.WebConnectivity;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import localhost.toolkit.widget.HeterogeneousRecyclerItem;

public class MeasurementItem extends HeterogeneousRecyclerItem<Measurement, MeasurementItem.ViewHolder> {
	private View.OnClickListener onClickListener;

	public MeasurementItem(Measurement extra, View.OnClickListener onClickListener) {
		super(extra);
		this.onClickListener = onClickListener;
	}

	@Override public ViewHolder onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup) {
		return new ViewHolder(layoutInflater.inflate(R.layout.item_measurement, viewGroup, false));
	}

	@Override public void onBindViewHolder(ViewHolder viewHolder) {
		viewHolder.text.setTag(extra);
		AbstractTest test = extra.getTest();
		if (extra.test_name.equals(WebConnectivity.NAME)) {
			viewHolder.text.setText(extra.url.url);
			viewHolder.text.setCompoundDrawablesRelativeWithIntrinsicBounds(extra.url.getCategoryIcon(viewHolder.text.getContext()), 0, extra.is_anomaly ? R.drawable.exclamation_point : extra.is_failed ? R.drawable.reload : R.drawable.tick, 0);
		} else {
			viewHolder.text.setText(test.getLabelResId());
			viewHolder.text.setCompoundDrawablesRelativeWithIntrinsicBounds(test.getIconResId(), 0, extra.is_anomaly ? R.drawable.exclamation_point : extra.is_failed ? R.drawable.reload : R.drawable.tick, 0);
		}
		DrawableCompat.setTint(DrawableCompat.wrap(viewHolder.text.getCompoundDrawablesRelative()[0]).mutate(), ContextCompat.getColor(viewHolder.text.getContext(), extra.is_failed ? R.color.color_gray5 : R.color.color_gray7));
		viewHolder.text.setBackgroundColor(ContextCompat.getColor(viewHolder.text.getContext(), extra.is_failed ? R.color.color_gray1 : android.R.color.transparent));
		viewHolder.text.setTextColor(ContextCompat.getColor(viewHolder.text.getContext(), extra.is_failed ? R.color.color_gray5 : R.color.color_gray9));
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.text) TextView text;

		ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
			text.setOnClickListener(onClickListener);
		}
	}
}
