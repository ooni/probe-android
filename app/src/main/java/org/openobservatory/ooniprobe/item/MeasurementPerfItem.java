package org.openobservatory.ooniprobe.item;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.test.test.Dash;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import localhost.toolkit.widget.HeterogeneousRecyclerItem;

public class MeasurementPerfItem extends HeterogeneousRecyclerItem<Measurement, MeasurementPerfItem.ViewHolder> {
	private final View.OnClickListener onClickListener;

	public MeasurementPerfItem(Measurement extra, View.OnClickListener onClickListener) {
		super(extra);
		this.onClickListener = onClickListener;
	}

	@Override public ViewHolder onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup) {
		return new ViewHolder(layoutInflater.inflate(R.layout.item_measurement_perf, viewGroup, false));
	}

	@Override public void onBindViewHolder(ViewHolder viewHolder) {
		viewHolder.itemView.setTag(extra);
		Context c = viewHolder.text.getContext();
		viewHolder.text.setText(extra.getTest().getLabelResId());
		viewHolder.text.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, extra.is_failed || (extra.is_uploaded && extra.report_id != null) ? 0 : R.drawable.cloudoff, 0);
		if (extra.test_name.equals(Dash.NAME)) {
			viewHolder.data1.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.video_quality, 0, 0, 0);
			viewHolder.data1.setText(extra.getTestKeys().getVideoQuality(true));
			viewHolder.data2.setVisibility(View.GONE);
		} else {
			viewHolder.data1.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.download_black, 0, 0, 0);
			viewHolder.data1.setText(c.getString(R.string.twoParam, extra.getTestKeys().getDownload(c), c.getString(extra.getTestKeys().getDownloadUnit())));
			viewHolder.data2.setVisibility(View.VISIBLE);
			viewHolder.data2.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.upload_black, 0, 0, 0);
			viewHolder.data2.setText(c.getString(R.string.twoParam, extra.getTestKeys().getUpload(c), c.getString(extra.getTestKeys().getUploadUnit())));
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
