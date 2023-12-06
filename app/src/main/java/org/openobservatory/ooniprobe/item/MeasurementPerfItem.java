package org.openobservatory.ooniprobe.item;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.databinding.ItemMeasurementPerfBinding;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.test.test.Dash;
import org.openobservatory.ooniprobe.test.test.HttpHeaderFieldManipulation;
import org.openobservatory.ooniprobe.test.test.HttpInvalidRequestLine;
import org.openobservatory.ooniprobe.test.test.Ndt;

import localhost.toolkit.widget.recyclerview.HeterogeneousRecyclerItem;
@Deprecated
public class MeasurementPerfItem extends HeterogeneousRecyclerItem<Measurement, MeasurementPerfItem.ViewHolder> {
	private final View.OnClickListener onClickListener;

	public MeasurementPerfItem(Measurement extra, View.OnClickListener onClickListener) {
		super(extra);
		this.onClickListener = onClickListener;
	}

	@Override public ViewHolder onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup) {
		return new ViewHolder(ItemMeasurementPerfBinding.inflate(layoutInflater, viewGroup, false));
	}

	@Override public void onBindViewHolder(ViewHolder viewHolder) {
		viewHolder.itemView.setTag(extra);
		Context c = viewHolder.binding.text.getContext();
		if (extra.getTest().getLabelResId() == (R.string.Test_Experimental_Fullname))
			viewHolder.binding.text.setText(extra.getTest().getName());
		else
			viewHolder.binding.text.setText(extra.getTest().getLabelResId());
		viewHolder.binding.text.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, extra.is_failed || extra.isUploaded() ? 0 : R.drawable.cloudoff, 0);
		if (extra.test_name.equals(Dash.NAME)) {
			viewHolder.binding.data1.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.video_quality, 0, 0, 0);
			viewHolder.binding.data1.setText(extra.getTestKeys().getVideoQuality(true));
			viewHolder.binding.data2.setVisibility(View.GONE);
		} else if (extra.test_name.equals(Ndt.NAME)) {
			viewHolder.binding.data1.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.download_black, 0, 0, 0);
			viewHolder.binding.data1.setText(c.getString(R.string.twoParam, extra.getTestKeys().getDownload(c), c.getString(extra.getTestKeys().getDownloadUnit())));
			viewHolder.binding.data2.setVisibility(View.VISIBLE);
			viewHolder.binding.data2.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.upload_black, 0, 0, 0);
			viewHolder.binding.data2.setText(c.getString(R.string.twoParam, extra.getTestKeys().getUpload(c), c.getString(extra.getTestKeys().getUploadUnit())));
		} else if (extra.test_name.equals(HttpHeaderFieldManipulation.NAME)
				|| extra.test_name.equals(HttpInvalidRequestLine.NAME)) {
			viewHolder.binding.data1.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.test_middle_boxes_small, 0, 0, 0);
			viewHolder.binding.data1.setText(extra.is_anomaly ?
					c.getString(R.string.TestResults_Overview_MiddleBoxes_Found) :
					c.getString(R.string.TestResults_Overview_MiddleBoxes_NotFound));
			viewHolder.binding.data2.setVisibility(View.GONE);
		}
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		ItemMeasurementPerfBinding binding;
		ViewHolder(ItemMeasurementPerfBinding binding) {
			super(binding.getRoot());
			this.binding = binding;
			binding.getRoot().setOnClickListener(onClickListener);
		}
	}
}
