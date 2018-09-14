package org.openobservatory.ooniprobe.item;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.jsonresult.TestKeys;
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
		Context c = viewHolder.text.getContext();
		viewHolder.text.setText(extra.getTest().getLabelResId());
		if (extra.test_name.equals(Dash.NAME)) {
			viewHolder.data1.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.video_quality, 0, 0, 0);
			viewHolder.data1.setText(testKeys.getVideoQuality(c, true));
			viewHolder.data2.setVisibility(View.GONE);
		} else {
			viewHolder.data1.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.download_black, 0, 0, 0);
			viewHolder.data1.setText(c.getString(R.string.twoParam, testKeys.getDownload(c), testKeys.getDownloadUnit(c)));
			viewHolder.data2.setVisibility(View.VISIBLE);
			viewHolder.data2.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.upload_black, 0, 0, 0);
			viewHolder.data2.setText(c.getString(R.string.twoParam, testKeys.getUpload(c), testKeys.getUploadUnit(c)));
		}
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.text) TextView text;
		@BindView(R.id.data1) TextView data1;
		@BindView(R.id.data2) TextView data2;

		ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
			itemView.setOnClickListener(onClickListener);
		}
	}
}
