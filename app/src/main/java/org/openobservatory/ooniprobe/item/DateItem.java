package org.openobservatory.ooniprobe.item;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.openobservatory.ooniprobe.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import localhost.toolkit.widget.recyclerview.HeterogeneousRecyclerItem;

public class DateItem extends HeterogeneousRecyclerItem<Date, DateItem.ViewHolder> {
	private final SimpleDateFormat SDF;

	public DateItem(Date extra) {
		super(extra);
		SDF = new SimpleDateFormat(DateFormat.getBestDateTimePattern(Locale.getDefault(), "MMMMyyyy"), Locale.getDefault());
	}

	@Override public ViewHolder onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup) {
		return new ViewHolder(layoutInflater.inflate(R.layout.item_date, viewGroup, false));
	}

	@Override public void onBindViewHolder(ViewHolder viewHolder) {
		viewHolder.textView.setText(SDF.format(extra));
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.textView) TextView textView;

		ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
