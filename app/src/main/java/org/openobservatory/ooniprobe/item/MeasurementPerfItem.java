package org.openobservatory.ooniprobe.item;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.Measurement;
import org.openobservatory.ooniprobe.model.TestKeys;
import org.openobservatory.ooniprobe.test.test.Dash;

import butterknife.BindView;
import butterknife.ButterKnife;
import localhost.toolkit.widget.HeterogeneousRecyclerItem;

public class MeasurementPerfItem extends HeterogeneousRecyclerItem<Measurement, MeasurementPerfItem.ViewHolder> {
	private View.OnClickListener onClickListener;

	public MeasurementPerfItem(Measurement extra, View.OnClickListener onClickListener) {
		super(extra);
		this.onClickListener = onClickListener;
	}

	@Override public ViewHolder onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup) {
		return new ViewHolder(layoutInflater.inflate(R.layout.item_measurement_perf, viewGroup, false));
	}

	@Override public void onBindViewHolder(ViewHolder viewHolder) {
		viewHolder.itemView.setTag(extra);
		TestKeys testKeys = extra.getTestKeys();
		Context c = viewHolder.data.getContext();
		viewHolder.text.setText(extra.getTest().getLabelResId());
		if (extra.test_name.equals(Dash.NAME)) {
			viewHolder.data.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.video_quality, 0, 0, 0);
			viewHolder.data.setText(testKeys.getVideoQuality(c, true));
		} else {
			viewHolder.data.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
			viewHolder.data.setText(c.getString(R.string.fourParam, testKeys.getDownload(c), testKeys.getDownloadUnit(c), testKeys.getUpload(c), testKeys.getUploadUnit(c)));
		}
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.text) TextView text;
		@BindView(R.id.data) TextView data;

		ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
			itemView.setOnClickListener(onClickListener);
		}
	}
}
