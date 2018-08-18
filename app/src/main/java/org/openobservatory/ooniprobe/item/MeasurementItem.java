package org.openobservatory.ooniprobe.item;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.Measurement;
import org.openobservatory.ooniprobe.test.impl.FacebookMessenger;
import org.openobservatory.ooniprobe.test.impl.Telegram;
import org.openobservatory.ooniprobe.test.impl.Whatsapp;

import butterknife.BindView;
import butterknife.ButterKnife;
import localhost.toolkit.widget.HeterogeneousRecyclerItem;

public class MeasurementItem extends HeterogeneousRecyclerItem<Measurement, MeasurementItem.ViewHolder> {
	public MeasurementItem(Measurement extra) {
		super(extra);
	}

	@Override public ViewHolder onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup) {
		return new ViewHolder(layoutInflater.inflate(R.layout.item_measurement, viewGroup, false));
	}

	@Override public void onBindViewHolder(ViewHolder viewHolder) {
		int textResId;
		int drawResId;
		switch (extra.test_name) {
			case FacebookMessenger.NAME:
				textResId = R.string.test_facebook_messenger;
				drawResId = R.drawable.test_facebook_messenger;
				break;
			case Telegram.NAME:
				textResId = R.string.test_telegram;
				drawResId = R.drawable.test_telegram;
				break;
			case Whatsapp.NAME:
				textResId = R.string.test_whatsapp;
				drawResId = R.drawable.test_whatsapp;
				break;
			default:
				textResId = 0;
				drawResId = 0;
				break;
		}
		viewHolder.text.setText(textResId);
		viewHolder.text.setCompoundDrawablesRelativeWithIntrinsicBounds(drawResId, 0, extra.is_failed ? R.drawable.cross : R.drawable.tick, 0);
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.text) TextView text;

		ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
