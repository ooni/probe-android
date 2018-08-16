package org.openobservatory.ooniprobe.item;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.Network;
import org.openobservatory.ooniprobe.model.Result;

import butterknife.BindView;
import butterknife.ButterKnife;
import localhost.toolkit.widget.HeterogeneousRecyclerItem;

public class MiddleboxesItem extends HeterogeneousRecyclerItem<Result, MiddleboxesItem.ViewHolder> {
	public MiddleboxesItem(Result extra) {
		super(extra);
	}

	@Override public ViewHolder onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup) {
		return new ViewHolder(layoutInflater.inflate(R.layout.item_middleboxes, viewGroup, false));
	}

	@Override public void onBindViewHolder(ViewHolder viewHolder) {
		viewHolder.asnName.setText(Network.getAsnName(viewHolder.asnName.getContext(), extra.getMeasurement().network));
		viewHolder.startTime.setText(DateFormat.getDateFormat(viewHolder.startTime.getContext()).format(extra.start_time));
		if (extra.countMeasurement(null, null, true) > 0)
			viewHolder.status.setText(R.string.TestResults_Overview_MiddleBoxes_Found);
		else if (extra.countMeasurement(null, true, null) == 0)
			viewHolder.status.setText(R.string.TestResults_Overview_MiddleBoxes_NotFound);
		else
			viewHolder.status.setText(R.string.TestResults_Overview_MiddleBoxes_Failed);
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.asnName) TextView asnName;
		@BindView(R.id.startTime) TextView startTime;
		@BindView(R.id.status) TextView status;

		ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
