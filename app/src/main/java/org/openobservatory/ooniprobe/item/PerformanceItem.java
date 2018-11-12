package org.openobservatory.ooniprobe.item;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Network;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.model.jsonresult.TestKeys;
import org.openobservatory.ooniprobe.test.test.Dash;
import org.openobservatory.ooniprobe.test.test.Ndt;

import java.util.Locale;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import localhost.toolkit.widget.HeterogeneousRecyclerItem;

public class PerformanceItem extends HeterogeneousRecyclerItem<Result, PerformanceItem.ViewHolder> {
	private View.OnClickListener onClickListener;
	private View.OnLongClickListener onLongClickListener;

	public PerformanceItem(Result extra, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener) {
		super(extra);
		this.onClickListener = onClickListener;
		this.onLongClickListener = onLongClickListener;
	}

	@Override public ViewHolder onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup) {
		return new ViewHolder(layoutInflater.inflate(R.layout.item_performance, viewGroup, false));
	}

	@Override public void onBindViewHolder(ViewHolder viewHolder) {
		Context c = viewHolder.itemView.getContext();
		viewHolder.itemView.setTag(extra);
		viewHolder.itemView.setOnClickListener(onClickListener);
		viewHolder.itemView.setOnLongClickListener(onLongClickListener);
		viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(c, extra.is_viewed ? android.R.color.transparent : R.color.color_yellow0));
		viewHolder.asnName.setText(Network.toString(viewHolder.asnName.getContext(), extra.network));
		viewHolder.startTime.setText(DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), "yMdHm"), extra.start_time));
		Measurement dashM = extra.getMeasurement(Dash.NAME);
		Measurement ndtM = extra.getMeasurement(Ndt.NAME);
		if (dashM != null) {
			TestKeys dashTK = dashM.getTestKeys();
			viewHolder.quality.setText(dashTK.getVideoQuality(false));
		} else {
			viewHolder.quality.setText(null);
		}
		if (ndtM != null) {
			TestKeys ndtTK = ndtM.getTestKeys();
			viewHolder.upload.setText(c.getString(R.string.twoParam, ndtTK.getUpload(c), c.getString(ndtTK.getUploadUnit())));
			viewHolder.download.setText(c.getString(R.string.twoParam, ndtTK.getDownload(c), c.getString(ndtTK.getDownloadUnit())));
		} else {
			viewHolder.upload.setText(null);
			viewHolder.download.setText(null);
		}
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.asnName) TextView asnName;
		@BindView(R.id.startTime) TextView startTime;
		@BindView(R.id.upload) TextView upload;
		@BindView(R.id.download) TextView download;
		@BindView(R.id.quality) TextView quality;

		ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
