package org.openobservatory.ooniprobe.item;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.Measurement;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.WebConnectivity;

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
		viewHolder.itemView.setTag(extra);
		AbstractTest test = extra.getTest();
		if (extra.test_name.equals(WebConnectivity.NAME)) {
			viewHolder.text.setText(extra.url.url);
			viewHolder.text.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.category_aldr, 0, extra.is_failed ? R.drawable.cross : R.drawable.tick, 0);
		} else {
			viewHolder.text.setText(test.getLabelResId());
			viewHolder.text.setCompoundDrawablesRelativeWithIntrinsicBounds(test.getIconResId(), 0, extra.is_failed ? R.drawable.cross : R.drawable.tick, 0);
		}
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.text) TextView text;

		ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
			itemView.setOnClickListener(onClickListener);
		}
	}
}
